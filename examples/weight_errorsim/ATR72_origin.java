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
            Value v_a = new Value();
            Value a = new Value();
            Value p = new Value();
            Value t = new Value();
            Value w = new Value();

            v_a.setValue( getData( "v_a", new Method( Method.Closest, "t" ) ) );
            a.setValue( getData( "a", new Method( Method.Closest, "t" ) ) );
            p.setValue( getData( "p", new Method( Method.Closest, "t" ) ) );
            t.setValue( getData( "t", new Method( Method.Closest, "t" ) ) );
            w.setValue( getData( "w", new Method( Method.Closest, "t" ) ) );
            double measured_weight = w.getValue();

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
