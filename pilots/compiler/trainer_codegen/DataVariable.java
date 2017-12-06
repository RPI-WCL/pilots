package pilots.compiler.trainer_codegen;

/*
DataVariable contains the variable name and the data source of this variable.
*/

import pilots.compiler.trainer_codegen.DataSource;

public class DataVariable{
    private DataSource source;
    private String name;
    private String unit;

    public DataVariable(String name, DataSource source){
        this.name = name;
        this.source = source;
    }

    public void setUnit(String unit){
        this.unit = unit;
    }

    public String getUnit(){
        return this.unit;
    }

    public String getName(){
        return this.name;
    }

    public DataSource getDataSource(){
        return this.source;
    }
}