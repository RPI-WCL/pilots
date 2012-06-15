package pilots.tests;

import java.util.Timer;
import java.util.TimerTask;
import java.net.Socket;
import pilots.runtime.*;


public class SimpleApp extends PilotsRuntime {
    private Timer timer_;

    public SimpleApp() {
        timer_ = new Timer();
    }

    public void startOutput() {
        try {
            openSocket( OutputType.Error, 0 /* index for sockets */, "e" );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

        timer_.scheduleAtFixedRate( new TimerTask() {
                public void run() {
                    double a, b, e;
                    a = getData( "a", new Method( Method.Closest, "t" ) );
                    b = getData( "b", new Method( Method.Closest, "t" ) );
                    e = b - 2 * a;
                    try {
                        dbgPrint( "a = " + a + ", b = " + b + ", e = " + e );
                        sendData( OutputType.Error, 0 /* index for sockets */, e );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                }
            }, 0, 1000 /* every 1 sec */ );
    }

    
    public static void main( String[] args ) {
        SimpleApp simple = new SimpleApp();

        try {
            simple.parseArgs( args );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        simple.startServer();
        simple.startOutput();
    }
}
