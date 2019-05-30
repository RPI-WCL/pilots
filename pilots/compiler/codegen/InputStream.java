package pilots.compiler.codegen;

import java.util.*;
import pilots.compiler.parser.*;
import pilots.runtime.*;

public class InputStream {
    private String[] varNames = null;
    private String[] dims = null;
    private List<Method> methods = null;

    public InputStream() {
        this.methods = new ArrayList<>();
    }

    public void setVarNames(String[] varNames) {
        this.varNames = varNames;
    }

    public String[] getVarNames() {
        return varNames;
    }
    
    public void setDims(String[] dims) {
        this.dims = dims;
    }

    public String[] getDims() {
        return dims;
    }
    
    public void addMethod(int id, String[] args) {
        Method method = new Method(id);  // cannot use Method(int id, String... args) here
        method.setArgs(args);
        methods.add(method);
    }

    public List<Method> getMethods() {
        return methods;
    }   
}
