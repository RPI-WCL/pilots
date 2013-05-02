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
    private Vector<ErrorSignature> errorSigs_;
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

        errorSigs_ = = new Vector<ErrorSignature>();
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Normal" ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.LINEAR, 2.0, "A failure" ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.LINEAR, -2.0, "B failure" ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win, 
                                  Value a, Value b, 
                                  Value a_corrected, Value b_corrected,
                                  Mode mode ) {
        a.setValue( getData( "a", new Method( Method.Closest, "t" ) ) );
        b.setValue( getData( "b", new Method( Method.Closest, "t" ) ) );
        double e = b.getValue() - 2 * a.getValue();

        win.push( e );
        mode.setMode( errorAnalyzer_.analyze( win ) );

        switch (mode.getMode()) {
        case 0: // s0
            a_corrected.setValue( a.getValue() );
            b_corrected.setValue( b.getValue() );
            break;
        case 1: // s1
            a_corrected.setValue( b.getValue() / 2 );
            break;
        case 2: // s2
            b_corrected.setValue( a.getValue() * 2 );
            break;
        }
    }

    public void startOutput_o1() {
        try {
            openSocket( OutputType.Output, 0 /* index for sockets */, "o1" );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

        timer_.scheduleAtFixedRate( new TimerTask(){
                public void run(){
                    Value a = new Value();
                    Value b = new Value();
                    Value a_corrected = new Value();
                    Value b_corrected = new Value();
                    Mode mode = new Mode();

                    getCorrectedData( win_o1_, a, b, a_corrected, b_corrected, mode );
                    double o1 = b_corrected.getValue() - 2 * a_corrected.getValue();

                    String desc = errorAnalyzer_.getDesc( mode.getMode() );
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
                    Value a = new Value();
                    Value b = new Value();
                    Value a_corrected = new Value();
                    Value b_corrected = new Value();
                    Mode mode = new Mode();

                    getCorrectedData( win_o1_, a, b, a_corrected, b_corrected, mode );
                    double o2 = b_corrected.getValue() - 2 * a_corrected.getValue();

                    String desc = errorAnalyzer_.getDesc( mode.getMode() );
                    if (desc != null)
                        dbgPrint( desc );

                    try {
                        sendData( OutputType.Output, 1 /* index for sockets */, o2 );
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
