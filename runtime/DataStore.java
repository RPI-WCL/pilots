package pilots.runtime;

// import java.util.concurrent.locks.ReadWriteLock;
// import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Vector;
import java.text.ParseException;
import pilots.runtime.*;

// enum UsingCmd {
//     ClosestX, ClosestY, ClosestZ, ClosestTime, EuclideanXY, EuclideanXYZ, 
//     InterpolateTime, InterpolateX, InterpolateY, InterpolateZ, InterpolateXY, InterpolateXYZ
// };

// enum UsingCmdElement {
//     X, Y, Z, Time
// };


public class DataStore {
    private static Vector<DataStore> stores_ = null;

    // private ReadWriteLock lock;
    private String[] varNames_;
    private Vector<SpatioTempoData> data_;

    public DataStore( String[] varNames ) {
        varNames_ = new String[varNames.length];

        for (int i = 0; i < varNames.length; i++)
            varNames_[i] = varNames[i];        /* shallow copy */

        // lock = new ReentrantReadWriteLock();
        data_ = new Vector<SpatioTempoData>();
    }


    static public DataStore getInstance( String str ) {
        if (stores_ == null) {
            stores_ = new Vector<DataStore>();
        }

        String[] varNames;
        try {
            varNames = parse( str );
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
        }
        else {
            store = foundStore;
        }
            
        return store;
    }

    
    static private String[] parse( String str ) throws ParseException {
        if (str.charAt(0) != '#') {
            throw new ParseException( "# not found in the first line", 0 );
        }

        String[] varNames = str.split( "[#, ]" );

        for (int i = 0; i < varNames.length; i++)
            System.out.println( "varNames[" + i + "]: " + varNames[i] );

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

        System.out.println( "# found identical varNames: " + flag );

        return flag;
    }


    public String[] getVarNames() {
        return varNames_;
    }


    public boolean add( String str ) {
        SpatioTempoData stData = new SpatioTempoData();

        if (!stData.parse( str )) {
            System.err.println( "parse failed: " + str );
            return false;
        }

        stData.print();
        
        data_.add( stData );


        return true;
    }


    // private EnumSet<UsingCmdElement> getUnusedElements( Vector<UsingCmd> cmds ) {
    //     EnumSet<UsingCmdElement> elements = EnumSet.allOf( UsingCmdElements.class );

    //     for (i = 0; i < cmds.size(); i++) {
    //         UsingCmd cmd = cmds.get( i );
    //         switch( cmd ) {
    //         case ClosestX:      
    //             elements.remove( X );
    //             break;
    //         case ClosestY:
    //             elements.remove( Y );
    //             break;
    //         case ClosestZ:
    //             elements.remove( Z );
    //             break;
    //         case ClosestTime:
    //             elements.remove( Time );
    //             break;
    //         case EuclideanXY:
    //             elements.remove( X );
    //             elements.remove( Y );
    //             break;
    //         case EuclideanYZ:
    //             elements.remove( Y );
    //             elements.remove( Z );
    //             break;
    //         case EuclideanXYZ:
    //             elements.remove( X );
    //             elements.remove( Y );
    //             elements.remove( Z );
    //             break;

    //         /* should not reach here */
    //         case InterpolateX:
    //         case InterpolateY:
    //         case InterpolateZ:
    //         case InterpolateTime:
    //         case InterpolateXY:
    //         case InterpolateYZ:
    //         case InterpolateXZ:
    //         case InterpolateXYZ:
    //         default:
    //             break;
    //         }
    //     }

    //     return elements
    // }


    // public Float getVarValue( Vector<UsingCmd> cmds, String varName, float x, float y, float z, Date time ) {
    //     int i, j;
    //     Float value = null;
    //     Vector<SpatioTempoData> interimData = data.clone();

    //     for (i = 0; i < cmds.size(); i++) {
    //         cmd = cmds.get(i);

