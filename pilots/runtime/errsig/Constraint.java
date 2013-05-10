package pilots.runtime.errsig;


public class Constraint {
    public static final int NULL = -1;
    public static final int GREATER_THAN = 0; 
    public static final int GREATER_THAN_OR_EQUAL_TO = 1;
    public static final int LESS_THAN = 2;
    public static final int LESS_THAN_OR_EQUAL_TO = 3;

    private int type_; // GREATER_THAN, LESS_THAN, ...
    private double value_; // the value of a endpoint

    public Constraint( int type, double value ) {
        type_ = type;
        value_ = value;
    }

    public int getType() {
        return type_;
    }

    public double getValue() {
        return value_;
    }

}
