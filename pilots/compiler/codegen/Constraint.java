package pilots.compiler.codegen;


public class Constraint {
    public static final int NULL = -1;
    public static final int GREATER_THAN = 0; 
    public static final int GREATER_THAN_OR_EQUAL_TO = 1;
    public static final int LESS_THAN = 2;
    public static final int LESS_THAN_OR_EQUAL_TO = 3;

    private int type; // GREATER_THAN, LESS_THAN, ...
    private String value; // the value of a endpoint

    public Constraint( int type, String value ) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void invertType() {
        type = (type + 2) % 4;
    }

    public String getTypeString() {
        String str = null;
        switch (type) {
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
        switch (type) {
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
        str += ", " + value;
        return str;
    }
}
