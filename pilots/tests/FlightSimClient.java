package pilots.tests;

import java.io.*;
import java.net.*;

public class FlightSimClient
{
    private static final String TARGET_HOST = "localhost";
    private static final int TARGET_PORT = 8888;

    String filename_;
    Socket sock_;
    OutputStream outputStream_;
    PrintWriter writer_;
    BufferedReader reader_;
    

    public static void main( String[] args ) {
        if (args.length < 1) {
            System.err.println( "Usage: ./java pilots.tests.SimClient <filename>" );
            return;
        }            

        FlightSimClient client = new FlightSimClient( args[0] ); // filename
        client.startSend();
    }

    public FlightSimClient( String filename ) {
        filename_ = filename;
        
        try {
            sock_ = new Socket( TARGET_HOST, TARGET_PORT );
            outputStream_ = sock_.getOutputStream();
            writer_ = new PrintWriter( outputStream_ );
            reader_ = new BufferedReader( new FileReader( filename_ ) );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startSend() {
        String str;

        try {
            while ((str = reader_.readLine()) != null) {
                writer_.println( str );
                writer_.flush();
            }
        
            outputStream_.close();
            writer_.close();
            reader_.close();
            sock_.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}