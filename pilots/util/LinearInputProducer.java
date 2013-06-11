package pilots.util;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.TimeZone;
import java.util.Random;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import pilots.runtime.SpatioTempoData;

public class LinearInputProducer extends Thread {
    private static final int DATA_SEND_FREQUENCY = 1000; // [ms]
    private static final String TARGET_HOST = "localhost";
    private static final int TARGET_PORT = 8888;

    private static int iteration_;
    private String varName_;
    private int value_;
    private int increment_;
    private int randRange_; // [%]

    private Random rand_;

    // socket communication
    private Socket sock_ = null;
    private OutputStream outputStream_ = null;
    private PrintWriter printWriter_ = null;


    public static void main( String[] args ) {
        if (args.length < 5) {
            System.err.println( "Usage: ./java pilots.util.LinearInputProducer <iteration> <varname> <init value> <increment value> <rand[%]>" );
            return;
        }

        LinearInputProducer client = 
            new LinearInputProducer( Integer.parseInt( args[0] ), /* iteration */
                                     args[1], /* variable name */
                                     Integer.parseInt( args[2] ), /* init value */
                                     Integer.parseInt( args[3] ), /* increment value */
                                     Integer.parseInt( args[4] )  /* random range */
            );
        client.start();
    }
    
    LinearInputProducer( int iteration, String varName, int value, int increment, int randRange ) {
        iteration_ = iteration;
        varName_ = varName;
        value_ = value;
        increment_ = increment;
        randRange_ = randRange;
        
        rand_ = new Random();

        try {
            sock_ = new Socket( TARGET_HOST, TARGET_PORT );
            outputStream_ = sock_.getOutputStream();
            printWriter_ = new PrintWriter( outputStream_ );
        }
        catch (IOException ex) {
            System.err.println( ex );
        }
    }

    public String createStData( int value ) {
        TimeZone.setDefault( TimeZone.getTimeZone( SpatioTempoData.timeZoneID ) );

        Date date = new Date();
        DateFormat df = new SimpleDateFormat( SpatioTempoData.datePattern );
        String stData = new String( ":" + df.format( date ) + ":" + value );

        return stData;
    }

    private void sockWrite( String str ) {
        if (printWriter_ == null) {
            System.err.println( "PrintWriter is not initialized" );
            return;
        }

        System.out.println( str );

        // add '\n' here at the end of the string
        printWriter_.println( str );
        printWriter_.flush();
    }

    private void sockClose() {
        try {
            outputStream_.close();
            printWriter_.close();
            sock_.close();
        }
        catch ( IOException ex ) {
            System.err.println( ex );
        }        
        
    }

    public void run() {
        
        sockWrite( "#" + varName_ );

        boolean infiniteLoop = (iteration_ < 0);

        for (int i = 0; infiniteLoop || (i < iteration_); i++) {
            value_ += increment_;

            sockWrite( createStData( value_ ) );
            
            int randWidth = (int)(DATA_SEND_FREQUENCY * (double)randRange_ / 100);
            int randDelay = 0;
            if (0 < randWidth) {
                randDelay = rand_.nextInt( randWidth ); // positive delay
            }

            try {
                Thread.sleep( DATA_SEND_FREQUENCY + randDelay );
            } catch (Exception ex) {
                System.err.println( ex );
            }
        }

        sockClose();
    }
}
