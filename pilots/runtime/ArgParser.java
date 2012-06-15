package pilots.runtime;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pilots.runtime.HostsPorts;


public class ArgParser {

    // System.err.println( "Usage: java " + args[0] +
    //                     "-input <port> -outputs <ipaddr:port>* -errors <ipaddr:port>*" );
    // System.err.println( "Arguments parsing exception:" );
    // System.err.println( "\tError: " + e );    

    public static void parse( String[] args, 
                              HostsPorts input, HostsPorts outputs, HostsPorts errors) throws ParseException {
        int i, argStart, argEnd;
        Pattern pattern;
        Matcher matcher;
        boolean foundInput = false, foundOutputs = false, foundErrors = false;

        // parse input
        for (i = 0; i < args.length; i++) {
            if (args[i].equals( "-input" )) {
                foundInput = true;
                break;
            }
        }
        // System.out.println( "args.length=" + args.length + ",i=" + i );

        if (!foundInput || i == args.length) { // input is mandatory
            throw new ParseException( "No input arguments", i );
        }
        // add port only
        input.addHostPort( null, Integer.parseInt( args[i + 1] ) );
        
        // outputs
        for (i = 0; i < args.length; i++) {
            if (args[i].equals( "-outputs" )) {
                foundOutputs = true;
                break;
            }
        }
        if (foundOutputs) {
            if (i == args.length) {
                throw new ParseException( "No output arguments", i );
            }

            pattern = Pattern.compile( "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+" );
            argStart = i + 1;
            for (i = argStart; i < args.length; i++) {
                matcher = pattern.matcher( args[i] );
                if (!matcher.matches())
                    break;
            }
            int numOutputs = argStart - i;
            if (0 < numOutputs) {
                for (i = 0; i < numOutputs; i++) {
                    int colon = args[i].indexOf(':');
                    outputs.addHostPort( args[i].substring( 0, colon - 1 ), 
                                         Integer.parseInt( args[i].substring( colon + 1 ) ) );
                }
            }
        }

        // errors
        for (i = 0; i < args.length; i++) {
            if (args[i].equals( "-errors" )) {
                foundErrors = true;
                break;
            }
        }
        if (foundErrors) {
            if (i == args.length) {
                throw new ParseException( "No error arguments", i );
            }

            argStart = i + 1;
            // TODO: support "localhost" and "xxx.yyy.zzz"
            pattern = Pattern.compile( "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+" );
            for (i = argStart; i < args.length; i++) {
                matcher = pattern.matcher( args[i] );
                if (!matcher.matches())
                    break;
            }
            argEnd = i - 1;

            System.out.println( "parse, argStart=" + argStart + ", argEnd " + argEnd );

            if (argStart <= argEnd) {
                for (i = argStart; i <= argEnd; i++) {
                    int colon = args[i].indexOf(':');
                    errors.addHostPort( args[i].substring( 0, colon ),
                                        Integer.parseInt( args[i].substring( colon + 1 ) ) );
                }
            }
        }
    }


    public static void parse( String[] args, int inputPort, 
                            String[] outputHosts, int[] outputPorts, 
                            String[] errorHosts, int[] errorPorts ) throws ParseException {
        int i, argStart;
        Pattern pattern;
        Matcher matcher;
        boolean foundInput = false, foundOutputs = false, foundErrors = false;

        // parse input
        for (i = 0; i < args.length; i++) {
            if (args[i].equals( "-input" )) {
                foundInput = true;
                break;
            }
        }
        // System.out.println( "args.length=" + args.length + ",i=" + i );

        if (!foundInput || i == args.length) { // input is mandatory
            throw new ParseException( "No input arguments", i );
        }
        inputPort = Integer.parseInt( args[i + 1] );
        // System.out.println( "inputPort=" + inputPort );
        
        // outputs
        for (i = 0; i < args.length; i++) {
            if (args[i].equals( "-outputs" )) {
                foundOutputs = true;
                break;
            }
        }
        if (foundOutputs) {
            if (i == args.length) {
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
        }

        // errors
        for (i = 0; i < args.length; i++) {
            if (args[i].equals( "-errors" )) {
                foundErrors = true;
                break;
            }
        }
        if (foundErrors) {
            if (i == args.length) {
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

}