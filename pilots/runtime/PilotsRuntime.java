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
    private static final int DEFAULT_OMEGA = 1;
    private static final double DEFAULT_TAU = 0.8;

    // hosts, ports, sockets
    private HostsPorts input;
    private HostsPorts outputs;
    private HostsPorts errors;

    private Socket[] outputSockets;
    private PrintWriter[] outputWriters;
    private Socket[] errorSockets;
    private PrintWriter[] errorWriters;

    private int omega;
    private double tau;

    protected DateFormat dateFormat;

    CurrentLocationTimeService currLocTime;

    // newly added for animation effect for the simulation mode
    private boolean animation;
    private double timeSpeed;
    private Calendar prevDate;

    public PilotsRuntime() {
        input = new HostsPorts();

        outputs = new HostsPorts();
        outputSockets = null;
        outputWriters = null;

        errors = new HostsPorts();
        errorSockets = null;
        errorWriters = null;

        omega = DEFAULT_OMEGA;
        tau = DEFAULT_TAU;

        dateFormat = new SimpleDateFormat(SpatioTempoData.datePattern);

        currLocTime = ServiceFactory.getCurrentLocationTime();

        animation = false;
        timeSpeed = 1.0;
        prevDate = null;

        String timeSpan = System.getProperty("timeSpan");
        String timeSpeed = System.getProperty("timeSpeed");
        if ((timeSpan != null) && (timeSpeed != null)) {
            if ((timeSpeed.charAt(0) != 'x') && (timeSpeed.charAt(0) != 'X')) {
                System.err.println("ERROR: -DtimeSpeed format: \"x\" 1*DIGIT (e.g., x100)");
            }
            else {
                animation = true;
                this.timeSpeed = Double.parseDouble(timeSpeed.substring(1));
            }
        }

        System.out.println("PILOTS Runtime v" + Version.ver + " has started.");
    }


    protected void parseArgs(String[] args) throws ParseException {
        Value omega = new Value(Value.NULL);
        Value tau = new Value(Value.NULL);

        try {
            // ArgParser.parse(args, // input 
            //                  input, outputs, errors); // output
            ArgParser.parse(args, // input 
                             input, outputs,
                             omega, tau);
        } catch (ParseException ex) {
            throw ex;
        }

        if (omega.getValue() != Value.NULL) 
            this.omega = (int)omega.getValue();

        if (tau.getValue() != Value.NULL) 
            this.tau = tau.getValue();
    }

    protected boolean startServer() {
        int inputPort = input.getPort(0);
        //dbgPrint("startServer " + inputPort + " begin");

        if (inputPort == 0) {
            System.err.println("input port is not initialized" );
            return false;
        }

        DataReceiver.startServer(inputPort);

        //dbgPrint("startServer end");

        return true;
    }


    protected void stopServer() {
        DataReceiver.stopServer();
    }


    protected Socket openSocket(OutputType outputType, int sockIndex, String... vars) 
        throws UnknownHostException, IOException {
        
        Socket sock;

        // create sockets if it has not been created
        switch (outputType) {
        case Output:    // OutputType.Output gives a compile error
            if (outputSockets == null) {
                outputSockets = new Socket[outputs.getSize()];
                for (int i = 0; i < outputSockets.length; i++)
                    outputSockets[i] = null;
            }
            if (outputSockets[sockIndex] == null) {
                outputSockets[sockIndex] = new Socket(outputs.getHost(sockIndex),
                                                      outputs.getPort(sockIndex));
            }
            sock = outputSockets[sockIndex];

            if (outputWriters == null) {
                outputWriters = new PrintWriter[outputs.getSize()];
                for (int i = 0; i < outputWriters.length; i++)
                    outputWriters[i] = null;
            }
            if (outputWriters[sockIndex] == null) {
                outputWriters[sockIndex] = new PrintWriter(sock.getOutputStream(), true);
                // send the first line of the output stream
                outputWriters[sockIndex].print("#");
                for (int i = 0; i < vars.length; i++) {
                    if (i == vars.length - 1)
                        outputWriters[sockIndex].println(vars[i]);
                    else
                        outputWriters[sockIndex].print(vars[i] + ",");
                }
                outputWriters[sockIndex].flush();
            }
            break;

        case Error:
            if (errorSockets == null) {
                errorSockets = new Socket[errors.getSize()];
                for (int i = 0; i < errorSockets.length; i++)
                    errorSockets[i] = null;
            }
            if (errorSockets[sockIndex] == null) {
                errorSockets[sockIndex] = new Socket(errors.getHost(sockIndex),
                                                     errors.getPort(sockIndex));
                System.out.println("openSocket, socket opened for " + 
                                    errors.getHost(sockIndex) + ":" + errors.getPort(sockIndex) +
                                    ", sock=" + errorSockets[sockIndex]);
            }
            sock = errorSockets[sockIndex];

            if (errorWriters == null) {
                errorWriters = new PrintWriter[errors.getSize()];
                for (int i = 0; i < errorWriters.length; i++)
                    errorWriters[i] = null;
            }
            if (errorWriters[sockIndex] == null) {
                errorWriters[sockIndex] = new PrintWriter(sock.getOutputStream(), true);
                // send the first line of the output stream

                errorWriters[sockIndex].print("#");
                for (int i = 0; i < vars.length; i++) {
                    if (i == vars.length - 1)
                        errorWriters[sockIndex].println(vars[i]);
                    else
                        errorWriters[sockIndex].print(vars[i] + ",");
                }
                errorWriters[sockIndex].flush();
            }
            break;

        default:
            sock = null;
            throw new IOException();
        }
        // PrintWriter printWriter = new PrintWriter(sock.getOutputStream(), true);

        // // send the first line of the output stream
        // printWriter.println("#" + var);
        // printWriter.flush();
        // printWriter.close(); // should we do this here???

        return sock;
    }


    protected void closeSocket(OutputType outputType, int sockIndex) {
        Socket sock= getSocket(outputType, sockIndex);
        
        try {
            sock.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }


    protected Socket getSocket(OutputType outputType, int sockIndex) {
        Socket sock;

        switch (outputType) {
        case Output:
            sock = outputSockets[sockIndex];
            break;
        case Error:
            sock = errorSockets[sockIndex];
            break;
        default:
            sock = null;
            break;
        }
        
        return sock;
    }

    protected PrintWriter getWriter(OutputType outputType, int sockIndex) {
        PrintWriter writer;

        switch (outputType) {
        case Output:
            writer = outputWriters[sockIndex];
            break;
        case Error:
            writer = errorWriters[sockIndex];
            break;
        default:
            writer = null;
            break;
        }
        
        return writer;
    }

    protected void sendData(OutputType outputType, int sockIndex, double... values) {
        Date date = currLocTime.getTime();
        
        if (animation && (prevDate != null)) {
            long currTime = date.getTime();
            long prevTime = prevDate.getTime().getTime();
            long waitTime = (long)((currTime - prevTime) / timeSpeed);
            // System.out.println("sendData, curr=" + (currTime - prevTime) + ", " + waitTime); 
            try {
                Thread.sleep(waitTime );
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }

        // write the value on the socket
        PrintWriter printWriter = getWriter(outputType, sockIndex);
        printWriter.print(":" + dateFormat.format(date) + ":");
        for (int i = 0; i < values.length; i++) {
            if (i == values.length - 1)
                printWriter.println(values[i]);
            else
                printWriter.print(values[i] + ",");
        }
        printWriter.flush();

        if (prevDate == null)
            prevDate = Calendar.getInstance();
        prevDate.setTime(date);
    }

    // addData adds a spatioTempoData into datastore
    protected void addData(String var, String value){
        DataStore store = DataStore.findStore(var);
        if (store != null){
            if (!store.addData(value)){
                dbgPrint("Unable to parse the input");
            }
        }else{
            dbgPrint("no matching variable stored for \"" + var + "\"");
        }
    }

    protected double getData(String var, Method... methods) {

        DataStore store = DataStore.findStore(var);
        double d = 0;

        if (store != null) {
            // for (int i = 0; i < methods.length; i++)
            //     System.out.println("methods[" + i + "]=" + methods[i]);
            // System.out.println("store=" + store + ",var=" + var + ",methods=" + methods);
            synchronized (this) {
                d = store.getData(var, methods);
            }
        }
        else {
            dbgPrint("no matching variable stored for \"" + var + "\"");
        }

        return d;
    }
    
    protected boolean isEndTime() {
        return currLocTime.isEndTime();
    }

    protected void progressTime(int timeOffset) {
        currLocTime.progressTime(timeOffset);
    }

    protected Date getTime() {
        return currLocTime.getTime();
    }

    protected double[] getLocation() {
        return currLocTime.getLocation();
    }

    protected int getOmega() {
        return omega;
    }

    protected double getTau() {
        return tau;
    }
}
