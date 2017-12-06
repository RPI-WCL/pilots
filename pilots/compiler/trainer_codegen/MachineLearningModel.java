package pilots.compiler.trainer_codegen;

/*
Machine Learning Model Description.
*/

import java.util.List;
import java.util.Map;

public class MachineLearningModel{
    public List<Expression> features;
    public List<Expression> labels;
    public MachineLearningAlgorithm algorithm;
    public TrainingMode trainingMode;
    
    // TODO: This is a hack
    public Map<String, String> unitChange;
}