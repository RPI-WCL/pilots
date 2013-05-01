package pilots.tests;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;


public class CorrectApp extends PilotsRuntime {

    private Timer timer_;
    private SlidingWindow win_;
    private Vector<ErrorSignature> errorSigs_e_;
    private ErrorAnalyzer errorAnalyzer_;

    public CorrectApp( int omega, double tau ) {
        timer_ = new Timer();
        Vector<ErrorSignature> signatures_e_ = new Vector<ErrorSignature>();
        errorSigs_e_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Normal" ) );
        errorSigs_e_.add( new ErrorSignature( ErrorSignature.LINEAR, 2.0, "A failure" ) );
        errorSigs_e_.add( new ErrorSignature( ErrorSignature.LINEAR, -2.0, "B failure" ) );
        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_e_, omega, tau );
    }

    public void startOutput_o() {
        try {
            openSocket( OutputType.Output, 0 /* index for sockets */, "o" );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

        timer_.scheduleAtFixedRate( new TimerTask(){
                public void run(){
                    double a, b, o, e;
                    a = getData( "a", new Method( Method.Closest, "t" ) );
                    b = getData( "b", new Method( Method.Closest, "t" ) );
                    e = b - 2 * a;

                    errorAnalyzer_.push( e );
                    int mode = errorAnalyzer_.analyze();
                    switch (mode) {
                    case 0: // s0
                        break;
                    case 1: // s1
                        a = b / 2;
                        break;
                    case 2: // s2
                        b = a * 2;
                        break;
                    }
                    o = b - 2 * a;

                    String desc = errorAnalyzer_.getDesc( mode );
                    if (desc != null)
                        dbgPrint( desc );

                    try {
                        sendData( OutputType.Output, 0 /* index for sockets */, o );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                }
            }, 0, 1000 /* every 1 sec */ );
    }
    
    public static void main( String[] args ) {
        if (args.length < 1){
            System.err.println( "Usage: java pilots.tests.CorrectApp <omega> <tau>" );
            return;
        }

        CorrectApp correct = new CorrectApp( 5, 0.8 );

        try {
            correct.parseArgs( args );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        correct.startServer();
        correct.startOutput_o();
    }
}
