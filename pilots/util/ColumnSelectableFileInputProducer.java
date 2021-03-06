package pilots.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;


public class ColumnSelectableFileInputProducer
{
    private static final String DEFAULT_TARGET_HOST = "localhost";
    private static final int DEFAULT_TARGET_PORT = 8888;
    private static final int DATA_SEND_INTERVAL_MS = 1000;
    private static final String DATE_PATTERN    = "yyyy-MM-dd HHmmssSSSZ";
    private static final String TIME_ZONE_ID = "America/New_York";

    private static final int DATA_FORMAT_NULL = -1;
    private static final int DATA_FORMAT_PILOTS = 0;
    private static final int DATA_FORMAT_CSV = 1;
    private static final int DATA_FORMAT_UNKNOWN = 100;

    private String filename;
    private Set<String> selectedVars;
    private Map<String, String> varAliases;
    private boolean passThroughFirstLine;
    private Socket sock;
    private OutputStream outputStream;
    private PrintWriter writer;
    private BufferedReader reader;
    private DateFormat dateFormat;
    private List<Integer> selectedColumnIndices;
    private boolean debug;
    private boolean sim;
    

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ColumnSelectableFileInputProducer [-Dsim] [-Ddebug] <csv file> <comma-separated list of variables> [ip:port]");
            return;
        }

        String host = DEFAULT_TARGET_HOST;
        int port = DEFAULT_TARGET_PORT;
        if (3 <= args.length) {
            int colon = args[2].indexOf(":");
            if (colon < 0) {
                System.err.println("Invalid ip:port format: " + args[2]);
                System.exit(1);
            }
            host = args[2].substring(0, colon);
            port = Integer.parseInt(args[2].substring(colon + 1));
        }

        ColumnSelectableFileInputProducer producer = new ColumnSelectableFileInputProducer(args[0], args[1], host, port);
        producer.start();
    }

    public ColumnSelectableFileInputProducer(String filename, String selectedVars, String host, int port) {
        this.filename = filename;
        this.selectedVars = new HashSet<>();
        this.varAliases = new HashMap<>();

        if (selectedVars.equals("all")) {
            // In case of "*", we pass through first line of the input file            
            this.passThroughFirstLine = true;
        } else {
            // Otherwise, we parse the selected variables list
            this.passThroughFirstLine = false;            
            for (String var : selectedVars.split(",")) {
                int colonIndex = 0;
                if (0 < (colonIndex = var.indexOf(':'))) {
                    // An alias can be specified for a variable in the format "var:alias"
                    String varWithAlias = var.substring(0, colonIndex);
                    String alias = var.substring(colonIndex + 1);
                    this.varAliases.put(varWithAlias, alias);
                    var = varWithAlias;
                }
                this.selectedVars.add(var);
            }
        }
        
        TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE_ID));
        this.dateFormat = new SimpleDateFormat(DATE_PATTERN);
        this.selectedColumnIndices = new ArrayList<>();
        
        this.sim = System.getProperty("sim") != null;                
        this.debug = System.getProperty("debug") != null;
        
        try {
            this.reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        if (!debug) {
            try {
                this.sock = new Socket(host, port);
                this.outputStream = sock.getOutputStream();
                this.writer = new PrintWriter(outputStream);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String addTimeComponent(String csv) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        String tempoData = ":" + dateFormat.format(cal.getTime()) + ":" + csv;
        return tempoData;
    }

    public void sendData(String str) {
        System.out.println(str);        
        if (!debug) {
            writer.println(str);
            writer.flush();            
        }
    }

    public String getSelectedColumns(String csv) {
        String[] data = csv.split(",");                
        String str = "";
        for (int i = 0 ; i < selectedColumnIndices.size(); i++) {
            if (i == selectedColumnIndices.size() - 1)
                str += data[selectedColumnIndices.get(i)];
            else
                str += data[selectedColumnIndices.get(i)] + ",";
        }
        return str;
    }
    
    public void start() {
        try {
            String line = reader.readLine();
            String[] vars = line.substring(1).split(","); // skip the first '#'
            List<String> selectedVarsList = new ArrayList<>();
            for (int i = 0; i < vars.length; i++) {
                if (passThroughFirstLine || selectedVars.contains(vars[i])) {
                    selectedColumnIndices.add(i);
                    String var = varAliases.getOrDefault(vars[i], vars[i]);
                    selectedVarsList.add(var);
                }
            }
            sendData("#" + String.join(",", selectedVarsList));

            int dataFormat = DATA_FORMAT_NULL;
            Pattern pilotsDataPattern = Pattern.compile("([^:]*):([^:]*):([^:]*)");
            Pattern csvPattern = Pattern.compile("(([^,]*),)*([^,]*)");
            Matcher m = null;
            // Regex test data
            // Matcher m = p.matcher("42.748,-73.802~41.476,-75.483:2019-06-06 013008015-0400:162,246");
            // Matcher m = p.matcher("42.748,-73.802~41.476,-75.483::162,246");
            // Matcher m = p.matcher(":2019-06-06 013008015-0400:162,246");
            String data = null, selectedColumns = null;
            
            while ((line = reader.readLine()) != null) {
                switch (dataFormat) {
                case DATA_FORMAT_NULL:
                    m  = pilotsDataPattern.matcher(line);
                    if (m.find()) {
                        // System.out.println("DATA_FORMAT_PILOTS: " + m.group(0));
                        dataFormat = DATA_FORMAT_PILOTS;
                        selectedColumns = getSelectedColumns(m.group(3));
                        if (sim) {
                            data = m.group(1) + ":" + m.group(2) + ":" + selectedColumns;
                        } else {
                            // ignore time component
                            data = m.group(1) + addTimeComponent(selectedColumns);
                        }
                    } else {
                        m = csvPattern.matcher(line);                        
                        if (m.find()) {
                            // System.out.println("DATA_FORMAT_CSV: " + m.group(0));
                            dataFormat = DATA_FORMAT_CSV;
                            data = addTimeComponent(getSelectedColumns(line));                            
                        } else {
                            dataFormat = DATA_FORMAT_UNKNOWN;
                        }
                    }
                    break;
                    
                case DATA_FORMAT_PILOTS:
                    m = pilotsDataPattern.matcher(line);
                    data = "";
                    if (m.find()) {
                        selectedColumns = getSelectedColumns(m.group(3));
                        if (sim) {
                            data = m.group(1) + ":" + m.group(2) + ":" + selectedColumns;
                        } else {
                            data = m.group(1) + addTimeComponent(selectedColumns);
                        }
                    }
                    break;
                    
                case DATA_FORMAT_CSV:
                    data = addTimeComponent(getSelectedColumns(line));
                    break;

                default:
                    System.err.println("Unknown data format: " + line);
                    System.exit(1);
                    break;
                }
                   
                sendData(data);

                if (!sim)
                    Thread.sleep(DATA_SEND_INTERVAL_MS);
            }

            if (!debug) {
                outputStream.close();
                writer.close();
                reader.close();
                sock.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
