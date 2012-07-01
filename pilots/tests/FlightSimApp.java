package pilots.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import pilots.runtime.*;


public class FlightSimApp extends PilotsRuntime {
    CurrentLocationTimeService currLocTime_;
    int timeElapsed_; // msec

    public FlightSimApp() {
        currLocTime_ = ServiceFactory.getCurrentLocationTime();
        timeElapsed_ = 0;
    }
    

    public void startOutput() {
        // try {
        //     openSocket( OutputType.Error, 0 /* index for sockets */, "e" );
        // } catch ( Exception ex ) {
        //     System.err.println( ex );
        // }

        dbgPrint( "time, wind_speed, wind_angle, air_speed, air_angle, ground_speed, ground_angle, calc_ground_speed, e, crab_angle" );

        Double wind_speed = 0.0, wind_angle = 0.0;
        Double air_speed = 0.0, air_angle = 0.0;
        Double ground_speed = 0.0, ground_angle = 0.0, calc_ground_speed = 0.0;
        Double e = 0.0, crab_angle = 0.0;

        while ( !currLocTime_.isTimeEnd() ) {

            wind_speed = getData( "wind_speed", 
                                  new Method( Method.Euclidean, "x", "y"), 
                                  new Method( Method.Closest, "t"), 
                                  new Method( Method.Interpolate, "z", "2" ) );
            wind_angle = getData( "wind_angle", 
                                  new Method( Method.Euclidean, "x", "y"), 
                                  new Method( Method.Closest, "t"), 
                                  new Method( Method.Interpolate, "z", "2" ) );

            boolean isAirspeedSet;
            isAirspeedSet = false;
            if (System.getProperty( "error" ).equals( "airspeed" ) || 
                System.getProperty( "error" ).equals( "both" )) {
                if (41 * 60 * 1000 <= timeElapsed_) { // 41 min
                    air_speed = new Double( 50.0 );
                    isAirspeedSet = true;
                }
                else if (40 * 60 * 1000 <= timeElapsed_) { // 40 min
                    air_speed = new Double( 100.0 );
                    isAirspeedSet = true;
                }
            }
            if (!isAirspeedSet) {
                air_speed = getData( "air_speed", new Method( Method.Euclidean, "x", "y") );
            }
            air_angle = getData( "air_angle", new Method( Method.Euclidean, "x", "y") );

            boolean isGroundValSet;
            isGroundValSet = false;
            if (System.getProperty( "error" ).equals( "GPS" ) ||
                System.getProperty( "error" ).equals( "both" )) {
                if (40 * 60 * 1000 <= timeElapsed_) { // 40 min
                    // pretend that the values are already set
                    ground_speed = new Double( 0.0 );
                    ground_angle = new Double( 0.0 );
                    isGroundValSet = true;
                }
            }
            if (!isGroundValSet) {
                ground_speed = getData( "ground_speed", 
                                        new Method( Method.Euclidean, "x", "y"), 
                                        new Method( Method.Closest, "t") );
                ground_angle = getData( "ground_angle", 
                                        new Method( Method.Euclidean, "x", "y"), 
                                        new Method( Method.Closest, "t") );
            }

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

                crab_angle = 180 * Math.asin( wind_speed * Math.sin( 2 * Math.PI * (air_angle - wind_angle) / 360) /
                                              calc_ground_speed ) / Math.PI;

                // try {
                dbgPrint( currLocTime_.getTime() + ", " + wind_speed + ", " + wind_angle + 
                          ", " + air_speed + ", " + air_angle + 
                          ", " + ground_speed + ", " + ground_angle + 
                          ", " + calc_ground_speed + 
                          ", " + e + ", " + crab_angle );
                //sendData( OutputType.Error, 0 /* index for sockets */, e );
                // } catch ( Exception ex ) {
                //     ex.printStackTrace();
                // }
            }

            int timeOffset = 60 * 1000;
            timeElapsed_ += timeOffset;
            currLocTime_.progressTime( timeOffset ); // 1 min
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
