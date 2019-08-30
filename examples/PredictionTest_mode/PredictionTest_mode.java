import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.text.*;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class PredictionTest_mode extends PilotsRuntime {
    private static Logger LOGGER = Logger.getLogger(PredictionTest_mode.class.getName());
    private int currentMode;
    private int currentModeCount;
    private int time; // msec
    private long[] nextSendTimes;


    public PredictionTest_mode(String args[]) {
        super(args);

        time = 0;
        nextSendTimes = new long[1];
        Arrays.fill(nextSendTimes, 0L);
    }

    public void produceOutputs() {
        try {
            openOutput(0, "mode");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        final int interval = 1000;
        Map<String, Double> data = new HashMap<>();
        while (!isEndTime()) {
            // Inputs
            data.put("a", getData("a", new Method(Method.CLOSEST, "t")));
            data.put("b", getData("b", new Method(Method.CLOSEST, "t")));
            data.put("mode", getData("mode", new Method(Method.PREDICT, "bayes_prediction_test", "a", "b")));
            LOGGER.fine("Inputs: " + "a=" + data.get("a") + ", " + "b=" + data.get("b") + ", " + "mode=" + data.get("mode"));

            // Data transfer
            Date now = getTime();
            try {
                sendData(0, data.get("mode"));
                LOGGER.info("Outputs: " + now + " " + "mode=" + data.get("mode") + " ");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            time += interval;
            progressTime(interval);
        }

        LOGGER.info("Finished at " + getTime());
    }

    public static void main(String[] args) {
        PredictionTest_mode app = new PredictionTest_mode(args);
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
