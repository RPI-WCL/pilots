package pilots.runtime;

import java.text.ParseException;
import java.util.logging.Logger;
import java.util.*;

import pilots.util.trainer.DataVector;
import pilots.util.model.Client;


public class DataStore {
    private static Logger LOGGER = Logger.getLogger(SimTimeService.class.getName());
    
    private static List<DataStore> stores = null;
    private static CurrentLocationTimeService currLocTime = null;
    private static Comparator<SpatioTempoData> distComparator = null;
    private static int MAX_DATA_NUM = 8192; // TODO: optimize this number depending on sim or real mode
    private static Map<String, Method[]> methodDictionary = new HashMap<>();
    private String[] varNames;
    private List<SpatioTempoData> data;

    
    public DataStore(String[] varNames) {
        this.varNames = new String[varNames.length];
        methodDictionary = new HashMap<>();
        for (int i = 0; i < varNames.length; i++)
            this.varNames[i] = varNames[i];        // shallow copy

        data = new ArrayList<>();

        if (currLocTime == null) 
            currLocTime = ServiceFactory.getCurrentLocationTime();

        if (distComparator == null) {
            distComparator = new Comparator<SpatioTempoData> () {
                public int compare(SpatioTempoData stData1, SpatioTempoData stData2) {
                    double dist1 = stData1.getDist();
                    double dist2 = stData2.getDist();
                    return Double.compare(dist1, dist2); // ascending order 
                }
            };
        }
    }

    public static DataStore getInstance(String str) {
        if (stores == null) {
            stores = new ArrayList<DataStore>();
        }

        String[] varNames;
        try {
            varNames = parseVarNames(str);
        } catch (ParseException ex) {
            LOGGER.severe(ex + " at " + ex.getErrorOffset());
            return null;
        }

        // check if the variables are in the store already, otherwise create a new one
        DataStore foundStore = null, store;
        for (int i = 0; i < stores.size(); i++) {
            store = stores.get(i);
            if (store.hasIdenticalVarNames(varNames)) {
                foundStore = store;
                break;
            }
        }

        if (foundStore == null) {
            store = new DataStore(varNames);
            stores.add(store);
            LOGGER.info("Created DataStore for " + str);
        }
        else {
            store = foundStore;
            LOGGER.info("Found exsiting DataStore for " + str);
        }
        return store;
    }

    public static DataStore findStore(String varName) {
        LOGGER.finest("findStore, varName=" + varName);
        DataStore store = null;

        if (stores == null) {
            return null;
        }

        boolean found = false;
        for (int i = 0; i < stores.size(); i++) {
            store = stores.get(i);
            if (store.containsVarName(varName)) {
                LOGGER.finest("Store found!!");
                found = true;
                break;
            }
        }

        return found ? store : null;
    }


    private List<SpatioTempoData> applyClosest(List<SpatioTempoData> data, String arg) {
        LOGGER.finest("Entering applyClosest");

        List<SpatioTempoData> newData = new ArrayList<>();

        int coord = Dimension.parseCoord(arg);
        if (coord == Dimension.UNKNOWN)
            return null;

        if (coord == Dimension.TIME) {
            Date currTime = currLocTime.getTime();
            LOGGER.finest("currTime=" + currTime);

            long minDiff = Long.MAX_VALUE;

            for (int i = 0; i < data.size(); i++) {
                SpatioTempoData stData = data.get(i);

                if (!stData.hasTimes()) {
                    // if the data has no time, we just add all the data to newData
                    newData.add(stData);
                }
                else {
                    long diff = stData.calcTimeDiff(currTime);
                    LOGGER.finest("i=" + i + ", minDiff=" + minDiff + ", diff=" + diff);
                    stData.print();
                    if (diff < minDiff) {
                        LOGGER.finest("Clo, dist < minDist: ");
                        newData.clear();
                        newData.add(stData);
                        minDiff = diff;
                    }
                    else if (diff == minDiff) {
                        LOGGER.finest("Clo, dist == minDist: ");
                        newData.add(stData);
                    }

                    if (diff == 0.0) {
                        LOGGER.finest("Clo, exact data found, exit the loop");
                        break;
                    }
                }
            }
        }
        else {
            // Dimension.X or Y or Z
            double[] currLocation = currLocTime.getLocation();
            double minDiff = Double.MAX_VALUE;

            for (int i = 0; i < data.size(); i++) {
                SpatioTempoData stData = data.get(i);

                if (!stData.hasLocations()) {
                    newData.add(stData);
                }
                else {
                    double diff = stData.calcLocationDiff(coord, currLocation[ coord ]);

                    if (diff < minDiff) {
                        newData.clear();
                        newData.add(stData);
                        minDiff = diff;
                    }
                    else if (diff == minDiff) {
                        newData.add(stData);
                    }

                    if (diff == 0.0) {
                        LOGGER.finest("Clo, exact data found, exit the loop");
                        break;
                    }                    
                }
            }
        }

        for (int i = 0; i < newData.size(); i++) 
            LOGGER.finest("newData[" + i + "]=" + newData.get(i));

        return newData;
    }

