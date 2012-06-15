package pilots.runtime;

public class Method {
    public static final int Closest = 0;
    public static final int Euclidean = 1;
    public static final int Interpolate = 2;

    private int id_;
    private String args_[];

    public Method( int id ) {
        if ((id < Closest) || (Interpolate < id)) {
            System.err.println( "Invalid id: " + id );
            return;
        }

        id_ = id;
        args_ = null;
    }

    public Method( int id, String... args ) {
        id_ = id;

        args_ = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            args_[i] = args[i];
        }
    }

    public int getID() {
        return id_;
    }

    public String[] getArgs() {
        return args_;
    }

    public int searchArg( String arg ) {
        int found = -1;

        for (int i = 0; i < args_.length; i++) {
            if (args_[i].equals( arg )) {
                found = i;
                break;
            }
        }

        return found;
    }
}
