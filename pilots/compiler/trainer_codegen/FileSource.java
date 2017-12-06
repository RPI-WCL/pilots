package pilots.compiler.trainer_codegen;

public class FileSource implements DataSource{
    private String dataSourcePath;
    private String fileType;
    private VectorSchema schema;

    public FileSource(String dataSourcePath){
        this.dataSourcePath = dataSourcePath;
        setTypeName();
    }

    public FileSource(String dataSourcePath, VectorSchema schema){
        // By default the path supports only the path on mounted file system.
        this.dataSourcePath = dataSourcePath;
        this.schema = schema;
        setTypeName();
    }

    public void setSchema(VectorSchema schema){
        this.schema = schema;
    }

    private void setTypeName(){
        String[] splitted = dataSourcePath.split("\\.");
        this.fileType = splitted[splitted.length - 1];
    }
    public String getPath(){
        return this.dataSourcePath;
    }

    public VectorSchema getSchema(){
        return schema;
    }

    public String getTypeName(){
        return this.fileType;
    }

    public String getName(){
        return "FileSource";
    }
}