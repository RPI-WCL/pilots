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
    private TimeSeriesCollection timeSeriesCollection_;
    private JFreeChart chart_;
    private final static int MAX_TIME_SERIES = 10;
    private static int currSeries_;

    // color support
    private final static int AVAILABLE_COLORS = 13;
    private Color[] defaultColorMap;
    private HashMap<String, Color> colorMap;


    public ChartServer( int port ) {
        port_ = port;
        timeSeries_ = new TimeSeries[ MAX_TIME_SERIES ];
        timeSeriesCollection_ = new TimeSeriesCollection();
        currSeries_ = 0;
        // timeSeries_ = new TimeSeries("Series 1",
        //                              "timeSeries domain",
        //                              "timeSeries range" );
        // chart_ = ChartFactory.createTimeSeriesChart ("PilotsChartServer",
        //                                              "domain",
        //                                              "range",
        //                                              new TimeSeriesCollection( timeSeries_ ),
        //                                              true, true, true);
        defaultColorMap = new Color[AVAILABLE_COLORS];
        defaultColorMap[0] = Color.green;
        defaultColorMap[1] = Color.red;
        defaultColorMap[2] = Color.blue;
        defaultColorMap[3] = Color.magenta;
        defaultColorMap[4] = Color.cyan;
        defaultColorMap[5] = Color.darkGray;
        defaultColorMap[6] = Color.gray;
        defaultColorMap[7] = Color.black;
        defaultColorMap[8] = Color.lightGray;
        defaultColorMap[9] = Color.orange;
        defaultColorMap[10] = Color.pink;
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
        
        initChart();
        configChart();
        drawChart();
    }

    protected void initChart() {

        String chartTitle = System.getProperty( "chartTitle" );
        chartTitle = (chartTitle == null) ? "PilotsChartServer" : chartTitle;
        String xAxisLegend = System.getProperty( "xAxisLegend" );
        xAxisLegend = (xAxisLegend == null) ? "Value" : xAxisLegend;
        String yAxisLegend = System.getProperty( "yAxisLegend" );
        yAxisLegend = (yAxisLegend == null) ? "Time" : yAxisLegend;

        chart_ = ChartFactory.createTimeSeriesChart (chartTitle, xAxisLegend, yAxisLegend, 
                                                     timeSeriesCollection_,
                                                     true, true, true);
    }
        

    protected void addChartData( Date date, Vector<Double> values ) {
        try {
            for (int i = 0; i < values.size(); i++) {
                timeSeries_[currSeries_ + i].add( new Millisecond( date ), values.get( i ) );
            }
        } catch (SeriesException ex) {
            ex.printStackTrace();
        }
        
        chart_.fireChartChanged();
    }


    public void startServer() {
        try {
            ServerSocket serverSock = new ServerSocket( port_ );

            while (true) {
                System.out.println( "Started listening to port:" + port_ );
                Socket newSock = serverSock.accept();
                BufferedReader in = new BufferedReader( new InputStreamReader( newSock.getInputStream() ) );
                String str = null;
                String[] varNames = null;

                System.out.println( "Connection accepted" );

                while ( (str = in.readLine() ) != null ) {
                    if ( str.length() == 0 ) {
                        System.out.println( "EOS marker received" );
                        break;
                    }
                    else if ( str.charAt(0) == '#' ) {
                        System.out.println( "first line received: " + str );
                        varNames = str.split( "[#, ]" );
                        addTimeSeries( varNames );
                    }
                    else {
                        SpatioTempoData stData = new SpatioTempoData( str );
                        Date[] times = stData.getTimes();
                        Vector<Double> values = stData.getValues();
                        addChartData( times[0], values );

                        // just print the value
                        System.out.println( str );
                    }
                }
                currSeries_ += varNames.length - 1;
                System.out.println( "Finished receiving time series" );
                in.close();
                newSock.close();
            }

        } catch (Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected void addTimeSeries( String[] varNames ) {
        for (int i = 1; i < varNames.length; i++) {
            timeSeries_[currSeries_ + i - 1] = new TimeSeries( varNames[i], 
                                                               "timeSeries domain",
                                                               "timeSeries range" );
            timeSeriesCollection_.addSeries( timeSeries_[currSeries_ + i - 1] );
        }
    }
        
        
    protected void configChart() {
        XYPlot xyPlot = chart_.getXYPlot();
        xyPlot.setRenderer( new XYLineAndShapeRenderer() );

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();

        // setting strokes
        for (int i = 0; i < MAX_TIME_SERIES; i++)
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
            for (int i = 0; i < MAX_TIME_SERIES; i++)
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

    protected void drawChart() {
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
