package pilots.compiler.trainer_codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

// VectorSchema provides description of a vector comes from a data source. This
// is the meta data for the data source.
public class VectorSchema{
    private List< Map<String, String> > schemas;
    private final String[] fields;

    public VectorSchema(int vectorSize, String[] fields){
        schemas = new ArrayList<>();
        this.fields = fields;
        for (int i = 0; i < vectorSize; i++){
            Map<String, String> map = new HashMap<>();
                for (String f : fields){
                    map.put(f, "");
                }
            schemas.add(map);
        }
    }

    public String[] getFields(){
        return this.fields;
    }

    public int getVectorSize(){
        return schemas.size();
    }

    public boolean put(int index, String field, String value){
        if (index > schemas.size() || index < 0){
            return false;
        }
    
        Map<String, String> d = schemas.get(index);
        if (!d.containsKey(field)){
            return false;
        }
        d.put(field, value);
        return true;
    }

    public String get(int index, String field){
        return schemas.get(index).get(field);
    }
}
