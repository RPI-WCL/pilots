import java.util.*;
import java.util.logging.*;
import java.text.*;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class Squared extends PilotsRuntime {
    private static Logger LOGGER = Logger.getLogger(Squared.class.getName());
    private int currentMode;
    private int currentModeCount;
    private Timer timer;
    private long[] nextSendTimes;


    public Squared(String args[]) {
        super(args);

        timer = new Timer();
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
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                // Inputs
                data.put("x", getData("x", new Method(Method.CLOSEST, "t")));
                LOGGER.fine("Inputs: " + "x=" + data.get("x"));

                // Outputs computation
                data.put("o", data.get("x")*data.get("x"));

                // Data transfer
                Date now = getTime();
                try {
                    sendData(0, data.get("o"));
                    LOGGER.info("Outputs: " + now + " " + "o=" + data.get("o") + " ");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, 0, interval);
    }

    public static void main(String[] args) {
        Squared app = new Squared(args);
        app.startServer();
        app.produceOutputs();
    }
}
