package pilots.compiler.codegen;

import java.io.*;
import java.util.HashSet;
import java.util.Vector;
import pilots.compiler.parser.*;
import pilots.runtime.*;

public class PilotsCodeGenerator implements PilotsParserVisitor {
    private static final String TAB = "    ";

    private String appName_ = null;
    private Vector<InputStream> inputs_ = null;
    private Vector<OutputStream> outputs_ = null;
    private Vector<OutputStream> errors_ = null;
    private String code_ = null;
    
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
        code_ = new String();
    }

    public void acceptChildren( SimpleNode node, Object data ) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild( i ).jjtAccept( this, data );
        }
    }

    public void generateImports() {
        code_ += "package pilots.tests;\n";
        code_ += "\n";
        code_ += "import java.util.Timer;\n";
        code_ += "import java.util.TimerTask;\n";
        code_ += "import java.net.Socket;\n";
        code_ += "import pilots.runtime.*;\n";
        code_ += "\n";
    }

    public void generateClassDeclaration() {
        code_ += "public class " + appName_ + " extends PilotRuntime {\n";
        code_ += TAB + "private Timer timer_;\n";
        code_ += "\n";
    }

    public void generateConstructor() {
        code_ += TAB + "public " + appName_ + "() {\n";
        code_ += TAB + TAB + "timer_ = new Timer();\n";
        code_ += TAB + "}\n";
        code_ += "\n";
    }        

    public void generateStartOutputs( Vector<OutputStream> outputs ) {
        for (int i = 0; i < outputs.size(); i++) {
            OutputStream output = outputs.get( i );

            // method declaration
            String[] outputVarNames = output.getVarNames();
            code_ += TAB + "public void startOutput_";
            code_ += outputVarNames[0];
            // if (1 < varNames.length) {
            //     for (int j = 1; j < varNames.length; j++)
            //         code_ += "_" + varNames[j];
            // }
            code_ += "() {\n";
            code_ += TAB + TAB + "try {\n";
            if (output.getOutputType() == OutputType.Output)
                code_ += TAB + TAB + TAB + "openSocket( OutputType.Output, " + i + ", \"" + outputVarNames[0] + "\" );\n";
            else 
                code_ += TAB + TAB + TAB + "openSocket( OutputType.Error, " + i + ", \"" + outputVarNames[0] + "\" );\n";
            code_ += TAB + TAB + "} catch ( Exception ex ) {\n";
            code_ += TAB + TAB + TAB + "ex.printStackTrace();\n";
            code_ += TAB + TAB + "}\n";
            code_ += TAB + "\n";
            code_ += TAB + TAB + "timer_.scheduleAtFixedRate( new TimerTask() {\n";
            code_ += TAB + TAB + TAB + "public void run() {\n";

            // variable declaration
            code_ += TAB + TAB + TAB + TAB + "double ";
            HashSet<String> set = output.getDeclaredVarNames();
            for (int j = 0; j < outputVarNames.length; j++)
                set.add( outputVarNames[j] ); // union of outputVarNames and declaredVarNames
            boolean flag = true;
            for (Object obj : set) {
                if (flag) {
                    code_ += (String)obj;
                    flag = false;
                }
                else 
                    code_ += ", " + (String)obj;
            }
            code_ += ";\n";

            // input data streams
            for (int j = 0; j < inputs_.size(); j++) {
                InputStream input = inputs_.get( j );

                String[] inputVarNames = input.getVarNames();
                for (int k = 0; k < inputVarNames.length; k++) {
                    if (set.contains( inputVarNames[k] )) { // is this input var actually used ?
                        code_ += TAB + TAB + TAB + TAB + inputVarNames[k] + 
                            " = getData( \"" + inputVarNames[k] + "\", ";

                        // methods
                        Vector<Method> methods = input.getMethods();
                        for (int l = 0; l < methods.size(); l++) {
                            Method method = methods.get( l );
                            if (l == 0)
                                code_ += "new Method( " + method.toString() + ")";
                            else 
                                code_ += ", new Method( " + method.toString() + ")";
                        }
                        code_ += " );\n";
                    }
                }
            }

            // output expression
            code_ += TAB + TAB + TAB + TAB + outputVarNames[0] + " = " + output.getExp() + ";\n";
            
            // finally send it
            code_ += TAB + TAB + TAB + TAB + "try {\n";
            if (output.getOutputType() == OutputType.Output)
                code_ += TAB + TAB + TAB + TAB + TAB + "sendData( OutputType.Output, " + i + ", " + outputVarNames[0] + " );\n";
            else 
                code_ += TAB + TAB + TAB + TAB + TAB + "sendData( OutputType.Error, " + i + ", " + outputVarNames[0] + " );\n";
            code_ += TAB + TAB + TAB + TAB + "} catch { Exception ex ) {\n";
            code_ += TAB + TAB + TAB + TAB + TAB + "ex.printStackTrace()\n";
            code_ += TAB + TAB + TAB + TAB + "}\n";
            code_ += TAB + TAB + TAB + "}\n";
            code_ += TAB + TAB + "}, 0, " + output.getFrequency() + ");\n";
            code_ += TAB + "}\n";
            code_ += "\n";
        }
    }

    public void generateMain() {
        code_ += TAB + "public static void main( String[] args ) {\n";
        code_ += TAB + TAB + appName_ + " app = new " + appName_ + "();\n";
        code_ += "\n";
        code_ += TAB + TAB + "try {\n";
        code_ += TAB + TAB + TAB + "app.parseArgs( args );\n";
        code_ += TAB + TAB + "} catch ( Exception ex ) {\n";
        code_ += TAB + TAB + TAB + "ex.printStackTrace();\n";
        code_ += TAB + TAB + "}\n";
        code_ += "\n";
        code_ += TAB + TAB + "app.startServer()\n";
        for (int i = 0; i < outputs_.size(); i++) {
            OutputStream output = outputs_.get( i );
            String[] outputVarNames = output.getVarNames();
            code_ += TAB + TAB + "app.startOutput_" + outputVarNames[0] + "();\n";
        }
        for (int i = 0; i < errors_.size(); i++) {
            OutputStream output = errors_.get( i );
            String[] outputVarNames = output.getVarNames();
            code_ += TAB + TAB + "app.startOutput_" + outputVarNames[0] + "();\n";
        }
        code_ += TAB + "}\n";
        code_ += "}\n";
    }

    public void generateCode() {
        generateImports();
        generateClassDeclaration();
        generateConstructor();
        if (0 < outputs_.size())
            generateStartOutputs( outputs_ );
        if (0 < errors_.size())
            generateStartOutputs( errors_ );
        generateMain();
    }

    public void outputCode() {
        System.out.println( code_ );
    }

    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    public Object visit( ASTPilots node, Object data ) {
        appName_ = (String) node.jjtGetValue();
        appName_ = appName_.substring(0, 1).toUpperCase() + appName_.substring(1);
        // System.out.println( "ASTPilots: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );

        acceptChildren( node, null );
        generateCode();
        outputCode();

        return null;
    }

    public Object visit(ASTInput node, Object data) {
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

        return null;
    }
    public Object visit(ASTOutput node, Object data) {
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

        return null;
    }

    public Object visit(ASTError node, Object data) {
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
        errors_.add( output );

        node.jjtGetChild( 1 ).jjtAccept( this, output ); // accept Exps() only

        return null;
    }

    public Object visit(ASTVars node, Object data) {
        // System.out.println( "ASTVars: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        return null;
    }

    public Object visit(ASTDims node, Object data) {
        // System.out.println( "ASTDims: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        return null;
    }

    public Object visit(ASTMethod node, Object data) {
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

        return null;
    }

    public Object visit(ASTMethods node, Object data) {
        // System.out.println( "ASTMethods: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        acceptChildren( node, data );
        return null;
    }

    public Object visit(ASTTime node, Object data) {
        // System.out.println( "ASTTime: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        return null;
    }

    public Object visit(ASTExps node, Object data) {
        // System.out.println( "ASTExps: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        acceptChildren( node, data );
        return null;
    }

    public Object visit(ASTExp node, Object data) {
        // System.out.println( "ASTExp: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        acceptChildren( node, data );
        return null;
    }

    public Object visit(ASTExp2 node, Object data) {
        // System.out.println( "ASTExp2: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        acceptChildren( node, data );
        return null;
    }

    public Object visit(ASTFunc node, Object data) {
        // System.out.println( "ASTFunc: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        return null;
    }

    public Object visit(ASTNumber node, Object data) {
        // System.out.println( "ASTNumber: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        return null;
    }

    public Object visit(ASTValue node, Object data) {
        // System.out.println( "ASTValue: value=" + node.jjtGetValue() + ",#children="  + node.jjtGetNumChildren() );
        if (data instanceof OutputStream && (node.jjtGetValue() != null)) {
            OutputStream output = (OutputStream)data;
            output.addDeclaredVarNames( (String)node.jjtGetValue() );
        }
        return null;
    }
}
