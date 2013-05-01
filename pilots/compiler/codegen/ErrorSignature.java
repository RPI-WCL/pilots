package pilots.compiler.codegen;

import java.util.Vector;

public class ErrorSignature {
    private static int global_id = 0;

    private int id_;
    private String name_;
    private String constant_;
    private Vector<String> exps_;
    private String desc_;

    public ErrorSignature() {
        id_ = global_id++;
        name_ = null;
        constant_ = null;
        exps_ = new Vector<String>();
        desc_ = null;
    }

    public void setName( String name ) {
        name_ = name;
    }

    public void setConstant( String constant ) {
        constant_ = constant;
    }

    public void addExp( String exp ) {
        exps_.add( exp );
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

    public Vector<String> getExps() {
        return exps_;
    }
    
    public String getDesc() {
        return desc_;
    }
}
