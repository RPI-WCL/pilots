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



public class PilotsCompiler {
    private PilotsParser parser;
    private PilotsCodeGenerator codegen;
    private String file;
    private Namespace opts;

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("PilotsCompiler").build()
            .defaultHelp(true)
            .description("Translate PILOTS programs into java code.");
        parser.addArgument("-s", "--sim")
            .action(Arguments.storeTrue())
            .help("Flag to generate simulation code");
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
            parser = new PilotsParser(new FileReader(file)); // setting a static input stream
            codegen = new PilotsCodeGenerator();
            codegen.setOptions(opts);
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
        