    private List<SpatioTempoData> applyEuclidean(List<SpatioTempoData> data, String[] args) {
        LOGGER.finest("Entering applyEuclidean");

        List<SpatioTempoData> newData = new ArrayList<>();

        double[] currLoc = currLocTime.getLocation();
        if (currLoc == null) {
            LOGGER.warning("Current location is null");
            return null;
        }

        int[] coords = new int[3];
        int dimension = args.length;

        for (int i = 0; i < dimension; i++)
            coords[i] = Dimension.parseCoord(args[i]);
        double minDist = Double.MAX_VALUE;

        for (int i = 0; i < currLoc.length; i++)
            LOGGER.finest("Euc, currLoc[" + i + "]=" + currLoc[i]);

        for (int i = 0; i < data.size(); i++) {
            SpatioTempoData stData = data.get(i);

            double diff, sum = 0.0;
            // we can assume 2-D <= dimension
            for (int j = 0; j < dimension; j++) {
                diff = stData.calcLocationDiff(coords[j], currLoc[coords[j]]);
                sum += (diff * diff);
            }
            double dist = Math.sqrt(sum);

            if (dist < minDist) {
                LOGGER.finest("Euc, dist(" + dist + ") < minDist(" + minDist + "): ");
// stData.print();
                newData.clear();
                newData.add(stData);
                minDist = dist;
            }
            else if (dist == minDist) {
// System.out.print("Euc, dist(" + dist + ") == minDist(" + minDist + "): ");
// stData.print();
                newData.add(stData);
            }
        }

        return newData;
    }

    private SpatioTempoData[] sortInTimeDist(List<SpatioTempoData> data, Date currTime) {
        SpatioTempoData[] stDataArray = data.toArray(new SpatioTempoData[data.size()]);
        
        for (int i = 0; i < stDataArray.length; i++) {
            long diff = stDataArray[i].calcTimeDiff(currTime);
            stDataArray[i].setDist((double)diff);
        }

        Arrays.sort(stDataArray, distComparator);
        
        return stDataArray;
    }

    private SpatioTempoData[] sortIn1D_Dist(List<SpatioTempoData> data, int coord, double[] currLoc) {
        SpatioTempoData[] stDataArray = data.toArray(new SpatioTempoData[data.size()]);
        
        for (int i = 0; i < stDataArray.length; i++) {
            double diff = stDataArray[i].calcLocationDiff(coord, currLoc[ coord ]);
            stDataArray[i].setDist(diff);
        }

        Arrays.sort(stDataArray, distComparator);
        
        return stDataArray;
    }

    private SpatioTempoData[] sortIn2D_Dist(List<SpatioTempoData> data, int[] coords, double[] currLoc) {
        SpatioTempoData[] stDataArray = data.toArray(new SpatioTempoData[data.size()]);
        
        for (int i = 0; i < stDataArray.length; i++) {
            double diff, sum = 0.0;
            for (int j = 0; j < coords.length; j++) {
                diff = stDataArray[i].calcLocationDiff(coords[j], currLoc[ coords[j] ]);
                sum += (diff * diff);
            }
            stDataArray[i].setDist(Math.sqrt(sum));
        }

        Arrays.sort(stDataArray, distComparator);
        
        return stDataArray;
    }

