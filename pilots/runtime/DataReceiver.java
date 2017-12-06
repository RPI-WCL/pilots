package pilots.runtime;

import java.net.*;
import java.io.*;
import java.util.logging.*;

/*
* DataReceiver receives data streams and stores the incoming data into
* corresponding data store.
*/
public class DataReceiver implements Runnable  {
    private static int DEFAULT_PORT = 8888;
    private static boolean loop_ = true;
    private static int globalID_ = 0;
    private Socket sock_;
    private int id_;
    
    private static final Logger LOGGER = Logger.getLogger(DataReceiver.class.getSimpleName());

    public DataReceiver( Socket sock ) {
        sock_ = sock;
        id_ = globalID_++;
    }

    private void dbgPrint(String message) {
        LOGGER.log(Level.INFO, "(Thread " + id_ + ") " + message);
    }

    public void run() {
        dbgPrint( "started" );

        try {

            BufferedReader in = new BufferedReader( new InputStreamReader( sock_.getInputStream() ) );
            String str = null, varNames = null;
            DataStore dataStore = null;

            while ( (str = in.readLine() ) != null ) {
                if ( str.length() == 0 ) {
                    dbgPrint( "EOS marker received" );
                    break;
                }
                else if ( str.charAt(0) == '#' ) {
                    dbgPrint( "first line received: " + str );
                    varNames = str;
                    synchronized (this) {
                        dataStore = DataStore.getInstance( str );
                    }
                }
                else {
                    if ( dataStore == null ) {
                        dbgPrint( "no data store" );
                        break;
                    }

                    dbgPrint( "data received for \"" + varNames + "\": " + str );
                    dataStore.addData( str );
                }

            }

            in.close();
            sock_.close();

        } catch (IOException ex) {
            System.err.println( ex );
        }

        dbgPrint( "finished" );
    }


    public static void startServer( int port ) {
        loop_ = true;
        final int serverPort = port;

        // daemon thread listening port 8888
        new Thread() {
            public void run() {
                try {
                    ServerSocket serverSock = new ServerSocket( serverPort );
                    System.out.println( "[DataReceiver] started listening to port:" + serverPort );

                    while ( loop_ ) {
                        Socket newSock = serverSock.accept();
                        DataReceiver dataReceiver = new DataReceiver( newSock );
                        Thread t = new Thread( dataReceiver );
                        t.start();
                    } 
                } catch (Exception ex ) {
                    System.err.println( ex );
                }
            }
        }.start();
    }


    public static void stopServer() {
        loop_ = false;
    }

    
    public static void main( String[] args ) {
        int port;
        if (args.length == 2) {
            port = Integer.parseInt( args[1] );
        }
        else {
            port = DEFAULT_PORT;
        }

        DataReceiver.startServer( port );
    }
}
