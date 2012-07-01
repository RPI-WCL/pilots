package pilots.tests;

import java.io.*;
import java.net.*;
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
//import org.jfree.data.xy.*;

import pilots.runtime.*;

public class ChartServer {
    private int port_;
    private TimeSeries[] timeSeries_;
    private JFreeChart chart_;


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
                    configChart();
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
        
    public void configChart() {
        XYPlot xyPlot = chart_.getXYPlot();

        ValueAxis xAxis = xyPlot.getDomainAxis();
        xAxis.setAutoRange(true);

        ValueAxis yAxis = xyPlot.getRangeAxis();
        yAxis.setAutoRange(true);
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
