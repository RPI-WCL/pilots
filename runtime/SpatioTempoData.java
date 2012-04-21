package pilots.runtime;

import java.util.Date;
import java.util.Vector;
import java.text.DateFormat;
import java.text.ParseException;


public class SpatioTempoData {
    private static int MAX_DIMENSION = 3;
    private static int currentId = 0;

    private int id;
    private float[] loc0 = new float[MAX_DIMENSION];
    private float[] loc1 = new float[MAX_DIMENSION];
    private int dimension;
    private boolean isSpaceInterval;

    private Date time0, time1;
    private boolean isTimeInterval;
    private DateFormat dateFormat;

    private Vector<Float> value;

    public SpatioTempoData() {
        id = currentId++;
        dateFormat = new SimpleDateFormat( "yyyy-MM-dd HHMMZ" );
    }

    public SpatioTempoData( String str ) {
        id = currentId++;
        dateFormat = new SimpleDateFormat( "yyyy-MM-dd HHMMZ" );

        if (!parse( str )) {
            System.err.println( "parse failed" );
        }
    }
    
    protected boolean parse( String str ) {
        // input string examples
        // 1. x0,y0,z0-x1,y1,z1:t0-t1:val0,val1,...
        // 2. x,y,z:t0-t1:val0,val1,...
        // 3. x0-x1:t:val0,val1,...
        // 4. :t:val0,val1,...

        // ----------- parse spatio part
        int firstColon = str.indexOf( ':' );

        if (firstColon < 0) {
            System.err.println( "parse failed, no first colon separator!!" );
            return false;
        }

        if (0 < firstColon) {
            /* parse space part first */
            this.loc0[0] = this.loc0[1] = this.loc0[2] = -1.0F;
            this.loc1[0] = this.loc1[1] = this.loc1[2] = -1.0F;

            String spaceStr = str.substring( 0, firstColon - 1 );
            int hyphen = spaceStr.indexOf( '-' );
            
            String loc = (0 < hyphen) ? spaceStr.substring( 0, hyphen - 1 ) : spaceStr;
            String[] locArray = loc.split( "," );

            if (locArray.length == 0)
                this.loc0[0] = Float.parseFloat( loc );
            else {
                for (int i = 0; i < locArray.length; i++)
                    this.loc0[i] = Float.parseFloat( locArray[i] );
            }
            dimension = locArray.length;

            if (0 < hyphen) {
                /* data is defined for interval */
                loc = spaceStr.substring( hyphen + 1, firstColon - 1 );
                locArray = loc.split( "," );

                if (dimension != locArray.length) {
                    System.err.println( "parse failed, dimesion is not matched(" + 
                                        dimension + "," + locArray.length + ")" );
                    return false;
                }

                if (locArray.length == 0)
                    this.loc1[0] = Float.parseFloat( loc );
                else {
                    for (int i = 0; i < locArray.length; i++)
                        this.loc1[i] = Float.parseFloat( locArray[i] );
                }
                this.isTimeInterval = true;
            }
        }

        // ----------- parse tempo part
        int secondColon = str.indexOf( ':', firstColon + 1 );
        if (secondColon <= 0) {
            System.err.println( "parse failed, no second colon separator!!" );
            return false;
        }

        String tempoStr = str.substring( firstColon + 1, secondColon - 1);
        int hyphen = tempoStr.indexOf( '-' );

        String time  = (0 < hyphen) ? tempoStr.substring( 0, hyphen - 1 ) : tempoStr;
        try {
            this.time0 = dateFormat.parse( time );
        } catch (ParseException e) {
            System.out.println( e );
        }

        if (0 < hyphen) {
            /* data is defined for interval */
            time = tempoStr.substring( hyphen + 1, secondColon - 1 );
            try {
                this.time1 = dateFormat.parse( time );
            } catch (ParseException e) {
                System.out.println( e );
            }
            this.isTimeInterval = true;
        }

        // ----------- parse value part
        String valueStr = str.substring( secondColon + 1 );
        String[] valueArray = valueStr.split( "," );

        for (int i = 0; i < valueArray.length; i++)
            value.add( new Float( valueArray[i] ) );

        return true;
    }

    
    protected void print() {
        switch( dimension ) {
        case 1:
            System.out.print( loc0[0] );
            break;
        case 2:
            System.out.print( loc0[0] + "," + loc0[1] );
            break;
        case 3:
            System.out.print( loc0[0] + "," + loc0[1] + "," + loc0[2] );
            break;
        }
        if (isSpaceInterval) {
            System.out.print( "-" );
            switch( dimension ) {
            case 1:
                System.out.print( loc0[0] );
                break;
            case 2:
                System.out.print( loc0[0] + "," + loc0[1] );
                break;
            case 3:
                System.out.print( loc0[0] + "," + loc0[1] + "," + loc0[2] );
                break;
            }
        }
            
        System.out.print( ":" + time0 );
        if (isTimeInterval)
            System.out.print( "-" + time1 );

        System.out.print( ":" );
        for (int i = 0; i < value.size(); i++) {
            Float f = value.get( i );
            System.out.print( f );
            if ((1 < value.size()) && (i < (value.size() - 1))) 
                System.out.print( "," );
        }


        System.out.println();
    }
}
    




