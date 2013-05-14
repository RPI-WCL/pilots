package pilots.compiler.codegen;

import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import pilots.compiler.parser.*;
import pilots.runtime.*;

public class PilotsCodeGenerator implements PilotsParserVisitor {
    private static final String TAB = "    ";
    private static final String DEFAULT_PACKAGE = "pilots.examples";
    private static int indent = 0;

    private String appName_ = null;
    private Vector<InputStream> inputs_ = null;
    private Vector<OutputStream> outputs_ = null;
    private Vector<OutputStream> errors_ = null;
    private Vector<Signature> sigs_ = null;
    private Vector<Correct> corrects_ = null;
    private String code_ = null;
    private boolean sim_ = false;

    private static int depth = 0;
    
    public static void main( String[] args ) {
        try {
            PilotsParser parser = new PilotsParser( new FileReader(args[0]) );
            Node node = parser.Pilots();
            PilotsCodeGenerator visitor = new PilotsCodeGenerator();
            node.jjtAccept( visitor, null );
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

    }

    public PilotsCodeGenerator() {
        inputs_ = new Vector<InputStream> ();
        outputs_ = new Vector<OutputStream> ();
        errors_ = new Vector<OutputStream> ();
        sigs_ = new  Vector<Signature> ();
        corrects_ = new Vector<Correct> ();
        code_ = new String();

        if (System.getProperty( "sim" ) != null)
            sim_ = true;
    }

    private void goDown( String node ) {
        // for (int i = 0; i < depth; i++)
        //     System.out.print( " " );
        // System.out.println( node );
        depth++;
    }

    private void goUp() {
        depth--;
    }

    private void incIndent() {
        indent++;
    }

    private void decIndent() {
        indent--;
    }

    private String insIndent() {
        String tab = "";
        for (int i = 0; i < indent; i++)
            tab += TAB;

        return tab;
    }

    private String incInsIndent() {
        incIndent();
        return insIndent();
    }

    private String decInsIndent() {
        decIndent();
        return insIndent();
    }

    protected void acceptChildren( SimpleNode node, Object data ) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild( i ).jjtAccept( this, data );
        }
    }

    protected void generateImports() {
        String p = System.getProperty( "package" );
        if (p != null)
            code_ += "package " + p + ";\n";
        else 
            code_ += "package " + DEFAULT_PACKAGE + ";\n";
        code_ += "\n";
        if (sim_) {
            code_ += "import java.io.BufferedReader;\n";
            code_ += "import java.io.InputStreamReader;\n";
        }
        else {
            code_ += "import java.util.Timer;\n";
            code_ += "import java.util.TimerTask;\n";
        }
        code_ += "import java.util.Vector;\n";
        code_ += "import java.net.Socket;\n";
        code_ += "import pilots.runtime.*;\n";
        code_ += "import pilots.runtime.errsig.*;\n";
        code_ += "\n";
    }

    protected void generateClassDeclaration() {
        code_ += "public class " + appName_ + " extends PilotsRuntime {\n";
        if (sim_)
            code_ += incInsIndent() + "private int time_; // msec\n";
        else
            code_ += incInsIndent() + "private Timer timer_;\n";
        for (int i = 0; i < outputs_.size(); i++) {
            OutputStream output = outputs_.get( i );
            String[] outputVarNames = output.getVarNames();
            code_ += insIndent() + "private SlidingWindow win_" + outputVarNames[0] + "_;\n";
        }
        if (sigs_ != null) {
            code_ += insIndent() + "private Vector<ErrorSignature> errorSigs_;\n";
            code_ += insIndent() + "private ErrorAnalyzer errorAnalyzer_;\n";
        }
        code_ += "\n";
    }

    protected void generateConstructor() {
        code_ += insIndent() + "public " + appName_ + "( String args[] ) {\n";
        code_ += incInsIndent() + "try {\n";
        code_ += incInsIndent() + "parseArgs( args );\n";
        code_ += decInsIndent() + "} catch (Exception ex) {\n";
        code_ += incInsIndent() + "ex.printStackTrace();\n";
        code_ += decInsIndent() + "};\n";
        code_ += "\n";
        if (sim_)
            code_ += insIndent() + "time_ = 0;\n";
        else 
            code_ += insIndent() + "timer_ = new Timer();\n";
        code_ += "\n";
        for (int i = 0; i < outputs_.size(); i++) {
            OutputStream output = outputs_.get( i );
            String[] outputVarNames = output.getVarNames();
            code_ += insIndent() + "win_" + outputVarNames[0] + "_ = new SlidingWindow( getOmega() );\n";
        }
        code_ += "\n";
        code_ += insIndent() + "errorSigs_ = new Vector<ErrorSignature>();\n\n";

        int constIndex = 1;
        for (int i = 0; i < sigs_.size(); i++) {
            Signature sig = sigs_.get( i );

            if (sig.isConstrained()) {
                code_ += insIndent() + "Vector<Constraint> constraints" + constIndex + " = new Vector<Constraint>();\n";
                Vector<Constraint> constraints_ = sig.getConstraints();
                for (int j = 0; j < constraints_.size(); j++) {
                    Constraint c = constraints_.get( j );
                    code_ += insIndent() + "constraints" + constIndex + ".add( new Constraint( Constraint." + c.getTypeString() + ", " + c.getValue() + " ) );\n";
                }
            }

            code_ += insIndent() + "errorSigs_.add( new ErrorSignature( ErrorSignature.";
            if (sig.getType() == Signature.CONST)
                code_ += "CONST, ";
            else if (sig.getType() == Signature.LINEAR)
                code_ += "LINEAR, ";
            else {
                System.err.println( "No valid type found for: " + sig );
            }
            if (sig.getDesc() != null)
                code_ += sig.getValue() + ", " + sig.getDesc();
            else 
                code_ += sig.getValue() + ", null";

            if (sig.isConstrained()) {
                code_ += ", constraints" + constIndex + " ) );\n";
                constIndex++;
            }
            else
                code_ += " ) );\n";
            code_ += "\n";
            

        }

        code_ += insIndent() + "errorAnalyzer_ = new ErrorAnalyzer( errorSigs_, getTau() );\n";
        code_ += decInsIndent() + "}\n";
        code_ += "\n";
    }


    protected String replaceVar( String exp, HashMap<String,String> map ) {
        String newExp = "";
        StringTokenizer tokenizer = new StringTokenizer( exp, "()/*+-", true );

        while (tokenizer.hasMoreElements()) {
            String var = (String)tokenizer.nextElement();
            String hashVar = map.get( var );
            if (hashVar != null)
                newExp += hashVar;
            else 
                newExp += var;
        }

        return newExp;
    }

    protected void generateGetCorrectedData() {
        // get all input variables in vars
        Vector<String> vars = new Vector<String>();
        HashMap<String,String> map = new HashMap<String,String>();
        for (int i = 0; i < inputs_.size(); i++) {
            InputStream input = inputs_.get( i );
            String[] inputVarNames = input.getVarNames();
            for (int j = 0; j < inputVarNames.length; j++) {
                vars.add( inputVarNames[j] );
                map.put( inputVarNames[j], inputVarNames[j] + ".getValue()" );
            }
        }

        // declaration
        code_ += insIndent() + "public void getCorrectedData( SlidingWindow win,\n";
        for (int i = 0; i < vars.size(); i++) {
            String var = vars.get( i );
            code_ += insIndent() + "                              ";
            code_ += "Value " + var + ", Value " + var + "_corrected,\n";
        }
        code_ += insIndent() + "                              Mode mode, int frequency ) {\n";

        // body --->
        
        // getData
        incIndent();
        generateGetData();
        // for (int i = 0; i < inputs_.size(); i++) {
        //     InputStream input = inputs_.get( i );
        //     String[] inputVarNames = input.getVarNames();
        //     for (int j = 0; j < inputVarNames.length; j++) {
        //         code_ += insIndent() + inputVarNames[j] + ".setValue( getData( \"";
        //         code_ += inputVarNames[j] + "\", ";

        //         // methods
        //         Vector<Method> methods = input.getMethods();
        //         for (int l = 0; l < methods.size(); l++) {
        //             Method method = methods.get( l );
        //             if (l == 0)
        //                 code_ += "new Method( " + method.toString() + " )";
        //             else 
        //                 code_ += ", new Method( " + method.toString() + " )";
        //         }
        //         code_ += " ) );\n";
        //     }
        // }

        // e
        code_ += insIndent() + "double e = ";
        code_ += replaceVar( replaceMathFuncs( errors_.get( 0 ).getExp() ), map );
        code_ += ";\n";
        code_ += "\n";

        // win & mode
        code_ += insIndent() + "win.push( e );\n";
        code_ += insIndent() + "mode.setMode( errorAnalyzer_.analyze( win, frequency ) );\n";
        code_ += "\n";

        // correct values 
        for (int i = 0; i < vars.size(); i++) {
            String var = vars.get( i );
            code_ += insIndent() + var + "_corrected.setValue( ";
            code_ += map.get( var ) + " );\n";
        }
        if (0 < corrects_.size())
            code_ += insIndent() + "switch (mode.getMode()) {\n";
        for (int i = 0; i < corrects_.size(); i++) {
            Correct correct = corrects_.get( i ) ;
            code_ += insIndent() + "case " + correct.getMode() + ":\n";
            code_ += incInsIndent() + correct.getVar() + "_corrected.setValue( ";
            code_ += replaceVar( replaceMathFuncs( correct.getExp() ), map );
            code_ += " );\n";
            code_ += insIndent() + "break;\n";
            decIndent();
        }
        if (0 < corrects_.size())
            code_ += insIndent() + "}\n";

        code_ += decInsIndent() + "}\n";
        code_ += "\n";
    }

    public void generateGetData() {
        for (int i = 0; i < inputs_.size(); i++) {
            InputStream input = inputs_.get( i );
            String[] inputVarNames = input.getVarNames();
            for (int j = 0; j < inputVarNames.length; j++) {
                code_ += insIndent() + inputVarNames[j] + ".setValue( getData( \"";
                code_ += inputVarNames[j] + "\", ";

                // methods
                Vector<Method> methods = input.getMethods();
                for (int l = 0; l < methods.size(); l++) {
                    Method method = methods.get( l );
                    if (l == 0)
                        code_ += "new Method( " + method.toString() + " )";
                    else 
                        code_ += ", new Method( " + method.toString() + " )";
                }
                code_ += " ) );\n";
            }
        }
    }


    public String replaceMathFuncs( String exp ) {
        String[] funcs1 = { "asin", "acos", "atan" };
        String[] funcs2 = { "sqrt", "sin", "cos", "abs", "PI" };
        String[] funcs3 = { "arcs", "arcc", "arct" };

        for (int i = 0; i < funcs1.length; i++)
            exp = exp.replaceAll( funcs1[i], funcs3[i] );

        for (int i = 0; i < funcs2.length; i++)
            exp = exp.replaceAll( funcs2[i], "Math." + funcs2[i] );

        for (int i = 0; i < funcs3.length; i++) 
            exp = exp.replaceAll( funcs3[i], "Math." + funcs1[i] );

        return exp;
    }


    protected void generateStartOutputs() {
        for (int i = 0; i < outputs_.size(); i++) {
            OutputStream output = outputs_.get( i );

            // method declaration
            String[] outputVarNames = output.getVarNames();
            code_ += insIndent() + "public void startOutput_" + outputVarNames[0] + "() {\n";

            // openSocket
            code_ += incInsIndent() + "try {\n";
            code_ += incInsIndent() + "openSocket( OutputType.Output, " + i + ", \"" + outputVarNames[0] + "\" );\n";
            code_ += decInsIndent() + "} catch ( Exception ex ) {\n";
            code_ += incInsIndent() + "ex.printStackTrace();\n";
            code_ += decInsIndent() + "}\n";
            code_ += insIndent() + "\n";
            
            // timer thread --->
            code_ += insIndent() + "final int frequency = " + output.getFrequency() + ";\n";
            if (sim_)
                code_ += insIndent() + "while (!isEndTime()) {\n";
            else {
                code_ += insIndent() + "timer_.scheduleAtFixedRate( new TimerTask() {\n";
                incIndent();
                code_ += incInsIndent() + "public void run() {\n";
            }

            // variable declaration
            Vector<String> vars = new Vector<String>();
            HashMap<String,String> map = new HashMap<String,String>();
            for (int j = 0; j < inputs_.size(); j++) {
                InputStream input = inputs_.get( j );
                String[] inputVarNames = input.getVarNames();
                for (int k = 0; k < inputVarNames.length; k++) {
                    vars.add( inputVarNames[k] );
                    map.put( inputVarNames[k], inputVarNames[k] + "_corrected.getValue()" );
                }
            }
            incIndent();
            for (int j = 0; j < vars.size(); j++) {
                String var = vars.get( j );
                code_ += insIndent() + "Value " + var + " = new Value();\n";
                code_ += insIndent() + "Value " + var + "_corrected = new Value();\n";
            }
            code_ += insIndent() + "Mode mode = new Mode();\n";
            code_ += "\n";
            code_ += insIndent() + "getCorrectedData( win_" + outputVarNames[0] + "_, ";
            for (int j = 0; j < vars.size(); j++) {
                String var = vars.get( j );
                code_ += var + ", " + var + "_corrected, ";
            }
            code_ += "mode, frequency );\n";
            code_ += insIndent() + "double " + outputVarNames[0] + " = ";
            code_ += replaceVar( replaceMathFuncs(output.getExp()), map ) + ";\n";
            code_ += "\n";

            // errorAnalyzer
            code_ += insIndent() + "String desc = errorAnalyzer_.getDesc( mode.getMode() );\n";
            code_ += insIndent() + "dbgPrint( desc + \", " + outputVarNames[0] + "=\" + " + outputVarNames[0] + " + \" at \" + getTime() );\n";
            code_ += "\n";

            // sendData
            code_ += insIndent() + "try {\n";
            code_ += incInsIndent() + "sendData( OutputType.Output, " + i + ", " + outputVarNames[0] + " );\n";
            code_ += decInsIndent() + "} catch ( Exception ex ) {\n";
            code_ += incInsIndent() + "ex.printStackTrace();\n";
            code_ += decInsIndent() + "}\n";

            if (sim_) {
                code_ += "\n";
                code_ += insIndent() + "time_ += frequency;\n";
                code_ += insIndent() + "progressTime( frequency );\n";
                code_ += decInsIndent() + "}\n";
                code_ += "\n";
                code_ += insIndent() + "dbgPrint( \"Finished at \" + getTime() );\n";
            }
            else {
                code_ += decInsIndent() + "}\n";
                decIndent();
                code_ += decInsIndent() + "}, 0, frequency );\n";
            }

            code_ += decInsIndent() + "}\n";
            code_ += "\n";
        }
    }

    protected void generateStartOutputsNoCorrection() {
        for (int i = 0; i < outputs_.size(); i++) {
            OutputStream output = outputs_.get( i );

            // method declaration
            String[] outputVarNames = output.getVarNames();
            code_ += insIndent() + "public void startOutput_" + outputVarNames[0] + "() {\n";

            // openSocket
            code_ += incInsIndent() + "try {\n";
            code_ += incInsIndent() + "openSocket( OutputType.Output, " + i + ", \"" + outputVarNames[0] + "\" );\n";
            code_ += decInsIndent() + "} catch ( Exception ex ) {\n";
            code_ += incInsIndent() + "ex.printStackTrace();\n";
            code_ += decInsIndent() + "}\n";
            code_ += insIndent() + "\n";
            
            // timer thread --->
            code_ += insIndent() + "final int frequency = " + output.getFrequency() + ";\n";
            if (sim_)
                code_ += insIndent() + "while (!isEndTime()) {\n";
            else {
                code_ += insIndent() + "timer_.scheduleAtFixedRate( new TimerTask() {\n";
                incIndent();
                code_ += incInsIndent() + "public void run() {\n";
            }

            // variable declaration
            Vector<String> vars = new Vector<String>();
            HashMap<String,String> map = new HashMap<String,String>();
            for (int j = 0; j < inputs_.size(); j++) {
                InputStream input = inputs_.get( j );
                String[] inputVarNames = input.getVarNames();
                for (int k = 0; k < inputVarNames.length; k++) {
                    vars.add( inputVarNames[k] );
                    map.put( inputVarNames[k], inputVarNames[k] + ".getValue()" );
                }
            }
            incIndent();
            for (int j = 0; j < vars.size(); j++) {
                String var = vars.get( j );
                code_ += insIndent() + "Value " + var + " = new Value();\n";
            }
            code_ += "\n";
            
            // getData
            generateGetData();

            code_ += insIndent() + "double " + outputVarNames[0] + " = ";
            code_ += replaceVar( replaceMathFuncs(output.getExp()), map ) + ";\n";
            code_ += "\n";

            code_ += insIndent() + "dbgPrint( \"" + outputVarNames[0] + "=\" + " + outputVarNames[0] + " + \" at \" + getTime() );\n";

            // sendData
            code_ += insIndent() + "try {\n";
            code_ += incInsIndent() + "sendData( OutputType.Output, " + i + ", " + outputVarNames[0] + " );\n";
            code_ += decInsIndent() + "} catch ( Exception ex ) {\n";
            code_ += incInsIndent() + "ex.printStackTrace();\n";
            code_ += decInsIndent() + "}\n";

            if (sim_) {
                code_ += "\n";
                code_ += insIndent() + "time_ += frequency;\n";
                code_ += insIndent() + "progressTime( frequency );\n";
                code_ += decInsIndent() + "}\n";
                code_ += "\n";
                code_ += insIndent() + "dbgPrint( \"Finished at \" + getTime() );\n";
            }
            else {
                code_ += decInsIndent() + "}\n";
                decIndent();
                code_ += decInsIndent() + "}, 0, frequency );\n";
            }

            code_ += decInsIndent() + "}\n";
            code_ += "\n";
        }
    }

    protected void generateMain() {
        code_ += insIndent() + "public static void main( String[] args ) {\n";
        code_ += incInsIndent() + appName_ + " app = new " + appName_ + "( args );\n";
        code_ += insIndent() + "app.startServer();\n";

        if (sim_) {
            code_ += "\n";
            code_ += insIndent() + "BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );\n";
            code_ += insIndent() + "System.out.println( \"Hit any key after running the clients\" );\n";
            code_ += insIndent() + "try {\n";
            code_ += incInsIndent() + "reader.readLine();\n";
            code_ += decInsIndent() + "} catch (Exception ex) {\n";
            code_ += incInsIndent() + "ex.printStackTrace();\n";
            code_ += decInsIndent() + "}\n";
            code_ += "\n";
        }

        for (int i = 0; i < outputs_.size(); i++) {
            OutputStream output = outputs_.get( i );
            String[] outputVarNames = output.getVarNames();
            code_ += insIndent() + "app.startOutput_" + outputVarNames[0] + "();\n";
        }
        code_ += decInsIndent() + "}\n";
        code_ += decInsIndent() + "}\n";
    }


    protected void generateCode() {
        boolean correction = (0 < errors_.size() && 0 < sigs_.size());
        // System.out.println( "####### correction=" + correction + 
        //                     ",e=" + errors_.size() + 
        //                     ",s=" + sigs_.size() + 
        //                     ",c=" + corrects_.size() );

        generateImports();
        generateClassDeclaration();
        generateConstructor();
        if (correction) {
            generateGetCorrectedData();
            generateStartOutputs();
        }
        else {
            generateStartOutputsNoCorrection();
        }
        generateMain();
    }

    protected void outputCode() {
        if (System.getProperty( "stdout" ) != null) {
            System.out.println( code_ );
        }
        else {
            try {
                File file = new File( appName_ + ".java" );
                PrintWriter pw = new PrintWriter( new BufferedWriter( new FileWriter( file ) ) );
                pw.print( code_ );
                pw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    public Object visit( ASTPilots node, Object data ) {
        goDown( "Pilots" );

        appName_ = (String) node.jjtGetValue();
        appName_ = appName_.substring(0, 1).toUpperCase() + appName_.substring(1);
        // System.out.println( "ASTPilots: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );

        acceptChildren( node, null );
        generateCode();
        outputCode();

        goUp();

        return null;
    }

    public Object visit(ASTInput node, Object data) {
        goDown( "Input" );

        // System.out.println( "ASTInput: value=" + node.jjtGetValue() + 
        //                     ",#children=" + node.jjtGetNumChildren() +
        //                     ",data=" + data );
        InputStream input = new InputStream();
        String[] varNames = ((String) node.jjtGetValue()).split( "," );
        // for (int i = 0; i < varNames.length; i++) 
        //     System.out.println( "Input, varNames=" + varNames[i] );
        input.setVarNames( varNames );
        inputs_.add( input );

        acceptChildren( node, input );

        goUp();

        return null;
    }
    public Object visit(ASTOutput node, Object data) {
        goDown( "Output" );

        // System.out.println( "ASTOutput: value=" + node.jjtGetValue() + 
        //                     ",#children=" + node.jjtGetNumChildren() +
        //                     ",data=" + data );
        OutputStream output = new OutputStream();
        output.setOutputType( OutputType.Output );

        String[] str = ((String) node.jjtGetValue()).split( ":" );
        // for (int i = 0; i < str.length; i++) 
        //     System.out.println( "Output, str=" + str[i] );

        String[] varNames = str[0].split( "," );
        output.setVarNames( varNames );
        output.setExp( str[1] );

        int unit = 0;
        if (str[3].equalsIgnoreCase( "nsec" ) || str[3].equalsIgnoreCase( "usec" )) {
            unit = 0;
        }
        else if (str[3].equalsIgnoreCase( "msec" )) {
            unit = 1;
        }
        else if (str[3].equalsIgnoreCase( "sec" )) {
            unit = 1000;
        }
        else if (str[3].equalsIgnoreCase( "min" )) {
            unit = 60 * 1000;
        }
        else if (str[3].equalsIgnoreCase( "hour" )) {
            unit = 60 * 60 * 1000;
        }
        else if (str[3].equalsIgnoreCase( "day" )) {
            unit = 24 * 60 * 60 * 1000;
        }
        output.setFrequency( (int)(Double.parseDouble( str[2] ) * unit) ); // msec
        outputs_.add( output );

        node.jjtGetChild( 1 ).jjtAccept( this, output ); // accept Exps() only

        goUp();

        return null;
    }

    public Object visit(ASTError node, Object data) {
        goDown( "Error" );

        // System.out.println( "ASTError: value=" + node.jjtGetValue() + 
        //                     ",#children=" + node.jjtGetNumChildren() +
        //                     ",data=" + data );
        OutputStream output = new OutputStream();
        output.setOutputType( OutputType.Error );

        String[] str = ((String) node.jjtGetValue()).split( ":" );
        // for (int i = 0; i < str.length; i++) 
        //     System.out.println( "Output, str=" + str[i] );

        String[] varNames = str[0].split( "," );
        output.setVarNames( varNames );
        output.setExp( str[1] );
        output.setFrequency( -1 ); // No frequency for error
        errors_.add( output );

        node.jjtGetChild( 1 ).jjtAccept( this, output ); // accept Exps() only

        goUp();

        return null;
    }

    public Object visit(ASTSignature node, Object data) {
        goDown( "Signature" );

        // System.out.println( "ASTSignature: value=" + node.jjtGetValue() + 
        //                     ",#children=" + node.jjtGetNumChildren() +
        //                     ",data=" + data );

        String[] str = ((String) node.jjtGetValue()).split( ":" );
        // for (int i = 0; i < str.length; i++) 
        //     System.out.println( "Signature, str[" + i + "]=" + str[i] );

        Signature sig = new Signature( str[0], str[1], str[3], str[4] );
        sigs_.add( sig );

        goUp();
        return null;
    }

    public Object visit(ASTCorrect node, Object data) {
        goDown( "Correct" );

        // System.out.println( "ASTCorrect: value=" + node.jjtGetValue() + 
        //                     ",#children=" + node.jjtGetNumChildren() +
        //                     ",data=" + data );

        String[] str = ((String) node.jjtGetValue()).split( ":" );
        // for (int i = 0; i < str.length; i++) 
        //     System.out.println( "Correct, str[" + i + "]=" + str[i] );
        
        int mode = -1;
        for (int i = 0; i < sigs_.size(); i++) {
            Signature sig = sigs_.get( i ) ;
            if (sig.getName().equalsIgnoreCase( str[0] )) {
                mode = i;
                break;
            }
        }

        Correct correct = new Correct( mode, str[0], str[1], str[2], str[3] );
        corrects_.add( correct );

        goUp();

        return null;
    }

    public Object visit(ASTVars node, Object data) {
        goDown( "Vars" );        
        // System.out.println( "ASTVars: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );

        goUp();
        return null;
    }

    public Object visit(ASTConst node, Object data) {
        goDown( "Const" );        
        // System.out.println( "ASTConst: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );

        goUp();
        return null;
    }

    public Object visit(ASTDim node, Object data) {
        goDown( "Dim" );
        // System.out.println( "ASTDim: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        goUp();
        return null;
    }

    public Object visit(ASTMethod node, Object data) {
        goDown( "Method" );
        // System.out.println( "ASTMethod: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );

        InputStream input = (InputStream)data;
        String[] str = ((String) node.jjtGetValue()).split( ":" );
        String[] args = str[1].split( "," );
        // System.out.println( "str[0]=" + str[0] );
        // System.out.println( "str[1]=" + str[1] );

        int id;
        if (str[0].equalsIgnoreCase( "closest" )) {
            id = Method.Closest;
        } else if (str[0].equalsIgnoreCase( "euclidean" )) {
            id = Method.Euclidean;
        } else if (str[0].equalsIgnoreCase( "interpolate" )) {
            id = Method.Interpolate;
        } else {
            System.err.println( "Invalid method: " + str[0] );
            return null;
        }
        
        input.addMethod( id, args );
        goUp();

        return null;
    }

    public Object visit(ASTMethods node, Object data) {
        goDown( "Methods" );
        // System.out.println( "ASTMethods: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        acceptChildren( node, data );
        goUp();
        return null;
    }

    public Object visit(ASTTime node, Object data) {
        goDown( "Time" );
        // System.out.println( "ASTTime: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        goUp();
        return null;
    }

    public Object visit(ASTExps node, Object data) {
        goDown( "Exps" );
        // System.out.println( "ASTExps: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        acceptChildren( node, data );
        goUp();
        return null;
    }

    public Object visit(ASTExp node, Object data) {
        goDown( "Exp" );
        // System.out.println( "ASTExp: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        acceptChildren( node, data );
        goUp();
        return null;
    }

    public Object visit(ASTExp2 node, Object data) {
        goDown( "Exp2" );
        // System.out.println( "ASTExp2: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        acceptChildren( node, data );
        goUp();
        return null;
    }

    public Object visit(ASTFunc node, Object data) {
        goDown( "Func" );
        // System.out.println( "ASTFunc: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        goUp();
        return null;
    }

    public Object visit(ASTNumber node, Object data) {
        goDown( "Number" );
        // System.out.println( "ASTNumber: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        goUp();
        return null;
    }

    public Object visit(ASTValue node, Object data) {
        goDown( "Value" );
        // System.out.println( "ASTValue: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        if (data instanceof OutputStream && (node.jjtGetValue() != null)) {
            OutputStream output = (OutputStream)data;
            output.addDeclaredVarNames( (String)node.jjtGetValue() );
        }
        goUp();
        return null;
    }
}
