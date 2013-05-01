package pilots.runtime.errsig;

import java.util.Vector;
import pilots.runtime.errsig.ErrorSignature;
import pilots.runtime.errsig.SlidingWindow;

public class ErrorAnalyzer {
    private Vector<ErrorSignature> errorSigs_;
    private int omega_;
    private double tau_;
    private SlidingWindow win_;


    public ErrorAnalyzer( Vector<ErrorSignature> errorSigs, int omega, double tau ) {
        errorSigs_ = errorSigs;
        omega_ = omega;
        tau_ = tau;
        win_ = new SlidingWindow( omega_ );
    }
    
    public void push( double error ) {
        win_.push( error );
    }

    public int analyze() {
        return 1;
    }

    public String getDesc( int mode ) {
        ErrorSignature errorSig = errorSigs_.get( mode );
        return errorSig.getDesc();
    }
}
