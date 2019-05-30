package pilots.runtime.errsig;


public class Constraint {
    public static final int NULL = -1;
    public static final int GREATER_THAN = 0; 
    public static final int GREATER_THAN_OR_EQUAL_TO = 1;
    public static final int LESS_THAN = 2;
    public static final int LESS_THAN_OR_EQUAL_TO = 3;

    private int ineq; // GREATER_THAN, LESS_THAN, ...
    private double endPoint; // the value of a endpoint

    public Constraint(int ineq, double endPoint) {
        this.ineq = ineq;
        this.endPoint = endPoint;
    }

    public int getIneq() {
        return ineq;
    }

    public double getEndPoint() {
        return endPoint;
    }

}
