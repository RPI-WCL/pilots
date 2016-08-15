import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class Squared extends PilotsRuntime {
    private Timer timer_;
    private SlidingWindow win_o_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public Squared( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        timer_ = new Timer();

        win_o_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void startOutput_o() {
        try {
            openSocket( OutputType.Output, 0, "o" );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 1000;
        timer_.scheduleAtFixedRate( new TimerTask() {
                public void run() {
                    Value x = new Value();

                    x.setValue( getData( "x", new Method( Method.Closest, "t" ) ) );
                    double o = x.getValue()*x.getValue();

                    dbgPrint( "o=" + o + " at " + getTime() );
                    try {
                        sendData( OutputType.Output, 0, o );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                }
        }, 0, frequency );
    }

    public static void main( String[] args ) {
        Squared app = new Squared( args );
        app.startServer();
        app.startOutput_o();
    }
}
