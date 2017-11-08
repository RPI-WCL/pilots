package pilots.runtime;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import pilots.runtime.service.*;
import pilots.runtime.model.*;

/*
 * DataStore holds all received spatio temporal data and provides CRU operations
 * and query methods.
 */
public class DataStore extends DebugPrint {
    private static Vector<DataStore> stores_ = null;
    private static CurrentLocationTimeService currLocTime_ = null;
    private static Comparator<SpatioTempoData> distComparator_ = null;
    private static int MAX_DATA_NUM = 10;
    private static Map<String, Method[]> methodDictionary = new HashMap<>();
    private String[] varNames_;
    private Vector<SpatioTempoData> data_;

    
    public DataStore( String[] varNames ) {
        varNames_ = new String[varNames.length];
        methodDictionary = new HashMap<>();
        for (int i = 0; i < varNames.length; i++)
            varNames_[i] = varNames[i];        // shallow copy

        data_ = new Vector<SpatioTempoData>();

        if (currLocTime_ == null) 
            currLocTime_ = ServiceFactory.getCurrentLocationTime();

        if (distComparator_ == null) {
            distComparator_ = new Comparator<SpatioTempoData> () {
                public int compare( SpatioTempoData stData1, SpatioTempoData stData2 ) {
                    double dist1 = stData1.getDist();
                    double dist2 = stData2.getDist();
                    return Double.compare( dist1, dist2 ); // ascending order 
                }
            };
        }
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
// System.out.println( "### applyClosest" );

        Vector<SpatioTempoData> newData = new Vector<SpatioTempoData>();

        int coord = Dimension.parseCoord( arg );
        if (coord == Dimension.UNKNOWN)
            return null;

        if (coord == Dimension.TIME) {
            Date currTime = currLocTime_.getTime();
            // System.out.println( "currTime=" + currTime );

            long minDiff = Long.MAX_VALUE;

            for (int i = 0; i < data.size(); i++) {
                SpatioTempoData stData = data.get( i );

                if (!stData.hasTimes()) {
                    // if the data has no time, we just add all the data to newData
                    newData.add( stData );
                }
                else {
                    long diff = stData.calcTimeDiff( currTime );
// System.out.println( "i=" + i + ", minDiff=" + minDiff + ", diff=" + diff );
// stData.print();                
                    if (diff < minDiff) {
// System.out.println( "Clo, dist < minDist: ");
                        newData.clear();
                        newData.add( stData );
                        minDiff = diff;
                    }
                    else if (diff == minDiff ) {
// System.out.println( "Clo, dist == minDist: ");
                        newData.add( stData );
                    }
                }
            }
        }
        else {
            // Dimension.X or Y or Z
            double[] currLocation = currLocTime_.getLocation();
            double minDiff = Double.MAX_VALUE;

            for (int i = 0; i < data.size(); i++) {
                SpatioTempoData stData = data.get( i );

                if (!stData.hasLocations()) {
                    newData.add( stData );
                }
                else {
                    double diff = stData.calcLocationDiff( coord, currLocation[ coord ] );

                    if (diff < minDiff) {
                        newData.clear();
                        newData.add( stData );
                        minDiff = diff;
                    }
                    else if (diff == minDiff) {
                        newData.add( stData );
                    }                
                }
            }
        }

// for (int i = 0; i < newData.size(); i++) 
//     System.out.println( "newData[" + i + "]=" + newData.get(i) );

        return newData;
    }

