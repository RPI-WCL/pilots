package pilots.tests;

import java.util.Timer;
import java.util.TimerTask;
import pilots.runtime.*;


public class Twice {
    private PilotsRuntime pilots;
    private Timer timer;

    public Twice() {
        pilots = new PilotsRuntime();
        timer = new Timer();
    }

    public void startOutput_e() {
        try {
            Socket sock = pilots.openSocket( OutputType.Error. 0 /* index for sockets */, "e" );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

        timer.scheduleAtFixedRate( new TimerTask() {
                public void run() {
                    double a, b, e;
                    a = pilots.getData( "a", Methods.ClosestTime );
                    b = pilots.getData( "b", Methods.ClosestTime );
                    e = b - 2 * a;
                    try {
                        pilots.sendData( sock, e );
                    } catch ( Exception ex ) {
                        System.err.println( ex );
                    }
                }
            }, 0, 1000 /* every 1 sec */ );
    }

    
    public static void main( String[] args ) {
        Twice twice = new Twice();

        try {
            pilots.parseArgs( args );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

        twice.startOutput_e();
    }
}
