package pilots.runtime;

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

    private int dim;

    public Dimension( int dim ) {
        if (MAX_SPATIAL_DIMENSION < dim) {
            System.err.println( "Dimension exceeded limit: " + dim );
            return;
        }
        this.dim = dim;
    }

    public int getDim() {
        return dim;
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

        return coord;
    }

}
