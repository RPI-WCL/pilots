import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class ATR72 extends PilotsRuntime {
    private int time_; // msec
    private SlidingWindow win_measured_weight_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public ATR72( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_measured_weight_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        Vector<Constraint> constraints1 = new Vector<Constraint>();
        constraints1.add( new Constraint( Constraint.LESS_THAN, 0.035 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "No error", constraints1 ) );

        Vector<Constraint> constraints2 = new Vector<Constraint>();
        constraints2.add( new Constraint( Constraint.GREATER_THAN, 0.035 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "weight error", constraints2 ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value speed, Value speed_corrected,
                                  Value aoa, Value aoa_corrected,
                                  Value pressure, Value pressure_corrected,
                                  Value temperature, Value temperature_corrected,
                                  Value weight, Value weight_corrected,
                                  Value cl, Value cl_corrected,
                                  Mode mode, int frequency ) {
        speed.setValue( getData( "speed", new Method( Method.Closest, "t" ) ) );
        aoa.setValue( getData( "aoa", new Method( Method.Closest, "t" ) ) );
        pressure.setValue( getData( "pressure", new Method( Method.Closest, "t" ) ) );
        temperature.setValue( getData( "temperature", new Method( Method.Closest, "t" ) ) );
        weight.setValue( getData( "weight", new Method( Method.Closest, "t" ) ) );
        cl.setValue( getData( "cl", new Method( Method.Predict, "aoa" ) ) );
        double e = Math.abs(weight.getValue()-pressure.getValue()*(speed.getValue()*speed.getValue())*61*cl.getValue()/(2*286.9*temperature.getValue()))/weight.getValue();

        win.push( e );
        mode.setMode( errorAnalyzer_.analyze( win, frequency ) );

        speed_corrected.setValue( speed.getValue() );
        aoa_corrected.setValue( aoa.getValue() );
        pressure_corrected.setValue( pressure.getValue() );
        temperature_corrected.setValue( temperature.getValue() );
        weight_corrected.setValue( weight.getValue() );
        cl_corrected.setValue( cl.getValue() );
        switch (mode.getMode()) {
        case 1:
            weight_corrected.setValue( pressure.getValue()*(speed.getValue()*speed.getValue())*61*cl.getValue()/(2*286.9*temperature.getValue()) );
            break;
        }
    }

    public void startOutput_measured_weight() {
        try {
            openSocket( OutputType.Output, 0, new String( "measured_weight" ) );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 1000;
        while (!isEndTime()) {
            Value speed = new Value();
            Value speed_corrected = new Value();
            Value aoa = new Value();
            Value aoa_corrected = new Value();
            Value pressure = new Value();
            Value pressure_corrected = new Value();
            Value temperature = new Value();
            Value temperature_corrected = new Value();
            Value weight = new Value();
            Value weight_corrected = new Value();
            Value cl = new Value();
            Value cl_corrected = new Value();
            Mode mode = new Mode();

            getCorrectedData( win_measured_weight_, speed, speed_corrected, aoa, aoa_corrected, pressure, pressure_corrected, temperature, temperature_corrected, weight, weight_corrected, cl, cl_corrected, mode, frequency );
            double measured_weight = weight_corrected.getValue();

            String desc = errorAnalyzer_.getDesc( mode.getMode() );
            dbgPrint( desc + ", measured_weight=" + measured_weight + " at " + getTime() );

            try {
                sendData( OutputType.Output, 0, measured_weight );
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }

            time_ += frequency;
            progressTime( frequency );
        }

        dbgPrint( "Finished at " + getTime() );
    }

    public static void main( String[] args ) {
        ATR72 app = new ATR72( args );
        app.startServer();

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "Hit ENTER key after running input producer(s)." );
        try {
            reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.startOutput_measured_weight();
    }
}
