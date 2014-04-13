package pilots.util;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.time.*;
import org.jfree.ui.*;
import org.jfree.data.*;
import org.jfree.data.general.*;

import pilots.runtime.*;

public class ChartServer {
    private int port_;
    private TimeSeries[] timeSeries_;
    private JFreeChart chart_;
    final static int AVAILABLE_COLORS = 13;
    private Color[] defaultColorMap;
    private HashMap<String, Color> colorMap;


    public ChartServer( int port ) {
        port_ = port;
        // timeSeries_ = new TimeSeries("Series 1",
        //                              "timeSeries domain",
        //                              "timeSeries range" );
        // chart_ = ChartFactory.createTimeSeriesChart ("PilotsChartServer",
        //                                              "domain",
        //                                              "range",
        //                                              new TimeSeriesCollection( timeSeries_ ),
        //                                              true, true, true);
        defaultColorMap = new Color[AVAILABLE_COLORS];
        defaultColorMap[0] = Color.red;
        defaultColorMap[1] = Color.green;
        defaultColorMap[2] = Color.cyan;
        defaultColorMap[3] = Color.darkGray;
        defaultColorMap[4] = Color.gray;
        defaultColorMap[5] = Color.black;
        defaultColorMap[6] = Color.lightGray;
        defaultColorMap[7] = Color.magenta;
        defaultColorMap[8] = Color.orange;
        defaultColorMap[9] = Color.pink;
        defaultColorMap[10] = Color.blue;
        defaultColorMap[11] = Color.white;
        defaultColorMap[12] = Color.yellow;

        colorMap = new HashMap<String, Color>();
        colorMap.put("blue", Color.blue);
        colorMap.put("green", Color.green);
        colorMap.put("cyan", Color.cyan);
        colorMap.put("darkGray", Color.darkGray);
        colorMap.put("gray", Color.gray);
        colorMap.put("black", Color.black);
        colorMap.put("lightGray", Color.lightGray);
        colorMap.put("magenta", Color.magenta);
        colorMap.put("orange", Color.orange);
        colorMap.put("pink", Color.pink);
        colorMap.put("red", Color.red);
        colorMap.put("white", Color.white);
        colorMap.put("yellow", Color.yellow);
    }

    protected void initChart( String[] varNames ) {
        // varNames[0] does not have a value

        timeSeries_ = new TimeSeries[ varNames.length - 1 ];
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        

        for (int i = 1; i < varNames.length; i++) {
            timeSeries_[i - 1] = new TimeSeries( varNames[i], 
                                                 "timeSeries domain",
                                                 "timeSeries range" );
            timeSeriesCollection.addSeries( timeSeries_[i - 1] );
        }
        chart_ = ChartFactory.createTimeSeriesChart ("PilotsChartServer",
                                                     "Time",
                                                     "Value",
                                                     timeSeriesCollection,
                                                     true, true, true);
    }
        

    protected void addChartData( Date date, Vector<Double> values ) {
        try {
            for (int i = 0; i < values.size(); i++) {
                timeSeries_[i].add( new Millisecond( date ), values.get( i ) );
            }
        } catch (SeriesException ex) {
            ex.printStackTrace();
        }
        
        chart_.fireChartChanged();
    }

    public void startServer() {
        try {
            ServerSocket serverSock = new ServerSocket( port_ );
            System.out.println( "Started listening to port:" + port_ );

            Socket newSock = serverSock.accept();
            BufferedReader in = new BufferedReader( new InputStreamReader( newSock.getInputStream() ) );
            String str = null;

            System.out.println( "Connection accepted" );

            while ( (str = in.readLine() ) != null ) {
                if ( str.length() == 0 ) {
                    System.out.println( "EOS marker received" );
                    break;
                }
                else if ( str.charAt(0) == '#' ) {
                    System.out.println( "first line received: " + str );
                    String[] varNames = str.split( "[#, ]" );

                    initChart( varNames ); // this will create a chart
                    configChart( varNames );
                    drawChart();
                }
                else {
                    // just print the value
                    SpatioTempoData stData = new SpatioTempoData( str );
                    Date[] times = stData.getTimes();
                    Vector<Double> values = stData.getValues();
                    addChartData( times[0], values );
                    
                    System.out.println( str );
                }

            }

            System.out.println( "Server is going to exit" );

            in.close();
            newSock.close();

        } catch (Exception ex ) {
            ex.printStackTrace();
        }
    }
        
    public void configChart( String[] varNames ) {
        XYPlot xyPlot = chart_.getXYPlot();
        xyPlot.setRenderer( new XYLineAndShapeRenderer() );

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();

        // setting strokes
        for (int i = 0; i < varNames.length; i++)
            renderer.setSeriesStroke( i, new BasicStroke( 2.0f ) );

        // configuring colors
        String seriesColors = System.getProperty( "seriesColors" );
        if (seriesColors != null) {
            // colors are specified by the user
            String[] colors = seriesColors.split( "," );
            for (int i = 0; i < colors.length; i++)
                renderer.setSeriesPaint( i, colorMap.get( colors[i] ) );
        }
        else {
            // configure default colors
            for (int i = 0; i < varNames.length; i++)
                renderer.setSeriesPaint( i, defaultColorMap[i % AVAILABLE_COLORS] );
        }

        // cofiguring X-Axis
        ValueAxis xAxis = xyPlot.getDomainAxis();

        String timeSpan = System.getProperty( "timeSpan" );
        String timeRange = System.getProperty( "timeRange" );
        timeRange = (timeSpan != null) ? timeSpan : timeRange;
        if (timeRange != null) {
            String[] timeStrs = timeRange.split( "~" );
            String datePattern = "yyyy-MM-dd HHmmssZ";
            DateFormat dateFormat = new SimpleDateFormat( datePattern );
            Date[] times = new Date[2];
            for (int i = 0; i < 2; i++) {
                if (timeStrs[i] != null) {
                    try {
                        times[i] = dateFormat.parse( timeStrs[i] );
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            xAxis.setRange( times[0].getTime(), times[1].getTime() );
        }
        else {
            xAxis.setAutoRange(true);            
        }

        // cofiguring Y-Axis
        ValueAxis yAxis = xyPlot.getRangeAxis();

        String valueRange = System.getProperty( "valueRange" );
        if (valueRange != null) {
            String[] valueStrs = valueRange.split( "~" );
            double[] values = new double[2];
            values[0] = Double.parseDouble( valueStrs[0] );
            values[1] = Double.parseDouble( valueStrs[1] );
            yAxis.setRange( values[0], values[1] );
        }
        else {
            yAxis.setAutoRange(true);
        }
    }

    public void drawChart() {
        ChartFrame cFrame = new ChartFrame( "PilotsChartFrame", chart_ );
        RefineryUtilities.centerFrameOnScreen( cFrame );
        cFrame.setSize( 800, 500 );
        cFrame.setVisible( true );
        cFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }


    public static void main( String[] args ) {
        int serverPort = Integer.parseInt( args[0] );
        ChartServer server = new ChartServer( serverPort );

        server.startServer();
    }

}
