/*
object generator for pilots parser. PilotsCodegen implements PilotsParserVisitor
to generate intermediate stateful information for offline trainer.

Overview of the pipe:
PilotsParser - parsed tree -> 
PilotsCodegen - Trainer object -> 
Pilots Trainer Code
*/

package pilots.compiler.trainer_codegen.visitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pilots.compiler.trainer_parser.*;
import pilots.compiler.trainer_codegen.*;

public class PilotsObjGen implements PilotsParserVisitor{
    private Trainer trainer;

    public PilotsObjGen(){
        trainer = new Trainer();
    }

    public Trainer getObj(){
        return this.trainer;
    }

    public Object visit(SimpleNode node, Object data){
        return null;
    }

    public Object visit(ASTPROGRAM node, Object data){
        // children: name
        String name = (String)node.jjtGetChild(0).jjtAccept(this, null);
        trainer.setName(name);
        // constant? data model
        for (int i = 1; i < node.jjtGetNumChildren(); i++){
            node.jjtGetChild(i).jjtAccept(this, null);
        }
        return null;
    }

    public Object visit(ASTCONSTANTS node, Object data){
        Map<String, Double> varMap = new HashMap<>();
        node.childrenAccept(this, varMap);
        for (Map.Entry<String, Double> constant : varMap.entrySet()){
            trainer.constMap.put(constant.getKey(), new Constant(constant.getKey(), constant.getValue().doubleValue()));
        }
        return null;
    }

    public Object visit(ASTNUMBERASSIGNMENT node, Object data){
        Map<String, Double> varMap = (Map<String, Double>) data;
        String varName = (String) node.jjtGetChild(0).jjtAccept(this, null);
        Double varVal = (Double) node.jjtGetChild(1).jjtAccept(this, null);
        varMap.put(varName, varVal);
        return null;
    }

    public Object visit(ASTReal node, Object data){
        return (Double) node.jjtGetValue();
    }

    public Object visit(ASTData node, Object data){
        node.childrenAccept(this, null);
        return null;
    }

    public Object visit(ASTDataItem node, Object data){
        // TODO: implement model source.
        // these variables are using the same file source
        String[] variables = (String[])node.jjtGetChild(0).jjtAccept(this, null);
        // whether it's a file or a model
        node.jjtGetChild(1).jjtAccept(this, variables);
        return null;
    }

    public Object visit(ASTFile node, Object data){
        // file contains a file path
        String filePath = (String) node.jjtGetChild(0).jjtAccept(this, null);

        FileSource source = new FileSource(filePath);
        String[] varNames = (String[]) data;
        // TODO: this is a hack to provide the files with its column names, this
        // can be resolved by actually accessing the files for its header.
        VectorSchema schema = new VectorSchema(varNames.length, new String[]{"name"});
        for (int i = 0; i < varNames.length; i++){
            trainer.addDataVar(new DataVariable(varNames[i], source));
            schema.put(i, "name", varNames[i]);
        }
        source.setSchema(schema);
        return null;
    }

    public Object visit(ASTModelUser node, Object data){
        // model user contains the model and its parameters.
        // the model should be found in the local file system.
        String[] parameters = (String[]) node.jjtGetChild(0).jjtAccept(this, null);
        
        DataVariable[] params = new DataVariable[parameters.length-1];
        for (int i = 1; i < parameters.length; i ++){
            params[i-1] = trainer.getDataVar(parameters[i]);
        }
    
        DataSource source = new ModelSource(parameters[0], params);
    
        String[] varNames = (String[]) data;
        for (String name : varNames){
            trainer.addDataVar(new DataVariable(name, source));
        }
        return null;
    }

    public Object visit(ASTString node, Object data){
        String value = (String) node.jjtGetValue();
        return value.substring(1, value.length() - 1);
    }

    public Object visit(ASTModel node, Object data){
        // Preprocess/Features/labels/algorithm/training
        node.childrenAccept(this, null);
        return null;
    }

    public Object visit(ASTSchema node, Object data){
        // schema describes the schema of the file, currently this only
        // describes the unit of each column if shown. This will attach to the
        // data variables.
        Object mapObj = node.jjtGetChild(0).jjtAccept(this, null);
        // well wild card is weird but it's ensured the content is String.
        // this is a hack to suppress uncheck cast in Java.
        Map<?, ?> map = (Map<?, ?>) mapObj;
        // map (data variable name -> unit)
        // retrieve the data variable from symbol map
        for (Map.Entry<?, ?> entry : map.entrySet()){
            DataVariable var = trainer.getDataVar((String)entry.getKey());
            var.setUnit((String)entry.getValue());
        }
        return null;
    }

