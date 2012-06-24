package pilots.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import pilots.runtime.*;


public class FlightSimApp extends PilotsRuntime {
    CurrentLocationTimeService currLocTime_;

    public FlightSimApp() {
        currLocTime_ = ServiceFactory.getCurrentLocationTime();
    }
    

    public void startOutput() {
        // try {
        //     openSocket( OutputType.Error, 0 /* index for sockets */, "e" );
        // } catch ( Exception ex ) {
        //     System.err.println( ex );
        // }

        dbgPrint( "time, wind_speed, wind_angle, air_speed, air_angle, ground_speed, ground_angle, calc_ground_speed, e" );

        while ( !currLocTime_.isTimeEnd() ) {
            Double wind_speed, wind_angle, air_speed, air_angle, ground_speed, ground_angle, calc_ground_speed, e;
            wind_speed = getData( "wind_speed", 
                                  new Method( Method.Euclidean, "x", "y"), 
                                  new Method( Method.Closest, "t"), 
                                  new Method( Method.Interpolate, "z", "2" ) );
            wind_angle = getData( "wind_angle", 
                                  new Method( Method.Euclidean, "x", "y"), 
                                  new Method( Method.Closest, "t"), 
                                  new Method( Method.Interpolate, "z", "2" ) );

            air_speed = getData( "air_speed", new Method( Method.Euclidean, "x", "y") );
            air_angle = getData( "air_angle", new Method( Method.Euclidean, "x", "y") );

            ground_speed = getData( "ground_speed", 
                                    new Method( Method.Euclidean, "x", "y"), 
                                    new Method( Method.Closest, "t") );
            ground_angle = getData( "ground_angle", 
                                    new Method( Method.Euclidean, "x", "y"), 
                                    new Method( Method.Closest, "t") );

            if ((wind_speed == null) || (wind_angle == null) ||
                (air_speed == null) || (air_angle == null) ||
                (ground_speed == null) || (ground_angle == null)) {
                dbgPrint( "getData returned null value" );
            }
            else {
                calc_ground_speed = Math.sqrt( air_speed * air_speed + 
                                               2 * air_speed * wind_speed * Math.cos( 2 * Math.PI * (air_angle - wind_angle) / 360 ) +
                                               wind_speed * wind_speed );
                e = ground_speed - calc_ground_speed;
                // try {
                dbgPrint( currLocTime_.getTime() + ", " + wind_speed + ", " + wind_angle + 
                          ", " + air_speed + ", " + air_angle + 
                          ", " + ground_speed + ", " + ground_angle + 
                          ", " + calc_ground_speed + 
                          ", " + e );
                //sendData( OutputType.Error, 0 /* index for sockets */, e );
                // } catch ( Exception ex ) {
                //     ex.printStackTrace();
                // }
            }

            currLocTime_.progressTime( 60 * 1000 ); // 1 min
        }

        dbgPrint( "Finished at " + currLocTime_.getTime() );
    }


    public static void main( String[] args ) {
        FlightSimApp app = new FlightSimApp();

        try {
            app.parseArgs( args );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        app.startServer();
        
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "Hit any key after running the clients" );

        try {
            reader.readLine();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        app.startOutput();
    }
}
