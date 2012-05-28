package pilots.runtime;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ArgParser {

    // System.err.println( "Usage: java " + args[0] +
    //                     "-input <port> -outputs <ipaddr:port>* -errors <ipaddr:port>*" );
    // System.err.println( "Arguments parsing exception:" );
    // System.err.println( "\tError: " + e );    

    public static void parse( String[] args, int inputPort, 
                            String[] outputHosts, int[] outputPorts, 
                            String[] errorHosts, int[] errorPorts ) throws ParseException {
        int i, argStart;
        Pattern pattern;
        Matcher matcher;

        // parse input
        for (i = 1; i < args.length; i++) {
            if (args[i].equals( "-input" ))
                break;
        }
        if ((i == args.length) || ( args.length <= (i + 1))) {
            throw new ParseException( "No input arguments", i );
        }
        inputPort = Integer.parseInt( args[i + 1] );
        
        // outputs
        for (i = 1; i < args.length; i++) {
            if (args[i].equals( "-outputs" ))
                break;
        }
        if ((i == args.length) || ( args.length <= (i + 1))) {
            throw new ParseException( "No output arguments", i );
        }

        pattern = Pattern.compile( "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+" );
        argStart = i + 1;
        for (i = argStart; i < args.length; i++) {
            matcher = pattern.matcher( args[i] );
            if (!matcher.matches())
                break;
        }
        int numOutputs = i - argStart;
        if (0 < numOutputs) {
            outputHosts = new String[numOutputs];
            for (i = 0; i < numOutputs; i++) {
                int colon = args[i].indexOf(':');
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
            throw new ParseException( "No error arguments", i );
        }

        pattern = Pattern.compile( "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+" );
        argStart = i + 1;
        for (i = argStart; i < args.length; i++) {
            matcher = pattern.matcher( args[i] );
            if (!matcher.matches())
                break;
        }
        int numErrors = i - argStart;
        if (0 < numErrors) {
            errorHosts = new String[numErrors];
            for (i = 0; i < numErrors; i++) {
                int colon = args[i].indexOf(':');
                errorHosts[i] = args[i].substring( 0, colon - 1 );
                errorPorts[i] = Integer.parseInt( args[i].substring( colon + 1 ) );
            }
        }
    }

}