    private Vector<SpatioTempoData> applyEuclidean( Vector<SpatioTempoData> data, String[] args ) {
// System.out.println( "### applyEuclidean" );

        Vector<SpatioTempoData> newData = new Vector<SpatioTempoData>();

        double[] currLoc = currLocTime_.getLocation();
        if (currLoc == null) {
            dbgPrint( "current location is null" );
            return null;
        }

        int[] coords = new int[3];
        int dimension = args.length;

        for (int i = 0; i < dimension; i++)
            coords[i] = Dimension.parseCoord( args[i] );
        double minDist = Double.MAX_VALUE;

// for (int i = 0; i < currLoc.length; i++)
//     System.out.println( "Euc, currLoc[" + i + "]=" + currLoc[i]  );

        for (int i = 0; i < data.size(); i++) {
            SpatioTempoData stData = data.get( i );

            double diff, sum = 0.0;
            // we can assume 2-D <= dimension
            for (int j = 0; j < dimension; j++) {
                diff = stData.calcLocationDiff( coords[j], currLoc[coords[j]] );
                sum += (diff * diff);
            }
            double dist = Math.sqrt( sum );

            if (dist < minDist) {
// System.out.print( "Euc, dist(" + dist + ") < minDist(" + minDist + "): ");
// stData.print();
                newData.clear();
                newData.add( stData );
                minDist = dist;
            }
            else if (dist == minDist) {
// System.out.print( "Euc, dist(" + dist + ") == minDist(" + minDist + "): ");
// stData.print();
                newData.add( stData );
            }
        }

        return newData;
    }

    private SpatioTempoData[] sortInTimeDist( Vector<SpatioTempoData> data, Date currTime ) {
        SpatioTempoData[] stDataArray = data.toArray( new SpatioTempoData[data.size()] );
        
        for (int i = 0; i < stDataArray.length; i++) {
            long diff = stDataArray[i].calcTimeDiff( currTime );
            stDataArray[i].setDist( (double)diff );
        }

        Arrays.sort( stDataArray, distComparator_ );
        
        return stDataArray;
    }

    private SpatioTempoData[] sortIn1D_Dist( Vector<SpatioTempoData> data, int coord, double[] currLoc ) {
        SpatioTempoData[] stDataArray = data.toArray( new SpatioTempoData[data.size()] );
        
        for (int i = 0; i < stDataArray.length; i++) {
            double diff = stDataArray[i].calcLocationDiff( coord, currLoc[ coord ] );
            stDataArray[i].setDist( diff );
        }

        Arrays.sort( stDataArray, distComparator_ );
        
        return stDataArray;
    }


    private SpatioTempoData[] sortIn2D_Dist( Vector<SpatioTempoData> data, int[] coords, double[] currLoc ) {
        SpatioTempoData[] stDataArray = data.toArray( new SpatioTempoData[data.size()] );
        
        for (int i = 0; i < stDataArray.length; i++) {
            double diff, sum = 0.0;
            for (int j = 0; j < coords.length; j++) {
                diff = stDataArray[i].calcLocationDiff( coords[j], currLoc[ coords[j] ] );
                sum += (diff * diff);
            }
            stDataArray[i].setDist( Math.sqrt( sum ) );
        }

        Arrays.sort( stDataArray, distComparator_ );
        
        return stDataArray;
    }

