package pilots.compiler.codegen;

import java.util.*;
import pilots.compiler.parser.*;
import pilots.runtime.*;

public class OutputStream {
    private static int globalId = 0;
    private OutputType type;
    private int sockIndex;
    private String[] varNames = null;
    private Set<String> declaredVarNames = null; // use HashSet to avoid duplicate int the variable declaration
    private String exp = null;
    private int interval = -1;

    public OutputStream() {
        this.declaredVarNames = new HashSet<>();
        this.sockIndex = globalId++;
    }

    public void setOutputType(OutputType type) {
        this.type = type;
    }

    public OutputType getOutputType() {
        return type;
    }

    public int getSockIndex() {
        return sockIndex;
    }

    public void setVarNames(String[] varNames) {
        this.varNames = varNames;
    }
    
    public String[] getVarNames() {
        return varNames;
    }

    public void addDeclaredVarNames(String varName) {
        declaredVarNames.add(varName);
    }

    public Set<String> getDeclaredVarNames() {
        return declaredVarNames;
    }
    
    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getExp() {
        return exp;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }
}