    public Object visit(ASTPreprocess node, Object data){
        // preprocess contains unit change function if it's available.
        Map<String, String> map = (Map<String, String>) node.jjtGetChild(0).jjtAccept(this, null);
        trainer.model.unitChange = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()){
            trainer.model.unitChange.put(entry.getKey(), entry.getValue());
        }
        return null;
    }

    public Object visit(ASTFeatures node, Object data){
        // This is a super hacky way of using the expressions, because they are
        // not handled by this object generation step;
        String allFeatures = (String) node.jjtGetValue();
        String[] featureFormulas = allFeatures.split(",");
        trainer.model.features = new ArrayList<>();
        for (String feature : featureFormulas){
            trainer.model.features.add(new Expression(feature));
        }
        return null;
    }

    public Object visit(ASTLabels node, Object data){
        String allLabels = (String) node.jjtGetValue();
        String[] labelFormulas = allLabels.split(",");
        trainer.model.labels = new ArrayList<>();
        for (String label: labelFormulas){
            trainer.model.labels.add(new Expression(label));
        }
        return null;
    }

    public Object visit(ASTAlgorithm node, Object data){
        Map<String, FuncSig> algo = (Map<String, FuncSig>) node.jjtGetChild(0).jjtAccept(this, null);
        // there should be exactly 1 algorithm returned. 
        // TODO: add a new class describe this.
        String type = algo.keySet().iterator().next();
        FuncSig func = algo.get(type);

        // set the algorithm information in the trainer
        // TODO: this is the part that may requrie the machine learning module
        // to get the parameters automatically.
        trainer.model.algorithm = new MachineLearningAlgorithm(type, func.funcName, func.params);
        return null;
    }

    public Object visit(ASTTraining node, Object data){
        return null;
    }

    public Object visit(ASTMap node, Object data){
        Map<String, String> mapObj = new HashMap<>();
        for (int i = 0; i < node.jjtGetNumChildren(); i++){
            String[] entry = (String[]) node.jjtGetChild(i).jjtAccept(this, null);
            String key = entry[0];
            String value = entry[1];
            mapObj.put(key, value);
        }
        return mapObj;
    }

    public Object visit(ASTMapItem node, Object data){
        String[] result = new String[2];
        result[0] = (String) node.jjtGetChild(0).jjtAccept(this, null);
        result[1] = (String) node.jjtGetChild(1).jjtAccept(this, null);
        return result;
    }

    public Object visit(ASTPredicate node, Object data){
        String varName = (String) node.jjtGetChild(0).jjtAccept(this, null);
        Map<String, FuncSig> mapVarFunc = new HashMap<>();
        if (node.jjtGetNumChildren() == 2){
            mapVarFunc.put(varName, (FuncSig) node.jjtGetChild(1).jjtAccept(this, null));
        }else{
            mapVarFunc.put(varName, new FuncSig());
        }
        return mapVarFunc;
    }

    public Object visit(ASTFuncExp node, Object data){
        FuncSig sig = new FuncSig();
        sig.funcName = (String) node.jjtGetChild(0).jjtAccept(this, null);
        if (node.jjtGetNumChildren() == 2){
            sig.params = (Map<String, Double>) node.jjtGetChild(1).jjtAccept(this, null);
        }else{
            // TODO: put this in the constructor.
            sig.params = new HashMap<>();
        }
        return sig;
    }

    public Object visit(ASTNumMap node, Object data){
        Map<String, Double> map = new HashMap<>();
        node.childrenAccept(this, map);
        return map;
    }

    public Object visit(ASTNumMapItem node, Object data){
        Map<String, Double> map = (Map<String, Double>) data;
        String varName = (String) node.jjtGetChild(0).jjtAccept(this, null);
        Double number = (Double) node.jjtGetChild(1).jjtAccept(this, null);
        map.put(varName, number);
        return null;
    }

    public Object visit(ASTVARS node, Object data){
        String[] variables = new String[node.jjtGetNumChildren()];
        for (int i = 0; i < variables.length; i++){
            variables[i] = (String) node.jjtGetChild(i).jjtAccept(this, null);
        }
        return variables;
    }

    public Object visit(ASTVAR node, Object data){
        return (String) node.jjtGetValue();
    }

    public Object visit(ASTExps node, Object data){
        Expression[] exps = new Expression[node.jjtGetNumChildren()];
        for (int i = 0; i < node.jjtGetNumChildren(); i++){
            exps[i] = (Expression) node.jjtGetChild(i).jjtAccept(this, null);
        }
        return exps;
    }

    public Object visit(ASTExp node, Object data){
        node.childrenAccept(this, null);
        return new Expression((String) node.jjtGetValue());
    }

    public Object visit(ASTExp2 node, Object data){
        node.childrenAccept(this, null);
        return null;
    }

    public Object visit(ASTFunc node, Object data){
        node.childrenAccept(this, null);
        return null;
    }

    public Object visit(ASTNumber node, Object data){
        node.childrenAccept(this, null);
        return null;
    }

    public Object visit(ASTValue node, Object data){
        node.childrenAccept(this, null);
        return node.jjtGetValue();
    }
}