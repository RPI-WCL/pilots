package pilots.compiler.codegen;

import java.util.Vector;

public class Signature {
    private static int global_id = 0;

    private int id_;
    private String name_;
    private String constant_;
    private double value_;
    private String desc_;

    public Signature() {
        id_ = global_id++;
        name_ = null;
        constant_ = null;
        value_ = 0.0;
        desc_ = null;
    }

    public Signature( String name, String constant, String exp, String desc ) {
        id_ = global_id++;
        name_ = name;
        constant_ = constant;
        if (constant.equalsIgnoreCase( "null" ))
            value_ = Double.parseDouble( exp ); // should be constant
        else
            value_ = extractValue( exp );
        desc_ = desc;
    }

    public void setName( String name ) {
        name_ = name;
    }

    public void setConstant( String constant ) {
        constant_ = constant;
    }

    public void setValue( double value ) {
        value_ = value;
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

    public void setDesc( String desc ) {
        desc_ = desc;
    }

    public String getName() {
        return name_;
    }

    public String getConstant() {
        return constant_;
    }

    public double getValue() {
        return value_;
    }
    
    public String getDesc() {
        return desc_;
    }
}
