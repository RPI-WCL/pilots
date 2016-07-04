import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class ATR72_origin extends PilotsRuntime {
    private int time_; // msec
    private SlidingWindow win_measured_weight_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public ATR72_origin( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_measured_weight_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void startOutput_measured_weight() {
        try {
            openSocket( OutputType.Output, 0, "measured_weight" );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 1000;
        while (!isEndTime()) {
            Value speed = new Value();
            Value angle = new Value();
            Value pressure = new Value();
            Value temperature = new Value();
            Value weight = new Value();

            speed.setValue( getData( "speed", new Method( Method.Closest, "t" ) ) );
            angle.setValue( getData( "angle", new Method( Method.Closest, "t" ) ) );
            pressure.setValue( getData( "pressure", new Method( Method.Closest, "t" ) ) );
            temperature.setValue( getData( "temperature", new Method( Method.Closest, "t" ) ) );
            weight.setValue( getData( "weight", new Method( Method.Closest, "t" ) ) );
            double measured_weight = weight.getValue();

            dbgPrint( "measured_weight=" + measured_weight + " at " + getTime() );
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
        ATR72_origin app = new ATR72_origin( args );
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
