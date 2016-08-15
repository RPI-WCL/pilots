import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class SpeedCheck extends PilotsRuntime {
    private int time_; // msec
    private SlidingWindow win_o_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public SpeedCheck( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_o_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        Vector<Constraint> constraints1 = new Vector<Constraint>();
        constraints1.add( new Constraint( Constraint.GREATER_THAN_OR_EQUAL_TO, -16.2 ) );
        constraints1.add( new Constraint( Constraint.LESS_THAN_OR_EQUAL_TO, 16.2 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "No error", constraints1 ) );

        Vector<Constraint> constraints2 = new Vector<Constraint>();
        constraints2.add( new Constraint( Constraint.GREATER_THAN_OR_EQUAL_TO, 91.8 ) );
        constraints2.add( new Constraint( Constraint.LESS_THAN_OR_EQUAL_TO, 145.8 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Pitot tube failure", constraints2 ) );

        Vector<Constraint> constraints3 = new Vector<Constraint>();
        constraints3.add( new Constraint( Constraint.GREATER_THAN_OR_EQUAL_TO, -178.2 ) );
        constraints3.add( new Constraint( Constraint.LESS_THAN_OR_EQUAL_TO, -145.8 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "GPS failure", constraints3 ) );

        Vector<Constraint> constraints4 = new Vector<Constraint>();
        constraints4.add( new Constraint( Constraint.GREATER_THAN_OR_EQUAL_TO, -70.2 ) );
        constraints4.add( new Constraint( Constraint.LESS_THAN_OR_EQUAL_TO, -16.2 ) );
        errorSigs_.add( new ErrorSignature( ErrorSignature.CONST, 0.0, "Pitot tube + GPS failure", constraints4 ) );

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void getCorrectedData( SlidingWindow win,
                                  Value wind_speed, Value wind_speed_corrected,
                                  Value wind_angle, Value wind_angle_corrected,
                                  Value air_speed, Value air_speed_corrected,
                                  Value air_angle, Value air_angle_corrected,
                                  Value ground_speed, Value ground_speed_corrected,
                                  Value ground_angle, Value ground_angle_corrected,
                                  Mode mode, int frequency ) {
        wind_speed.setValue( getData( "wind_speed", new Method( Method.Euclidean, "x", "y" ), new Method( Method.Interpolate, "z", "2" ) ) );
        wind_angle.setValue( getData( "wind_angle", new Method( Method.Euclidean, "x", "y" ), new Method( Method.Interpolate, "z", "2" ) ) );
        air_speed.setValue( getData( "air_speed", new Method( Method.Closest, "t" ) ) );
        air_angle.setValue( getData( "air_angle", new Method( Method.Closest, "t" ) ) );
        ground_speed.setValue( getData( "ground_speed", new Method( Method.Closest, "t" ) ) );
        ground_angle.setValue( getData( "ground_angle", new Method( Method.Closest, "t" ) ) );
        double e = ground_speed.getValue()-Math.sqrt(air_speed.getValue()*air_speed.getValue()+wind_speed.getValue()*wind_speed.getValue()+2*air_speed.getValue()*wind_speed.getValue()*Math.cos((Math.PI/180)*(wind_angle.getValue()-air_angle.getValue())));

        win.push( e );
        mode.setMode( errorAnalyzer_.analyze( win, frequency ) );

        wind_speed_corrected.setValue( wind_speed.getValue() );
        wind_angle_corrected.setValue( wind_angle.getValue() );
        air_speed_corrected.setValue( air_speed.getValue() );
        air_angle_corrected.setValue( air_angle.getValue() );
        ground_speed_corrected.setValue( ground_speed.getValue() );
        ground_angle_corrected.setValue( ground_angle.getValue() );
        switch (mode.getMode()) {
        case 1:
            air_speed_corrected.setValue( Math.sqrt(ground_speed.getValue()*ground_speed.getValue()+wind_speed.getValue()*wind_speed.getValue()-2*ground_speed.getValue()*wind_speed.getValue()*Math.cos((Math.PI/180)*(ground_angle.getValue()-wind_angle.getValue()))) );
            break;
        case 2:
            ground_speed_corrected.setValue( Math.sqrt(air_speed.getValue()*air_speed.getValue()+wind_speed.getValue()*wind_speed.getValue()+2*air_speed.getValue()*wind_speed.getValue()*Math.cos((Math.PI/180)*(wind_angle.getValue()-air_angle.getValue()))) );
            break;
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
            Value wind_speed = new Value();
            Value wind_speed_corrected = new Value();
            Value wind_angle = new Value();
            Value wind_angle_corrected = new Value();
            Value air_speed = new Value();
            Value air_speed_corrected = new Value();
            Value air_angle = new Value();
            Value air_angle_corrected = new Value();
            Value ground_speed = new Value();
            Value ground_speed_corrected = new Value();
            Value ground_angle = new Value();
            Value ground_angle_corrected = new Value();
            Mode mode = new Mode();

            getCorrectedData( win_o_, wind_speed, wind_speed_corrected, wind_angle, wind_angle_corrected, air_speed, air_speed_corrected, air_angle, air_angle_corrected, ground_speed, ground_speed_corrected, ground_angle, ground_angle_corrected, mode, frequency );
            double o = ground_speed_corrected.getValue()-Math.sqrt(air_speed_corrected.getValue()*air_speed_corrected.getValue()+wind_speed_corrected.getValue()*wind_speed_corrected.getValue()+2*air_speed_corrected.getValue()*wind_speed_corrected.getValue()*Math.cos((Math.PI/180)*(wind_angle_corrected.getValue()-air_angle_corrected.getValue())));

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
        SpeedCheck app = new SpeedCheck( args );
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
