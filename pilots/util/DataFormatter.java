package pilots.util;

import java.io.*;
import java.text.*;
import java.util.*;
import pilots.runtime.*;

public class DataFormatter {
    // assuming '[time]\SP[data]' input

    private String varName_;
    private String inputFile_;
    private DateFormat dateFormat_;
    private Date now_;
    private long baseTime_;
    // private int outputFrequencyMsec_;

    public DataFormatter( String varName, String inputFile, String baseDateStr ) {
        varName_ = varName;
        inputFile_ = inputFile;
        dateFormat_ = new SimpleDateFormat( SpatioTempoData.datePattern );
        try {
            now_ = dateFormat_.parse( baseDateStr );
        } catch (Exception ex) {
            System.err.println( ex );
        }
        baseTime_ = now_.getTime();
        // outputFrequencyMsec_ = outputFrequencyMsec;
    } 


    public void format() {
        String outputFile = varName_ + ".txt";

		try {
			BufferedReader in = new BufferedReader( new FileReader( inputFile_ ) );
            PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( outputFile ) ) );
            out.println( "#" + varName_ );
            
            String str = null;
            while ((str = in.readLine()) != null) {
                String[] data = str.split( "," );
                double timeSec = Double.parseDouble( data[0] );
                long timeMsec = (long)(timeSec * 1000);
                now_.setTime( baseTime_ + timeMsec );

                String timeStr = dateFormat_.format( now_ );
                out.println( ":" + timeStr + ":" + data[1] );
            }

            out.close();
            in.close();
        } catch (Exception ex) {
            System.err.println( ex );
        }
    }


    public static void main( String[] args ) {
        if (args.length != 3) {
            System.err.println( "Usage: java DataFormatter <var name> <input file> <base date>" );
            return;
        }

        DataFormatter starter = new DataFormatter( args[0], args[1], args[2] );
        starter.format();
    }
}
