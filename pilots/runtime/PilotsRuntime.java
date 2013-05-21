package pilots.runtime;

import java.net.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import pilots.Version;
import pilots.runtime.*;


public class PilotsRuntime extends DebugPrint {
    private static final int DEFAULT_OMEGA = 10;
    private static final double DEFAULT_TAU = 0.8;

    // hosts, ports, sockets
    private HostsPorts input_;
    private HostsPorts outputs_;
    private HostsPorts errors_;

    private Socket[] outputSockets_;
    private PrintWriter[] outputWriters_;
    private Socket[] errorSockets_;
    private PrintWriter[] errorWriters_;

    private int omega_;
    private double tau_;

    private DateFormat dateFormat_;

    CurrentLocationTimeService currLocTime_;

    public PilotsRuntime() {
        input_ = new HostsPorts();

        outputs_ = new HostsPorts();
        outputSockets_ = null;
        outputWriters_ = null;

        errors_ = new HostsPorts();
        errorSockets_ = null;
        errorWriters_ = null;

        omega_ = DEFAULT_OMEGA;
        tau_ = DEFAULT_TAU;

        dateFormat_ = new SimpleDateFormat( SpatioTempoData.datePattern );

        currLocTime_ = ServiceFactory.getCurrentLocationTime();

        System.out.println( "PILOTS Runtime v" + Version.ver + " has started." );
    }


    protected void parseArgs( String[] args ) throws ParseException {
        Value omega = new Value( Value.NULL );
        Value tau = new Value( Value.NULL );

        try {
            // ArgParser.parse( args, // input 
            //                  input_, outputs_, errors_ ); // output
            ArgParser.parse( args, // input 
                             input_, outputs_,
                             omega, tau );
        } catch (ParseException ex) {
            throw ex;
        }

        if (omega.getValue() != Value.NULL) 
            omega_ = (int)omega.getValue();

        if (tau.getValue() != Value.NULL) 
            tau_ = tau.getValue();
    }

    protected boolean startServer() {
        int inputPort = input_.getPort( 0 );
        //dbgPrint( "startServer " + inputPort + " begin" );

        if (inputPort == 0) {
            System.err.println( "input port is not initialized"  );
            return false;
        }

        DataReceiver.startServer( inputPort );

        //dbgPrint( "startServer end" );

        return true;
    }


    protected void stopServer() {
        DataReceiver.stopServer();
    }


    protected Socket openSocket( OutputType outputType, int sockIndex, String var ) 
        throws UnknownHostException, IOException {
        
        Socket sock;

        // create sockets if it has not been created
        switch (outputType) {
        case Output:    // OutputType.Output gives a compile error
            if (outputSockets_ == null) {
                outputSockets_ = new Socket[ outputs_.getSize() ];
                for (int i = 0; i < outputSockets_.length; i++)
                    outputSockets_[i] = null;
            }
            if (outputSockets_[sockIndex] == null) {
                outputSockets_[sockIndex] = new Socket( outputs_.getHost( sockIndex ),
                                                        outputs_.getPort( sockIndex ) );
            }
            sock = outputSockets_[sockIndex];

            if (outputWriters_ == null) {
                outputWriters_ = new PrintWriter[ outputs_.getSize() ];
                for (int i = 0; i < outputWriters_.length; i++)
                    outputWriters_[i] = null;
            }
            if (outputWriters_[sockIndex] == null) {
                outputWriters_[sockIndex] = new PrintWriter( sock.getOutputStream(), true );
                // send the first line of the output stream
                outputWriters_[sockIndex].println( "#" + var );
                outputWriters_[sockIndex].flush();
            }
            break;

        case Error:
            if (errorSockets_ == null) {
                errorSockets_ = new Socket[ errors_.getSize() ];
                for (int i = 0; i < errorSockets_.length; i++)
                    errorSockets_[i] = null;
            }
            if (errorSockets_[sockIndex] == null) {
                errorSockets_[sockIndex] = new Socket( errors_.getHost( sockIndex ),
                                                       errors_.getPort( sockIndex ) );
                System.out.println( "openSocket, socket opened for " + 
                                    errors_.getHost( sockIndex ) + ":" + errors_.getPort( sockIndex ) +
                                    ", sock=" + errorSockets_[sockIndex] );
            }
            sock = errorSockets_[sockIndex];

            if (errorWriters_ == null) {
                errorWriters_ = new PrintWriter[ errors_.getSize() ];
                for (int i = 0; i < errorWriters_.length; i++)
                    errorWriters_[i] = null;
            }
            if (errorWriters_[sockIndex] == null) {
                errorWriters_[sockIndex] = new PrintWriter( sock.getOutputStream(), true );
                // send the first line of the output stream
                errorWriters_[sockIndex].println( "#" + var );
                errorWriters_[sockIndex].flush();
            }
            break;

        default:
            sock = null;
            throw new IOException();
        }
        // PrintWriter printWriter = new PrintWriter( sock.getOutputStream(), true );

        // // send the first line of the output stream
        // printWriter.println( "#" + var );
        // printWriter.flush();
        // printWriter.close(); // should we do this here???

        return sock;
    }


    protected void closeSocket( OutputType outputType, int sockIndex ) {
        Socket sock= getSocket( outputType, sockIndex );
        
        try {
            sock.close();
        } catch (IOException ex) {
            System.err.println( ex );
        }
    }


    protected Socket getSocket( OutputType outputType, int sockIndex ) {
        Socket sock;

        switch (outputType) {
        case Output:
            sock = outputSockets_[sockIndex];
            break;
        case Error:
            sock = errorSockets_[sockIndex];
            break;
        default:
            sock = null;
            break;
        }
        
        return sock;
    }

    protected PrintWriter getWriter( OutputType outputType, int sockIndex ) {
        PrintWriter writer;

        switch (outputType) {
        case Output:
            writer = outputWriters_[sockIndex];
            break;
        case Error:
            writer = errorWriters_[sockIndex];
            break;
        default:
            writer = null;
            break;
        }
        
        return writer;
    }

    

    protected void sendData( OutputType outputType, int sockIndex, double val ) {
        Date date = currLocTime_.getTime();

        // write the value on the socket
        PrintWriter printWriter = getWriter( outputType, sockIndex );
        printWriter.println( ":" + dateFormat_.format( date ) + ":" + val );
        printWriter.flush();
    }


    protected double getData( String var, Method... methods ) {

        DataStore store = DataStore.findStore( var );
        double d = 0;

        if (store != null) {
            d = store.getData( var, methods );
        }
        else {
            dbgPrint( "no matching variable stored for \"" + var + "\"");
        }

        return d;
    }
    
    protected boolean isEndTime() {
        return currLocTime_.isEndTime();
    }

    protected void progressTime( int timeOffset ) {
        currLocTime_.progressTime( timeOffset );
    }

    protected Date getTime() {
        return currLocTime_.getTime();
    }

    protected int getOmega() {
        return omega_;
    }

    protected double getTau() {
        return tau_;
    }
}
