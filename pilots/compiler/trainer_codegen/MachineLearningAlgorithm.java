package pilots.compiler.trainer_codegen;

import java.util.Map;

// this is a description of a model to be trained.
public class MachineLearningAlgorithm{
    public String id;
    public String path;
    public Map<String, Double> params;

    public MachineLearningAlgorithm(String type, String algorithmName, Map<String, Double> params){
        // TODO: search for the algorithm id and loading path fall into the type
        // and algorithm name. Temporarily set it to algorithmName directly
        // And the loading path to empty string;
        if (!type.isEmpty() && !algorithmName.isEmpty()){
            this.id = type + "." + algorithmName;
        }else if (type.isEmpty() && !algorithmName.isEmpty()){
            this.id = "." + algorithmName;
        }else{
            this.id = type;
        }
        this.path = "";
        this.params = params;
    }
}