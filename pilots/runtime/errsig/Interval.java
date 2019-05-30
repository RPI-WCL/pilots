package pilots.runtime.errsig;

import pilots.runtime.errsig.Constraint;


public class Interval {
    private static final int NULL = -1;
    private static final int GREATER_THAN = 0; 
    private static final int GREATER_THAN_OR_EQUAL_TO = 1;
    private static final int LESS_THAN = 2;
    private static final int LESS_THAN_OR_EQUAL_TO = 3;

    // representing (a, b), [a, b],...
    private double[] endPoints = new double[2];
    private int[] ineqs = new int[2];
    

    public Interval(double[] endPoints, int[] ineqs) {
        this.endPoints[0] = endPoints[0];
        this.ineqs[0] = ineqs[0];        
        this.endPoints[1] = endPoints[1];        
        this.ineqs[1] = ineqs[1];
    }

    public Interval(Constraint c) {
        switch (c.getIneq()) {
        case Constraint.GREATER_THAN:
        case Constraint.GREATER_THAN_OR_EQUAL_TO:
            endPoints[0] = c.getEndPoint();
            ineqs[0] = c.getIneq();
            endPoints[1] = Double.MAX_VALUE;
            ineqs[1] = LESS_THAN_OR_EQUAL_TO;
            break;
        case Constraint.LESS_THAN:
        case Constraint.LESS_THAN_OR_EQUAL_TO:
            endPoints[0] = -Double.MAX_VALUE;
            ineqs[0] = GREATER_THAN_OR_EQUAL_TO;
            endPoints[1] = c.getEndPoint();
            ineqs[1] = c.getIneq();
            break;
        }
    }

    public boolean intersects(Interval interval) {
        double[] endPoints = interval.getEndPoints();
        boolean intersects = (contains(endPoints[0]) || contains(endPoints[1]));
        return intersects;
    }
        
    public void merge(Interval interval) {
        // modify this instance
        double[] endPoints = interval.getEndPoints();
        int[] ineqs = interval.getIneqs();
        if (this.endPoints[0] < endPoints[0]) {
            this.endPoints[0] = endPoints[0];
            this.ineqs[0] = ineqs[0];
        }
        if (this.endPoints[1] > endPoints[1]) {
            this.endPoints[1] = endPoints[1];
            this.ineqs[1] = ineqs[1];
        }
    }


    public boolean contains(double point) {
        boolean contains =
            ((this.endPoints[0] < point && this.ineqs[0] == GREATER_THAN) ||
             (this.endPoints[0] <= point && this.ineqs[0] == GREATER_THAN_OR_EQUAL_TO)) &&
            ((point < this.endPoints[1] && this.ineqs[1] == LESS_THAN) ||
             (point <= this.endPoints[1] && this.ineqs[1] == LESS_THAN_OR_EQUAL_TO));

        return contains;
    }

    public double[] getEndPoints() {
        return endPoints;
    }

    public int[] getIneqs() {
        return ineqs;
    }

    public String toString() {
        String str = "";
        switch (ineqs[0]) {
        case GREATER_THAN:
            str += "(";
            break;
        case GREATER_THAN_OR_EQUAL_TO:
            str += "[";
            break;
        default:
            str += ineqs[0];
            break;
        }
        str += endPoints[0] + ", " + endPoints[1];
        
        switch (ineqs[1]) {
        case LESS_THAN:
            str += ")";
            break;
        case LESS_THAN_OR_EQUAL_TO:
            str += "]";
            break;
        default:
            str += ineqs[1];
            break;
        }

        return str;
    }

}
