package pilots.runtime.errsig;

import pilots.runtime.errsig.ErrorSignatures;
import pilots.runtime.errsig.SlidingWindow;

public class ErrorAnalyzer {
    private ErrorSignatures errorSigs_;
    private double tau_;

    public ErrorAnalyzer( ErrorSignatures errorSigs, double tau ) {
        errorSigs_ = errorSigs;
        tau_ = tau;
    }

    public void test() {
        int numSignatures = errorSigs_.getNumSignatures();
        System.out.println( "errorSigs=" + errorSigs_ );
        System.out.println( "numSignatures=" + numSignatures );
        
        for (int mode = 0; mode < numSignatures; mode++) {
            Double sigVal = errorSigs_.computeSignature( mode, 2, 3.0 );
            System.out.println( "sigVal=" + sigVal );
            System.out.println( "desc=" + errorSigs_.getDescription( mode ) );
        }
    }

    public int analyze( double error, SlidingWindow win, int time ) {
        return 1;
    }
}
