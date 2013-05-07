package pilots.tests;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class CorrectApp extends PilotsRuntime {
    private Timer timer_;
    private SlidingWindow win_o_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public CorrectApp( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        timer_ = new Timer();

        win_o_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Normal mode") );
        errorSigs_.add( new ErrorSignature( ErrorSignature.LINEAR, 2.0, "A failure") );
        errorSigs_.add( new ErrorSignature( ErrorSignature.LINEAR, -2.0, "B failure") );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value a, Value b, 
                                  Value a_corrected, Value b_corrected, 
                                  Mode mode ) {
        a.setValue( getData( "a", new Method( Method.Closest, "t" ) ) );
        b.setValue( getData( "b", new Method( Method.Closest, "t" ) ) );
        double e = b.getValue()-2*a.getValue();

        win.push( e );
        mode.setMode( errorAnalyzer_.analyze( win ) );

        switch (mode.getMode()) {
        case 1:
            a_corrected.setValue( b.getValue()/2 );
            break;
        case 2:
            b_corrected.setValue( a.getValue()*2 );
            break;
        default:
            a_corrected.setValue( a.getValue() );
            b_corrected.setValue( b.getValue() );
            break;
        }
    }

    public void startOutput_o() {
        try {
            openSocket( OutputType.Output, 0, "o" );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        timer_.scheduleAtFixedRate( new TimerTask() {
                public void run() {
                    Value a = new Value();
                    Value a_corrected = new Value();
                    Value b = new Value();
                    Value b_corrected = new Value();
                    Mode mode = new Mode();

                    getCorrectedData( win_o_, a, a_corrected, b, b_corrected, mode );
                    double o = b_corrected.getValue()-2*a_corrected.getValue();

                    String desc = errorAnalyzer_.getDesc( mode.getMode() );
                    dbgPrint( desc );

                    try {
                        sendData( OutputType.Output, 0, o );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                }
        }, 0, 1000);
    }

    public static void main( String[] args ) {
        CorrectApp app = new CorrectApp( args );
        app.startServer();
        app.startOutput_o();
    }
}
