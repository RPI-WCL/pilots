package pilots.runtime.model;

import java.util.Date;
import java.util.Vector;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import pilots.runtime.model.Dimension;


public class SpatioTempoData {
    public static String datePattern = "yyyy-MM-dd HHmmssSSSZ";
    /*public static String datePattern = "HH-mm-ss";*/
    public static String timeZoneID = "America/New_York";

    private static int currentId_ = 0;

    private int id_;
    private double[][] locations_;
    private Dimension dimension_;
    private boolean isLocationInterval_;
    private boolean hasLocations_;

    private DateFormat dateFormat_;     // not thread safe
    private Date[] times_;
    private boolean isTimeInterval_;
    private boolean hasTimes_;

    private Vector<Double> values_;

    private double dist_; // used for calculating 1-D/euclidean distance

    public SpatioTempoData() {
        id_ = currentId_++;

        locations_ = null;
        dimension_ = null;
        isLocationInterval_ = false;
        hasLocations_ = false;

        dateFormat_ = new SimpleDateFormat( datePattern );
        times_ = null;
        isTimeInterval_ = false;
        hasTimes_ = false;
        TimeZone.setDefault( TimeZone.getTimeZone( timeZoneID ) );

        values_ = new Vector<Double>();

        dist_ = 0.0;
    }

    public SpatioTempoData( String str ) {
        id_ = currentId_++;

        locations_ = null;
        dimension_ = null;
        isLocationInterval_ = false;
        hasLocations_ = false;

        dateFormat_ = new SimpleDateFormat( datePattern );
        times_ = null;
        isTimeInterval_ = false;
        hasTimes_ = false;
        TimeZone.setDefault( TimeZone.getTimeZone( timeZoneID ) );

        values_ = new Vector<Double>();

        dist_ = 0.0;

        if (!parse( str )) {
            System.err.println( "parse failed" );
        }
    }

    public double calcLocationDiff( int coord, double base ) {
        // calculate the difference between the given base and locations

        double diff = 0.0;
        if (isLocationInterval_) {
            diff = Math.min( Math.abs( locations_[0][coord] - base ),
                             Math.abs( locations_[1][coord] - base ) );
        }
        else {
            diff = Math.abs( locations_[0][coord] - base );
        }

        return diff;
    }

    public long calcTimeDiff( Date base ) {
        // calculate the time difference between the given base and time

        long diff = 0;
        if (isTimeInterval_) {
            diff = Math.min( Math.abs( times_[0].getTime() - base.getTime() ),
                             Math.abs( times_[1].getTime() - base.getTime() ) );
        }
        else {
            diff = Math.abs( times_[0].getTime() - base.getTime() );
        }

        return diff;
    }


