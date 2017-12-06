package pilots.compiler.codegen;

import java.util.Vector;

import pilots.runtime.model.*;

public class InputStream {
    private String[] varNames_ = null;
    private String[] dims_ = null;
    private Vector<Method> methods_ = null;

    public InputStream() {
        methods_ = new Vector<Method> ();
    }

    public void setVarNames( String[] varNames ) {
        varNames_ = varNames;
    }

    public void setDims( String[] dims ) {
        dims_ = dims;
    }

    public void addMethod( int id, String[] args ) {
        Method method = new Method( id );  // cannot use Method( int id, String... args) here
        method.setArgs( args );
        methods_.add(  method );
    }

    public String[] getVarNames() {
        return varNames_;
    }
    
    public String[] getDims() {
        return dims_;
    }

    public Vector<Method> getMethods() {
        return methods_;
    }
    

}
