package pilots.runtime;

// import java.util.concurrent.locks.ReadWriteLock;
// import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Date;
import java.util.Vector;
import java.text.ParseException;
import pilots.runtime.*;


public class DataStore extends DebugPrint {
    private static Vector<DataStore> stores_ = null;
    private static CurrentLocationTimeService currLocTime_ = null;
    private static int MAX_DATA_NUM = 10;

    // private ReadWriteLock lock;
    private String[] varNames_;
    private Vector<SpatioTempoData> data_;

    public DataStore( String[] varNames ) {
        varNames_ = new String[varNames.length];

        for (int i = 0; i < varNames.length; i++)
            varNames_[i] = varNames[i];        // shallow copy

        // lock = new ReentrantReadWriteLock();
        data_ = new Vector<SpatioTempoData>();

        if (currLocTime_ == null) 
            currLocTime_ = ServiceFactory.getCurrentLocationTime();
    }


    public static DataStore getInstance( String str ) {
        if (stores_ == null) {
            stores_ = new Vector<DataStore>();
        }

        String[] varNames;
        try {
            varNames = parseVarNames( str );
        } catch (ParseException ex) {
            System.err.println( ex + " at " + ex.getErrorOffset() );
            return null;
        }

        // check if the variables are in the store already, otherwise create a new one
        DataStore foundStore = null, store;
        for (int i = 0; i < stores_.size(); i++) {
            store = stores_.get( i );
            if (store.hasIdenticalVarNames( varNames )) {
                foundStore = store;
                break;
            }
        }

        if (foundStore == null) {
            store = new DataStore( varNames );
            stores_.add( store );
            store.dbgPrint( "created DataStore for " + str );
        }
        else {
            store = foundStore;
            store.dbgPrint( "found exsiting DataStore for " + str );
        }
            
        return store;
    }


    public static DataStore findStore( String varName ) {
        //System.out.println( "findStore, varName=" + varName );
        DataStore store = null;

        if (stores_ == null) {
            return null;
        }

        boolean found = false;
        for (int i = 0; i < stores_.size(); i++) {
            store = stores_.get( i );
            if (store.containVarName( varName )) {
                // System.out.println( "store found!!" );
                found = true;
                break;
            }
        }

        return found ? store : null;
    }


    private Vector<SpatioTempoData> applyClosest( Vector<SpatioTempoData> data, String arg ) {

        Vector<SpatioTempoData> newData = new Vector<SpatioTempoData>();

        int coord = Dimension.parseCoord( arg );
        if (coord == Dimension.UNKNOWN)
            return null;

        if (coord == Dimension.TIME) {
            // dbgPrint( "applyClosest( TIME ) is not implmented!!" );
            Date currTime = currLocTime_.getTime();
            long minDiff = Long.MAX_VALUE;

            for (int i = 0; i < data.size(); i++) {
                SpatioTempoData stData = data.get( i );
                long diff = stData.calcTimeDiff( currTime );

                if (diff < minDiff) {
                    newData.clear();
                    newData.add( stData );
                    minDiff = diff;
                }
                else if (diff == minDiff ) {
                    newData.add( stData );
                }                
            }
        }
        else {
            // Dimension.X or Y or Z
            double[] currLocation = currLocTime_.getLocation();
            double minDiff = Double.MAX_VALUE;

            for (int i = 0; i < data.size(); i++) {
                SpatioTempoData stData = data.get( i );
                double diff = stData.calcLocationDiff( coord, currLocation[ coord ] );

                if (diff < minDiff) {
                    newData.clear();
                    newData.add( stData );
                    minDiff = diff;
                }
                else if (diff == minDiff ) {
                    newData.add( stData );
                }                
            }
        }

        return newData;
    }

    private Vector<SpatioTempoData> applyEuclidean( Vector<SpatioTempoData> data, String[] args ) {
        return null;
    }

    private Vector<SpatioTempoData> applyInterpolation( Vector<SpatioTempoData> data, String[] args ) {
        return null;
    }

