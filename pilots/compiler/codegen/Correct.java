package pilots.compiler.codegen;

public class Correct {
    private int assocSigId_;
    private String name_;
    private String constant_; 
    private String exp_;
    private String desc_;

    public Correct( int assocSigId ) {
        assocSigId_ = assocSigId;
        name_ = null;
        constant_ = null;
        exp_ = null;
        desc_ = null;
    }

    public void setAssocSigId( int assocSigId ) {
        assocSigId_ = assocSigId;
    }

    public void setName( String name ) {
        name_ = name;
    }

    public void setConstant( String constant ) {
        constant_ = constant;
    }

    public void setExp( String exp ) {
        exp_ = exp;
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

    public String getExp() {
        return exp_;
    }
}
