package pilots.runtime;


public class ArgParser {

    // System.err.println( "Usage: java " + args[0] +
    //                     "-input <port> -outputs <ipaddr:port>* -errors <ipaddr:port>*" );
    // System.err.println( "Arguments parsing exception:" );
    // System.err.println( "\tError: " + e );    

    public static parse( String[] args, int inputPort, 
                            String[] outputHosts, int[] outputPorts, 
                            String[] errorHosts, int[] errorPorts ) throws ParseException {
        int i, argStart;
        Pattern pattern;
        Macther matcher;

        // parse input
        for (i = 1; i < args.length; i++) {
            if (args[i].equals( "-input" ))
                break;
        }
        if ((i == args.length) || ( args.length <= (i + 1))) {
            throw new ParseException();
        }
        inputPort = Integer.parseInt( args[i + 1] );
        
        // outputs
        for (i = 1; i < args.length; i++) {
            if (args[i].equals( "-outputs" ))
                break;
        }
        if ((i == args.length) || ( args.length <= (i + 1))) {
            throw new ParseException();
        }

        pattern = Pattern.compile( "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+" );
        argStart = i + 1;
        for (i = argStart; i < args.length; i++) {
            matcher = pattern.matcher( args[i] );
            if (!matcher.matches())
                break;
        }
        numOutputs = i - argStart;
        if (0 < numOutputs) {
            outputHosts = new String[numOutputs];
            for (i = 0; i < numOutputs; i++) {
                index colon = args[i].index(':');
                outputHosts[i] = args[i].substring( 0, colon - 1 );
                outputPorts[i] = Integer.parseInt( args[i].substring( colon + 1 ) );
            }
        }

        // errors
        for (i = 1; i < args.length; i++) {
            if (args[i].equals( "-errors" ))
                break;
        }
        if ((i == args.length) || ( args.length <= (i + 1))) {
            throw new ParseException();
        }

        pattern = Pattern.compile( "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+" );
        argStart = i + 1;
        for (i = argStart; i < args.length; i++) {
            matcher = pattern.matcher( args[i] );
            if (!matcher.matches())
                break;
        }
        numErrors = i - argStart;
        if (0 < numErrors) {
            errorHosts = new String[numErrors];
            for (i = 0; i < numErrors; i++) {
                index colon = args[i].index(':');
                errorHosts[i] = args[i].substring( 0, colon - 1 );
                errorPorts[i] = Integer.parseInt( args[i].substring( colon + 1 ) );
            }
        }
    }

}