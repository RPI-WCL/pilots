package pilots.runtime;

import java.net.*;
import java.io.*;
import pilots.runtime.*;

enum Method {
    ClosestX, ClosestY, ClosestZ, ClosestTime, EuclideanXY, EuclideanXYZ, 
        InterpolateX, InterpolateY, InterpolateZ, InterpolateTime, InterpolateXY, InterpolateXYZ
}

enum OutputType {
    Output, Error
}

public class PilotsRuntime {
    // hosts, ports, sockets
    private int inputPort = -1;

    private int numOutputs= -1;
    private String[] outputHosts = null;
    private int[] outputPorts = null;
    private Socket[] outputSockets= null;

    private int numErrors = -1;
    private String[] errorHosts = null;
    private int[] errorPorts= null;;
    private Socket[] errorSockets = null;

    private long timeAdjustment = -1;
    private DateFormat dateFormat = null;

    private DataStore dataStore;

    public PilotsRuntime() {
        dateFormat = new SimpleDateFormat( "yyyy-MM-dd HHMMZ" );
        dataStorage = new DataStorage();
    }

    void parseArgs( String[] args ) throws ParseException {
        try {
            ArgParser.parse( args, // input 
                             inputPort, outputHosts, outputPorts, errorHosts, errorPorts ); // outputs
            numOutputs = outputHosts.length;
            numErrors = errorHosts.length;
        } catch (ParseException ex) {
            System.err.println( "Arguments parsing failed: " + ex );
            throw new ParseExpception();
        }
    }


    void openSocket( OutputType outputType, int sockIndex, String var ) 
        throws UnknownHostException, IOException {
        
        Socket sock;

        // create sockets if it has not been created
        switch (outputType) {
        case OutputType.Output:
            if (outputPorts == null) {
                outputPorts = new Socket[numOutputs];
            }
            if (outputPorts[sockIndex] == null) {
                outputPorts[sockIndex] = new Socket( outputHosts[sockIndex], outputPorts[sockIndex] );
            }
            sock = outputPorts[sockIndex];
            break;
        case OutputType.Error:
            if (errorPorts == null) {
                errorPorts = new Socket[numErrors];
            }
            if (errorPorts[sockIndex] == null) {
                errorPorts[sockIndex] = new Socket( errorHosts[sockIndex], errorPorts[sockIndex] );
            }
            sock = errorPorts[sockIndex];
            break;
        default:
            break;
        }
        PrintWriter writer = new PrintWriter( sock.getOutputStream(), true );

        // send the first line of the output stream
        writer.writeln( "#" + var );
        writer.flush();
        writer.close(); // should we do this here???
    }


    void closeSocket( OutputType outputType, int socketIndex ) {
        switch (outputType) {
        case OutputType.Output:
            sock = outputPorts[sockIndex];
            break;
        case OutputType.Error:
            sock = errorPorts[sockIndex];
            break;
        default:
            break;
        }

        sock.close();
    }

    
    void setBaseCal( Calendar cal ) {
        timeAdjustment = Calendar.getInstance().getTimeInMillis() - cal.getTimeInMillis();
    }
        

    void sendData( Socket sock, double val ) {
        Calendar now = Calendar.getInstance();
        now.add( Calendar.MILLISECOND, -1 * timeAdjustment );
        Date date = now.getTime();

        // write the value on the socket
        PrintWriter writer = new PrintWriter( sock.getOutputStream(), true );
        writer.writeln( ":" + dateFormat.format( date ) + ":" + val );
        write.flush();
    }


    double getData( String var, Method method ) {
        return dataStorage.getData( var, method );
    }


}
