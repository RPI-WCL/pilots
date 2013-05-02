package pilots.runtime;

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
}
