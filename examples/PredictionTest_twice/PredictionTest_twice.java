import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.text.*;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class PredictionTest_twice extends PilotsRuntime {
    private static Logger LOGGER = Logger.getLogger(PredictionTest_twice.class.getName());
    private int currentMode;
    private int currentModeCount;
    private int time; // msec
    private long[] nextSendTimes;


    public PredictionTest_twice(String args[]) {
        super(args);

        time = 0;
        nextSendTimes = new long[1];
        Arrays.fill(nextSendTimes, 0L);
    }

    public void produceOutputs() {
        try {
            openOutput(0, "o");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        final int interval = 1000;
        Map<String, Double> data = new HashMap<>();
        while (!isEndTime()) {
            // Inputs
            data.put("a", getData("a", new Method(Method.CLOSEST, "t")));
            data.put("c", getData("c", new Method(Method.CLOSEST, "t")));
            data.put("b", getData("b", new Method(Method.PREDICT, "linear_regression_twice", "a")));
            LOGGER.fine("Inputs: " + "a=" + data.get("a") + ", " + "c=" + data.get("c") + ", " + "b=" + data.get("b"));

            // Outputs computation
            data.put("o", data.get("c")-data.get("b"));

            // Data transfer
            Date now = getTime();
            try {
                sendData(0, data.get("o"));
                LOGGER.info("Outputs: " + now + " " + "o=" + data.get("o") + " ");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            time += interval;
            progressTime(interval);
        }

        LOGGER.info("Finished at " + getTime());
    }

    public static void main(String[] args) {
        PredictionTest_twice app = new PredictionTest_twice(args);
        app.startServer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Hit ENTER key after running input producer(s).");
        try {
            reader.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.produceOutputs();
    }
}
