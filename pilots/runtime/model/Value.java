package pilots.runtime.model;

public class Value {
    public static final double NULL = Double.MAX_VALUE;
    double value_;

    public Value() {
        value_ = NULL;
    }

    public Value( double value ) {
        value_ = value;
    }

    public void setValue( double value ) {
        value_ = value;
    }

    public double getValue() {
        return value_;
    }

    public String toString() {
        return Double.toString( value_ );
    }
}
