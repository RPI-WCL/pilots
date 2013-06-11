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

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void startOutput_o() {
        try {
            openSocket( OutputType.Output, 0, "o" );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 60000;
        while (!isEndTime()) {
            Value wind_speed = new Value();
            Value wind_angle = new Value();
            Value air_speed = new Value();
            Value air_angle = new Value();
            Value ground_speed = new Value();
            Value ground_angle = new Value();

            wind_speed.setValue( getData( "wind_speed", new Method( Method.Euclidean, "x", "y" ), new Method( Method.Interpolate, "z", "2" ) ) );
            wind_angle.setValue( getData( "wind_angle", new Method( Method.Euclidean, "x", "y" ), new Method( Method.Interpolate, "z", "2" ) ) );
            air_speed.setValue( getData( "air_speed", new Method( Method.Euclidean, "x", "y" ), new Method( Method.Closest, "t" ) ) );
            air_angle.setValue( getData( "air_angle", new Method( Method.Euclidean, "x", "y" ), new Method( Method.Closest, "t" ) ) );
            ground_speed.setValue( getData( "ground_speed", new Method( Method.Euclidean, "x", "y" ), new Method( Method.Closest, "t" ) ) );
            ground_angle.setValue( getData( "ground_angle", new Method( Method.Euclidean, "x", "y" ), new Method( Method.Closest, "t" ) ) );
            double o = ground_speed.getValue()-Math.sqrt(air_speed.getValue()*air_speed.getValue()+wind_speed.getValue()*wind_speed.getValue()+2*air_speed.getValue()*wind_speed.getValue()*Math.cos((Math.PI/180)*(wind_angle.getValue()-air_angle.getValue())));

            dbgPrint( "o=" + o + " at " + getTime() );
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
