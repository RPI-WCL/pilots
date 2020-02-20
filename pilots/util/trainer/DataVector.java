package pilots.util.trainer;

import java.util.function.*;
import java.util.*;

public class DataVector {

    private boolean singleton;
    private List<Double> data;

    // Default constructor
    public DataVector() {
	singleton = false;
	data = new ArrayList<>();
    }

    // Constant value vector: singleton
    public DataVector( int value ) {
	singleton = true;
	data = new ArrayList<>();
	data.add( new Double( value ) );
    }

    // Constant value vector: singleton    
    public DataVector( double value ) {
	singleton = true;
	data = new ArrayList<>();
	data.add( value );
    }

    // Copy constructor
    public DataVector( DataVector d ) {
	singleton = d.singleton;
	data = new ArrayList<>();
	for ( Double val : d.data ) {
	    data.add( val );
	}
    }

    public int size() {
	return data.size();
    }

    // Get value
    public Double get( int index ) {
	return data.get( index );
    }

    // Append value
    public boolean add( double value ) {
	return data.add( value );
    }

    // Get singleton value
    public Double getValue() {
	if ( singleton ) {
	    return data.get(0);
	}
	return null;
    }

    // =========================================================================

    // Apply Binary Operator
    private DataVector binaryApplyOp( DoubleBinaryOperator func, DataVector other ) {
	DataVector result = new DataVector();
	if ( singleton ) {
	    for ( Double val : other.data ) {
		result.add( func.applyAsDouble( data.get(0), val ) );
	    }
	} else if ( other.singleton ) {
	    for ( Double val : data ) {
		result.add( func.applyAsDouble( val, other.data.get(0) ) );
	    }
	} else {
	    if ( data.size() != other.data.size() ) return null;
	    for ( int idx = 0; idx < data.size(); ++idx ) {
		result.add( func.applyAsDouble( data.get(idx), other.data.get(idx) ) );
	    }
	}
	return result;
    }

    // ==== Definition of Binary Operations ====
    
    public DataVector add( DataVector other ) {
	DoubleBinaryOperator addOp = (a, b) -> a+b;
	return binaryApplyOp( addOp, other );
    }

    public DataVector sub( DataVector other ) {
	DoubleBinaryOperator subOp = (a, b) -> a-b;
	return binaryApplyOp( subOp, other );
    }

    public DataVector mult( DataVector other ) {
	DoubleBinaryOperator multOp = (a, b) -> a*b;
	return binaryApplyOp( multOp, other );
    }

    public DataVector div( DataVector other ) {
	DoubleBinaryOperator divOp = (a, b) -> a / b;
	return binaryApplyOp( divOp, other );
    }

    public DataVector pow( DataVector other ) {
	DoubleBinaryOperator powOp = (a, b) -> Math.pow(a,b);
	return binaryApplyOp( powOp, other );
    }

    // =========================================================================

    private DataVector unaryApplyOp( DoubleUnaryOperator func ) {
	if ( singleton ) {
	    return new DataVector( func.applyAsDouble( getValue() ) );
	} else {
	    DataVector result = new DataVector();
	    for ( int idx = 0; idx < data.size(); ++idx ) {
		result.add( func.applyAsDouble( data.get(idx) ) );
	    }
	    return result;
	}
    }

    
    // ==== Unary Operation Defintions ====

    public static DataVector sin( DataVector other ) {
	DoubleUnaryOperator sinOp = (a) -> Math.sin(a);
	return other.unaryApplyOp( sinOp );
    }

    public static DataVector cos( DataVector other ) {
	DoubleUnaryOperator cosOp = (a) -> Math.cos(a);
	return other.unaryApplyOp( cosOp );
    }

    public static DataVector tan( DataVector other ) {
	DoubleUnaryOperator tanOp = (a) -> Math.tan(a);
	return other.unaryApplyOp( tanOp );
    }

    public static DataVector arcsin( DataVector other ) {
	DoubleUnaryOperator arcsinOp = (a) -> Math.asin(a);
	return other.unaryApplyOp( arcsinOp );
    }

    public static DataVector arccos( DataVector other ) {
	DoubleUnaryOperator arccosOp = (a) -> Math.acos(a);
	return other.unaryApplyOp( arccosOp );
    }

    public static DataVector arctan( DataVector other ) {
	DoubleUnaryOperator arctanOp = (a) -> Math.atan(a);
	return other.unaryApplyOp( arctanOp );
    }

    public static DataVector abs( DataVector other ) {
	DoubleUnaryOperator absOp = (a) -> Math.abs(a);
	return other.unaryApplyOp( absOp );
    }

    
}
