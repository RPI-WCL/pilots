package pilots.runtime;

import java.net.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.impl.Arguments;

import pilots.Version;
import pilots.runtime.*;


public class PilotsRuntime {
    private static Logger LOGGER = Logger.getLogger(PilotsRuntime.class.getName());

    private static final int DEFAULT_INPUT_PORT = 8888;
    private static final int DEFAULT_OMEGA = 1;
    private static final double DEFAULT_TAU = 0.8;

    private WriterManager writerManager;
    private Namespace opts;
    private int omega;
    private double tau;
    protected DateFormat dateFormat;

    CurrentLocationTimeService currLocTime;

    // newly added for animation effect for the simulation mode
    private boolean animation;
    private double timeSpeed;
    private Calendar prevDate;

    public PilotsRuntime(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("PilotsRuntime").build()
            .defaultHelp(true)
            .description("PILOTS application runtime");
        parser.addArgument("-c", "--current-loctime")
            .help("Full class name for current location time service");
        parser.addArgument("-r", "--time-range")
            .help("Time range to run in simulation mode");
        parser.addArgument("-s", "--time-speed")
            .help("Rate of time progression in simulation mode");
        parser.addArgument("-p", "--input-port")
            .type(Integer.class)
            .setDefault(DEFAULT_INPUT_PORT)
            .help("Data input port");
        parser.addArgument("-o", "--outputs")
            .nargs("*")
            .help("List of outputs in 'host:port' format");
        parser.addArgument("-m", "--omega")
            .type(Double.class)
            .setDefault(DEFAULT_OMEGA)
            .help("Omega parameter");
        parser.addArgument("-t", "--tau")
            .type(Double.class)
            .setDefault(DEFAULT_TAU)
            .help("Tau parameter");        

        try {
            opts = parser.parseArgs(args);
        } catch (ArgumentParserException ex) {
            parser.handleError(ex);
            System.exit(1);
        }

        writerManager = new WriterManager();
        omega = opts.get("omega");
        tau = opts.get("tau");

        dateFormat = new SimpleDateFormat(SpatioTempoData.datePattern);

        currLocTime = ServiceFactory.getCurrentLocationTime();

        animation = false;
        timeSpeed = 1.0;
        prevDate = null;

        String timeRange = opts.get("time-range");
        String timeSpeed = opts.get("time-speed");
        if ((timeRange != null) && (timeSpeed != null)) {
            if ((timeSpeed.charAt(0) != 'x') && (timeSpeed.charAt(0) != 'X')) {
                LOGGER.warning("ERROR: -DtimeSpeed format: \"x\" 1*DIGIT (e.g., x100)");
            }
            else {
                animation = true;
                this.timeSpeed = Double.parseDouble(timeSpeed.substring(1));
            }
        }

        int i = 0;
        for (String hostport : opts.<String> getList("outputs")) {
            writerManager.addHost(i++, hostport);
        }

        LOGGER.info("PILOTS Runtime v" + Version.ver + " initialized.");
    }

    protected void startServer() {
        DataReceiver.startServer(opts.get("input-port"));
    }

    protected void stopServer() {
        writerManager.closeAll();
        DataReceiver.stopServer();
    }

    protected void openOutput(int sockIndex, String... vars) {
        PrintWriter writer = writerManager.open(sockIndex);

        writer.print("#");
        for (int i = 0; i < vars.length; i++) {
            if (i == vars.length - 1)
                writer.println(vars[i]);
            else
                writer.print(vars[i] + ",");
        }
        writer.flush();
    }

    protected void closeOutput(int sockIndex) {
        writerManager.close(sockIndex);
    }

    protected void sendData(int sockIndex, double... values) {
        Date date = currLocTime.getTime();
        
        if (animation && (prevDate != null)) {
            long currTime = date.getTime();
            long prevTime = prevDate.getTime().getTime();
            long waitTime = (long)((currTime - prevTime) / timeSpeed);
            LOGGER.finer("sendData, curr=" + (currTime - prevTime) + ", " + waitTime);
            try {
                Thread.sleep(waitTime );
            } catch (InterruptedException ex) {
                LOGGER.severe(ex.toString());
            }
        }

        // write the value on the socket
        PrintWriter writer = writerManager.get(sockIndex);
        writer.print(":" + dateFormat.format(date) + ":");
        for (int i = 0; i < values.length; i++) {
            if (i == values.length - 1)
                writer.println(values[i]);
            else
                writer.print(values[i] + ",");
        }
        writer.flush();

        if (prevDate == null)
            prevDate = Calendar.getInstance();
        prevDate.setTime(date);
    }

    // addData adds a spatioTempoData into datastore
    protected void addData(String var, String value){
        DataStore store = DataStore.findStore(var);
        if (store != null){
            if (!store.addData(value)){
                LOGGER.warning("Unable to parse the input");
            }
        }else{
            LOGGER.warning("No matching variable stored for \"" + var + "\"");
        }
    }

    protected double getData(String var, Method... methods) {

        DataStore store = DataStore.findStore(var);
        double d = 0;

        if (store != null) {
            for (int i = 0; i < methods.length; i++)
                LOGGER.finest("methods[" + i + "]=" + methods[i]);
            LOGGER.finest("store=" + store + ",var=" + var + ",methods=" + methods);
            synchronized (this) {
                d = store.getData(var, methods);
            }
        }
        else {
            LOGGER.warning("No matching variable stored for \"" + var + "\"");
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
