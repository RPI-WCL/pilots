package pilots.util;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.time.*;
import org.jfree.ui.*;
import org.jfree.data.*;
import org.jfree.data.general.*;
import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.constructor.*;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.impl.Arguments;

import pilots.runtime.*;


public class MultiChartsServer {
    private static Logger LOGGER = Logger.getLogger(MultiChartsServer.class.getName());    

    private final static Color BLUE     = new Color(49, 139, 223);
    private final static Color RED      = new Color(240, 64, 72);
    private final static Color GREEN    = new Color(34, 177, 76);
    private final static Color YELLOW   = new Color(232, 220, 0);
    private final static Color ORANGE   = new Color(255, 127, 39);
    private final static Color PURPLE   = new Color(106, 78, 161);
    private final static Color TEAL     = new Color(0, 143, 149);
    private final static Color PINK     = new Color(250, 114, 104);
    private final static Color TURQOISE = new Color(0, 162, 232);
    private final static Color[] defaultColors = new Color[]{
        BLUE, RED, GREEN, YELLOW, ORANGE, PURPLE, TEAL, PINK, TURQOISE};
    private final static Map<String, Color> colorMap = new HashMap<>() {{
            put("blue", BLUE);
            put("red", RED);            
            put("green", GREEN);
            put("yellow", YELLOW);
            put("orange", ORANGE);            
            put("purple", PURPLE);
            put("teal", TEAL);
            put("pink", PINK);
            put("turqoise", TURQOISE);
            put("cyan", Color.cyan);
            put("darkgray", Color.darkGray);
            put("black", Color.black);
            put("lightgray", Color.lightGray);
            put("magenta", Color.magenta);
            put("white", Color.white);
        }};
    private final static int DEFAULT_INPUT_PORT = 9999;
    private final static String DEFAULT_CONFIG_FILE = "./config.yaml";
    private final static int PANELS_LAYOUT_ROWS = 3;
    private final static int H = 250;
    private final static int W = 500;

    private int port;
    private java.util.List<Map<String, String>> configList;
    private Map<String, TimeSeries> varSeriesMap;
    private Map<String, JFreeChart> varChartMap;    
    private JFrame frame;
    
    class TimeSeriesPanel extends JPanel {

        // Use maximumItemAge for history effect
//     /**
//      * Sets the number of time units in the 'history' for the series.  This
//      * provides one mechanism for automatically dropping old data from the
//      * time series. For example, if a series contains daily data, you might set
//      * the history count to 30.  Then, when you add a new data item, all data
//      * items more than 30 days older than the latest value are automatically
//      * dropped from the series.
//      *
//      * @param periods  the number of time periods.
//      *
//      * @see #getMaximumItemAge()
//      */
// public void setMaximumItemAge(long periods) {
        
        // TimeSeries timeSeries;
        TimeSeriesCollection timeSeriesCollection;
        JFreeChart chart;        
        
        TimeSeriesPanel(String title, String xAxisLegend, String yAxisLegend) {
            // timeSeries = new TimeSeries("Random Test");
            timeSeriesCollection = new TimeSeriesCollection();
            chart = ChartFactory.createTimeSeriesChart(title, xAxisLegend, yAxisLegend, 
                                                       timeSeriesCollection,
                                                       true, true, true);
            this.add(new ChartPanel(chart, W, H, W, H, W, H,
                                    false, true, true, true, true, true));
        }
    }

