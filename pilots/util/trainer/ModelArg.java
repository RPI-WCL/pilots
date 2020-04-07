
package pilots.util.trainer;

public class ModelArg {

    public enum Type { BOOL, INT, DOUBLE; }

    private Type t;
    private Boolean b;
    private Integer i;
    private Double d;

    public ModelArg( Boolean arg ) {
	t = Type.BOOL;
	b = arg;
	i = null;
	d = null;
    }

    public ModelArg( Integer arg ) {
	t = Type.INT;
	b = null;
	i = arg;
	d = null;
    }

    public ModelArg( Double arg ) {
	t = Type.DOUBLE;
	b = null;
	i = null;
	d = arg;
    }

    public Type getType() {
	return t;
    }

    public Boolean getBoolean() {
	return b;
    }

    public Integer getInteger() {
	return i;
    }

    public Double getDouble() {
	return d;
    }
}
