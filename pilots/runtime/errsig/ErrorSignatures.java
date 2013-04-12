package pilots.runtime.errsig;

import java.lang.reflect.Method;


public abstract class ErrorSignatures {
    private int numSignatures_ = 0;
    private String[] descriptions_ = null;
    private String[] names_ = { "s0", "s1", "s2", "s3", "s4" };

    public abstract Double s0( double t, double K );
    public abstract Double s1( double t, double K );
    public abstract Double s2( double t, double K );
    public abstract Double s3( double t, double K );
    public abstract Double s4( double t, double K );

    public ErrorSignatures( int numSignatures, String[] descriptions ) {
        numSignatures_ = numSignatures;
        descriptions_ = descriptions;
    }

    public ErrorSignatures( int numSignatures ) {
        numSignatures_ = numSignatures;
    }

    public int getNumSignatures() {
        return numSignatures_;
    }

    public String getDescription( int mode ) {
        String desc;
        if (descriptions_ == null)
            return null;
        
        if (0 <= mode && mode < numSignatures_)
            desc = descriptions_[mode];
        else 
            desc = "Unknown mode";
        return desc;
    }

    public String getName( int mode ) {
        String name;
        if (0 <= mode && mode < numSignatures_) 
            name = names_[mode];
        else 
            name = null;
        return name;
    }
        
    public Double computeSignature( int mode, double t, double K ) {
        String name = getName( mode );
        
        if (name == null)
            return null;

        Method method = null;;
        Double retval = null;
        try {
            method = this.getClass().getMethod( name, double.class, double.class );
            retval = (Double)method.invoke( this, t, K );
        } catch (Exception ex) {
            System.err.println( ex );
        }

        return retval;
    }

}
