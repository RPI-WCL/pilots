package pilots.runtime;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pilots.runtime.HostsPorts;


public class ArgParser {
    // Supported arguments format:
    // $ java pilots.test.CorrectApp -input=8888 -outputs=127.0.0.1:9998,127.0.0.1:9999 -omega=10 -tau=0.8

    public static void parse( String[] args, 
                              HostsPorts input, HostsPorts outputs,
                              Value omega, Value tau ) throws ParseException {
        Pattern pattern;
        Matcher matcher;

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith( "-input" )) {
                int j = args[i].indexOf( '=' );
                input.addHostPort( null, Integer.parseInt( args[i].substring( j + 1 ) ) );
            }
            else if (args[i].startsWith( "-outputs" )) {
                int j = args[i].indexOf( '=' );
                String str = args[i].substring( j + 1 );
                String[] ipaddrs = str.split( "," );
                
                for (int k = 0; k < ipaddrs.length; k++) {
                    pattern = Pattern.compile( "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+" );
                    matcher = pattern.matcher( ipaddrs[k] );
                    if (!matcher.matches()) {
                        throw new ParseException( "illegal IP Address format found for outputs: ", i );
                    }
                    int colon = ipaddrs[k].indexOf( ':' );
                    outputs.addHostPort( ipaddrs[k].substring( 0, colon ),
                                         Integer.parseInt( ipaddrs[k].substring( colon + 1 ) ) );
                }
            }
            else if (args[i].startsWith( "-omega" )) {
                int j = args[i].indexOf( '=' );
                omega.setValue( Integer.parseInt( args[i].substring( j + 1 ) ) );
            }
            else if (args[i].startsWith( "-tau" )) {
                int j = args[i].indexOf( '=' );
                tau.setValue( Double.parseDouble( args[i].substring( j + 1 ) ) );
            }
            else {
                throw new ParseException( "illegal option found: ", i );
            }
        }
    }


    public static void main (String args[]) {
        // test
        HostsPorts input = new HostsPorts();
        HostsPorts outputs = new HostsPorts();
        Value omega = new Value();
        Value tau = new Value();

        try {
            ArgParser.parse( args, input, outputs, omega, tau );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        System.out.println( "input=" + input );
        System.out.println( "outputs=" + outputs );
        System.out.println( "omega=" + omega );
        System.out.println( "tau=" + tau );
    }
}
