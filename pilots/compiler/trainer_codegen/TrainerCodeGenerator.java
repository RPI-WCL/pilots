package pilots.compiler.trainer.codegen;

import java.io.*;
import java.util.regex.*;
import java.util.*;

import pilots.compiler.trainer.parser.*;
import pilots.util.trainer.*;

public class TrainerCodeGenerator implements TrainerParserVisitor {

    // =====================================================

    private static final String TAB = "    ";
    private static int indent = 0;

    private String appName = null;
    private String code = null;
    
    private List<String> constants = null;
    private List<String> data_sources = null;
    private List<String> features = null;
    private List<String> labels = null;
    private List<String> test_features = null;
    private List<String> test_labels = null;

    private String modelName = null;
    
    private int num_algos = 0;
    private List<String> algoNames = null;
    private List<String> alg_params = null;
    
    private static int depth = 0;

    // =====================================================

    public static void main(String[] args) {
	// This main is for testing purposes
        try {
            TrainerParser parser = new TrainerParser(new FileReader(args[0]));
            Node node = parser.Trainer();
            TrainerCodeGenerator visitor = new TrainerCodeGenerator();
            node.jjtAccept(visitor, null);
        } 
        catch (FileNotFoundException ex) {
	    System.err.println("File Not Found");
        }
        catch (TokenMgrError ex) {
	    System.err.println("Token Error");
        }
        catch (ParseException ex) {
	    System.err.println("Parse Execption");
        }
    }

    public TrainerCodeGenerator() {
	code = new String();
	
	constants = new ArrayList<>();
	data_sources = new ArrayList<>();
	features = new ArrayList<>();
	labels = new ArrayList<>();
	test_features = new ArrayList<>();
	test_labels = new ArrayList<>();

	modelName = new String();
	
	int num_algos = 0;
	alg_params = new ArrayList<>();
	algoNames = new ArrayList<>();
	
	System.out.println("Finished code generation");
    }

    // =====================================================

    private void goDown( String node ) { depth++; }

    private void goUp() { depth--; }

    private void incIndent() { indent++; }

    private void decIndent() { indent--; }

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

    // =====================================================
    
