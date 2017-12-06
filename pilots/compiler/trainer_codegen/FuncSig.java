package pilots.compiler.trainer_codegen;

import java.util.HashMap;
import java.util.Map;

public class FuncSig{
    public String funcName;
    public Map<String, Double> params;
    public FuncSig(){
        funcName = "";
        params = new HashMap<>();
    }
}