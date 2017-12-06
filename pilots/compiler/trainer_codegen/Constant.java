package pilots.compiler.trainer_codegen;

public class Constant{
    private double value;
    private String name;
    public Constant(String name, double value){
        this.value = value;
        this.name = name;
    }

    public double get(){
        return value;
    }

    public String getName(){
        return name;
    }
}
