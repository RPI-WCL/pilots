package pilots.runtime.model;

/*
Dimension defines a set of types of spacial-temporal data.
*/
public class Dimension {
    public static final int MAX_SPATIAL_DIMENSION = 3;
    public static final int ONE_DIMENSION = 1;
    public static final int TWO_DIMENSION = 2;
    public static final int THREE_DIMENSION = 3;

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    public static final int TIME = 3;
    public static final int UNKNOWN = 4;

    int dim_;

    public Dimension( int dim ) {
        if (MAX_SPATIAL_DIMENSION < dim) {
            System.err.println( "Dimension exceeded limit: " + dim );
            return;
        }
        dim_ = dim;
    }

    public int getDim() {
        return dim_;
    }

    public static int parseCoord( String str ) {
        int coord = UNKNOWN;

        if (str.equalsIgnoreCase( "x" ) || str.equalsIgnoreCase( "lat" )) {
            coord = X;
        }
        else if (str.equalsIgnoreCase( "y" ) || str.equalsIgnoreCase( "long" )) {
            coord = Y;
        }
        else if (str.equalsIgnoreCase( "z" )) {
            coord = Z;
        }
        else if (str.equalsIgnoreCase( "t" )) {
            coord = TIME;
        }            
        else {
            System.err.println( "Coordinate not defined: " + str );
            coord = UNKNOWN;
        }

        // if (coord <= dim_) {
        //     System.err.println( "Invalid coordinate: " + str + ", dimension: " + dim_ );
        // }

        return coord;
    }

}