    public MultiChartsServer(int port, String configFile) {
        this.port = port;
        this.configList = new ArrayList<>();
        this.varSeriesMap = new HashMap<>();
        this.varChartMap = new HashMap<>();        

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
            .getClassLoader()
            .getResourceAsStream(configFile);
        for (Object obj : yaml.loadAll(inputStream)) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>)(obj);
            configList.add(map);
        }

        initCharts();
    }

    private void initCharts() {
        frame = new JFrame("MultiChartServer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // layout
        int rows = PANELS_LAYOUT_ROWS;
        int columns = 1;
        if (configList.size() == 4) {
            // Special case
            rows = 2;
            columns = 2;
        } else if (configList.size() < PANELS_LAYOUT_ROWS) {
            rows = configList.size();
        } else if (PANELS_LAYOUT_ROWS < configList.size()) {
            columns = (1 + (configList.size() / PANELS_LAYOUT_ROWS)) * PANELS_LAYOUT_ROWS;
        }
        frame.setLayout(new GridLayout(rows, columns));

        // configurations for each panel
        for (Map<String, String> map : configList) {
            TimeSeriesPanel panel = new TimeSeriesPanel(map.get("title"),
                                                        map.get("xAxis"),
                                                        map.get("yAxis"));
            XYPlot xyPlot = panel.chart.getXYPlot();
            xyPlot.setBackgroundPaint(Color.white);
            xyPlot.setDomainGridlinePaint(Color.lightGray);
            xyPlot.setRangeGridlinePaint(Color.lightGray);

            // TODO: configure ranges
            ValueAxis xAxis = xyPlot.getDomainAxis();
            xAxis.setAutoRange(true);
            ValueAxis yAxis = xyPlot.getRangeAxis();
            yAxis.setAutoRange(true);
            
            xyPlot.setRenderer(new XYLineAndShapeRenderer());
            xyPlot.setForegroundAlpha(0.50f);
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();

            // series-wise configurations            
            String[] vars = map.get("vars").split(",");
            String[] colors = map.get("colors").split(",");
            String[] legends = null;
            if (map.get("legends") != null) {
                legends = map.get("legends").split(",");
            }
            for (int i = 0; i < vars.length; i++) {
                TimeSeries series = new TimeSeries(legends == null ? vars[i] : legends[i], null, null);
                varSeriesMap.put(vars[i], series);
                varChartMap.put(vars[i], panel.chart);
                panel.timeSeriesCollection.addSeries(series);
                renderer.setSeriesPaint(i, colorMap.get(colors[i]));
                renderer.setSeriesStroke(i, new BasicStroke(2.0f)); // stroke size
                // renderer.setSeriesShape(1, new Ellipse2D.Double(-1.0, -1.0, 2.0, 2.0)); // marker size
            }

            frame.add(panel);            
        }
        
        frame.pack();
        frame.setVisible(true);        
    }
    

    public void startServer() {
        try {
            ServerSocket serverSock = new ServerSocket(port);
            Map<Integer, TimeSeries> indexSeriesMap = new HashMap<>();
            Map<Integer, JFreeChart> indexChartMap = new HashMap<>();            

            while (true) {
                System.out.println("Started listening to port:" + port);
                Socket newSock = serverSock.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(newSock.getInputStream()));
                String str = null;
                
                System.out.println("Connection accepted");
                
                try {
	                while ((str = in.readLine()) != null) {
	                    if (str.length() == 0) {
	                        System.out.println("EOS marker received");
	                        break;
	                    }
	                    else if (str.charAt(0) == '#') {
	                        System.out.println("first line received: " + str);
	                        String[] vars = str.substring(1).split(",");
                            for (int i = 0; i < vars.length; i++) {
                                // i -> var -> series
                                indexSeriesMap.put(i, varSeriesMap.get(vars[i]));
                                // i -> var -> chart
                                indexChartMap.put(i, varChartMap.get(vars[i]));
                            }
	                    }
	                    else {
                            // update data
	                        SpatioTempoData stData = new SpatioTempoData(str);
	                        Date[] times = stData.getTimes();
	                        java.util.List<Double> values = stData.getValues();
                            for (int i = 0; i < values.size(); i++) {
                                TimeSeries series = indexSeriesMap.get(i);
                                try {
                                    series.add(new Millisecond(times[0]), values.get(i));
                                } catch(SeriesException ex) {
                                    ex.printStackTrace();
                                }
                                indexChartMap.get(i).fireChartChanged();
                            }
                            
	                        // just print the value
	                        System.out.println(str);
	                    }
	                }
	            } catch (SocketException ex) {
	                System.out.println("Connectin reset by peer");
				} finally {
	                System.out.println("Finished receiving time series");
		            in.close();
	                newSock.close();	
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("PilotsRuntime").build()
            .defaultHelp(true)
            .description("Server to render multiple charts");
        parser.addArgument("-p", "--port")
            .type(Integer.class)
            .setDefault(DEFAULT_INPUT_PORT)
            .help("Data input port");
        parser.addArgument("-c", "--config_file")
            .setDefault(DEFAULT_CONFIG_FILE)
            .help("Configuration file in yaml format");

        Namespace opts = null;  
        try {
            opts = parser.parseArgs(args);
        } catch (ArgumentParserException ex) {
            parser.handleError(ex);
            System.exit(1);
        }

        MultiChartsServer server = new MultiChartsServer(opts.get("port"),
                                                         opts.get("config_file"));
        server.startServer();        
    }
}
