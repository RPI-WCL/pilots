package pilots.tests;

import java.io.*;
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
import org.jfree.data.xy.*;

public class JFChartTest {
    /**
     * シリーズ数
     */
    public static final int SERIES_COUNT = 3;

    /**
     * シリーズ毎のアイテム数
     */
    public static final int ITEM_COUNT = 24;

    XYDataset xyData_;
    TimeSeries timeSeries_;
    int time_ = 0;
    JFreeChart tsChart_;

    /**
     * TimeSeriesChartの作成と使用.
     */
    public void workTimeSeriesChart() {
        // まずTimeSeriesChartを作成する.
        JFreeChart tsChart = getTimeSeriesChart();
        configTimeSeriesChart(tsChart);

        // 作成したTimeSeriesChartでPNGファイルを作成.
        // File outFile = new File("./timeserieschart.png");
        // try {
        //     ChartUtilities.saveChartAsPNG(outFile, tsChart, 500, 500);
        // } catch(IOException e) {
        //     e.printStackTrace();
        // }

        // 作成したTimeSeriesChartでChartFrameを作成.
        ChartFrame cFrame = new ChartFrame ("TimeSeriesChartFrame", tsChart);
        RefineryUtilities.centerFrameOnScreen(cFrame);
        cFrame.setSize(500, 500);
        cFrame.setVisible(true);
        cFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while (true) {
            addXYDataset();
            try {
                Thread.sleep( 1000 );
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            tsChart_.fireChartChanged();
        }
    }

    /**
     * JFreeChartオブジェクトの作成
     */
    public JFreeChart getTimeSeriesChart() {
        // XYDatasetオブジェクトの作成
        timeSeries_ = new TimeSeries("Series 1",
                                     "timeSeries domain",
                                     "timeSeries range" );
        xyData_ = new TimeSeriesCollection( timeSeries_ ); // createXYDataset();
        // XYDatasetをデータにしてJFreeChartを作成
        tsChart_ = ChartFactory.createTimeSeriesChart ("JFChartTest",
                                                       "domain",
                                                       "range",
                                                       xyData_,
                                                       true, true, true);
        return tsChart_;
    }

    public void addXYDataset() {
        Random random = new Random();
        int value = 0;
        
        try {
            timeSeries_.add(new Second(time_, 1, 1, 1, 1, 2004), random.nextInt() % 100 );
        } catch (SeriesException ex) {
            ex.printStackTrace();
        }

        time_++;
    }

    /**
     * XYDatasetのオブジェクト作成
     */
    public XYDataset createXYDataset() {
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

        for (int series = 0; series < SERIES_COUNT; series++) {
            timeSeriesCollection.addSeries(createTimeSeries(series));
        }
        return timeSeriesCollection;
    }

    /**
     * TimeSeriesオブジェクトの作成
     */
    public TimeSeries createTimeSeries (int seriesNo) {
        Random random = new Random();
        int value = 0;
        TimeSeries timeSeries = new TimeSeries("Series" + seriesNo,
                                               "timeSeries domain",
                                               "timeSeries range" );
        for (int i = 0; i < ITEM_COUNT; i++) {
            value += Math.abs(random.nextInt() % (60 * 60 * 24));
            int second = value % 60;
            int minute = ((value - second) / 60) % 60;
            int hour = ((value - minute * 60 - second)  / 3600) % 24;
            try {
                timeSeries.add(new Second(second, minute, hour, 1, 1, 2004), (seriesNo + 1) * value);
            } catch (SeriesException e) {
                System.out.println("" + value + ":::" + second + ":"
                                   + minute + ":" + hour);
                i--;
                continue;
            }
        }
        return timeSeries;
    }

    /**
     * JFreeChartの設定
     */
    public void configTimeSeriesChart(JFreeChart tsChart) {
        XYPlot xyPlot = tsChart.getXYPlot();
        /* 横軸の設定 */
        ValueAxis xAxis = xyPlot.getDomainAxis();
        //xAxis.setAutoRange(true);
        xAxis.setAutoRange(true);

        /* 縦軸の設定 */
        ValueAxis yAxis = xyPlot.getRangeAxis();
        yAxis.setAutoRange(true);

        /* レンダラの設定 */
        // System.out.println("Change renderer to XYBarRenderer from "
        //                    + xyPlot.getRenderer().getClass().getName());
        // xyPlot.setRenderer(new XYBarRenderer());
    }

    public static void main (String args[]) {
        JFChartTest stsc = new JFChartTest();
        stsc.workTimeSeriesChart();
    }

}