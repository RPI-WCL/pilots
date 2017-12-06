package pilots.compiler.trainer_codegen;

import java.util.Map;

public interface DataSource{
    
    public VectorSchema getSchema();
    // getName is used to distinguish the data sources.
    public String getName(); 
    // getPath to the depended source.
    public String getPath();

    // retrieve the type of the data source.
    public String getTypeName();
}