    // format
    // "(<Double>(,<Double>)?(,<Double>)?)?:<Date>(~<Date>)?:(<Double>(,<Double>)*)?"
    // e.g. <Date> -> yyyy-MM-dd HHmmssSSSZ
    public boolean parse( String str ) {

        String[] data = str.split( ":" );

        // for (int i = 0; i < data.length; i++) 
        //     System.out.println( data[i] );

        // Spatio part
        if (0 < data[0].length()) {
            String[] locationStr = data[0].split( "~" );
            if (2 < locationStr.length) {
                System.err.println( "Invalid location length: " + locationStr.length );
                return false;
            }
            isLocationInterval_ = (locationStr.length == 2) ? true : false;

            for (int i = 0; i < locationStr.length; i++) {
                // check spatial dimension
                String[] dimensionStr = locationStr[i].split( "," );
                if (Dimension.MAX_SPATIAL_DIMENSION < dimensionStr.length) {
                    System.err.println( "Invalid dimension length: " + dimensionStr.length );
                    return false;
                }
                dimension_ = new Dimension( dimensionStr.length );

                // alloc locations
                locations_ = (isLocationInterval_) ? 
                    new double[2][dimensionStr.length] : 
                    new double[1][dimensionStr.length];

                for (int j = 0; j < dimensionStr.length; j++) {
                    locations_[i][j] = Double.parseDouble( dimensionStr[j] );
                }
            }
            hasLocations_ = true;
        }

        // Temporal part
        if (0 < data[1].length()) {
            String[] timeStr = data[1].split( "~" );
            if (2 < timeStr.length) {
                System.err.println( "Invalid time length: " + timeStr.length );
                return false;
            }
            isTimeInterval_ = (timeStr.length == 2) ? true : false;
        
            // alloc times
            times_ = (isTimeInterval_) ? new Date[2] : new Date[1];

            for (int i = 0; i < timeStr.length; i++) {
                try {
                    // dateFormat_ = new SimpleDateFormat( "yyyy-MM-dd HHMMZ" );
                    times_[i] = dateFormat_.parse( timeStr[i] );
                } catch (ParseException e) {
                    System.out.println( e );
                }
            }
            hasTimes_ = true;
        }

        // Value part
        if (0 < data[2].length()) {
            String[] valueStr = data[2].split( "," );
            for (int i = 0; i < valueStr.length; i++)
                values_.add( new Double( valueStr[i] ) );
        }

        return true;
    }


    public double getData( int varIndex ) {
        Double d = values_.get( varIndex );
        return d.doubleValue();
    }

    public double getDist() {
        return dist_;
    }

    public void setDist( double dist ) {
        dist_ = dist;
    }


    public void print() {
        System.out.println(marshal());
        System.out.println( " (dist=" + dist_ + ")" );
        //System.out.println();
    }

    public Date[] getTimes() {
        return times_;
    }

    public Vector<Double> getValues() {
        return values_;
    }

    public boolean hasLocations() {
        return hasLocations_;
    }

    public boolean hasTimes() {
        return hasTimes_;
    }


    // marshal generates a string, which can be parsed back to the same spatio 
    // tempo data.
    public String marshal(){
        StringBuilder builder = new StringBuilder();
        if (locations_ != null) {
            switch( dimension_.getDim() ) {
            case Dimension.ONE_DIMENSION:
                builder.append(locations_[0][0]);
                break;
            case Dimension.TWO_DIMENSION:
                builder.append(locations_[0][0] + "," + locations_[0][1]);
                break;
            case Dimension.THREE_DIMENSION:
                builder.append(locations_[0][0] + "," + locations_[0][1] + "," + locations_[0][2] );
                break;
            }
            if (isLocationInterval_) {
                builder.append("~");
                switch( dimension_.getDim() ) {
                case Dimension.ONE_DIMENSION:
                    builder.append(locations_[0][0]);
                    break;
                case Dimension.TWO_DIMENSION:
                    builder.append(locations_[0][0] + "," + locations_[0][1]);
                    break;
                case Dimension.THREE_DIMENSION:
                    builder.append(locations_[0][0] + "," + locations_[0][1] + "," + locations_[0][2]);
                    break;
                }
            }
        }
        
        if (times_ != null) {
            builder.append(":" + dateFormat_.format( times_[0] ));
            if (isTimeInterval_)
                builder.append("~" + dateFormat_.format( times_[1] ));
        }

        builder.append(":");
        for (int i = 0; i < values_.size(); i++) {
            Double d = values_.get( i );
            builder.append(d);
            if ((1 < values_.size()) && (i < (values_.size() - 1))) 
                builder.append(",");
        }

        return builder.toString();
    }

    public String toString() {
        // skip locations for now
        
        String str = "";
        if (times_.length == 2)
            str += times_[0] + "~" + times_[1] + ":";
        else if (times_.length ==1)
            str += times_[0] + ":";

        for (int i= 0; i < values_.size(); i++) {
            if (i == values_.size() - 1) 
                str += values_.get( i );
            else
                str += values_.get( i ) + ",";
        }

        return str;
    }

}
    




