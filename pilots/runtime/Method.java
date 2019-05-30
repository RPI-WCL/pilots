package pilots.runtime;

public class Method {
    public static final int CLOSEST = 0;
    public static final int EUCLIDEAN = 1;
    public static final int INTERPOLATE = 2;
    public static final int PREDICT = 3;
    public static final String[] methodNames = {"Method.CLOSEST",
                                                "Method.EUCLIDEAN",
                                                "Method.INTERPOLATE",
                                                "Method.PREDICT"};

    private int id;
    private String args[];

    
    public Method(int id) {
        if ((id < CLOSEST) || (PREDICT < id)) {
            System.err.println("Invalid id: " + id);
            return;
        }

        this.id = id;
        this.args = null;
    }

    public Method(int id, String... args) {
        this.id = id;

        this.args = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            this.args[i] = args[i];
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public int searchArg(String arg) {
        int found = -1;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(arg)) {
                found = i;
                break;
            }
        }

        return found;
    }

    public String toString() {
        // this is for the compiler 
        String str = methodNames[id] + ", ";

        for (int i = 0; i < args.length; i++) {
            if (i == 0) 
                str += "\"" + args[i] + "\"";
            else
                str += ", \"" + args[i] + "\"";
        }

        return str;
    }
}
