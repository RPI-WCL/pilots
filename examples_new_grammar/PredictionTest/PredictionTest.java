import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class PredictionTest extends PilotsRuntime {
    private int time_; // msec
    private SlidingWindow win_difference_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public PredictionTest( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_difference_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void startOutput_difference() {
        try {
            openSocket( OutputType.Output, 0, "difference" );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 1000;
        while (!isEndTime()) {
            Value a = new Value();
            Value b = new Value();
            Value c = new Value();

            a.setValue( getData( "a", new Method( Method.Closest, "t" ) ) );
            b.setValue( getData( "b", new Method( Method.Closest, "t" ) ) );
            c.setValue( getData( "c", new Method( Method.Predict, "linear", "a" ) ) );
            double difference = b.getValue()-c.getValue();

            dbgPrint( "difference=" + difference + " at " + getTime() );
            try {
                sendData( OutputType.Output, 0, difference );
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }

            time_ += frequency;
            progressTime( frequency );
        }

        dbgPrint( "Finished at " + getTime() );
    }

    public static void main( String[] args ) {
        PredictionTest app = new PredictionTest( args );
        app.startServer();

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "Hit ENTER key after running input producer(s)." );
        try {
            reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.startOutput_difference();
    }
}
