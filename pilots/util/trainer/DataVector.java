package pilots.util.trainer;

import java.util.function.*;
import java.util.*;

public class DataVector {

    private boolean singleton;
    private List<Double> data;

    public DataVector() {
	singleton = false;
	data = new ArrayList<>();
    }

    public DataVector( int value ) {
	singleton = true;
	data = new ArrayList<>();
	data.add( new Double( value ) );
    }
    
    public DataVector( double value ) {
	singleton = true;
	data = new ArrayList<>();
	data.add( value );
    }

    public int size() {
	return data.size();
    }

    public Double get( int index ) {
	return data.get( index );
    }

    public boolean add( double value ) {
	return data.add( value );
    }

    public Double getValue() {
	if ( singleton ) {
	    return data.get(0);
	}
	return null;
    }


    private DataVector applyOp( DoubleBinaryOperator func, DataVector other ) {
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

	
    
    public DataVector add( DataVector other ) {
	DoubleBinaryOperator addOp = (a, b) -> a+b;
	return applyOp( addOp, other );
    }

    public DataVector sub( DataVector other ) {
	DoubleBinaryOperator subOp = (a, b) -> a-b;
	return applyOp( subOp, other );
    }

    public DataVector mult( DataVector other ) {
	DoubleBinaryOperator multOp = (a, b) -> a*b;
	return applyOp( multOp, other );
    }

    public DataVector div( DataVector other ) {
	DoubleBinaryOperator divOp = (a, b) -> a / b;
	return applyOp( divOp, other );
    }

    public DataVector pow( DataVector other ) {
	DoubleBinaryOperator powOp = (a, b) -> Math.pow(a,b);
	return applyOp( powOp, other );
    }

}
