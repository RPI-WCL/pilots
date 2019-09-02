import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.text.*;
import java.net.Socket;
import pilots.runtime.*;
import pilots.runtime.errsig.*;

public class AoaSpeedCheck1 extends PilotsRuntime {
    private static Logger LOGGER = Logger.getLogger(AoaSpeedCheck1.class.getName());
    private int currentMode;
    private int currentModeCount;
    private int time; // msec
    private long[] nextSendTimes;

    private static final double V_CRUISE = 110;
    private static final double SPEED_NORMAL_L = -0.1*V_CRUISE;
    private static final double SPEED_NORMAL_H = 0.33*V_CRUISE;
    private static final double SPEED_PITOT_L = 0.34*V_CRUISE;
    private static final double SPEED_PITOT_H = 16.10*V_CRUISE;
    private static final double SPEED_GPS_L = -13.83*V_CRUISE;
    private static final double SPEED_GPS_H = -0.67*V_CRUISE;
    private static final double SPEED_GPS_PITOT_L = -0.66*V_CRUISE;
    private static final double SPEED_GPS_PITOT_H = -0.1*V_CRUISE;
    private static final double AOA_NORMAL = 0.10*V_CRUISE;
    private static final double PI = 3.141592;
    private static final double MPS2KNOT = 1.94384;
    private static final double K1 = 2.90094;
    private static final double K2 = 0.00024;
    private static final double K3 = 0.00108;

    public AoaSpeedCheck1(String args[]) {
        super(args);

        time = 0;
        nextSendTimes = new long[1];
        Arrays.fill(nextSendTimes, 0L);
    }

    public void produceOutputs() {
        try {
            openOutput(0, "va", "vg", "aoa", "mode");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        final int interval = 1000;
        Map<String, Double> data = new HashMap<>();
        while (!isEndTime()) {
            // Inputs
            data.put("va", getData("va", new Method(Method.CLOSEST, "t")));
            data.put("vg", getData("vg", new Method(Method.CLOSEST, "t")));
            data.put("vw", getData("vw", new Method(Method.CLOSEST, "t")));
            data.put("aa", getData("aa", new Method(Method.CLOSEST, "t")));
            data.put("ag", getData("ag", new Method(Method.CLOSEST, "t")));
            data.put("aw", getData("aw", new Method(Method.CLOSEST, "t")));
            data.put("aoa", getData("aoa", new Method(Method.CLOSEST, "t")));
            LOGGER.fine("Inputs: " + "va=" + data.get("va") + ", " + "vg=" + data.get("vg") + ", " + "vw=" + data.get("vw") + ", " + "aa=" + data.get("aa") + ", " + "ag=" + data.get("ag") + ", " + "aw=" + data.get("aw") + ", " + "aoa=" + data.get("aoa"));

            // Errors computation
            data.put("e1", data.get("vg")-Math.sqrt(Math.pow(data.get("va"), 2)+Math.pow(data.get("vw"), 2)+2*data.get("va")*data.get("vw")*Math.cos((PI/180)*(data.get("aw")-data.get("aa")))));
            data.put("e2", data.get("va")-MPS2KNOT*Math.sqrt(K1/(K2*data.get("aoa")+K3)));
            LOGGER.fine("Errors: " + "e1=" + data.get("e1") + ", " + "e2=" + data.get("e2"));

            // Error detection
            int mode = -1;
            if (SPEED_NORMAL_L<data.get("e1") && data.get("e1")<SPEED_NORMAL_H && Math.abs(data.get("e2"))<AOA_NORMAL) {
                mode = 0;	// "Normal"
            } else if (SPEED_NORMAL_L<data.get("e1") && data.get("e1")<SPEED_NORMAL_H && AOA_NORMAL<=Math.abs(data.get("e2"))) {
                mode = 1;	// "AoA sensor failure"
            } else if (SPEED_PITOT_L<data.get("e1") && data.get("e1")<SPEED_PITOT_H) {
                mode = 9;	// "Pitot tube + (AoA sensor) failure"
            } else if (SPEED_GPS_L<data.get("e1") && data.get("e1")<SPEED_GPS_H && Math.abs(data.get("e2"))<AOA_NORMAL) {
                mode = 4;	// "GPS failure"
            } else if (SPEED_GPS_L<data.get("e1") && data.get("e1")<SPEED_GPS_H && AOA_NORMAL<=Math.abs(data.get("e2"))) {
                mode = 5;	// "GPS failure + AoA sensor failure"
            } else if (SPEED_GPS_PITOT_L<data.get("e1") && data.get("e1")<SPEED_GPS_PITOT_H) {
                mode = 11;	// "GPS + Pitot tube + (AoA sensor) failure"
            }
            LOGGER.fine("Detected: mode=" + mode);

            // Correct data estimation
            switch (mode) {
            case 1:
                data.put("aoa", (1/K2)*((Math.pow(MPS2KNOT, 2)/Math.pow(data.get("va"), 2))*K1-K3));
                LOGGER.fine("Estimated: " + "aoa=" + data.get("aoa"));
                break;
            case 4:
                data.put("vg", Math.sqrt(Math.pow(data.get("va"), 2)+Math.pow(data.get("vw"), 2)+2*data.get("va")*data.get("vw")*Math.cos((PI/180)*(data.get("aw")-data.get("aa")))));
                LOGGER.fine("Estimated: " + "vg=" + data.get("vg"));
                break;
            case 5:
                data.put("vg", Math.sqrt(Math.pow(data.get("va"), 2)+Math.pow(data.get("vw"), 2)+2*data.get("va")*data.get("vw")*Math.cos((PI/180)*(data.get("aw")-data.get("aa")))));
                data.put("aoa", (1/K2)*((Math.pow(MPS2KNOT, 2)/Math.pow(data.get("va"), 2))*K1-K3));
                LOGGER.fine("Estimated: " + "vg=" + data.get("vg") + ", " + "aoa=" + data.get("aoa"));
                break;
            case 9:
                data.put("va", Math.sqrt(Math.pow(data.get("vg"), 2)+Math.pow(data.get("vw"), 2)-2*data.get("vg")*data.get("vw")*Math.cos((PI/180)*(data.get("ag")-data.get("aw")))));
                data.put("aoa", (1/K2)*((Math.pow(MPS2KNOT, 2)/Math.pow(data.get("va"), 2))*K1-K3));
                LOGGER.fine("Estimated: " + "va=" + data.get("va") + ", " + "aoa=" + data.get("aoa"));
                break;
            default:
                break;
            }

            // Data transfer
            Date now = getTime();
            try {
                sendData(0, data.get("va"), data.get("vg"), data.get("aoa"), mode);
                LOGGER.info("Outputs: " + now + " " + "va=" + data.get("va") + " " + "vg=" + data.get("vg") + " " + "aoa=" + data.get("aoa") + " " + "mode=" + mode + " ");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            time += interval;
            progressTime(interval);
        }

        LOGGER.info("Finished at " + getTime());
    }

    public static void main(String[] args) {
        AoaSpeedCheck1 app = new AoaSpeedCheck1(args);
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
