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

        Vector<Constraint> constraints1 = new Vector<Constraint>();
        constraints1.add( new Constraint( Constraint.GREATER_THAN, -0.035 ) );
        constraints1.add( new Constraint( Constraint.LESS_THAN, 0.035 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Normal", constraints1 ) );

        Vector<Constraint> constraints2 = new Vector<Constraint>();
        constraints2.add( new Constraint( Constraint.GREATER_THAN, 0.035 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "over weight", constraints2 ) );

        Vector<Constraint> constraints3 = new Vector<Constraint>();
        constraints3.add( new Constraint( Constraint.LESS_THAN, -0.035 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "under weight", constraints3 ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value v_a, Value v_a_corrected,
                                  Value a, Value a_corrected,
                                  Value p, Value p_corrected,
                                  Value t, Value t_corrected,
                                  Value w, Value w_corrected,
                                  Value cl, Value cl_corrected,
                                  Mode mode, int frequency ) {
        v_a.setValue( getData( "v_a", new Method( Method.Closest, "t" ) ) );
        a.setValue( getData( "a", new Method( Method.Closest, "t" ) ) );
        p.setValue( getData( "p", new Method( Method.Closest, "t" ) ) );
        t.setValue( getData( "t", new Method( Method.Closest, "t" ) ) );
        w.setValue( getData( "w", new Method( Method.Closest, "t" ) ) );
        cl.setValue( getData( "cl", new Method( Method.Predict, "linear_regression", "a" ) ) );
        double e = (w.getValue()-p.getValue()*(v_a.getValue()*v_a.getValue())*61*cl.getValue()/(2*286.9*t.getValue()))/w.getValue();

        win.push( e );
        mode.setMode( errorAnalyzer_.analyze( win, frequency ) );

        v_a_corrected.setValue( v_a.getValue() );
        a_corrected.setValue( a.getValue() );
        p_corrected.setValue( p.getValue() );
        t_corrected.setValue( t.getValue() );
        w_corrected.setValue( w.getValue() );
        cl_corrected.setValue( cl.getValue() );
        switch (mode.getMode()) {
        case 1:
            w_corrected.setValue( p.getValue()*(v_a.getValue()*v_a.getValue())*61*cl.getValue()/(2*286.9*t.getValue()) );
            break;
        case 2:
            w_corrected.setValue( p.getValue()*(v_a.getValue()*v_a.getValue())*61*cl.getValue()/(2*286.9*t.getValue()) );
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
            Value v_a = new Value();
            Value v_a_corrected = new Value();
            Value a = new Value();
            Value a_corrected = new Value();
            Value p = new Value();
            Value p_corrected = new Value();
            Value t = new Value();
            Value t_corrected = new Value();
            Value w = new Value();
            Value w_corrected = new Value();
            Value cl = new Value();
            Value cl_corrected = new Value();
            Mode mode = new Mode();

            getCorrectedData( win_corrected_weight_, v_a, v_a_corrected, a, a_corrected, p, p_corrected, t, t_corrected, w, w_corrected, cl, cl_corrected, mode, frequency );
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
