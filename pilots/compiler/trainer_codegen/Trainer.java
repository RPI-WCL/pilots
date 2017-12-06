/*
Trainer contains the definition of a trainer file.
*/
package pilots.compiler.trainer_codegen;

import java.util.HashMap;
import java.util.Map;

public class Trainer{
    public Map<String, DataVariable> dataVarMap;
    public Map<String, Constant> constMap;
    public MachineLearningModel model;
    private String name;

    public Trainer(){
        dataVarMap = new HashMap<>();
        constMap = new HashMap<>();
        
        model = new MachineLearningModel();
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void addDataVar(DataVariable var){
        dataVarMap.put(var.getName(), var);
    }

    public void addConstVar(Constant var){
        constMap.put(var.getName(), var);
    }

    public DataVariable getDataVar(String name){
        return dataVarMap.get(name);
    }
}