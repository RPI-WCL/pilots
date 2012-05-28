package pilots.runtime;

import java.net.*;
import java.io.*;
import pilots.runtime.*;


public class DataReceiver extends Thread {
    private static int DEFAULT_PORT = 8888;
    private static boolean loop_ = true;
    private Socket sock_;
    
    public DataReceiver( Socket sock ) {
        sock_ = sock;
    }

    public void run() {
        try {
            System.out.println( "-- Connected" );

            BufferedReader in = new BufferedReader( new InputStreamReader( sock_.getInputStream() ) );
            String str = null;
            DataStore dataStore = null;

            while ( (str = in.readLine() ) != null ) {
                if ( str.length() == 0 ) {
                    System.out.println( "End-Of-Stream marker received!!!" );
                    break;
                }
                else if ( str.charAt(0) == '#' ) {
                    System.out.println( "# received!!" );
                    dataStore = DataStore.getInstance( str );
                }
                else {
                    if ( dataStore == null ) {
                        System.err.println( "No data store" );
                        break;
                    }
                        
                    // System.out.println( str );
                    dataStore.add( str );
                }

            }

            in.close();
            sock_.close();

            System.out.println("-- Connection Closed");

        } catch (IOException ex) {
            System.err.println( ex );
        }
    }


    public static void startServer( int port ) {
        loop_ = true;
        final int serverPort = port;
        new Thread() {
            public void run() {
                try {
                    ServerSocket serverSock = new ServerSocket( serverPort );

                    while ( loop_ ) {
                        System.out.println( "DataReceiver listening to port:" + serverPort );

                        Socket newSock = serverSock.accept();
                        DataReceiver dataReceiver = new DataReceiver( newSock );
                        dataReceiver.start();
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

        // try {
        //     ServerSocket serverSock = new ServerSocket( port );

        //     while ( true ) {
        //         System.out.println("Server listening to port:" + port);

        //         Socket newSock = serverSock.accept();
        //         DataReceiver dataReceiver = new DataReceiver( newSock );
        //         dataReceiver.start();
        //     } 
        // } catch (Exception ex ) {
        //     System.err.println( ex );
        // }
    }
}