    //         switch (cmd) {
    //         case ClosestX:
    //             interimData = findClosestX( interimData, x );
    //             break;
    //         case ClosestY:
    //             interimData = findClosestY( interimData, y );
    //             break;
    //         case ClosestZ:
    //             interimData = findClosestZ( interimData, z );
    //             break;
    //         case ClosestTime:
    //             interimData = findClosestX( interimData, time );
    //             break;
    //         case EuclideanXY:
    //             interimData = findEuclideanXY( interimData, x, y );
    //             break;
    //         case EuclideanYZ:
    //             interimData = findEuclideanYZ( interimData, y, z );
    //             break;
    //         case EuclideanXZ:
    //             interimData = findEuclideanYZ( interimData, x, z );
    //             break;
    //         case EuclideanXYZ:
    //             interimData = findEuclideanXYZ( interimData, x, y, z );
    //             break;
    //         case InterpolateX:
    //             value = new Float( InterpolateX( interimData, x ) );
    //             break;
    //         case InterpolateY:
    //             value = new Float( InterpolateY( interimData, y ) );
    //             break;
    //         case InterpolateZ:
    //             value = new Float( InterpolateZ( interimData, z ) );
    //             break;
    //         case InterpolateTime:
    //             value = new Float( InterpolateTime( interimData, time ) );
    //             break;
    //         case InterpolateXY:
    //             value = new Float( InterpolateXY( interimData, x, y ) );
    //             break;
    //         case InterpolateYZ:
    //             value = new Float( InterpolateYZ( interimData, z, y ) );
    //             break;
    //         case InterpolateXZ:
    //             value = new Float( InterpolateXZ( interimData, x, y ) );
    //             break;
    //         case InterpolateXYZ:
    //             value = new Float( InterpolateXYZ( interimData, x, y, z ) );
    //             break;
    //         default:
    //             break;
    //         }
    //     }

    //     if (value == null) {
    //         if ( interimData.size() == 1 )
    //             ; /* get the target value */
    //         else {
    //             /* there are more than one entry of data */
    //             EnumSet<UsingCmdElement> e = getUnusedElements( cmds );
    //             if (!e.contains( Time )) {
    //                 if (e.contains( X ) && !e.contains( Y ) && !e.contains( Z ))
    //                     value = new Float( InterpolateX( interimData, x ) );
    //                 else if (!e.contains( X ) && e.contains( Y ) && !e.contains( Z ))
    //                     value = new Float( InterpolateX( interimData, y ) );
    //                 else if (!e.contains( X ) && !e.contains( Y ) && e.contains( Z ))
    //                     value = new Float( InterpolateX( interimData, z ) );
    //                 else if (e.contains( X ) && e.contains( Y ) && !e.contains( Z ))
    //                     value = new Float( InterpolateXY( interimData, x, y ) );
    //                 else if (!e.contains( X ) && e.contains( Y ) && e.contains( Z ))
    //                     value = new Float( InterpolateYZ( interimData, y, z ) );
    //                 else if (e.contains( X ) && !e.contains( Y ) && e.contains( Z ))
    //                     value = new Float( InterpolateXZ( interimData, x, z ) );
    //                 else if (e.contains( X ) && !e.contains( Y ) && e.contains( Z ))
    //                     value = new Float( InterpolateXYZ( interimData, x, y, z ) );
    //             }
    //             /* if e.contains(Time) is true, return value will be null */
    //         }
    //     }
                
    //     return value;
    // }


    // public float getValue( Vector<UsingCmd> cmds, float x, float y, Date time ) {
    //     return getValue( cmds, x, y, 0, time );
    // }

    // public float getValue( Vector<UsingCmd> cmds, float x, Date time ) {
    //     return getValue( cmds, x, 0, 0, time );
    // }

    // public float getValue( Vector<UsingCmd> cmds, Date time ) {
    //     return getValue( cmds, 0, 0, 0, time );
    // }

    // private Vector<SpatioTempoData> findClosestX( Vector<SpatioTempoData> data, float x ) {
    // }

    // private Vector<SpatioTempoData> findClosestY( Vector<SpatioTempoData> data, float y ) {
    // }

    // private Vector<SpatioTempoData> findClosestZ( Vector<SpatioTempoData> data, float z ) {
    // }

    // private Vector<SpatioTempoData> findClosestTime( Vector<SpatioTempoData> data, Date time  ) {
    // }

    // private Vector<SpatioTempoData> findEuclideanXY( Vector<SpatioTempoData> data, float x, float y ) {
    // }

    // private Vector<SpatioTempoData> findEuclideanXYZ( Vector<SpatioTempoData> data, float x, float y, float z ) {
    // }

    // private float interpolateTime( Vector<SpatioTempoData> data, Date time ) {
    // }

    // private float interpolateX( Vector<SpatioTempoData> data, float x ) {
    // }

    // private float interpolateY( Vector<SpatioTempoData> data, float y ) {
    // }

    // private float interpolateZ( Vector<SpatioTempoData> data, float z ) {
    // }

    // private float interpolateXY( Vector<SpatioTempoData> data, float x, float y ) {
    // }

    // private float interpolateXYZ( Vector<SpatioTempoData> data, float x, float y, float z ) {
    // }

    // public void remove() {
    // }
 }

    