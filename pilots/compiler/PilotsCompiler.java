package pilots.compiler;

import java.io.*;
import java.util.logging.*;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.impl.Arguments;

import pilots.Version;
import pilots.compiler.parser.*;
import pilots.compiler.codegen.*;
import pilots.compiler.trainer.parser.*;
import pilots.compiler.trainer.codegen.*;

public class PilotsCompiler {

    private PilotsParser parser;
    private PilotsCodeGenerator codegen;
    
    private TrainerParser tr_parser;
    private TrainerCodeGenerator tr_codegen;

    private String file;
    private Namespace opts;

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("PilotsCompiler").build()
            .defaultHelp(true)
            .description("Translate PILOTS programs into java code.");
        parser.addArgument("-s", "--sim")
            .action(Arguments.storeTrue())
            .help("Flag to generate simulation code");
	parser.addArgument("-t", "--trainer")
	    .action(Arguments.storeTrue())
	    .help("Flag to compile trainer file");
        parser.addArgument("-o", "--stdout")
            .action(Arguments.storeTrue())
            .help("Flag to print generated code in stdout");
        parser.addArgument("-p", "--package")
            .help("Specify package name");
        parser.addArgument("file")
            .help("File to traslate");

        Namespace opts = null;
        try {
            opts = parser.parseArgs(args);
        } catch (ArgumentParserException ex) {
            parser.handleError(ex);
            System.exit(1);
        }
        
        PilotsCompiler compiler = new PilotsCompiler(opts);
        compiler.compile();
    }

    public PilotsCompiler(Namespace opts) {
        this.opts = opts;
        this.file = opts.get("file");
    }

    public void compile() {
        System.out.println("PILOTS Compiler v" + Version.ver + " compiling " + file + "...");
        try {
	    if ( opts.get("trainer" ) ) { // Compiler trainer
		 // setting a static input stream
		tr_parser = new TrainerParser(new FileReader(file));
		tr_codegen = new TrainerCodeGenerator();
		pilots.compiler.trainer.parser.Node node = tr_parser.Trainer();
		node.jjtAccept(tr_codegen, null);
	    } else { // Compiler pilots program
		// setting a static input stream
		parser = new PilotsParser(new FileReader(file));
		codegen = new PilotsCodeGenerator();
		codegen.setOptions(opts);
		pilots.compiler.parser.Node node = parser.Pilots(); 
		node.jjtAccept(codegen, null);
	    }
	} 
        catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " +  ex.getMessage());
        }
        catch (pilots.compiler.parser.TokenMgrError ex) {
            System.err.println("TokeMgrError: " +  ex.getMessage());
        }
        catch (pilots.compiler.parser.ParseException ex) {
            System.err.println("ParseException: " +  ex.getMessage());
        }
	catch (pilots.compiler.trainer.parser.TokenMgrError ex) {
            System.err.println("TokeMgrError: " +  ex.getMessage());
        }
        catch (pilots.compiler.trainer.parser.ParseException ex) {
            System.err.println("ParseException: " +  ex.getMessage());
        }


    }
}
        
