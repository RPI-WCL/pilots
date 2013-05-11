package pilots.compiler.codegen;


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

    public void invertType() {
        // switch (type_) {
        // case GREATER_THAN:
        //     type_ = LESS_THAN;
        //     break;
        // case GREATER_THAN_OR_EQUAL_TO:
        //     type_ = LESS_THAN_OR_EQUAL_TO;
        //     break;
        // case LESS_THAN:
        //     type_ = GREATER_THAN;
        //     break;
        // case LESS_THAN_OR_EQUAL_TO:
        //     type_ = GREATER_THAN_OR_EQUAL_TO;
        //     break;
        // default:
        //     break;
        // }
        type_ = (type_ + 2) % 4;
    }

    public String getTypeString() {
        String str = null;

        switch (type_) {
        case GREATER_THAN:
            str = "GREATER_THAN";
            break;
        case GREATER_THAN_OR_EQUAL_TO:
            str = "GREATER_THAN_OR_EQUAL_TO";
            break;
        case LESS_THAN:
            str = "LESS_THAN";
            break;
        case LESS_THAN_OR_EQUAL_TO:
            str = "LESS_THAN_OR_EQUAL_TO";
            break;
        default:
            break;
        }

        return str;
    }

    public String toString() {
        String str = "";
        switch (type_) {
        case NULL:
            str += "NULL";
            break;
        case GREATER_THAN:
            str += "GREATER_THAN(>)";
            break;
        case GREATER_THAN_OR_EQUAL_TO:
            str += "GREATER_THAN(>=)";
            break;
        case LESS_THAN:
            str += "LESS_THAN(<)";
            break;
        case LESS_THAN_OR_EQUAL_TO:
            str += "LESS_THAN_OR_EQUAL_TO(<=)";
            break;
        }

        str += ", " + value_;

        return str;
    }
}
