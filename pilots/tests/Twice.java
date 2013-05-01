package pilots.tests;

import java.util.Timer;
import java.util.TimerTask;
import java.net.Socket;
import pilots.runtime.*;

public class Twice extends PilotsRuntime {
    private Timer timer_;

    public Twice() {
        timer_ = new Timer();
    }

    public void startOutput_e() {
        try {
            openSocket( OutputType.Error, 0, "e" );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    
        timer_.scheduleAtFixedRate( new TimerTask() {
            public void run() {
                double e, b, a;
                a = getData( "a", new Method( Method.Closest, "t") );
                b = getData( "b", new Method( Method.Closest, "t") );
                e = b-2*a;
                try {
                    sendData( OutputType.Error, 0, e );
                } catch ( Exception ex ) {
                    ex.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    public static void main( String[] args ) {
        Twice app = new Twice();

        try {
            app.parseArgs( args );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        app.startServer();
        app.startOutput_e();
    }
}

