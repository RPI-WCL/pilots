package pilots.runtime;

public class Method {
    public static final int Closest = 0;
    public static final int Euclidean = 1;
    public static final int Interpolate = 2;
    public static final int Predict = 3;

    private int id_;
    private String args_[];

    public Method( int id ) {
        if ((id < Closest) || (Predict < id)) {
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

    public void setArgs( String[] args ) {
        args_ = args;
    }

    public String toString() {
        // this is for the compiler 
        String str = new String();

        // id 
        switch (id_) {
        case Closest:
            str += "Method.Closest";
            break;
        case Euclidean:
            str += "Method.Euclidean";
            break;
        case Interpolate:
            str += "Method.Interpolate";
            break;
        case Predict:
            str += "Method.Predict";
            break;
        default:
            break;
        }
        str += ", ";

        // args
        for (int i = 0; i < args_.length; i++) {
            if (i == 0) 
                str += "\"" + args_[i] + "\"";
            else
                str += ", \"" + args_[i] + "\"";
        }

        return str;
    }
}
