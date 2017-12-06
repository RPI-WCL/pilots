package pilots.compiler.trainer_codegen;

public class Expression{
    private String exp;
    public Expression(String exp){
        this.exp = exp;
        // this is a hack;
    }
    
    public String toString(){
        return exp;
    }
}