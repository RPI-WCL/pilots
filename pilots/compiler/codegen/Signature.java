package pilots.compiler.codegen;

import java.util.Vector;
import java.text.ParseException;
import pilots.compiler.codegen.Constraint;
import pilots.runtime.Value;

public class Signature {
    public static final int CONST = 0;
    public static final int LINEAR = 1;
    private static int global_id = 0;

    private int id_;
    private String name_;
    private int type_;          // LINEAR or CONSTANT
    private String arg_;        // K
    private double value_;
    private Vector<Constraint> constraints_;
    private String desc_;

    public Signature() {
        id_ = global_id++;
        name_ = null;
        arg_ = null;
        constraints_ = null;
        value_ = 0.0;
        desc_ = null;
    }

    public Signature( String name, String arg, String exps, String desc ) {
        id_ = global_id++;
        name_ = name;
        arg_ = arg;

        try {
            parseExps( exps );
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        desc_ = desc;
    }

    public void setName( String name ) {
        name_ = name;
    }

    public void setArg( String arg ) {
        arg_ = arg;
    }

    public void setType( int type ) {
        type_ = type;
    }

    public void setValue( double value ) {
        value_ = value;
    }

    public void parseExps( String exps ) throws ParseException {

        String[] splitExps = exps.split( "," );
        // for (int i = 0; i < splitExps.length; i++) 
        //     System.out.println( "parseExps, splitExps[" + i + "]=" + splitExps[i] );

        if (splitExps.length == 0) {
            throw new ParseException( "no expression found in the signature", 0 );
        }

        // check the first exp
        int i = 0;
        type_ = CONST;
        String delimiter = "+/*"; // parentheses are not supported
        while (i < splitExps[0].length()) {
            if (0 <= delimiter.indexOf( splitExps[0].charAt( i ) )) {
                // if one of "+/*" is found, splitExps[0] must be linear
                type_ = LINEAR;
                break;
            }
            i++;
        }
        if (type_ == LINEAR)
            value_ = extractValue( splitExps[0] );
        else {
            // constant value or 'K'
            if (!arg_.equalsIgnoreCase( splitExps[0] ))
                value_ = Double.parseDouble( splitExps[0] );
        }

        // check the following exps
        constraints_ = null;
        if (1 < splitExps.length) {
            // there must be a constraint

            for (i = 1; i < splitExps.length; i++) {
                Constraint constraint = extractConstraint( splitExps[i] );
                if (constraint != null) {
                    if (constraints_ == null) 
                        constraints_ = new Vector<Constraint>();
                    constraints_.add( constraint );
                    // System.out.println( "constraint=" + constraint );
                }
            }
        } 
    }

    public double extractValue( String exp ) {
        String delimiter = "+/* "; // parentheses are not supported
        String[] tokens = new String[2];

        int multIndex = exp.indexOf( '*' );

        if (multIndex < 0) {
            return -1;
        }

        int i = multIndex - 1;

        // scan backwards
        while (0 <= i && (delimiter.indexOf( exp.charAt( i )) < 0)) {
           i-- ;
        }
        tokens[0] = exp.substring( i+1, multIndex );
        // System.out.println( "extractValue, tokens[0]=" + tokens[0] );

        // scan forwards
        int expLen = exp.length();
        i = multIndex + 1;
        while (i < expLen && (delimiter.indexOf( exp.charAt( i )) < 0)) {
           i++ ;
        }
        tokens[1] = exp.substring( multIndex+1, i );
        // System.out.println( "extractValue, tokens[1]=" + tokens[1] );

        double value = -1.0;
        if (tokens[0].equalsIgnoreCase( "t" )) {
            value = Double.parseDouble( tokens[1] );
        }
        else if (tokens[1].equalsIgnoreCase( "t" )) {
            value = Double.parseDouble( tokens[0] );
        }

        return value;
    }


    public Constraint extractConstraint( String exp ) {
        String comparator = "<>=";
        String[] tokens = new String[2];

        int type = Constraint.NULL;
        int i = 0, j = 0;
        int expLen = exp.length();
        while (i < expLen) {
            // i: start of comparator, j: end of comparator
            if (0 <= comparator.indexOf(exp.charAt( i ))) {
                j = i;
                switch (exp.charAt( i )) {
                case '<':
                    type = Constraint.LESS_THAN;
                    break;
                case '>':
                    type = Constraint.GREATER_THAN;
                    break;
                case '=':
                    // throw exception
                    break;
                }
                if (exp.charAt( j + 1 ) == '=') {
                    type++;
                    j++;
                }
                break;
            }

            i++;
        }

        tokens[0] = exp.substring( 0, i );
        // System.out.println( "extractConstraint, tokens[0]=" + tokens[0] );

        tokens[1] = exp.substring( j + 1 );
        // System.out.println( "extractConstraint, tokens[1]=" + tokens[1] );

        double value = -1.0;
        Constraint constraint = null;
        if (tokens[0].equalsIgnoreCase( arg_ )) {  // arg_ --> 'K'
            value = Double.parseDouble( tokens[1] );
            constraint = new Constraint( type, value );
        }
        else if (tokens[1].equalsIgnoreCase( arg_ )) {
            value = Double.parseDouble( tokens[0] );
            constraint = new Constraint( type, value );
            constraint.invertType();
        }

        return constraint;
    }


    public void setDesc( String desc ) {
        desc_ = desc;
    }

    public String getName() {
        return name_;
    }

    public int getType() {
        return type_;
    }

    public String getArg() {
        return arg_;
    }

    public double getValue() {
        return value_;
    }
    
    public String getDesc() {
        return desc_;
    }
}
