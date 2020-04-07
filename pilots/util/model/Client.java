package pilots.util.model;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

import pilots.util.trainer.*;

public class Client {

    public static final String server_url = "http://127.0.0.1:5000";

    private static String getURL( String engine, String operation ) {
	return server_url + "/" + operation + "/" + engine;
    }

    // ===== JSON Creation functions =====

    private static void addDataToJSON( JSONObject obj, String name, List<DataVector> data ) {
	JSONArray all_data = new JSONArray();
	for ( DataVector col : data ) {
	    JSONArray arr = new JSONArray();
	    for ( int i = 0; i < col.size(); ++i ) {
		arr.put( col.get(i) );
	    }
	    all_data.put( arr );
	}
	obj.put( name, all_data );
    }

    private static void addSettingsToJSON( JSONObject obj, String name,
					   Map<String, ModelArg> settings ) {
	JSONObject map = new JSONObject();
	for ( Map.Entry<String, ModelArg> en : settings.entrySet() ) {
	    ModelArg.Type t = en.getValue().getType();
	    if ( t == ModelArg.Type.BOOL ) {
		map.put( en.getKey(), en.getValue().getBoolean() );
	    } else if ( t == ModelArg.Type.INT ) {
		map.put( en.getKey(), en.getValue().getInteger() );
	    } else {
		map.put( en.getKey(), en.getValue().getDouble() );
	    }
	}
	obj.put( name, map );
    }

    // ===== JSON Parsing functions =====

    private static boolean parseJSONLoad( JSONObject obj ) {
	return (boolean)obj.get("success");
    }

    private static List<DataVector> parseJSONPredict( JSONObject obj ) {
	JSONArray arr = obj.getJSONArray("value");
	if ( arr.length()  == 0){
	    return null; // exception
	}
	
	List<DataVector> r_values = new ArrayList<>();       
	for (int i = 0; i < arr.length(); i++){
	    JSONArray item = arr.getJSONArray(i);
	    DataVector col = new DataVector();
	    for (int j = 0; j < item.length(); j++){
		col.add( item.getDouble(j) );
	    }
	    r_values.add( col );
	}
	return r_values;
    }

    private static Double parseJSONTrain( JSONObject obj ) {
	if ( obj == null ) { return null; }
	boolean success = obj.getBoolean("success");
	if ( success ) {
	    return obj.getDouble("accuracy");
	}
	return null;
    }
    
    // ===== HTTP JSON functions =====

    private static void writeJSON( HttpURLConnection con, String req ) {
	try ( OutputStream os = con.getOutputStream() ) {
	    byte[] input = req.getBytes();
	    os.write( input, 0, input.length );
	} catch ( Exception e ) {
	    System.err.println( "Error while writing JSON:" );
	    e.printStackTrace();
	    return;
	}
    }
    
    private static JSONObject readJSON( HttpURLConnection con ) {
	try ( BufferedReader br =
	      new BufferedReader( new InputStreamReader( con.getInputStream() ) ) ) {
	    StringBuilder response = new StringBuilder();
	    String responseLine = null;
	    while ( (responseLine = br.readLine()) != null ) {
		response.append( responseLine.trim() );
	    }
	    return new JSONObject( response.toString() );
	} catch ( Exception e ) {
	    System.err.println( "Error while reading JSON:" );
	    //e.printStackTrace();
	    return null;
	}
    }

    // ===== HTTP functions =====

    private static HttpURLConnection makePOST( URL url ) {
	try {
	    HttpURLConnection con = (HttpURLConnection)url.openConnection();
	    con.setRequestMethod("POST");
	    con.setRequestProperty("Content-Type", "application/json" );
	    con.setRequestProperty("Accept", "application/json" );
	    con.setDoOutput(true);
	    return con;
	} catch ( Exception e ) {
	    System.err.println( "Error while making POST:" );
	    e.printStackTrace();
	    return null;
	}
    }

    private static HttpURLConnection makeGET( URL url ) {
	try {
	    HttpURLConnection con = (HttpURLConnection)url.openConnection();
	    con.setRequestMethod("GET");
	    return con;
	} catch ( Exception e ) {
	    System.err.println( "Error while making GET:" );
	    e.printStackTrace();
	    return null;
	}
    }

    // ==================== Public Client Operations ===========================

    
    public static boolean load( String engine ) {
	try {
	    URL url = new URL( getURL( engine, "load" ) );
	    HttpURLConnection con = makeGET( url );
	    JSONObject response = readJSON( con );
	    return parseJSONLoad( response );
	} catch ( Exception e ) {
	    System.err.println( "Error during LOAD:" );
	    e.printStackTrace();
	    return false;
	}
    }

    public static List<DataVector> predict( String engine, List<DataVector> data ) {
	try {
	    URL url = new URL( getURL( engine, "run" ) );
	    HttpURLConnection con = makePOST( url );
	    
	    // === Create JSON output ===
	    JSONObject json_req = new JSONObject();
	    addDataToJSON( json_req, "data", data );
	    
	    writeJSON( con, json_req.toString() );
	    JSONObject response = readJSON( con );
	    return parseJSONPredict( response );
	} catch ( Exception e ) {
	    System.err.println( "Error during PREDICT:" );
	    e.printStackTrace();
	    return null;
	}
    }

    public static Double test( String engine, List<DataVector> test_features,
			       List<DataVector> test_labels ) {
	try {
	    URL url = new URL( getURL( engine, "test" ) );
	    HttpURLConnection con = makePOST( url );
	    
	    // === Create JSON output ===
	    JSONObject json_req = new JSONObject();
	    addDataToJSON( json_req, "test_features", test_features );
	    addDataToJSON( json_req, "test_labels", test_labels );
	    
	    writeJSON( con, json_req.toString() );
	    JSONObject response = readJSON( con );
	    return parseJSONTrain( response );
	} catch ( Exception e ) {
	    System.err.println( "Error during TESTING:" );
	    e.printStackTrace();
	    return null;
	}
    }

    public static Double train( String algorithm, String engine, Map<String, ModelArg> settings,
				List<DataVector> features, List<DataVector> labels ) {
	try {
	    URL url = new URL( getURL( algorithm + ":" + engine, "train" ) );
	    HttpURLConnection con = makePOST( url );
	    
	    // === Create JSON output ===
	    JSONObject json_req = new JSONObject();
	    addSettingsToJSON( json_req, "settings", settings );
	    addDataToJSON( json_req, "features", features );
	    addDataToJSON( json_req, "labels", labels );
	    
	    writeJSON( con, json_req.toString() );
	    JSONObject response = readJSON( con );
	    return parseJSONTrain( response );
	} catch ( Exception e ) {
	    System.err.println( "Error during TRAIN:" );
	    e.printStackTrace();
	    return null;
	}
    }
}
