import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;import java.util.Date;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class SumDemo extends PilotsRuntime {
private int currentMode;
private int currentModeCount;
    private int time_; // msec
    private SlidingWindow win_o_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public SumDemo( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_o_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "No error" ) );

        Vector<Constraint> constraints1 = new Vector<Constraint>();
        constraints1.add( new Constraint( Constraint.GREATER_THAN, 220.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "b failure", constraints1 ) );

        Vector<Constraint> constraints2 = new Vector<Constraint>();
        constraints2.add( new Constraint( Constraint.LESS_THAN, -420.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "c failure", constraints2 ) );

        Vector<Constraint> constraints3 = new Vector<Constraint>();
        constraints3.add( new Constraint( Constraint.GREATER_THAN, -200.0 ) );
        constraints3.add( new Constraint( Constraint.LESS_THAN, -10.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "both b and c failure", constraints3 ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value a, Value a_corrected,
                                  Value b, Value b_corrected,
                                  Value c, Value c_corrected,
                                  Value e_a, Value e_a_corrected,
                                  Mode mode, int frequency ) {
        a.setValue( getData( "a", new Method( Method.Closest, "t" ) ) );
        b.setValue( getData( "b", new Method( Method.Closest, "t" ) ) );
        c.setValue( getData( "c", new Method( Method.Closest, "t" ) ) );
        e_a.setValue( getData( "e_a", new Method( Method.Closest, "t" ) ) );
        double e = c.getValue()-b.getValue()-a.getValue();

        win.push( e );
        mode.setMode( errorAnalyzer_.analyze( win, frequency ) );

        a_corrected.setValue( a.getValue() );
        b_corrected.setValue( b.getValue() );
        c_corrected.setValue( c.getValue() );
        e_a_corrected.setValue( e_a.getValue() );
        switch (mode.getMode()) {
        case 0:
            e_a_corrected.setValue( c.getValue()-b.getValue() );
setModeCount(0);
triggerSaveState(0, 10, "e_a", e_a_corrected.getValue());
            break;
        case 1:
						System.out.println("e_a=" + Double.toString(e_a.getValue()));
            b_corrected.setValue( c.getValue()-e_a.getValue() );
setModeCount(1);
            break;
        case 2:
						System.out.println("e_a=" + Double.toString(e_a.getValue()));
            c_corrected.setValue( e_a.getValue()+b.getValue() );
setModeCount(2);
            break;
        default: setModeCount(-1);
}
    }

    public void startOutput_o() {
        try {
            openSocket( OutputType.Output, 0, new String( "o" ) );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 1000;
        while (!isEndTime()) {
            Value a = new Value();
            Value a_corrected = new Value();
            Value b = new Value();
            Value b_corrected = new Value();
            Value c = new Value();
            Value c_corrected = new Value();
            Value e_a = new Value();
            Value e_a_corrected = new Value();
            Mode mode = new Mode();

            getCorrectedData( win_o_, a, a_corrected, b, b_corrected, c, c_corrected, e_a, e_a_corrected, mode, frequency );
            double o = c_corrected.getValue()-b_corrected.getValue()-a_corrected.getValue();

            String desc = errorAnalyzer_.getDesc( mode.getMode() );
            dbgPrint( desc + ", o=" + o + " at " + getTime() );

            try {
                sendData( OutputType.Output, 0, o );
            } catch ( Exception ex ) {
 
            }

            time_ += frequency;
            progressTime( frequency );
        }

        dbgPrint( "Finished at " + getTime() );
    }

		private void setModeCount(int mode){
				if (currentMode != mode){currentMode = mode;currentModeCount = 0;}else{currentModeCount++;}}
		private void triggerSaveState(int mode, int count, String var, double value){
        if (currentMode == mode && currentModeCount > count){
						System.out.println("mode recorded!" + Double.toString(value));
						addData(var, String.format(":%s:%s", 
																			 (new SimpleDateFormat("yyyy-MM-dd HHmmssSSSZ")).format(getTime()), Double.toString(value)));}}
		public static void main( String[] args ) {
        SumDemo app = new SumDemo( args );
        app.startServer();

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "Hit ENTER key after running input producer(s)." );
        try {
            reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.startOutput_o();
    }
}
