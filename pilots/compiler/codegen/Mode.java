package pilots.compiler.codegen;

import java.util.*;


public class Mode {
    private int id;    // TODO: check if the same id is already defined
    private String condition;
    private String desc;

    public Mode() {
        this.id = -1;
        this.condition = null;
        this.desc = null;
    }

    public Mode(int id, String condition, String desc) {
        this.id = id;
        this.condition = condition;
        this.desc = desc;
    }
    
    public Mode(String id, String condition, String desc) {
        this(-1, condition, desc);        

        if (id.charAt(0) != 'm' && id.charAt(0) != 'M') {
            System.err.println("Illegel start of mode identifier: " + id.charAt(0));
        }

        this.id = Integer.parseInt(id.substring(1));
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
    
    public void setDesc( String desc ) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
