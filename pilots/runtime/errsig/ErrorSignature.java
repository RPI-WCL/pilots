package pilots.runtime.errsig;


public class ErrorSignature {
    public static int CONST     = 0;
    public static int LINEAR    = 1;

    private int type_; // CONST or LINEAR
    private double value_; // the value of a constant or slope
    private String desc_;

    public ErrorSignature( int type, double value, String desc ) {
        type_ = type;
        value_ = value;
        desc_ = desc;
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

}
