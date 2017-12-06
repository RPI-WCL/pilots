package pilots.compiler.trainer_codegen;

// ModelSource describes a trained machine learning model and the parameters
// given to use the trained machine learning model.
public class ModelSource implements DataSource{
    private String modelPath;
    private DataVariable[] params;

    public ModelSource(String modelPath, DataVariable[] params){
        // modelPath is the path to load the model
        this.modelPath = modelPath;
        // params is the parameters for the model to use.
        this.params = params;
        // TODO: load the model into here.
    }

    public String getPath(){
        return this.modelPath;
    }

    public VectorSchema getSchema(){
        // TODO: implement the schema by specifying the output of the model. 
        // This information should be included in the model.
        return null;
    }

    public String getTypeName(){
        // Type name doesn't make sense here.
        return null;
    }

    public String getName(){
        // TODO: remove getName from the interface because we can directly
        // retrieve the class name.
        return "ModelSource";
    }
}
