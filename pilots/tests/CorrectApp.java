package pilots.tests;

import java.util.Timer;
import java.util.TimerTask;
//import java.util.Vector;
//import java.lang.Double;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

/*
//new idea: write all debugging output to a log file
PrintWriter dbg = new PrintWriter(new BufferedWriter(new FileWriter("/home/rich/Projects/pilots/dbgout.txt",true)));
dbg.println(text);
out.close();
*/

public class CorrectApp extends PilotsRuntime {

    private Timer timer_;
    private SlidingWindow win_;
    private ErrorSignatures errorSigs_;
    private ErrorAnalyzer errorAnalyzer_;
    private int omega_;
    private double tau_;
    private int time_;


    public class ErrorSignatures_e extends ErrorSignatures {
        public ErrorSignatures_e( int numSignatures, String[] descriptions ) {
            super( numSignatures, descriptions );
        }
        public Double s0( double t, double K ) {
            double e = 0;
            return new Double(e);
        }
        public Double s1( double t, double K ) {
            double e = 2 * t + K;
            return new Double(e);
        }
        public Double s2( double t, double K ) {
            double e = -2 * t + K;
            return new Double(e);
        }
        public Double s3( double t, double K ) { return null; }
        public Double s4( double t, double K ) { return null; }
    }

    public CorrectApp( int omega, double tau ) {
        timer_ = new Timer();
        win_ = new SlidingWindow( omega );
        int numSignatures = 3;
        String[] descriptions = {"Normal", "A failure", "B failure"};
        errorSigs_ = new ErrorSignatures_e( numSignatures, descriptions );
        errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, tau );
        errorAnalyzer_.test();
        omega_ = omega;
        tau_ = tau;
        time_ = 0;
    }

    public void startOutput_e() {
        try {
            openSocket( OutputType.Error, 0 /* index for sockets */, "e" );
        } catch ( Exception ex ) {
            System.err.println( ex );
        }

        timer_.scheduleAtFixedRate( new TimerTask(){
                public void run(){ //SlidingWindow win) {
                    double a, b, e;
                    a = getData( "a", new Method( Method.Closest, "t" ) );
                    b = getData( "b", new Method( Method.Closest, "t" ) );
                    e = b - 2 * a;

                    win_.push( e );
                    int mode = errorAnalyzer_.analyze( e, win_, time_ );
                    switch (mode) {
                    case 0: // s0
                        break;
                    case 1: // s1
                        a = b / 2;
                        e = b - 2 * a;
                        break;
                    case 2: // s2
                        b = a * 2;
                        e = b - 2 * a;
                        break;
                    }
                    String desc = errorSigs_.getDescription( mode );
                    if (desc != null)
                        dbgPrint( desc );

                    try {
                        sendData( OutputType.Error, 0 /* index for sockets */, e );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                    }

                    time_++;
                }
            }, 0, 1000 /* every 1 sec */ );
    }
    
    public static void main( String[] args ) {
        if (args.length < 1){
            System.err.println( "Usage: java pilots.tests.CorrectApp <omega> <tau>" );
            return;
        }
        //Integer.parseInt(args[1])

        System.out.println("BEGIN!!");
        CorrectApp correct = new CorrectApp( 5, 0.8 );

        try {
            correct.parseArgs( args );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        correct.startServer();
        correct.startOutput_e();
    }
}
