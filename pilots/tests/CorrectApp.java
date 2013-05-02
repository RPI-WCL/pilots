package pilots.tests;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;


public class CorrectApp extends PilotsRuntime {

    private Timer timer_;
    private SlidingWindow win_o1_;
    private SlidingWindow win_o2_;
    private Vector<ErrorSignature> errorSigs_e_;
    private ErrorAnalyzer errorAnalyzer_;

    public CorrectApp( String args[] ) {
        try {
            parseArgs( args );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        timer_ = new Timer();

        win_o1_ = new SlidingWindow( getOmega() );
        win_o2_ = new SlidingWindow( getOmega() );

        Vector<ErrorSignature> signatures_e_ = new Vector<ErrorSignature>();
        errorSigs_e_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Normal" ) );
        errorSigs_e_.add( new ErrorSignature( ErrorSignature.LINEAR, 2.0, "A failure" ) );
        errorSigs_e_.add( new ErrorSignature( ErrorSignature.LINEAR, -2.0, "B failure" ) );
        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_e_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win, 
                                  double a, double b, 
                                  double a_corrected, double b_corrected,
                                  int mode, String desc ) {
        double e;
        a = getData( "a", new Method( Method.Closest, "t" ) );
        b = getData( "b", new Method( Method.Closest, "t" ) );
        e = b - 2 * a;

        win.push( e );
        mode = errorAnalyzer_.analyze( win );
        switch (mode) {
        case 0: // s0
            break;
        case 1: // s1
            a_corrected = b / 2;
            break;
        case 2: // s2
            b_corrected = a * 2;
            break;
        }

        desc = errorAnalyzer_.getDesc( mode );
    }

    public void startOutput_o1() {
        try {
            openSocket( OutputType.Output, 0 /* index for sockets */, "o1" );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

        timer_.scheduleAtFixedRate( new TimerTask(){
                public void run(){
                    double a, b, a_corrected, b_corrected, o1;
                    int mode = 0;
                    String desc = null;

                    a = b = a_corrected = b_corrected = 0;
                    getCorrectedData( win_o1_, a, b, a_corrected, b_corrected, mode, desc );
                    o1 = b_corrected - 2 * a_corrected;
                    if (desc != null)
                        dbgPrint( desc );

                    try {
                        sendData( OutputType.Output, 0 /* index for sockets */, o1 );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                }
            }, 0, 1000 );
    }

    public void startOutput_o2() {
        try {
            openSocket( OutputType.Output, 1 /* index for sockets */, "o2" );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

        timer_.scheduleAtFixedRate( new TimerTask(){
                public void run(){
                    double a, b, a_corrected, b_corrected, o2;
                    int mode = 0;
                    String desc = null;

                    a = b = a_corrected = b_corrected = 0;
                    getCorrectedData( win_o2_, a, b, a_corrected, b_corrected, mode, desc );
                    o2 = b_corrected - 2 * a_corrected;
                    if (desc != null)
                        dbgPrint( desc );

                    try {
                        sendData( OutputType.Output, 0 /* index for sockets */, o2 );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                }
            }, 0, 1500 );
    }
    
    public static void main( String[] args ) {
        CorrectApp correct = new CorrectApp( args );
        correct.startServer();
        correct.startOutput_o1();
        correct.startOutput_o2();
    }
}
