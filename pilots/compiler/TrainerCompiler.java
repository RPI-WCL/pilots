package pilots.compiler;

import java.io.*;
import pilots.compiler.trainer_parser.*;
import pilots.compiler.trainer_codegen.visitor.*;
import pilots.compiler.trainer_codegen.*;
import java.io.BufferedWriter;

import pilots.Version;

public class TrainerCompiler {
    private String filePath;

    public static void main( String[] args ) {
        System.out.println("PILOTS Trainer Compiler v" + Version.ver);
        if (args.length < 1){
            System.err.println("Invalid Argument! Expecting <Path to plt file>");
            return;
        }
        System.out.println( "PILOTS Compiler v" + Version.ver + " compiling " + args[0] + "..." );
        TrainerCompiler compiler = new TrainerCompiler(args[0]);
        compiler.compile();
    }
        
    public TrainerCompiler(String filePath) {
        this.filePath = filePath;
    }

    public void compile() {
        try {
            // parse the file
            Node program = PilotsParser.parseFile(this.filePath);
            // walk the AST Tree and retreive trainer description.
            PilotsObjGen objgen = new PilotsObjGen();
            program.jjtAccept( objgen, null );
            Trainer trainer = objgen.getObj();
            // generate JSON file for machine learning model.
            TrainerJSONGen gen = new TrainerJSONGen(trainer);
            String fileName = trainer.getName() + ".json";
            // generate the file to current location
            FileWriter writer = new FileWriter(new File(fileName));
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            gen.generate(bufferedWriter);
            bufferedWriter.close();
            System.out.println("Generated Training Definition JSON is saved to " + fileName);
        }

        catch ( FileNotFoundException ex ) {
            System.err.println( "FileNotFoundException: " +  ex.getMessage() );
        }
        catch ( TokenMgrError ex ) {
            System.err.println( "TokeMgrError: " +  ex.getMessage() );
        }
        catch ( ParseException ex ) {
            System.err.println( "ParseException: " +  ex.getMessage() );
        }
        catch ( Exception ex ){
            ex.printStackTrace();
            System.err.println("Unexpected Exception: " + ex.getMessage() );
        }

    }
}
        
