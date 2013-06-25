import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class AirFrance extends PilotsRuntime {
    private int time_; // msec
    private SlidingWindow win_air_angle_o_;
    private Vector<ErrorSignature> errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;

    public AirFrance( String args[] ) {
        try {
            parseArgs( args );
        } catch (Exception ex) {
            ex.printStackTrace();
        };

        time_ = 0;

        win_air_angle_o_ = new SlidingWindow( getOmega() );

        errorSigs_ = new Vector<ErrorSignature>();

        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );
    }

    public void startOutput_air_angle_o() {
        try {
            openSocket( OutputType.Output, 0, "air_angle_o" );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        final int frequency = 5000;
        while (!isEndTime()) {
            Value air_speed = new Value();
            Value ground_speed = new Value();
            Value wind_speed = new Value();
            Value air_angle = new Value();
            Value ground_angle = new Value();
            Value wind_angle = new Value();

            air_speed.setValue( getData( "air_speed", new Method( Method.Closest, "t" ) ) );
            ground_speed.setValue( getData( "ground_speed", new Method( Method.Closest, "t" ) ) );
            wind_speed.setValue( getData( "wind_speed", new Method( Method.Closest, "t" ) ) );
            air_angle.setValue( getData( "air_angle", new Method( Method.Closest, "t" ) ) );
            ground_angle.setValue( getData( "ground_angle", new Method( Method.Closest, "t" ) ) );
            wind_angle.setValue( getData( "wind_angle", new Method( Method.Closest, "t" ) ) );
            double air_angle_o = air_angle.getValue();

            dbgPrint( "air_angle_o=" + air_angle_o + " at " + getTime() );
            try {
                sendData( OutputType.Output, 0, air_angle_o );
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }

            time_ += frequency;
            progressTime( frequency );
        }

        dbgPrint( "Finished at " + getTime() );
    }

    public static void main( String[] args ) {
        AirFrance app = new AirFrance( args );
        app.startServer();

        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "Hit ENTER key after running input producer(s)." );
        try {
            reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.startOutput_air_angle_o();
    }
}
