import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class Twice extends PilotsRuntime {
    private int time_; // msec
    private SlidingWindow win_o_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public Twice( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_o_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Normal mode" ) );

        errorSigs_.add( new ErrorSignature( ErrorSignature.LINEAR, 2.0, "A failure" ) );

        errorSigs_.add( new ErrorSignature( ErrorSignature.LINEAR, -2.0, "B failure" ) );

        Vector<Constraint> constraints1 = new Vector<Constraint>();
        constraints1.add( new Constraint( Constraint.GREATER_THAN, 20.0 ) );
        constraints1.add( new Constraint( Constraint.LESS_THAN, -20.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Out-of-sync", constraints1 ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value a, Value a_corrected,
                                  Value b, Value b_corrected,
                                  Mode mode, int frequency ) {
        a.setValue( getData( "a", new Method( Method.Closest, "t" ) ) );
        b.setValue( getData( "b", new Method( Method.Closest, "t" ) ) );
        double e = b.getValue()-2*a.getValue();

        win.push( e );
        mode.setMode( errorAnalyzer_.analyze( win, frequency ) );

        a_corrected.setValue( a.getValue() );
        b_corrected.setValue( b.getValue() );
        switch (mode.getMode()) {
        case 1:
            a_corrected.setValue( b.getValue()/2 );
            break;
        case 2:
            b_corrected.setValue( a.getValue()*2 );
            break;
        }
    }

    public void startOutput_o() {
        try {
            openSocket( OutputType.Output, 0, "o" );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 1000;
        while (!isEndTime()) {
            Value a = new Value();
            Value a_corrected = new Value();
            Value b = new Value();
            Value b_corrected = new Value();
            Mode mode = new Mode();

            getCorrectedData( win_o_, a, a_corrected, b, b_corrected, mode, frequency );
            double o = b_corrected.getValue()-2*a_corrected.getValue();

            String desc = errorAnalyzer_.getDesc( mode.getMode() );
            dbgPrint( desc + ", o=" + o + " at " + getTime() );

            try {
                sendData( OutputType.Output, 0, o );
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }

            time_ += frequency;
            progressTime( frequency );
        }

        dbgPrint( "Finished at " + getTime() );
    }

    public static void main( String[] args ) {
        Twice app = new Twice( args );
        app.startServer();

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "Hit ENTER key after running input producer(s)" );
        try {
            reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.startOutput_o();
    }
}
