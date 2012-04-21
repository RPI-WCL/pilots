package pilots.tests;

import java.io.*;
import java.net.*;

public class tClient
{
    public static void main( String[] args ) {
        new tClient( args );
    }

    public tClient( String[] args ) {
        try {
            int port = 8888;

            Socket socket = new Socket( "localhost", port );

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter( outputStream );

			BufferedReader in = new BufferedReader( new FileReader( args[0] ) );

            String str;
            while( (str = in.readLine()) != null) {
                printWriter.println( str );
                printWriter.flush();
            }

            outputStream.close();
            printWriter.close();
            in.close();
            socket.close();
        }
        catch ( IOException ex ) {
            System.err.println( ex );
        }
    }
}