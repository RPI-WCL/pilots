import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class ATR72_bayes extends PilotsRuntime {
    private int time_; // msec
    private SlidingWindow win_signature_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public ATR72_bayes( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_signature_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void startOutput_signature() {
        try {
            openSocket( OutputType.Output, 0, "signature" );
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
            Value cl = new Value();

            v_a.setValue( getData( "v_a", new Method( Method.Closest, "t" ) ) );
            a.setValue( getData( "a", new Method( Method.Closest, "t" ) ) );
            p.setValue( getData( "p", new Method( Method.Closest, "t" ) ) );
            t.setValue( getData( "t", new Method( Method.Closest, "t" ) ) );
            w.setValue( getData( "w", new Method( Method.Closest, "t" ) ) );
            cl.setValue( getData( "cl", new Method( Method.Predict, "bayes", "v_a", "a", "p", "t", "w" ) ) );
            double signature = cl.getValue();

            dbgPrint( "signature=" + signature + " at " + getTime() );
            try {
                sendData( OutputType.Output, 0, signature );
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }

            time_ += frequency;
            progressTime( frequency );
        }

        dbgPrint( "Finished at " + getTime() );
    }

    public static void main( String[] args ) {
        ATR72_bayes app = new ATR72_bayes( args );
        app.startServer();

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "Hit ENTER key after running input producer(s)." );
        try {
            reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.startOutput_signature();
    }
}
