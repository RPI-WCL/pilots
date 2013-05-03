package pilots.compiler.codegen;

public class Correct {
    private int mode_;
    private String name_;
    private String constant_; 
    private String var_;
    private String exp_;

    public Correct() {
        mode_ = -1;
        name_ = null;
        constant_ = null;
        var_ = null;
        exp_ = null;
    }

    public Correct( int mode, String name, String constant, String var, String exp ) {
        mode_ = mode;
        name_ = name;
        constant_ = constant;
        var_ = var;
        exp_ = exp;
    }        

    public void setMode( int mode ) {
        mode_ = mode;
    }

    public void setName( String name ) {
        name_ = name;
    }

    public void setConstant( String constant ) {
        constant_ = constant;
    }

    public void setVar( String var ) {
        var_ = var;
    }

    public void setExp( String exp ) {
        exp_ = exp;
    }

    public int getMode() {
        return mode_;
    }

    public String getName() {
        return name_;
    }

    public String getConstant() {
        return constant_;
    }

    public String getVar() {
        return var_;
    }

    public String getExp() {
        return exp_;
    }
}
