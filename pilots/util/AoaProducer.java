package pilots.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import pilots.runtime.SpatioTempoData;

public class AoaProducer
{
    class ValuePair {
        double v1;
        double v2;

        ValuePair(double v1, double v2) {
            this.v1 = v1;
            this.v2 = v2; 
        }
    }

    private static final String TARGET_HOST = "localhost";
    private static final int TARGET_PORT = 8888;
    private static final int LISTEN_PORT = 7777;    // to accept commands
    private static final int INTERVAL_MS = 1000;
    private static final String DATE_PATTERN    = "yyyy-MM-dd HHmmssSSSZ";
    private static final String TIME_ZONE_ID = "America/New_York";
    private static final double meanLevelAoa = 1.00;
    private static final double meanErrorAoa = 15.00;
    private static final double sigmaAoa = 0.30;
    private static final double sigmaAirspeed = 0.15;

    private Socket pilotsSock;
    private PrintWriter pilotsWriter;
    private ServerSocket serverSock;
    private Random rand;
    private int dataIndex = 0;
    private DateFormat dateFormat;
    private boolean debug = false;
    private List<ValuePair> aoaAirspeedList;
    

    public static void main(String[] args) {
        AoaProducer producer = new AoaProducer(0 < args.length && args[0].equals("debug"), args[1]);
        producer.startSend();
    }


    public List<ValuePair> readCsv(String csvFile) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<ValuePair> list = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                ValuePair pair = new ValuePair(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
                list.add(pair);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }
    
    public AoaProducer(boolean debug, String csvFile) {
        this.debug = debug;
        rand = new Random();
        TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE_ID));
        dateFormat = new SimpleDateFormat(DATE_PATTERN);
        aoaAirspeedList = readCsv(csvFile);
        
        try {
            if (!debug) {
                pilotsSock = new Socket(TARGET_HOST, TARGET_PORT);
                pilotsWriter = new PrintWriter(pilotsSock.getOutputStream());
            }

            serverSock = new ServerSocket(7777);
            serverSock.setSoTimeout(INTERVAL_MS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public double getAirspeed(double aoa) {
        double MPS_TO_KNOT = 1.94384;
        double K1 = 1970.676, K2 = 0.215, K3 = 0.456;
        return MPS_TO_KNOT * Math.sqrt(K1 / (K2 * aoa + K3));
    }


    public String createPilotsData(double[] values) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        String[] valueStrs = Arrays.stream(values).mapToObj(String::valueOf).toArray(String[]::new);
        String pilotsData = ":" + dateFormat.format(cal.getTime()) + ":" + String.join(",", valueStrs);
        
        return pilotsData;
    }

    
    public void startSend() {
        Socket cmdSock = null;
        boolean loop = true, error = false;

        if (!debug) {
            pilotsWriter.println("#aoa,v");
            pilotsWriter.flush();
        }

        while (loop) {
            ValuePair pair = aoaAirspeedList.get(dataIndex);
            double aoa = pair.v1;
            double v = pair.v2;
            if (++dataIndex == aoaAirspeedList.size())
                dataIndex = 0;

            if (error) {
                aoa = Math.abs(rand.nextGaussian() * sigmaAoa + meanErrorAoa);
            }

            String data = createPilotsData(new double[]{aoa, v});
            System.out.println("erorr=" + error + ", data=" + data);
            
            if (!debug) {
                pilotsWriter.println(data);
                pilotsWriter.flush();
            }
            
            try {
                cmdSock = serverSock.accept();
            } catch (SocketTimeoutException ex) {
                cmdSock = null;
            } catch (IOException ex) {
                break;
            }

            try {
                if (cmdSock != null) {
                    InputStream in = cmdSock.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String cmd = reader.readLine();
                    if (cmd != null) {
                        System.out.println("Command received: " + cmd);
                        switch (cmd) {
                        case "bye":
                            loop = false;
                            break;
                        case "error":
                            error = true;
                            break;
                        case "noerror":
                            error = false;
                            break;                            
                        default:
                            break;
                        }
                    }
                    cmdSock.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Bye!");        
    }
}