    private SpatioTempoData[] sortIn3D_Dist(List<SpatioTempoData> data, int[] coords, double[] currLoc) {
        return sortIn2D_Dist(data, coords, currLoc);
    }

        
    private Double applyInterpolation(List<SpatioTempoData> data, String[] args, int varIndex) {
        LOGGER.finest("Entering applyInterpolation");

        int dimension = args.length - 1; // last arg is n_interp
        int numInterp;
        try {
            numInterp = Integer.parseInt(args[args.length - 1]);
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
            if (args[0].equalsIgnoreCase("t")) {
                // t
                currTime = currLocTime.getTime();
                stDataArray = sortInTimeDist(data, currTime);
            }
            else  {
                // x or y or z
                currLoc = currLocTime.getLocation();
                coords = new int[1];
                coords[0] = Dimension.parseCoord(args[0]);
                stDataArray = sortIn1D_Dist(data, coords[0], currLoc);
            }
            break;

        case Dimension.TWO_DIMENSION:
            // (x,y) or (x,z) or (y,z)
            currLoc = currLocTime.getLocation();
            coords = new int[2];
            for (int i = 0; i < dimension; i++)
                coords[i] = Dimension.parseCoord(args[i]);
            stDataArray = sortIn2D_Dist(data, coords, currLoc);
            break;

        case Dimension.THREE_DIMENSION:
            // (x,y,z)
            currLoc = currLocTime.getLocation();
            coords = new int[3];
            for (int i = 0; i < dimension; i++)
                coords[i] = Dimension.parseCoord(args[i]);
            stDataArray = sortIn3D_Dist(data, coords, currLoc);
            break;
            
        default:
            LOGGER.warning("applyEuclidean failed due to unknown dimension: " + dimension);
            break;
        }

        if (currTime != null)
            LOGGER.finest("currTime=" + currTime);
        
        double sum = 0.0;
        // stDataArray must be sorted in ascending order of whatever distance
        for (int i = 0; i < numInterp; i++) {
            stDataArray[i].print();
            sum += stDataArray[i].getDist();
        }
        double interpVal = 0.0;
        // calculate a weighted sum
        for (int i = 0; i < numInterp; i++) 
            interpVal += (1.0 - stDataArray[i].getDist() / sum) * stDataArray[i].getData(varIndex - 1);
            
        return interpVal;
    }

