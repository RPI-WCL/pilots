package pilots.compiler.codegen;

import java.util.HashSet;

import pilots.runtime.model.*;

public class OutputStream {
    private OutputType type_;
    private String[] varNames_ = null;
    private HashSet<String> declaredVarNames_ = null; // use HashSet to avoid duplicate int the variable declaration
    private String exp_ = null;
    private int frequency_ = -1;

    public OutputStream() {
        declaredVarNames_ = new HashSet<String> ();
    }

    public void setOutputType( OutputType type ) {
        type_ = type;
    }

    public void setVarNames( String[] varNames ) {
        varNames_ = varNames;
    }

    public void addDeclaredVarNames( String declaredVarNames ) {
        declaredVarNames_.add( declaredVarNames );
    }

    public void setExp( String exp ) {
        exp_ = exp;
    }

    public void setFrequency( int frequency ) {
        frequency_ = frequency;
    }

    public OutputType getOutputType() {
        return type_;
    }

    public String[] getVarNames() {
        return varNames_;
    }

    public HashSet<String> getDeclaredVarNames() {
        return declaredVarNames_;
    }
    
    public String getExp() {
        return exp_;
    }

    public int getFrequency() {
        return frequency_;
    }

}
