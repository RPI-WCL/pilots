package pilots.runtime.estimator.errsig;

import java.util.Vector;
import pilots.runtime.estimator.errsig.Constraint;

public class Interval {
    private static final int NULL = -1;
    private static final int GREATER_THAN = 0; 
    private static final int GREATER_THAN_OR_EQUAL_TO = 1;
    private static final int LESS_THAN = 2;
    private static final int LESS_THAN_OR_EQUAL_TO = 3;

    // representing (a, b), [a, b],...
    private double[] endPoints_ = new double[2];
    private int[] ineqs_ = new int[2];
    

    public Interval( double[] endPoints, int[] ineqs ) {
        endPoints_[0] = endPoints[0];
        ineqs_[0] = ineqs[0];

        endPoints_[1] = endPoints[1];
        ineqs_[1] = ineqs[1];
    }

    public Interval( Constraint c ) {
        switch (c.getIneq()) {
        case Constraint.GREATER_THAN:
        case Constraint.GREATER_THAN_OR_EQUAL_TO:
            endPoints_[0] = c.getEndPoint();
            ineqs_[0] = c.getIneq();
            endPoints_[1] = Double.MAX_VALUE;
            ineqs_[1] = LESS_THAN_OR_EQUAL_TO;
            break;
        case Constraint.LESS_THAN:
        case Constraint.LESS_THAN_OR_EQUAL_TO:
            endPoints_[0] = -Double.MAX_VALUE;
            ineqs_[0] = GREATER_THAN_OR_EQUAL_TO;
            endPoints_[1] = c.getEndPoint();
            ineqs_[1] = c.getIneq();
            break;
        }
    }


    public boolean intersects( Interval interval ) {
        double[] endPoints = interval.getEndPoints();
        boolean intersects = (contains( endPoints[0] ) || contains( endPoints[1] ));

        return intersects;
    }

        
    public void merge( Interval interval ) {
        // modify this instance
        double[] endPoints = interval.getEndPoints();
        int[] ineqs = interval.getIneqs();
        
        if (endPoints_[0] < endPoints[0]) {
            endPoints_[0] = endPoints[0];
            ineqs_[0] = ineqs[0];
        }

        if (endPoints_[1] > endPoints[1]) {
            endPoints_[1] = endPoints[1];
            ineqs_[1] = ineqs[1];
        }
    }


    public boolean contains( double point ) {
        boolean contains =
            ((endPoints_[0] < point && ineqs_[0] == GREATER_THAN) ||
             (endPoints_[0] <= point && ineqs_[0] == GREATER_THAN_OR_EQUAL_TO)) &&
            ((point < endPoints_[1] && ineqs_[1] == LESS_THAN) ||
             (point <= endPoints_[1] && ineqs_[1] == LESS_THAN_OR_EQUAL_TO));

        return contains;
    }

    
    public double[] getEndPoints() {
        return endPoints_;
    }


    public int[] getIneqs() {
        return ineqs_;
    }

    public String toString() {

        String str = "";
        switch (ineqs_[0]) {
        case GREATER_THAN:
            str += "(";
            break;
        case GREATER_THAN_OR_EQUAL_TO:
            str += "[";
            break;
        default:
            str += ineqs_[0];
            break;
        }
        str += endPoints_[0] + ", " + endPoints_[1];
        

        switch (ineqs_[1]) {
        case LESS_THAN:
            str += ")";
            break;
        case LESS_THAN_OR_EQUAL_TO:
            str += "]";
            break;
        default:
            str += ineqs_[1];
            break;
        }

        return str;
    }

}
