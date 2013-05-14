package pilots.runtime.errsig;

import java.util.Vector;
import pilots.runtime.errsig.Constraint;

public class ErrorSignature {
    public static final int CONST     = 0;
    public static final int LINEAR    = 1;

    private int type_; // CONST or LINEAR
    private double value_; // the value of a constant or slope
    private String desc_;
    private Vector<Constraint> constraints_;
    private Vector<Interval> intervals_;

    public ErrorSignature( int type, double value, String desc ) {
        type_ = type;
        value_ = value;
        desc_ = desc;
        constraints_ = null;
        intervals_ = null;
    }

    public ErrorSignature( int type, double value, String desc, 
                           Vector<Constraint> constraints ) {
        type_ = type;
        value_ = value;
        desc_ = desc;
        constraints_ = constraints;

        intervals_ = new Vector<Interval>();
        for (int i = 0; i < constraints_.size(); i++) {
            Constraint c = constraints_.get( i );
            Interval interval1 = new Interval( c );
            boolean intersects = false;

            for (int j = 0; j < intervals_.size(); j++) {
                Interval interval2 = intervals_.get( j );
                if (interval1.intersects( interval2 )) {
                    // System.out.println( interval1 + " intersects " + interval2 );
                    interval2.merge( interval1 );
                    // System.out.println( "merged=" + interval2 );
                    intersects = true;
                }
            }

            if (!intersects)
                intervals_.add( interval1 );
        }

        // for (int i = 0; i < intervals_.size(); i++) {
        //     Interval interval = intervals_.get( i );
        //     System.out.println( interval );
        // }
    }

    public boolean isConstrained() {
        return (constraints_ != null && 0 < constraints_.size());
    }

    private boolean isInInterval( double point ) {
        boolean flag = false;

        for (int i = 0; i < intervals_.size(); i++) {
            Interval interval = intervals_.get( i );
            if (interval.contains( point )) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public double getClosestEndPoint( double point ) {
        double minDist = Double.MAX_VALUE;
        double closest = 0;

        if (isInInterval( point )) {
            // the point is in one of the intervals
            closest = point;
        }
        else {
            // the point is NOT in the intervals
            for (int i = 0; i < intervals_.size(); i++) {
                Interval interval = intervals_.get( i );
                double[] endPoints = interval.getEndPoints();
                for (int j = 0; j < 2; j++) {
                    double dist = Math.abs( point - endPoints[j] );
                    if (dist < minDist) {
                        minDist = dist;
                        closest = endPoints[j];
                    }
                }
            }
        }

        return closest;
    }

    public int getType() {
        return type_;
    }

    public double getValue() {
        return value_;
    }

    public String getDesc() {
        return desc_;
    }


    public static void main( String[] args ) {
        // Vector<Constraint> constraints = new Vector<Constraint>();
        // constraints.add( new Constraint( Constraint.GREATER_THAN, -10.0 ) );
        // constraints.add( new Constraint( Constraint.LESS_THAN, 10.0 ) );
        // constraints.add( new Constraint( Constraint.GREATER_THAN_OR_EQUAL_TO, -5.0 ) );
        // constraints.add( new Constraint( Constraint.LESS_THAN_OR_EQUAL_TO,5.0 ) );
        // ErrorSignature errSig = new ErrorSignature( ErrorSignature.CONST, 0.0, "Pitot tube + GPS failure", constraints );

        // Vector<Constraint> constraints1 = new Vector<Constraint>();
        // constraints1.add( new Constraint( Constraint.GREATER_THAN, -50.0 ) );
        // constraints1.add( new Constraint( Constraint.LESS_THAN, 25.0 ) );
        // ErrorSignature errSig = new ErrorSignature( ErrorSignature.CONST, 0.0, "Normal", constraints1 );

        // Vector<Constraint> constraints2 = new Vector<Constraint>();
        // constraints2.add( new Constraint( Constraint.GREATER_THAN, 50.0 ) );
        // constraints2.add( new Constraint( Constraint.LESS_THAN, 100.0 ) );
        // ErrorSignature errSig = new ErrorSignature( ErrorSignature.CONST, 0.0, "Pitot tube failure", constraints2 );

        // Vector<Constraint> constraints3 = new Vector<Constraint>();
        // constraints3.add( new Constraint( Constraint.GREATER_THAN, -150.0 ) );
        // constraints3.add( new Constraint( Constraint.LESS_THAN, -100.0 ) );
        // ErrorSignature errSig = new ErrorSignature( ErrorSignature.CONST, 0.0, "GPS failure", constraints3 );

        Vector<Constraint> constraints4 = new Vector<Constraint>();
        constraints4.add( new Constraint( Constraint.GREATER_THAN, -100.0 ) );
        constraints4.add( new Constraint( Constraint.LESS_THAN, -50.0 ) );
        ErrorSignature errSig = new ErrorSignature( ErrorSignature.CONST, 0.0, "Pitot tube + GPS failure", constraints4 );
        
        System.out.println( "closest=" + errSig.getClosestEndPoint( 7.20 ) );
    }
        

}
