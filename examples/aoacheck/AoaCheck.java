import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;import java.util.Date;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class AoaCheck extends PilotsRuntime {
private int currentMode;
private int currentModeCount;
    private Timer timer_;
    private SlidingWindow win_aoa_out_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public AoaCheck( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        timer_ = new Timer();

        win_aoa_out_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        Vector<Constraint> constraints1 = new Vector<Constraint>();
        constraints1.add( new Constraint( Constraint.GREATER_THAN, -10.0 ) );
        constraints1.add( new Constraint( Constraint.LESS_THAN, 10.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Normal", constraints1 ) );

        Vector<Constraint> constraints2 = new Vector<Constraint>();
        constraints2.add( new Constraint( Constraint.GREATER_THAN, 20.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "AoA higher-than-actual", constraints2 ) );

        Vector<Constraint> constraints3 = new Vector<Constraint>();
        constraints3.add( new Constraint( Constraint.LESS_THAN, -13.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "AoA lower-than-actual", constraints3 ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value aoa, Value aoa_corrected,
                                  Value v, Value v_corrected,
                                  Mode mode, int frequency ) {
        aoa.setValue( getData( "aoa", new Method( Method.Closest, "t" ) ) );
        v.setValue( getData( "v", new Method( Method.Closest, "t" ) ) );
        double e = v.getValue()-1.94384*Math.sqrt((2*1156.6*9.80665)/((0.0881*aoa.getValue()+0.3143)*16.2*1.225));

        win.push( e );
        mode.setMode( errorAnalyzer_.analyze( win, frequency ) );

        aoa_corrected.setValue( aoa.getValue() );
        v_corrected.setValue( v.getValue() );
        switch (mode.getMode()) {
        case 1:
            aoa_corrected.setValue( ((2*1156.6*1.94384*1.94384*9.80665)/(0.0881*16.2*1.225*v.getValue()*v.getValue()))-0.3143/0.0881 );
setModeCount(1);
            break;
        case 2:
            aoa_corrected.setValue( ((2*1156.6*1.94384*1.94384*9.80665)/(0.0881*16.2*1.225*v.getValue()*v.getValue()))-0.3143/0.0881 );
setModeCount(2);
            break;
        default: setModeCount(-1);
}
    }

    public void startOutput_aoa_out() {
        try {
            openSocket( OutputType.Output, 0, new String( "aoa_out" ) );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 1000;
        timer_.scheduleAtFixedRate( new TimerTask() {
                public void run() {
                    Value aoa = new Value();
                    Value aoa_corrected = new Value();
                    Value v = new Value();
                    Value v_corrected = new Value();
                    Mode mode = new Mode();

                    getCorrectedData( win_aoa_out_, aoa, aoa_corrected, v, v_corrected, mode, frequency );
                    double aoa_out = aoa_corrected.getValue();

                    String desc = errorAnalyzer_.getDesc( mode.getMode() );
                    dbgPrint( desc + ", aoa_out=" + aoa_out + " at " + getTime() );

                    try {
                        sendData( OutputType.Output, 0, aoa_out );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                }
        }, 0, frequency );
    }

    private void setModeCount(int mode){
        if (currentMode != mode){
            currentMode = mode; currentModeCount = 0;
        }else{
            currentModeCount++;
        }
    }
    private void triggerSaveState(int mode, int count, String var, double value){
        if (currentMode == mode && currentModeCount > count){
            addData(var, String.format(":%s:%s", (new SimpleDateFormat("yyyy-MM-dd HHmmssSSSZ")).format(getTime()), Double.toString(value)));
        }
    }
    public static void main( String[] args ) {
        AoaCheck app = new AoaCheck( args );
        app.startServer();
        app.startOutput_aoa_out();
    }
}
