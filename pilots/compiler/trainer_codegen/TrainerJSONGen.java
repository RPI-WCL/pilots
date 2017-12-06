package pilots.compiler.trainer_codegen;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import pilots.Version;

import org.json.*;

/*
JSON Generator for the Trainer object.
*/

public class TrainerJSONGen{
    private Trainer trainer;
    private String schemaPath;

    public TrainerJSONGen(Trainer trainer){
        this.trainer = trainer;
    }

    // For version 0.4 only
    @Deprecated
    public TrainerJSONGen(Trainer trainer, String schemaPath){
        this.trainer = trainer;
        this.schemaPath = schemaPath;
    }

    // currently only support file source.
    private JSONObject getDataField() throws IOException{
        JSONObject root = new JSONObject();
        JSONObject constant = new JSONObject();

        for (Constant c : this.trainer.constMap.values()){
            constant.put(c.getName(), c.get());
        }

        String fp = null;
        String typeName = null;
        Map<String, DataSource> sources = new HashMap<>();
        for (DataVariable var :this.trainer.dataVarMap.values()){
            String path = var.getDataSource().getPath();
            sources.put(path, var.getDataSource());
            var.getDataSource().getTypeName();
            if (fp == null){
                fp = path;
            }
            if (typeName == null){
                typeName = var.getDataSource().getTypeName();
            }
        }

        if (this.schemaPath == null){
            // construct the path of schema
            Path path = Paths.get(fp).toAbsolutePath();
            Path schema = path.getParent().resolve("schema.json");
            this.schemaPath = schema.toString();
        }

        JSONArray fileArray = new JSONArray(sources.keySet());

        VectorSchema source_schema = null;
        for (Map.Entry<String, DataSource> source : sources.entrySet()){
            // WARNING: by the requirement of current machine learning model, only
            // files with the same type and schema can be included.
            if (source_schema == null){
                source_schema = source.getValue().getSchema();
            }else{
                VectorSchema schema = source.getValue().getSchema();
                // TODO: Implement VectorSchema comparable.
            }
        }

        // for this version, the schema file is stored in another file.
        // it also requires the data source object to 
        JSONObject schema = new JSONObject();
        JSONArray nameArray = new JSONArray();
        JSONArray unitArray = new JSONArray();
        for (int i = 0; i < source_schema.getVectorSize(); i++){
            String varName = source_schema.get(i, "name");
            String varUnit = trainer.getDataVar(varName).getUnit();
            nameArray.put(varName);
            unitArray.put(varUnit);
        }

        schema.put("names", nameArray);
        boolean toAddUnitArray = true;
        for (Object unit : unitArray){
            if (unit == null){
                toAddUnitArray = false;
                break;
            }
        }
        if (toAddUnitArray){
            schema.put("units", unitArray);
        }
        
        File schemaFile = new File(this.schemaPath);
        schemaFile.createNewFile();

        FileWriter writer = new FileWriter(schemaFile);
        writer.write(schema.toString());
        // WARN: potential IOException with writer unclosed.
        writer.close();

        root.put("schema", this.schemaPath);
        root.put("constants", constant);
        root.put("file", fileArray);
        root.put("type", typeName);
        return root;
    }

    private JSONObject getPreprocessing(){
        if (trainer.model.unitChange == null){
            return null;
        }
        JSONObject root = new JSONObject();
        JSONObject rules = new JSONObject();
        for (Map.Entry<String, String> entry : trainer.model.unitChange.entrySet()){
            rules.put(entry.getKey(), entry.getValue());
        }

        root.put("unit_transformation", rules);
        return root;
    }

    private JSONObject getModel(){
        JSONObject root = new JSONObject();
        JSONArray features = new JSONArray();
        JSONArray labels = new JSONArray();

        for (Expression exp : this.trainer.model.features){
            features.put(exp.toString());
        }
        root.put("features", features);
        for (Expression exp : this.trainer.model.labels){
            labels.put(exp.toString());
        }
        root.put("labels", labels);
        JSONObject algorithm = new JSONObject();
        algorithm.put("id", this.trainer.model.algorithm.id);
        algorithm.put("param", new JSONObject(this.trainer.model.algorithm.params));
        algorithm.put("save_file", this.trainer.getName() + "_" + this.trainer.model.algorithm.id + ".estimator");
        root.put("algorithm", algorithm);
        return root;
    }

    private JSONObject get() throws IOException{
        JSONObject data = getDataField();
        JSONObject preproc = getPreprocessing();
        JSONObject model = getModel();
        JSONObject root = new JSONObject();
        root.put("data", data);
        if (preproc != null){
            // preprocessing is optional.
            root.put("preprocessing", preproc);
        }
        root.put("model", model);
        return root;
    }

    public void generate(Writer writer) throws IOException{
        JSONObject obj = this.get();
        writer.write(obj.toString());
    }
}
