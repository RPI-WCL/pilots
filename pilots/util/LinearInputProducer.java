package pilots.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

import pilots.runtime.SpatioTempoData;


public class LinearInputProducer extends Thread {
    private static final int DATA_SEND_FREQUENCY = 1000; // [ms]
    private static final String TARGET_HOST = "localhost";
    private static final int TARGET_PORT = 8888;

    private static int iteration;

    private String varName;
    private int value;
    private int increment;
    private int randRange; // [%]
    private Random rand;

    // socket communication
    private Socket sock = null;
    private OutputStream outputStream = null;
    private PrintWriter printWriter = null;


    public static void main(String[] args) {
        if (args.length < 5) {
            System.err.println("Usage: ./java pilots.util.LinearInputProducer <iteration> <varname> <init value> <increment value> <rand[%]>");
            return;
        }

        LinearInputProducer client = 
            new LinearInputProducer(Integer.parseInt(args[0]), /* iteration */
                                     args[1], /* variable name */
                                     Integer.parseInt(args[2]), /* init value */
                                     Integer.parseInt(args[3]), /* increment value */
                                     Integer.parseInt(args[4])  /* random range */
           );
        client.start();
    }
    
    LinearInputProducer(int iteration, String varName, int value, int increment, int randRange) {
        this.iteration = iteration;
        this.varName = varName;
        this.value = value;
        this.increment = increment;
        this.randRange = randRange;
        this.rand = new Random();

        try {
            sock = new Socket(TARGET_HOST, TARGET_PORT);
            outputStream = sock.getOutputStream();
            printWriter = new PrintWriter(outputStream);
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public String createStData(int value) {
        TimeZone.setDefault(TimeZone.getTimeZone(SpatioTempoData.timeZoneID));

        Date date = new Date();
        DateFormat df = new SimpleDateFormat(SpatioTempoData.datePattern);
        String stData = new String(":" + df.format(date) + ":" + value);

        return stData;
    }

    private void sockWrite(String str) {
        if (printWriter == null) {
            System.err.println("PrintWriter is not initialized");
            return;
        }

        System.out.println(str);

        // add '\n' here at the end of the string
        printWriter.println(str);
        printWriter.flush();
    }

    private void sockClose() {
        try {
            outputStream.close();
            printWriter.close();
            sock.close();
        }
        catch (IOException ex) {
            System.err.println(ex);
        }        
    }

    public void run() {
        sockWrite("#" + varName);

        boolean infiniteLoop = (iteration < 0);

        for (int i = 0; infiniteLoop || (i < iteration); i++) {
            value += increment;

            sockWrite(createStData(value));
            
            int randWidth = (int)(DATA_SEND_FREQUENCY * (double)randRange / 100);
            int randDelay = 0;
            if (0 < randWidth) {
                randDelay = rand.nextInt(randWidth); // positive delay
            }

            try {
                Thread.sleep(DATA_SEND_FREQUENCY + randDelay);
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }

        sockClose();
    }
}
