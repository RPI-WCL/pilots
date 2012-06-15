package pilots.runtime;

import java.net.*;
import java.io.*;
import pilots.runtime.*;


public class DataReceiver extends DebugPrint implements Runnable  {
    private static int DEFAULT_PORT = 8888;
    private static boolean loop_ = true;
    private static int globalID_ = 0;
    private Socket sock_;
    private int id_;
    
    public DataReceiver( Socket sock ) {
        sock_ = sock;
        id_ = globalID_++;
    }

    private void dbgPrint2( String str ) {
        dbgPrint( "(Thread " + id_ + ") " + str);
    }

    public void run() {
        dbgPrint2( "started" );

        try {

            BufferedReader in = new BufferedReader( new InputStreamReader( sock_.getInputStream() ) );
            String str = null, varNames = null;
            DataStore dataStore = null;

            while ( (str = in.readLine() ) != null ) {
                if ( str.length() == 0 ) {
                    dbgPrint2( "EOS marker received" );
                    break;
                }
                else if ( str.charAt(0) == '#' ) {
                    dbgPrint2( "first line received: " + str );
                    varNames = str;
                    synchronized (this) {
                        dataStore = DataStore.getInstance( str );
                    }
                }
                else {
                    if ( dataStore == null ) {
                        dbgPrint2( "no data store" );
                        break;
                    }

                    dbgPrint2( "data received for \"" + varNames + "\": " + str );
                    dataStore.addData( str );
                }

            }

            in.close();
            sock_.close();

        } catch (IOException ex) {
            System.err.println( ex );
        }

        dbgPrint2( "finished" );
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
