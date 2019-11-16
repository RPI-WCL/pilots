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

    private static String mapToJSONEntries( String name, Map<String, Double> m ) {
	// Also return ":\"<all keys>\",
	String keys = "\"" + name + "\":\"";
	String result = new String();
	int count = 0;
	for ( Map.Entry<String, Double> en : m.entrySet() ) {
	    result += "\"" + en.getKey() + "\":" + String.valueOf(en.getValue());
	    keys += en.getKey();
	    if ( count < m.size() - 1 ) { result += ","; keys += ","; }
	    count++;
	}

	return keys + "\"," + result;
    }

    private static JSONObject dataToJSON( String name, List<DataVector> data ) {
	JSONObject ret = new JSONObject();

	List<String> data_col_names = new ArrayList<>();
	int count = 0;
	for ( DataVector col : data ) {
	    String col_name = name + String.valueOf( count );
	    data_col_names.add( col_name );
	    JSONArray arr = new JSONArray();
	    for ( int i = 0; i < col.size(); ++i ) {
		arr.put( col.get(i) );
	    }
	    ret.put( col_name, arr );
	    ++count;
	}
	ret.put( name, String.join(",", data_col_names) );
	return ret;
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

    private static double parseJSONTrain( JSONObject obj ) {
	return 0.0;
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
	    e.printStackTrace();
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
	    JSONObject json_req = dataToJSON( "data", data );
	    
	    writeJSON( con, json_req.toString() );
	    JSONObject response = readJSON( con );
	    return parseJSONPredict( response );
	} catch ( Exception e ) {
	    System.err.println( "Error during PREDICT:" );
	    e.printStackTrace();
	    return null;
	}
    }

    public static Double train(String engine, Map<String, Double> settings,
				Map<String, Double> values) {
	try {
	    URL url = new URL( getURL( engine, "train" ) );
	    HttpURLConnection con = makePOST( url );
	    
	    // === Create JSON output ===
	    String json_req = "{}";
	    
	    writeJSON( con, json_req );
	    JSONObject response = readJSON( con );
	    return parseJSONTrain( response );
	} catch ( Exception e ) {
	    System.err.println( "Error during TRAIN:" );
	    e.printStackTrace();
	    return null;
	}
    }
}
