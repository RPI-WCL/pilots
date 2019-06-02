package pilots.runtime.errsig;

import java.util.*;
import java.util.logging.Logger;
import pilots.runtime.errsig.ErrorSignature;
import pilots.runtime.errsig.SlidingWindow;
import net.sourceforge.argparse4j.inf.Namespace;


public class ErrorAnalyzer {
    private static Logger LOGGER = Logger.getLogger(ErrorAnalyzer.class.getName());
    
    private List<ErrorSignature> errorSigs;
    private double tau;
    private Namespace opts;


    public ErrorAnalyzer(List<ErrorSignature> errorSigs, double tau, Namespace opts) {
        this.errorSigs = errorSigs;
        this.tau = tau;
        this.opts = opts;
    }

    public int analyze(SlidingWindow win, int frequency) {
        int numSignatures = errorSigs.size();
        int winSize = win.getSize();

        double[] deltas = new double[numSignatures];
        Arrays.fill(deltas, 0.0);

        // calculate deltas array & find min delta
        double minDelta = Double.MAX_VALUE;
        for (int i = 0; i < numSignatures; i++) {
            ErrorSignature errorSig = errorSigs.get(i);

            switch (errorSig.getType()) {
            case ErrorSignature.CONST:
                if (errorSig.isConstrained()) {
                    // with constraints
                    double closestFail = Double.MAX_VALUE;
                    for (int j = 0; j < winSize; j++) {
                        // find a reference point
                        double ref = errorSig.getClosestEndPoint(win.at(j));
                        // System.out.println("win.at(" + j + ")=" + win.at(j) + " --> ref=" + ref);
                        double thisFail = 0.0;
                        for (int k = 0; k < winSize; k++) 
                            thisFail += calcDiff(win.at(k), ref);

                        closestFail = (thisFail < closestFail) ? thisFail : closestFail;
                    }
                    deltas[i] = closestFail;
                }
                else {
                    // no constraints
                    for (int j = 0; j < winSize; j++)
                        deltas[i] += calcDiff(win.at(j), errorSig.getValue());
                }
                break;


            case ErrorSignature.LINEAR:
                double closestFail = Double.MAX_VALUE;
                for (int j = 0; j < winSize; j++) {
                    // use win.at(j) as a reference point
                    double thisFail = 0.0;
                    double ref = win.at(j);
                    for (int k = 0; k < winSize; k++) {
                        double anticipated = ref + ((k - j) * ((double)frequency / 1000) * errorSig.getValue());
                        thisFail += calcDiff(win.at(k), anticipated);
                    }
                    closestFail = (thisFail < closestFail) ? thisFail : closestFail;
                }
                deltas[i] = closestFail;
                break;

            default:
                // error
                break;
            }

            minDelta = (deltas[i] < minDelta) ? deltas[i] : minDelta;
        }


        // compute the mode likelihood vector
        int mode = -1;
        double[] likelihood = new double[numSignatures];
        for (int i = 0; i < numSignatures; i++) {
            likelihood[i] = (deltas[i] == 0) ? 1 : minDelta / deltas[i];
            if (likelihood[i] == 1)
                mode = i;
        }

        // debug info
        String dbgInfo = "d = { ";
        if (opts.get("errorsig_debug")) {
            for (int i = 0; i < numSignatures; i++)
                dbgInfo += deltas[i] + " ";
            dbgInfo += "}, l = { ";
            for (int i = 0; i < numSignatures; i++)
                dbgInfo += likelihood[i] + " ";
            dbgInfo += "}, mode = " + mode;
            LOGGER.INFO(dbgInfo);
        }

        // sort the likelihood vector in asceding order (i.e., likelihood[numSignatures - 1] is the largest)
        Arrays.sort(likelihood);
        if (tau <= likelihood[numSignatures - 2])
            mode = -1;  // unknown

        return mode;
    }

    private double calcDiff(double a, double b) {
        // we can try other delta calculation methods
        double diff = Math.abs(a - b);
        return diff;
    }

    public String getDesc(int mode) {
        if (mode == -1)
            return "Unknown";

        ErrorSignature errorSig = errorSigs.get(mode);
        return errorSig.getDesc();
    }
}
