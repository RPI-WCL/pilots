import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class AoaCheckDemo extends PilotsRuntime {
    private int currentMode;
    private int currentModeCount;
    private Timer timer_;
    private SlidingWindow win_aoa_out_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;
    private double error;
    private boolean debug = false, logging = true;
    private FileWriter fw;

    // Cessna 172 parameters
    private static final double K = 1.94384;    // m to knot
    private static final double A =	0.0881;     // coefficient for cl
    private static final double B = 0.3143;     // coefficient for cl
    private static final double L = 1156.6;     // weight
    private static final double S = 16.2;       // wing surface
    private static final double rho = 1.225;    // air density
    private static final double G = 9.80665;    // gravitational acceleration
    

    public AoaCheckDemo( String args[] ) {
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
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "No error", constraints1 ) );        

        Vector<Constraint> constraints2 = new Vector<Constraint>();
        constraints2.add( new Constraint( Constraint.GREATER_THAN, 20.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "AoA higher-than-actual", constraints2 ) );

        Vector<Constraint> constraints3 = new Vector<Constraint>();
        constraints3.add( new Constraint( Constraint.LESS_THAN, -20.0 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "AoA lower-than-actual", constraints3 ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );

        if (logging) {
            try {
                fw = new FileWriter(new File("./log.txt"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value aoa, Value aoa_corrected,
                                  Value v, Value v_corrected,
                                  Mode mode, int frequency ) {
        aoa.setValue( getData( "aoa", new Method( Method.Closest, "t" ) ) );
        v.setValue( getData( "v", new Method( Method.Closest, "t" ) ) );
        error = v.getValue() - K * Math.sqrt((2*L*G) / ((A*aoa.getValue()+B)*S*rho));

        // System.out.println("v=" + v.getValue() + ", aoa=" + aoa.getValue() + ", est_v=" + (1.94384*Math.sqrt(11.34796/(0.00076*aoa.getValue()+0.00367))) + ", error=" + error);

        win.push( error );
        mode.setMode( errorAnalyzer_.analyze( win, frequency ) );

        aoa_corrected.setValue( aoa.getValue() );
        v_corrected.setValue( v.getValue() );
        switch (mode.getMode()) {
        case 1:
            aoa_corrected.setValue(((2*L*K*K*G)/(A*S*rho*v.getValue()*v.getValue())) - B/A);
            break;
        case 2:
            aoa_corrected.setValue(((2*L*K*K*G)/(A*S*rho*v.getValue()*v.getValue())) - B/A);            
            break;
        default: setModeCount(-1);
        }
    }

    public void startOutput_aoa_out() {
        if (!debug) {
            try {
                openSocket(OutputType.Output, 0,
                           new String( "aoa(monitored)" ),
                           new String( "aoa(estimated)" ) );
                openSocket(OutputType.Output, 1, new String( "error"));
                openSocket(OutputType.Output, 2, new String( "mode" ) );
                
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
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
                    // dbgPrint( desc + ", aoa_out=" + aoa_out + " at " + getTime() );
                    double estimated_v = K * Math.sqrt((2*L*G) / ((A*aoa.getValue()+B)*S*rho));
                    if (logging) {
                        try {
                            fw.write(aoa.getValue() + "," + aoa_corrected.getValue() + "," + v.getValue() + "," + estimated_v + "," + error + "," + mode.getMode() + "\n");
                            fw.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                    if (!debug && (aoa.getValue() != 0.0 || v.getValue() != 0.0)) {
                        try {
                            sendData(OutputType.Output, 0, aoa.getValue(), aoa_out);
                            sendData(OutputType.Output, 1, error);
                            sendData(OutputType.Output, 2, mode.getMode() );                        
                        } catch ( Exception ex ) {
                            ex.printStackTrace();
                        }
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
        AoaCheckDemo app = new AoaCheckDemo( args );
        app.startServer();
        app.startOutput_aoa_out();
    }
}
