import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class AirFranceDemo extends PilotsRuntime {
    private int time_; // msec
    private SlidingWindow win_true_air_speed_out_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;
    private double error;

    public AirFranceDemo( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_true_air_speed_out_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        Vector<Constraint> constraints1 = new Vector<Constraint>();
        constraints1.add( new Constraint( Constraint.GREATER_THAN, -47.0 ) );
        constraints1.add( new Constraint( Constraint.LESS_THAN, 47.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "No error", constraints1 ) );

        Vector<Constraint> constraints2 = new Vector<Constraint>();
        constraints2.add( new Constraint( Constraint.GREATER_THAN, 220.9 ) );
        constraints2.add( new Constraint( Constraint.LESS_THAN, 517.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Pitot tube failure", constraints2 ) );

        Vector<Constraint> constraints3 = new Vector<Constraint>();
        constraints3.add( new Constraint( Constraint.GREATER_THAN, -517.0 ) );
        constraints3.add( new Constraint( Constraint.LESS_THAN, -423.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "GPS failure", constraints3 ) );

        Vector<Constraint> constraints4 = new Vector<Constraint>();
        constraints4.add( new Constraint( Constraint.GREATER_THAN, -203.66 ) );
        constraints4.add( new Constraint( Constraint.LESS_THAN, -47.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Pitot tube + GPS failure", constraints4 ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value true_air_speed, Value true_air_speed_corrected,
                                  Value ground_speed, Value ground_speed_corrected,
                                  Value wind_speed, Value wind_speed_corrected,
                                  Value air_angle, Value air_angle_corrected,
                                  Value ground_angle, Value ground_angle_corrected,
                                  Value wind_angle, Value wind_angle_corrected,
                                  Mode mode, int frequency ) {
        true_air_speed.setValue( getData( "true_air_speed", new Method( Method.Closest, "t" ) ) );
        ground_speed.setValue( getData( "ground_speed", new Method( Method.Closest, "t" ) ) );
        wind_speed.setValue( getData( "wind_speed", new Method( Method.Closest, "t" ) ) );
        air_angle.setValue( getData( "air_angle", new Method( Method.Closest, "t" ) ) );
        ground_angle.setValue( getData( "ground_angle", new Method( Method.Closest, "t" ) ) );
        wind_angle.setValue( getData( "wind_angle", new Method( Method.Closest, "t" ) ) );
        error = ground_speed.getValue()-Math.sqrt(true_air_speed.getValue()*true_air_speed.getValue()+wind_speed.getValue()*wind_speed.getValue()+2*true_air_speed.getValue()*wind_speed.getValue()*Math.cos((Math.PI/180)*(wind_angle.getValue()-air_angle.getValue())));

        win.push( error );
        mode.setMode( errorAnalyzer_.analyze( win, frequency ) );

        true_air_speed_corrected.setValue( true_air_speed.getValue() );
        ground_speed_corrected.setValue( ground_speed.getValue() );
        wind_speed_corrected.setValue( wind_speed.getValue() );
        air_angle_corrected.setValue( air_angle.getValue() );
        ground_angle_corrected.setValue( ground_angle.getValue() );
        wind_angle_corrected.setValue( wind_angle.getValue() );
        switch (mode.getMode()) {
        case 1:
            true_air_speed_corrected.setValue( Math.sqrt(ground_speed.getValue()*ground_speed.getValue()+wind_speed.getValue()*wind_speed.getValue()-2*ground_speed.getValue()*wind_speed.getValue()*Math.cos((Math.PI/180)*(ground_angle.getValue()-wind_angle.getValue()))) );
            break;
        case 2:
            ground_speed_corrected.setValue( Math.sqrt(true_air_speed.getValue()*true_air_speed.getValue()+wind_speed.getValue()*wind_speed.getValue()+2*true_air_speed.getValue()*wind_speed.getValue()*Math.cos((Math.PI/180)*(wind_angle.getValue()-air_angle.getValue()))) );
            break;
        }
    }

    public void startOutput_true_air_speed_out() {
        try {
            openSocket( OutputType.Output, 0,
                        // new String( "airspeed_failed" ),
                        new String( "corrected_airspeed" ),
                        new String( "error" ) );
            openSocket( OutputType.Output, 1,
                        new String( "mode" ) );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        final int frequency = 1000;
        while (!isEndTime()) {
            Value true_air_speed = new Value();
            Value true_air_speed_corrected = new Value();
            Value ground_speed = new Value();
            Value ground_speed_corrected = new Value();
            Value wind_speed = new Value();
            Value wind_speed_corrected = new Value();
            Value air_angle = new Value();
            Value air_angle_corrected = new Value();
            Value ground_angle = new Value();
            Value ground_angle_corrected = new Value();
            Value wind_angle = new Value();
            Value wind_angle_corrected = new Value();
            Mode mode = new Mode();

            getCorrectedData( win_true_air_speed_out_, true_air_speed, true_air_speed_corrected, ground_speed, ground_speed_corrected, wind_speed, wind_speed_corrected, air_angle, air_angle_corrected, ground_angle, ground_angle_corrected, wind_angle, wind_angle_corrected, mode, frequency );
            double true_air_speed_out = true_air_speed_corrected.getValue();

            String desc = errorAnalyzer_.getDesc( mode.getMode() );
            dbgPrint( desc + ", true_air_speed_out=" + true_air_speed_out + " at " + getTime() );

            try {
                sendData( OutputType.Output, 0,
                          // true_air_speed.getValue(),
                          true_air_speed_out,
                          error );
                sendData( OutputType.Output, 1, mode.getMode() );
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }

            time_ += frequency;
            progressTime( frequency );
        }

        dbgPrint( "Finished at " + getTime() );
    }

    public static void main( String[] args ) {
        AirFranceDemo app = new AirFranceDemo( args );
        app.startServer();

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "Hit ENTER key after running input producer(s)." );
        try {
            reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.startOutput_true_air_speed_out();
    }
}
