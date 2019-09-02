package pilots.runtime;

public class Mode {
    private int mode;

    public Mode() {
        mode = 0;
    }

    public Mode(int mode) {
        this.mode = mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public String toString() {
        String str = "";
        str += mode;
        return str;
    }
}
