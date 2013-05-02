package pilots.runtime.errsig;

import java.util.Vector;
import pilots.runtime.errsig.ErrorSignature;
import pilots.runtime.errsig.SlidingWindow;

public class ErrorAnalyzer {
    private Vector<ErrorSignature> errorSigs_;
    private double tau_;


    public ErrorAnalyzer( Vector<ErrorSignature> errorSigs, double tau ) {
        errorSigs_ = errorSigs;
        tau_ = tau;
    }

    public int analyze( SlidingWindow win ) {
        return 1;
    }

    public String getDesc( int mode ) {
        ErrorSignature errorSig = errorSigs_.get( mode );
        return errorSig.getDesc();
    }
}
