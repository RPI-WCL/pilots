package pilots.runtime;

public class Value {
    public static final double NULL = Double.MAX_VALUE;
    private double value;

    public Value() {
        value = NULL;
    }

    public Value(double value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public String toString() {
        return Double.toString(value);
    }
}
