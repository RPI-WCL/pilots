package pilots.runtime;

public class Value {
    double value_;

    public Value() {
        value_ = 0.0;
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