    private int getVarIndex(String varName) {
        for (int i = 0; i < varNames.length; i++) {
            LOGGER.finest("varNames[" + i + "]=" + varNames[i]);
            if (varNames[i].equals(varName)) {
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

    private synchronized List<DataVector> getDatas(String[] varNames){
    	List<DataVector> result = new ArrayList<>();
    	for (String var : varNames){
	    result.add(new DataVector(findStore(var).getData(var, getMethods(var))));
    	}
    	return result;
    }

    private synchronized void printData(){
        for (String s : methodDictionary.keySet()){
            LOGGER.finest(methodDictionary.get(s).toString());
        }
    }
    
    // editted: every time getData is called, register the current method 
    public synchronized Double getData(String varName, Method[] methods) {
        registerMethods(varName, methods);
        List<SpatioTempoData> workData = new ArrayList<>();
        workData = data;  // shallow copy

        LOGGER.finest("varName=" + varName + ", methods="
                      + methods + ", data.size()=" + data.size());

        int varIndex = getVarIndex(varName);
        
        SpatioTempoData stData;
        Double d = null;
        if (workData.size() == 1) {
            stData = workData.get(0);
            d = stData.getData(varIndex - 1); // -1: workaround due to an issue on parseVarNames 
            return d;
        }

        boolean errorCondition = false;
        boolean interpolated = false;
        boolean predicted = false;
        for (int i = 0; i < methods.length; i++) {
            String[] args = methods[i].getArgs();
            if (args.length == 0) {
                LOGGER.severe("Invalid number of arguments for closest method: " + args.length);
                errorCondition = true;
                break;
            } 

            switch (methods[i].getId()) {
            case Method.CLOSEST:
                // this applies to one of {x, y, z, t}
                if (1 < args.length) {
                    LOGGER.severe("Invalid number of arguments for closest method: " + args.length);
                    errorCondition = true;
                    break;
                } 
                workData = applyClosest(workData, args[0]);
                break;
                
            case Method.EUCLIDEAN:
                // this applies to any combinations of {x, y, z}
                if (3 < args.length) {
                    LOGGER.severe("Invalid number of arguments for euclidean method: " + args.length);
                    errorCondition = true;
                    break;
                }
                workData = (args.length == 1) ? 
                    applyClosest(workData, args[0]) : applyEuclidean(workData, args);
                break;

            case Method.INTERPOLATE:
                // this applies to any combinations of {x, y, z} and t
                // also takes one argument to specify up to how many points to interpolate
                if ((args.length < 2) || (4 < args.length)) {
                    LOGGER.severe("Invalid number arguments for interpolation method: " + args.length);
                    errorCondition = true;
                    break;
                }
                d = applyInterpolation(workData, args, varIndex);
                if (d != null)
                    interpolated = true;
                break;
                
            case Method.MODEL:
                String model = args[0];
            	List<DataVector> result = getDatas(Arrays.copyOfRange(args,1,args.length));
                predicted = true;
            	List<DataVector> dv_tmp = pilots.util.model.Client.predict(model, result);
		// currently supports only one number prediction
		d = dv_tmp.get(0).get(0);
            default:
                break;
            }

            if (errorCondition || (workData == null) || interpolated || predicted) {
                break;
            }

            if (workData.size() == 1) {
                // no need to check methods anymore
                stData = workData.get(0);
                d = stData.getData(varIndex - 1);  // -1: workaround due to an issue on parseVarNames 
                break;
            }

            LOGGER.finest("workData.size()=" + workData.size());
        }

        if (!interpolated && 1 < workData.size() && !predicted) {
            // tie case, give priority to the first one
            stData = workData.get(0);
            d = stData.getData(varIndex - 1);  // -1: workaround due to an issue on parseVarNames 
        }

        // if (varName.equals("air_speed") && (d == 50.0)) {
        //     LOGGER.severe("########## Unusual airspeed found!!");
        //     System.exit(1);
        // }
        
        return d;
    }

    private static String[] parseVarNames(String str) throws ParseException {
        if (str.charAt(0) != '#') {
            throw new ParseException("# not found in the first line", 0);
        }

        String[] varNames = str.split("[#, ]");

        for (int i = 0; i < varNames.length; i++)
            LOGGER.finest("varNames[" + i + "]: " + varNames[i]);

        return varNames;
    }

    private boolean hasIdenticalVarNames(String[] varNames) {
        if (this.varNames.length != varNames.length) {
            return false;
        }

        boolean flag = true;
        for (int i = 0; i < varNames.length; i++) {
            if (0 < varNames[i].length()) {
                flag = false;
                for (int j = 0; j < this.varNames.length; j++) {
                    if (varNames[i].equals(this.varNames[j])) {
                        flag = true;
                        break;
                    }
                }
                
                if (flag == false) {
                    break;
                }
            }
        }

        LOGGER.finest("Found identical varNames: " + flag);

        return flag;
    }

    private boolean containsVarName(String varName) {
        boolean flag = false;

        for (int i = 0; i < this.varNames.length; i++) {
            if (this.varNames[i].equals(varName)) {
                flag = true;
                break;
            }
        }
        
        return flag;
    }

    public String[] getVarNames() {
        return varNames;
    }

    public synchronized int addData(String str) {
        SpatioTempoData stData = new SpatioTempoData();

        if (!stData.parse(str)) {
            LOGGER.severe("parse failed: " + str);
            return -1;
        }

        stData.print();
        
        if (System.getProperty("timeSpan") == null &&
             MAX_DATA_NUM <= data.size()) {
            // remove the oldest data only if working in real-time 
            data.remove(0);
        }
        data.add(stData);

        return data.size();
    }
 }

    