    private void outputCode() {
	try {
	    File file = new File(appName + ".java");
	    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	    pw.print(code);
	    pw.close();
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
    
    private void generateCode() {
	generateImports();
	generateClassDeclaration();
	generateConstructor();
	generateSetup();
	generateMain();
    }

    private void generateImports() {
	code += "import java.io.*;\n";
	code += "import java.util.*;\n";
	code += "\n";
	code += "import pilots.util.trainer.*;\n";
	code += "import pilots.util.model.*;\n";
	code += "\n";
    }

    private void generateClassDeclaration() {
	code += "public class " + appName + " extends PilotsTrainer {\n";
	code += incInsIndent() + "\n";
    }

    private void generateConstants() {
	code += insIndent() + "// === Constants ===\n";
	for (String constant : constants) {
            code += insIndent() + constant + "\n";
        }
        code += "\n";
    }

    private void generateData() {
	code += insIndent() + "// === Data ===\n";
	for ( String d : data_sources ) {
	    code += insIndent() + "super.pullData( " + d + " );\n";
	}
        code += "\n";
    }

    private void generateModel() {
	code += insIndent() + "// === Model ===\n";
	for ( String ff : features ) {
	    code += insIndent() + "addFeature( " + ff + " );\n";
	}
	code += "\n";
	for ( String ll : labels ) {
	    code += insIndent() + "addLabel( " + ll + " );\n";
	}
	code += "\n";

	for ( String ff : test_features ) {
	    code += insIndent() + "addTestFeature( " + ff + " );\n";
	}
	code += "\n";
	for ( String ll : test_labels ) {
	    code += insIndent() + "addTestLabel( " + ll + " );\n";
	}
	code += "\n";

	
	for ( String aa : alg_params ) {
	    code += insIndent() + aa + "\n";
	}
	code += "\n";
    }

    private void generateConstructor() {
	code += insIndent() + "public " + appName + "() {\n";
	code += incInsIndent() + "super();\n";
	code += "\n";
	code += insIndent() + "super.model_name = \"" + modelName + "\";\n";
	for ( String _alg : algoNames ) {
	    code += insIndent() + "super.addAlgorithm(\"" + _alg + "\");\n";
	}
	code += "\n";
	code += insIndent() + "setupTrainer();\n";
	code += decInsIndent() + "}\n";
        code += "\n";
	code += insIndent() + "public void train() { super.train(); }\n";
	code += "\n";
    }

    private void generateSetup() {
	code += insIndent() + "private void setupTrainer() {\n";
	incIndent();
	generateConstants();
	generateData();
	generateModel();
	code += decInsIndent() + "}\n";
        code += "\n";	
    }

    private void generateMain() {
        code += insIndent() + "public static void main(String[] args) {\n";
	code += incInsIndent() + "System.out.println(\"Hello, World!\");\n";
	code += insIndent() + appName + " tr = new " + appName + "();\n";
	code += insIndent() + "tr.train();\n";
        code += decInsIndent() + "}\n";
	code += decInsIndent() + "}\n"; // Class closing bracket
    }


    // =====================================================
    
    private void acceptChildren(SimpleNode node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild(i).jjtAccept(this, data);
        }
    }
    
    public Object visit( ASTTrainer node, Object data ) {
	goDown("Pilots");

	modelName = (String)node.jjtGetValue();
	appName = modelName.substring(0,1).toUpperCase() + modelName.substring(1);
	
	acceptChildren( node, null );
	generateCode();
	outputCode();
	
	goUp();
	return null;
    }

    public Object visit( ASTConstant node, Object data ) {
	goDown("Constant");

	String[] vals = ((String)node.jjtGetValue()).split(":");
	String c = "data.put( \"" + vals[0] + "\", new DataVector( " + parseConstExp( vals[1] ) + " ) );";
	constants.add( c );

	goUp();
	return null;
    }

    public Object visit( ASTData node, Object data ) {
	goDown("Data");

	String full_datum = (String)node.jjtGetValue();
	String[] vals = full_datum.split(":");
	if ( vals[0].equals( "model" ) ) {
	    data_sources.add( "\"" + full_datum + "\"" );
	} else if ( vals[0].equals( "file" ) ) {
	    String datum = "";
	    String file_name = vals[1].split("\"")[1];
	    if ( vals.length == 4 ) {
		datum += "\"file:" + file_name  + ":" + vals[2] + ":" + vals[3] + "\"";
	    } else {
		datum += "\"file:" + file_name  + ":" + vals[2] + ":\""; // No args
	    }
	    data_sources.add( datum );
	} else if ( vals[0].equals( "sequence" ) ) {
	    data_sources.add( "\"" + full_datum + "\"" );
	}
	
	goUp();
	return null;
    }

    public Object visit( ASTFeature node, Object data ) {
	goDown("Feature");

	String feats = parseExps( (String)node.jjtGetValue() );
	for ( String f : feats.split(";") ) {
	    features.add( f );
	}
	
	goUp();
	return null;
    }

    public Object visit( ASTLabel node, Object data ) {
	goDown("Label");

	String lab = parseExps( (String)node.jjtGetValue() );
	for ( String l : lab.split(";") ) {
	    labels.add( l );
	}
	
	goUp();
	return null;
    }

    public Object visit( ASTTest_Feature node, Object data ) {
	goDown("Test_Feature");

	String feats = parseExps( (String)node.jjtGetValue() );
	for ( String f : feats.split(";") ) {
	    test_features.add( f );
	}
	
	goUp();
	return null;
    }

    public Object visit( ASTTest_Label node, Object data ) {
	goDown("Test_Label");

	String lab = parseExps( (String)node.jjtGetValue() );
	for ( String l : lab.split(";") ) {
	    test_labels.add( l );
	}
	
	goUp();
	return null;
    }

    public Object visit( ASTAlgorithm node, Object data ) {
	goDown("Algorithm");
	num_algos++;
	algoNames.add( (String)node.jjtGetValue() );
	acceptChildren( node, data );
	goUp();
	return null;
    }

    
    public Object visit( ASTMap node, Object data ) {
	goDown("Map");
	acceptChildren( node, data );
	goUp();
	return null;
    }

    public Object visit( ASTMapItem node, Object data ) {
	goDown("MapItem");

	String[] vals = ((String)node.jjtGetValue()).split(":");
	String item = "addAlgArg( " + Integer.toString(num_algos-1) + "," +
	    " \"" + vals[0] + "\", " + vals[1] + ");";
	alg_params.add( item );
	
	goUp();
	return null;
    }

    public Object visit( ASTValue node, Object data ) { return null; }
 
    public Object visit( ASTNumber node, Object data ) { return null; }

    public Object visit( ASTFunc node, Object data ) { 	return null; }

    public Object visit( ASTExp2 node, Object data ) {
	goDown("Exps");
	goUp();
	return null;
    }

    public Object visit( ASTExp node, Object data ) {
	goDown("Exps");
	goUp();
	return null;
    }

    public Object visit( ASTExps node, Object data ) {
	goDown("Exps");
	goUp();
	return null;
    }

    public Object visit( ASTExps2 node, Object data ) {
	goDown("Exps");
	goUp();
	return null;
    }
    
    public Object visit( ASTVars node, Object data ) {
	goDown("Vars");
	goUp();
	return null;
    }

    public Object visit( ASTModelUser node, Object data ) { return null; }
    
    public Object visit( ASTFile node, Object data ) { return null; }

    public Object visit( ASTSequence node, Object data ) { return null; }

    public Object visit( SimpleNode node, Object data ) { return null; }

    // =====================================================

    // For DataVector operations
    private String replaceOperation( String op ) {
	if ( op.equals( "+" ) ) { return "add"; }
	else if ( op.equals( "-" ) ) { return "sub"; }
	else if ( op.equals( "*" ) ) { return "mult"; }
	else if ( op.equals( "/" ) ) { return "div"; }
	else if ( op.equals( "^" ) ) { return "pow"; }
	else if ( op.equals( "sqrt" ) ) { return "DataVector.sqrt"; }
	else if ( op.equals( "sin" ) ) { return "DataVector.sin"; }
	else if ( op.equals( "cos" ) ) { return "DataVector.cos"; }
	else if ( op.equals( "tan" ) ) { return "DataVector.tan"; }
	else if ( op.equals( "arcsin" ) ) { return "DataVector.arcsin"; }
	else if ( op.equals( "arccos" ) ) { return "DataVector.arccos"; }
	else if ( op.equals( "arctan" ) ) { return "DataVector.arctan"; }
	else if ( op.equals( "abs" ) ) { return "DataVector.abs"; }
	return "";
    }

    // For Double operations
    private String replaceOperation2( String op ) {
	if ( op.equals( "+" ) ) { return "+"; }
	else if ( op.equals( "-" ) ) { return "-"; }
	else if ( op.equals( "*" ) ) { return "*"; }
	else if ( op.equals( "/" ) ) { return "/"; }
	else if ( op.equals( "^" ) ) { return "^"; }
	else if ( op.equals( ">" ) ) { return ">"; }
	else if ( op.equals( ">=" ) ) { return ">="; }
	else if ( op.equals( "<" ) ) { return "<"; }
	else if ( op.equals( "<=" ) ) { return "<="; }
	else if ( op.equals( "!=" ) ) { return "!="; }
	else if ( op.equals( "==" ) ) { return "=="; }
	else if ( op.equals( "and" ) ) { return "and"; }
	else if ( op.equals( "or" ) ) { return "or"; }
	else if ( op.equals( "xor" ) ) { return "xor"; }
	else if ( op.equals( "not" ) ) { return "not"; }
	else if ( op.equals( "sqrt" ) ) { return "Math.sqrt"; }
	else if ( op.equals( "sin" ) ) { return "Math.sin"; }
	else if ( op.equals( "cos" ) ) { return "Math.cos"; }
	else if ( op.equals( "tan" ) ) { return "Math.tan"; }
	else if ( op.equals( "arcsin" ) ) { return "Math.arcsin"; }
	else if ( op.equals( "arccos" ) ) { return "Math.arccos"; }
	else if ( op.equals( "arctan" ) ) { return "Math.arctan"; }
	else if ( op.equals( "abs" ) ) { return "Math.abs"; }
	return "";
    }

    private String parseConstExps( String exps ) {
    	String[] all_exp = exps.split(";");
    	String result = new String();
    	for ( int i = 0; i < all_exp.length; ++i ) {
    	    if ( i > 0 ) { result += ";"; }
    	    result += parseConstExp( all_exp[i] );
    	}
    	return result;

    }

    private String parseConstExp( String exp ) {
	// Exps: exp,exp,...
	// Exp:
	// Case 1: {func} (exps)n exp2
	// Case 2: (exp) exp2
	// Case 3: [value] exp2
	// Exp2:
	// Case 1: {func} exp exp2
	// Case 2: nothing

	// === Replace func(exps) ===
	String rfunc = "\\|[^\\(\\[]+\\|";
	Pattern p = Pattern.compile( rfunc );
	Matcher m = p.matcher( exp );
	if ( m.find() ) {
	    String front = exp.substring( 0, m.start() );
	    String back = exp.substring( m.end() );
	    String oldFunc = exp.substring( m.start()+1, m.end()-1 );
	    String newFunc = replaceOperation2( oldFunc );
	    return parseConstExp( front ) + newFunc + parseConstExp( back );
	}

	// === Replace func
	String rfunc2 = "\\{[^\\(\\[]+\\}";
	Pattern p2 = Pattern.compile( rfunc2 );
	Matcher m2 = p2.matcher( exp );
	if ( m2.find() ) {
	    String front = exp.substring( 0, m2.start() );
	    String back = exp.substring( m2.end() );
	    String oldFunc = exp.substring( m2.start()+1, m2.end()-1 );
	    String newFunc = replaceOperation2( oldFunc );
	    return parseConstExp( front ) + newFunc + parseConstExp( back );
	}
	
	// === Replace value ===
	String rval = "\\[[^\\(\\[]+\\]";
	Pattern p3 = Pattern.compile( rval );
	Matcher m3 = p3.matcher( exp );
	if ( m3.find() ) {
	    String front = exp.substring( 0, m3.start() );
	    String back = exp.substring( m3.end() );
	    String oldVal = exp.substring( m3.start()+1, m3.end()-1 );
	    String newVal = "" + oldVal + "";
	    return front + newVal + parseConstExp( back );
	}
	
	return exp;
    }

    private String parseExps( String exps ) {
	String[] all_exp = exps.split(";");
	String result = new String();
	for ( int i = 0; i < all_exp.length; ++i ) {
	    if ( i > 0 ) { result += ";"; }
	    result += parseExp( all_exp[i] );
	}
	return result;
    }

    private String parseExp( String exp ) {
	// Exps: exp,exp,...
	// Exp:
	// Case 1: {func} (exps) exp2
	// Case 2: (exp) exp2
	// Case 3: [value] exp2
	// Exp2:
	// Case 1: {func} exp exp2
	// Case 2: nothing

	// === Replace func(exps) ===
	String rfunc = "\\|[^\\(\\[]+\\|";
	Pattern p = Pattern.compile( rfunc );
	Matcher m = p.matcher( exp );
	if ( m.find() ) {
	    String front = exp.substring( 0, m.start() );
	    String back = exp.substring( m.end() );
	    String oldFunc = exp.substring( m.start()+1, m.end()-1 );
	    String newFunc = replaceOperation( oldFunc );
	    return parseExp( front ) + newFunc + "" + parseExp( back ) + "";
	}

	// === Replace func
	String rfunc2 = "\\{[^\\(\\[]+\\}";
	Pattern p2 = Pattern.compile( rfunc2 );
	Matcher m2 = p2.matcher( exp );
	if ( m2.find() ) {
	    String front = exp.substring( 0, m2.start() );
	    String back = exp.substring( m2.end() );
	    String oldFunc = exp.substring( m2.start()+1, m2.end()-1 );
	    String newFunc = replaceOperation( oldFunc );
	    return parseExp( front ) + "." + newFunc + "" + parseExp( back ) + "";
	}
	
	// === Replace value ===
	String rval = "\\[[^\\(\\[]+\\]";
	Pattern p3 = Pattern.compile( rval );
	Matcher m3 = p3.matcher( exp );
	if ( m3.find() ) {
	    String front = exp.substring( 0, m3.start() );
	    String back = exp.substring( m3.end() );
	    String oldVal = exp.substring( m3.start()+1, m3.end()-1 );
	    String newVal = "super.get(\"" + oldVal + "\")";
	    return front + newVal + parseExp( back );
	}
	
	return exp;
    }

}