    private SpatioTempoData[] sortIn3D_Dist( Vector<SpatioTempoData> data, int[] coords, double[] currLoc ) {
        return sortIn2D_Dist( data, coords, currLoc );
    }

        
    private Double applyInterpolation( Vector<SpatioTempoData> data, String[] args, int varIndex ) {
// System.out.println( "### applyInterpolation" );

        int dimension = args.length - 1; // last arg is n_interp
        int numInterp;
        try {
            numInterp = Integer.parseInt( args[args.length - 1] );
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        SpatioTempoData[] stDataArray = null;
        Date currTime = null;;
        double[] currLoc = null;
        int[] coords = null;
        switch (dimension) {
        case Dimension.ONE_DIMENSION:
            if (args[0].equalsIgnoreCase( "t" )) {
                // t
                currTime = currLocTime_.getTime();
                stDataArray = sortInTimeDist( data, currTime );
            }
            else  {
                // x or y or z
                currLoc = currLocTime_.getLocation();
                coords = new int[1];
                coords[0] = Dimension.parseCoord( args[0] );
                stDataArray = sortIn1D_Dist( data, coords[0], currLoc );
            }
            break;

        case Dimension.TWO_DIMENSION:
            // (x,y) or (x,z) or (y,z)
            currLoc = currLocTime_.getLocation();
            coords = new int[2];
            for (int i = 0; i < dimension; i++)
                coords[i] = Dimension.parseCoord( args[i] );
            stDataArray = sortIn2D_Dist( data, coords, currLoc );
            break;

        case Dimension.THREE_DIMENSION:
            // (x,y,z)
            currLoc = currLocTime_.getLocation();
            coords = new int[3];
            for (int i = 0; i < dimension; i++)
                coords[i] = Dimension.parseCoord( args[i] );
            stDataArray = sortIn3D_Dist( data, coords, currLoc );
            break;
            
        default:
            dbgPrint( "applyEuclidean failed due to unknown dimension: " + dimension );
            break;
        }

// if (currTime != null)
//     System.out.println( "currTime=" + currTime );
        
        double sum = 0.0;
        // stDataArray must be sorted in ascending order of whatever distance
        for (int i = 0; i < numInterp; i++) {
// stDataArray[i].print();
            sum += stDataArray[i].getDist();
        }
        double interpVal = 0.0;
        // calculate a weighted sum
        for (int i = 0; i < numInterp; i++) 
            interpVal += (1.0 - stDataArray[i].getDist() / sum) * stDataArray[i].getData( varIndex - 1 );
            
        return (new Double(interpVal));
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
    public synchronized void registerMethods(String varName, Method[] methods){
    	methodDictionary.put(varName, methods);
    }
    public synchronized Method[] getMethods(String varName){
        return methodDictionary.get(varName);
    }
    private synchronized Map<String, Double> getDatas(String[] varNames){
    	Map<String, Double> result = new HashMap<>();
    	for (String var : varNames){
    		result.put(var, findStore(var).getData(var, getMethods(var)));
    	}
    	return result;
    }
    private synchronized void printData(){
        for (String s : methodDictionary.keySet()){
            System.out.println(methodDictionary.get(s).toString());
        }
    }
    // editted: every time getData is called, register the current method 
    public synchronized Double getData( String varName, Method[] methods ) {
        registerMethods(varName, methods);
        Vector<SpatioTempoData> workData = new Vector<SpatioTempoData>();
        workData = data_;  // shallow copy

//        System.out.println ("DataStore.getData, varName=" + varName );

        int varIndex = getVarIndex( varName );
        
        SpatioTempoData stData;
        Double d = null;
        if (workData.size() == 1) {
            stData = workData.get( 0 );
            d = stData.getData( varIndex - 1 ); // -1: workaround due to an issue on parseVarNames 
            return d;
        }

        boolean errorCondition = false;
        boolean interpolated = false;
        boolean predicted = false;
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
                // this applies to any combinations of {x, y, z} and t
                // also takes one argument to specify up to how many points to interpolate
                if ((args.length < 2) || (4 < args.length )) {
                    System.err.println( "Invalid number arguments for interpolation method: " + args.length );
                    errorCondition = true;
                    break;
                }
                d = applyInterpolation( workData, args, varIndex );
                if (d != null)
                    interpolated = true;
                break;
            case Method.Predict:
                String model = args[0];
            	Map<String, Double> result = getDatas(Arrays.copyOfRange(args,1,args.length));
                predicted = true;
            	d = pilots.util.learningmodel.Client.predict(model, result)[0][0]; // currently support only one number prediction
            default:
                break;
            }

            if (errorCondition || (workData == null) || interpolated || predicted) {
                break;
            }

            if (workData.size() == 1) {
                // no need to check methods anymore
                stData = workData.get( 0 );
                d = stData.getData( varIndex - 1 );  // -1: workaround due to an issue on parseVarNames 
                break;
            }

            // System.out.println( "workData.size()=" + workData.size() );
        }

        if (!interpolated && 1 < workData.size() && !predicted) {
            // tie case, give priority to the first one
            stData = workData.get( 0 );
            d = stData.getData( varIndex - 1 );  // -1: workaround due to an issue on parseVarNames 
        }

        // if (varName.equals( "air_speed" ) && (d == 50.0)) {
        //     System.err.println( "########## Unusual airspeed found!!" );
        //     System.exit( 1 );
        // }
        
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
        
        if ( System.getProperty( "timeSpan" ) == null &&
             MAX_DATA_NUM <= data_.size() ) {
            // remove the oldest data only if working in real-time 
            data_.remove( 0 );
        }
        data_.add( stData );

        return true;
    }
 }

    
