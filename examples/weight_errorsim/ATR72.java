import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class ATR72 extends PilotsRuntime {
    private int time_; // msec
    private SlidingWindow win_corrected_weight_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public ATR72( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_corrected_weight_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Normal" ) );

        Vector<Constraint> constraints1 = new Vector<Constraint>();
        constraints1.add( new Constraint( Constraint.GREATER_THAN, 0.035 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "over weight", constraints1 ) );

        Vector<Constraint> constraints2 = new Vector<Constraint>();
        constraints2.add( new Constraint( Constraint.LESS_THAN, -0.035 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "under weight", constraints2 ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value w, Value w_corrected,
                                  Value w_real, Value w_real_corrected,
                                  Mode mode, int frequency ) {
        w.setValue( getData( "w", new Method( Method.Closest, "t" ) ) );
        w_real.setValue( getData( "w_real", new Method( Method.Closest, "t" ) ) );
        double e = (w.getValue()-w_real.getValue())/w.getValue();

        win.push( e );
        mode.setMode( errorAnalyzer_.analyze( win, frequency ) );

        w_corrected.setValue( w.getValue() );
        w_real_corrected.setValue( w_real.getValue() );
        switch (mode.getMode()) {
        case 1:
            w_corrected.setValue( w_real.getValue() );
            break;
        case 2:
            w_corrected.setValue( w_real.getValue() );
            break;
        }
    }

    public void startOutput_corrected_weight() {
        try {
            openSocket( OutputType.Output, 0, new String( "corrected_weight" ) );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 1000;
        while (!isEndTime()) {
            Value w = new Value();
            Value w_corrected = new Value();
            Value w_real = new Value();
            Value w_real_corrected = new Value();
            Mode mode = new Mode();

            getCorrectedData( win_corrected_weight_, w, w_corrected, w_real, w_real_corrected, mode, frequency );
            double corrected_weight = w_corrected.getValue();

            String desc = errorAnalyzer_.getDesc( mode.getMode() );
            dbgPrint( desc + ", corrected_weight=" + corrected_weight + " at " + getTime() );

            try {
                sendData( OutputType.Output, 0, corrected_weight );
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }

            time_ += frequency;
            progressTime( frequency );
        }

        dbgPrint( "Finished at " + getTime() );
    }

    public static void main( String[] args ) {
        ATR72 app = new ATR72( args );
        app.startServer();

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "Hit ENTER key after running input producer(s)." );
        try {
            reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.startOutput_corrected_weight();
    }
}
