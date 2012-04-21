package activedata.flightplan;

import java.util.Vector;


public class Test {
    static Vector<Integer> data = new Vector<Integer>();

    public static void main( String[] args ) {
        Integer a = new Integer( 10 );
        Integer b = new Integer( 20 );
        data.add( a );
        data.add( b );

        Vector<Integer> temp = (Vector<Integer>)data.clone();
        temp.remove( a );

        System.out.println( "data.size() = " + data.size() );
        System.out.println( "temp.size() = " + temp.size() );
    }
}