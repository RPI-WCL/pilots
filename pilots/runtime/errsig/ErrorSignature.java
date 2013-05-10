package pilots.runtime.errsig;

import java.util.Vector;

public class ErrorSignature {
    public static final int CONST     = 0;
    public static final int LINEAR    = 1;

    private int type_; // CONST or LINEAR
    private double value_; // the value of a constant or slope
    private String desc_;
    private Vector<Constraint> constraints_;

    public ErrorSignature( int type, double value, String desc ) {
        type_ = type;
        value_ = value;
        desc_ = desc;
        constraints_ = null;
    }

    public ErrorSignature( int type, double value, String desc, 
                           Vector<Constraint> constraints ) {
        type_ = type;
        value_ = value;
        desc_ = desc;
        constraints_ = constraints;
    }

    public boolean isConstrained() {
        return (constraints_ != null && 0 < constraints_.size());
    }

    private boolean isConstraintInInterval( double point ) {
        boolean flag = false;

        for (int i = 0; i < constraints_.size(); i++) {
            Constraint c = constraints_.get( i );
  
            if (c.getValue() < point) {
                switch (c.getType()) {
                case Constraint.GREATER_THAN:
                case Constraint.GREATER_THAN_OR_EQUAL_TO:
                    flag = flag || true;
                    break;
                case Constraint.LESS_THAN:
                case Constraint.LESS_THAN_OR_EQUAL_TO:
                    flag = flag || false;
                    break;
                }
            }
            else {
                switch (c.getType()) {
                case Constraint.GREATER_THAN:
                case Constraint.GREATER_THAN_OR_EQUAL_TO:
                    flag = flag || false;
                    break;
                case Constraint.LESS_THAN:
                case Constraint.LESS_THAN_OR_EQUAL_TO:
                    flag = flag || true;
                    break;
                }
            }
        }

        return flag;
    }

    public double getClosestEndPoint( double point ) {
        double minDist = Double.MAX_VALUE;
        double closest = 0;
        

        if (isConstraintInInterval( point ))
            closest = point;
        else {
            for (int i = 0; i < constraints_.size(); i++) {
                Constraint c = constraints_.get( i );
                double dist = Math.abs( point - c.getValue() );
                if (dist < minDist) {
                    minDist = dist;
                    closest = c.getValue();
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
        Vector<Constraint> constraints = new Vector<Constraint>();
        Constraint c1 = new Constraint( Constraint.LESS_THAN, 4 );
        // Constraint c2 = new Constraint( Constraint.LESS_THAN, 5 );
        constraints.add( c1 );
        // constraints.add( c2 );
        
        ErrorSignature errSig = new ErrorSignature( ErrorSignature.LINEAR, 10, null, constraints );
        
        System.out.println( "closest=" + errSig.getClosestEndPoint( 3 ) );
    }
        

}
