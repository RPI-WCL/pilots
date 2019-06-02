package pilots.compiler.codegen;

import java.text.ParseException;
import java.util.*;
import java.util.regex.*;
import pilots.compiler.codegen.Constraint;
import pilots.runtime.Value;

public class Signature {
    public static final int CONST = 0;
    public static final int LINEAR = 1;

    private int id;
    private String name;
    private int type;          // LINEAR or CONSTANT
    private String arg;        // K
    private double value;
    private List<Constraint> constraints;
    private String desc;

    public Signature() {
        this.id = -1;
        this.name = null;
        this.arg = null;
        this.constraints = null;
        this.value = 0.0;
        this.desc = null;
    }

    public Signature(int id, String arg, String exps, String desc) {
        this.id = id;
        this.name = name;
        this.arg = arg;

        try {
            parseExps(exps);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        this.desc = desc;
    }

    public Signature(String id, String arg, String exps, String desc) {
        this(-1, arg, exps, desc);

        if (id.charAt(0) != 's' && id.charAt(0) != 'S') {
            System.err.println("Illegel start of signature identifier: " + id.charAt(0));
        }        

        int parenIndex = id.indexOf("(");
        String integerIdStr = (0 < parenIndex) ? id.substring(1, parenIndex) : id.substring(1);
        this.id = Integer.parseInt(integerIdStr);
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public String getArg() {
        return arg;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
    
    public List<Constraint> getConstraints() {
        return this.constraints;
    }

    public boolean isConstrained() {
        return (this.constraints != null);
    }
    
    public void parseExps(String exps) throws ParseException {
        String[] splitExps = exps.split(",");

        if (splitExps.length == 0) {
            throw new ParseException("no expression found in the signature", 0);
        }

        // check the first exp
        int i = 0;
        type = CONST;
        String delimiter = "+/*"; // parentheses are not supported
        while (i < splitExps[0].length()) {
            if (0 <= delimiter.indexOf(splitExps[0].charAt(i))) {
                // if one of "+/*" is found, splitExps[0] must be linear
                type = LINEAR;
                break;
            }
            i++;
        }
        if (type == LINEAR)
            this.value = extractValue(splitExps[0]);
        else {
            Pattern pattern = Pattern.compile("[A-Za-z]+");
            Matcher matcher = pattern.matcher(splitExps[0]);

            // constant value or 'K'
            if (this.arg.equals("null") && matcher.matches()) {
                throw new ParseException("constant must be defined on the left side", 0);
            }

            if (!this.arg.equalsIgnoreCase(splitExps[0]))
                this.value = Double.parseDouble(splitExps[0]);
        }

        // check the following exps
        this.constraints = null;
        if (1 < splitExps.length) {
            // there must be a constraint

            for (i = 1; i < splitExps.length; i++) {
                List<Constraint> constraints = extractConstraints(splitExps[i]);
                if (constraints != null) {
                    if (this.constraints == null) 
                        this.constraints = new ArrayList<>();
                    this.constraints.addAll(constraints);
                }
            }
        } 
    }

    public double extractValue(String exp) {
        String delimiter = "+/* "; // parentheses are not supported
        String[] tokens = new String[2];

        int multIndex = exp.indexOf('*');

        if (multIndex < 0) {
            return -1;
        }

        int i = multIndex - 1;

        // scan backwards
        while (0 <= i && (delimiter.indexOf(exp.charAt(i)) < 0)) {
           i-- ;
        }
        tokens[0] = exp.substring(i+1, multIndex);

        // scan forwards
        int expLen = exp.length();
        i = multIndex + 1;
        while (i < expLen && (delimiter.indexOf(exp.charAt(i)) < 0)) {
           i++ ;
        }
        tokens[1] = exp.substring(multIndex+1, i);

        double value = -1.0;
        if (tokens[0].equalsIgnoreCase("t")) {
            value = Double.parseDouble(tokens[1]);
        }
        else if (tokens[1].equalsIgnoreCase("t")) {
            value = Double.parseDouble(tokens[0]);
        }

        return value;
    }


    public List<Constraint> extractConstraints(String exp) {
        String comparator = "<>=";
        String[] tokens = new String[2];

        int type = Constraint.NULL;
        int i = 0, j = 0;
        int expLen = exp.length();
        while (i < expLen) {
            // i: start of comparator, j: end of comparator
            if (0 <= comparator.indexOf(exp.charAt(i))) {
                j = i;
                switch (exp.charAt(i)) {
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
                if (exp.charAt(j + 1) == '=') {
                    type++;
                    j++;
                }
                break;
            }

            i++;
        }

        tokens[0] = exp.substring(0, i);
        tokens[1] = exp.substring(j + 1);

        double value = -1.0;
        List<Constraint> constraints = null;
        Constraint constraint = null;
        // regular constraint
        if (tokens[0].equalsIgnoreCase(this.arg)) {
            constraints = new ArrayList<>();
            constraint = new Constraint(type, tokens[1]);
            constraints.add(constraint);
        }
        else if (tokens[1].equalsIgnoreCase(this.arg)) {
            constraints = new ArrayList<>();
            constraint = new Constraint(type, tokens[0]);
            constraint.invertType();
            constraints.add(constraint);
        }
        // abs constraint support
        else if (tokens[0].equalsIgnoreCase("abs(" + this.arg + ")")) {
            constraints = new ArrayList<>();
            constraint = new Constraint(type, tokens[1]);
            constraints.add(constraint);
            constraint = new Constraint(type, "-" + tokens[1]);
            constraint.invertType();
            constraints.add(constraint);
        }
        else if (tokens[1].equalsIgnoreCase("abs(" + this.arg + ")")) {
            constraints = new ArrayList<>();
            constraint = new Constraint(type, tokens[0]);
            constraint.invertType();
            constraints.add(constraint);
            constraint = new Constraint(type, "-" + tokens[0]);
            constraints.add(constraint);
        }

        return constraints;
    }
}
