package pilots.runtime;

import java.net.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import pilots.runtime.*;


public class PilotsRuntime {
    // hosts, ports, sockets
    private int inputPort_;

    private int numOutputs_;
    private String[] outputHosts_;
    private int[] outputPorts_;
    private Socket[] outputSockets_;

    private int numErrors_;
    private String[] errorHosts_;
    private int[] errorPorts_;
    private Socket[] errorSockets_;

    private long timeAdjustment_;
    private DateFormat dateFormat_;

    public PilotsRuntime() {
        inputPort_ = 0;

        numOutputs_ = 0;
        outputHosts_ =  null;
        outputPorts_ = null;
        outputSockets_ = null;

        numErrors_ = 0;
        errorHosts_ = null;
        errorPorts_ = null;
        errorSockets_ = null;

        timeAdjustment_ = 0;
        
        dateFormat_ = new SimpleDateFormat( "yyyy-MM-dd HHmmZ" );
    }


    protected void parseArgs( String[] args ) throws ParseException {
        try {
            ArgParser.parse( args, // input 
                             inputPort_, outputHosts_, outputPorts_, errorHosts_, errorPorts_ ); // outputs
            numOutputs_ = outputHosts_.length;
            numErrors_ = errorHosts_.length;
        } catch (ParseException ex) {
            throw ex;
        }
    }

    protected boolean startServer() {
        if (inputPort_ == 0) {
            System.err.println( "input port is not initialized"  );
            return false;
        }

        DataReceiver.startServer( inputPort_ );

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
                outputSockets_ = new Socket[numOutputs_];
                for (int i = 0; i < outputSockets_.length; i++)
                    outputSockets_[i] = null;
            }
            if (outputSockets_[sockIndex] == null) {
                outputSockets_[sockIndex] = new Socket( outputHosts_[sockIndex], outputPorts_[sockIndex] );
            }
            sock = outputSockets_[sockIndex];
            break;

        case Error:
            if (errorSockets_ == null) {
                errorSockets_ = new Socket[numErrors_];
                for (int i = 0; i < errorSockets_.length; i++)
                    errorSockets_[i] = null;
            }
            if (errorSockets_[sockIndex] == null) {
                errorSockets_[sockIndex] = new Socket( errorHosts_[sockIndex], errorPorts_[sockIndex] );
            }
            sock = errorSockets_[sockIndex];
            break;

        default:
            sock = null;
            throw new IOException();
        }
        PrintWriter printWriter = new PrintWriter( sock.getOutputStream(), true );

        // send the first line of the output stream
        printWriter.println( "#" + var );
        printWriter.flush();
        printWriter.close(); // should we do this here???

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

    
    protected void setBaseCal( Calendar cal ) {
        timeAdjustment_ = Calendar.getInstance().getTimeInMillis() - cal.getTimeInMillis();
    }
        

    protected void sendData( OutputType outputType, int sockIndex, double val ) {
        Socket sock = getSocket( outputType, sockIndex );

        Calendar now = Calendar.getInstance();
        now.add( Calendar.MILLISECOND, (int)(-1 * timeAdjustment_) );
        Date date = now.getTime();

        // write the value on the socket
        try {
            PrintWriter printWriter = new PrintWriter( sock.getOutputStream(), true );
            printWriter.println( ":" + dateFormat_.format( date ) + ":" + val );
            printWriter.flush();
        } catch (IOException ex) {
            System.err.println( ex );
        }
    }


    protected double getData( String var, Method... methods ) {

        DataStore store = DataStore.findStore( var );
        double d = -1.0;

        if (store != null) {
            d = store.getData( var, methods );
        }
        else {
            System.err.println( "no matching variable stored" );
        }

        return d;
    }
}
