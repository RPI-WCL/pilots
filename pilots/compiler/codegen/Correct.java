package pilots.compiler.codegen;

public class Correct {
    private int mode;
    private String name;
    private String constant; 
    private String var;
    private String exp;

    public boolean saveState;
    public int saveStateTriggerModeCount;

    public Correct() {
        mode = -1;
        name = null;
        constant = null;
        var = null;
        exp = null;
    }

    public Correct(int mode, String name, String constant, String var, String exp) {
        this.mode = mode;
        this.name = name;
        this.constant = constant;
        this.var = var;
        this.exp = exp;
    }        

    public void setMode(int mode) {
        mode = mode;
    }

    public int getMode() {
        return mode;
    }    

    public void setName(String name) {
        name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setConstant(String constant) {
        constant = constant;
    }

    public String getConstant() {
        return constant;
    }

    public void setVar(String var) {
        var = var;
    }

    public String getVar() {
        return var;
    }

    public void setExp(String exp) {
        exp = exp;
    }

    public String getExp() {
        return exp;
    }
}
