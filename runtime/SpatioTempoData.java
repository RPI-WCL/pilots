package pilots.runtime;

import java.util.Date;
import java.util.Vector;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class SpatioTempoData {
    private static int MAX_DIMENSION = 3;
    private static int currentId_ = 0;

    private int id_;
    private float[][] locations_;
    private int dimension_;
    private boolean isSpaceInterval_;

    private DateFormat dateFormat_;     // not thread safe
    private Date[] times_;
    private boolean isTimeInterval_;

    private Vector<Float> values_;

    public SpatioTempoData() {
        id_ = currentId_++;
        locations_ = new float[2][MAX_DIMENSION];
        times_ = new Date[2];
        dateFormat_ = new SimpleDateFormat( "yyyy-MM-dd HHmmZ" );
        TimeZone.setDefault( TimeZone.getTimeZone( "America/New_York" ) );
        values_ = new Vector<Float>();
    }


    public SpatioTempoData( String str ) {
        id_ = currentId_++;
        // locations_ = new float[2][MAX_DIMENSION];
        locations_ = new float[2][MAX_DIMENSION];
        times_ = new Date[2];
        dateFormat_ = new SimpleDateFormat( "yyyy-MM-dd HHmmZ" );
        TimeZone.setDefault( TimeZone.getTimeZone( "America/New_York" ) );
        values_ = new Vector<Float>();

        if (!parse( str )) {
            System.err.println( "parse failed" );
        }
    }
    

    public boolean parse( String str ) {

        String[] data = str.split( ":" );
        for (int i = 0; i < data.length; i++)
            System.out.println( "data[" + i + "]: " + data[i] );

        // Spatio part
        String[] locationStr = data[0].split( "~" );
        if (2 < locationStr.length) {
            System.err.println( "Invalid location length: " + locationStr.length );
            return false;
        }
        isSpaceInterval_ = (locationStr.length == 2) ? true : false;
        for (int i = 0; i < locationStr.length; i++) {
            String[] dimensionStr = locationStr[i].split( "," );
            if (3 < dimensionStr.length) {
                System.err.println( "Invalid dimension length: " + dimensionStr.length );
                return false;
            }
            for (int j = 0; j < dimensionStr.length; j++) {
                locations_[i][j] = Float.parseFloat( dimensionStr[j] );
                System.out.println( "dimension[" + j  +"]: " + dimensionStr[j] );
            }
            dimension_ = dimensionStr.length;
             
        }   

        // Temporal part
        String[] timeStr = data[1].split( "~" );
        if (2 < timeStr.length) {
            System.err.println( "Invalid time length: " + timeStr.length );
            return false;
        }
        isTimeInterval_ = (timeStr.length == 2) ? true : false;
        for (int i = 0; i < timeStr.length; i++) {
            try {
                // dateFormat_ = new SimpleDateFormat( "yyyy-MM-dd HHMMZ" );
                times_[i] = dateFormat_.parse( timeStr[i] );
            } catch (ParseException e) {
                System.out.println( e );
            }
            System.out.println( "time[" + i  +"]: " + timeStr[i] );
        }

        // Value part
        String[] valueStr = data[2].split( "," );
        for (int i = 0; i < valueStr.length; i++)
            values_.add( new Float( valueStr[i] ) );

        return true;
    }

    
    protected void print() {
        switch( dimension_ ) {
        case 1:
            System.out.print( locations_[0][0] );
            break;
        case 2:
            System.out.print( locations_[0][0] + "," + locations_[0][1] );
            break;
        case 3:
            System.out.print( locations_[0][0] + "," + locations_[0][1] + "," + locations_[0][2] );
            break;
        }
        if (isSpaceInterval_) {
            System.out.print( "~" );
            switch( dimension_ ) {
            case 1:
                System.out.print( locations_[0][0] );
                break;
            case 2:
                System.out.print( locations_[0][0] + "," + locations_[0][1] );
                break;
            case 3:
                System.out.print( locations_[0][0] + "," + locations_[0][1] + "," + locations_[0][2] );
                break;
            }
        }

        System.out.print( ":" + dateFormat_.format( times_[0] ) );
        if (isTimeInterval_)
            System.out.print( "~" + dateFormat_.format( times_[1] ) );

        System.out.print( ":" );
        for (int i = 0; i < values_.size(); i++) {
            Float f = values_.get( i );
            System.out.print( f );
            if ((1 < values_.size()) && (i < (values_.size() - 1))) 
                System.out.print( "," );
        }


        System.out.println();
    }
}
    




