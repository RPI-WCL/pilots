package pilots.runtime.estimator;

/*
Mode defines the current pattern in the data stream.
*/
public class Mode {
    int mode_;

    public Mode() {
        mode_ = 0;
    }

    public Mode( int mode ) {
        mode_ = mode;
    }

    public void setMode( int mode ) {
        mode_ = mode;
    }

    public int getMode() {
        return mode_;
    }

    public String toString() {
        String str = "";
        str += mode_;
        return str;
    }
}