    private int getVarIndex( String varName ) {
        for (int i = 0; i < varNames_.length; i++) {
            // System.out.println( "getVarIndex(), varNames_[" + i + "]=" + varNames_[i] );
            if (varNames_[i].equals( varName )) {
                return i;
            }
        }

        // should not happen; this will cause an exception eventually
        return -1;
    }

    public synchronized double getData( String varName, Method[] methods ) {
        Vector<SpatioTempoData> workData = new Vector<SpatioTempoData>();
        workData = data_;  // shallow copy

        // System.out.println ("DataStore.getData, workdata.size()=" + workData.size() );

        int varIndex = getVarIndex( varName );
        
        SpatioTempoData stData;
        Double d = 0.0;
        if (workData.size() == 1) {
            stData = workData.get( 0 );
            d = stData.getData( varIndex - 1 ); // -1: workaround due to an issue on parseVarNames 
            return d;
        }

        boolean errorCondition = false;
        for (int i = 0; i < methods.length; i++) {
            String[] args = methods[i].getArgs();
            if (args.length == 0) {
                System.err.println( "Invalid number of arguments for closest method: " + args.length );
                errorCondition = true;
                break;
            } 

            switch (methods[i].getID()) {
            case Method.Closest:
                // this applies to one of {x, y, z, t}
                if (1 < args.length) {
                    System.err.println( "Invalid number of arguments for closest method: " + args.length );
                    errorCondition = true;
                    break;
                } 
                workData = applyClosest( workData, args[0] );
                break;
                
            case Method.Euclidean:
                // this applies to any combinations of {x, y, z}
                if (3 < args.length) {
                    System.err.println( "Invalid number of arguments for euclidean method: " + args.length );
                    errorCondition = true;
                    break;
                }
                workData = (args.length == 1) ? 
                    applyClosest( workData, args[0] ) : applyEuclidean( workData, args );
                break;

            case Method.Interpolate:
                // this applies to any combinations of {x, y, z}
                // and also takes one argument to specify up to how many points to interpolate
                if (4 < args.length)  {
                    System.err.println( "Invalid number of arguments for interpolation method: " + args.length );
                    errorCondition = true;
                    break;
                }
                workData = applyInterpolation( workData, args );
                break;

            default:
                break;
            }

            if (errorCondition || (workData == null)) {
                break;
            }

            if (workData.size() == 1) {
                // no need to check methods anymore
                stData = workData.get( 0 );
                d = stData.getData( varIndex - 1 );  // -1: workaround due to an issue on parseVarNames 
                break;
            }
        }
        
        return d;
    }

    
    private static String[] parseVarNames( String str ) throws ParseException {
        if (str.charAt(0) != '#') {
            throw new ParseException( "# not found in the first line", 0 );
        }

        String[] varNames = str.split( "[#, ]" );

        // for (int i = 0; i < varNames.length; i++)
        //     System.out.println( "varNames[" + i + "]: " + varNames[i] );

        return varNames;
    }


    private boolean hasIdenticalVarNames( String[] varNames ) {

        if (varNames_.length != varNames.length) {
            return false;
        }

        boolean flag = true;
        for (int i = 0; i < varNames.length; i++) {
            if ( 0 < varNames[i].length() ) {
                flag = false;
                for (int j = 0; j < varNames_.length; j++) {
                    if (varNames[i].equals( varNames_[j] )) {
                        flag = true;
                        break;
                    }
                }
                
                if (flag == false) {
                    break;
                }
            }
        }

        //System.out.println( "# found identical varNames: " + flag );

        return flag;
    }


    private boolean containVarName( String varName ) {
        boolean flag = false;

        for (int i = 0; i < varNames_.length; i++) {
            if (varNames_[i].equals( varName )) {
                flag = true;
                break;
            }
        }
        
        return flag;
    }


    public String[] getVarNames() {
        return varNames_;
    }


    public synchronized boolean addData( String str ) {
        SpatioTempoData stData = new SpatioTempoData();

        if (!stData.parse( str )) {
            System.err.println( "parse failed: " + str );
            return false;
        }

        //stData.print();
        
        if (MAX_DATA_NUM <= data_.size()) {
            // remove the oldest data
            data_.remove( 0 );
        }
        data_.add( stData );

        return true;
    }
 }

    