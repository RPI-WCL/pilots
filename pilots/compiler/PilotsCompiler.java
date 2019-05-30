package pilots.compiler;

import java.io.*;
import pilots.Version;
import pilots.compiler.parser.*;
import pilots.compiler.codegen.*;

public class PilotsCompiler {
    private PilotsParser parser = null;
    private PilotsCodeGenerator codegen = null;
    private String file = null;

    public static void main(String[] args) {
        System.out.println("PILOTS Compiler v" + Version.ver + " compiling " + args[0] + "...");
        PilotsCompiler compiler = new PilotsCompiler(args);
        compiler.compile();
    }
        
    public PilotsCompiler(String[] args) {
        file = args[0];
    }

    public void compile() {
        try {
            parser = new PilotsParser(new FileReader(file)); // setting a static input stream
            codegen = new PilotsCodeGenerator();            
            Node node = parser.Pilots(); 
            node.jjtAccept(codegen, null);
        } 
        catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " +  ex.getMessage());
        }
        catch (TokenMgrError ex) {
            System.err.println("TokeMgrError: " +  ex.getMessage());
        }
        catch (ParseException ex) {
            System.err.println("ParseException: " +  ex.getMessage());
        }

    }
}
        
