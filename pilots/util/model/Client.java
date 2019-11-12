package pilots.util.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import org.json.*;


public class Client {

    public static final String server_url = "http://127.0.0.1:5000/";

    public static String get_values_req( Map<String, Double> values ) {
	StringBuilder result = new StringBuilder();
	StringBuilder nameString = new StringBuilder("&name=");
	StringBuilder valueString = new StringBuilder("&value=");
	int index = 0;
	int last_one = values.size() - 1;
	for (Map.Entry<String,Double> i : values.entrySet()){
	    nameString.append(i.getKey());
	    valueString.append(i.getValue());
	    if (index != last_one){
		nameString.append(',');
		valueString.append(',');
	    }
	    index += 1;
	}
	result.append(nameString.toString());
	result.append(valueString.toString());
	return result.toString(); 
    }

    public static String get_request_url(String engine, String cmd, Map<String, Double> values){
	StringBuilder builder = new StringBuilder();
	builder.append( Client.server_url + cmd );
	builder.append( "?model=" + engine );
	
	if ( values != null ) {
	    builder.append( get_values_req( values ) );
	}
	return builder.toString();
    }

    public static String getHTML( String req_url ) {
	URL server_url;
	try {
	    server_url = new URL(req_url);
	    URLConnection con = server_url.openConnection();
	    InputStreamReader con_isr = new InputStreamReader( con.getInputStream() );
	    BufferedReader con_br = new BufferedReader( con_isr );
	    
	    String inputLine;
	    StringBuilder builder = new StringBuilder();
	    
	    while ( (inputLine = con_br.readLine()) != null ) {
		builder.append(inputLine);
	    }
	    
	    con_br.close();
	    return builder.toString();
	} catch ( IOException e ) {
	    e.printStackTrace();
	}
	return null;
    }

    public static double[][] parseJSON( JSONObject obj ) {
	JSONArray arr = obj.getJSONArray("value");

	int rows = arr.length();
	if ( rows == 0 ){ return null; } // Error
	int columns = arr.getJSONArray(0).length();
	
	double[][] r_values = new double[rows][columns];
	// Copy from JSON object into r_values
	for ( int i = 0; i < arr.length(); ++i ) {
	    JSONArray item = arr.getJSONArray( i );
	    for ( int j = 0; j < item.length(); ++j ) {
		double current_number = item.getDouble( j );
		r_values[i][j] = current_number;
	    }
	}
	return r_values;
    }
    
    public static double[][] predict(String engine, Map<String, Double> values) {
	String req = get_request_url( engine, "", values );
	JSONObject result = new JSONObject( getHTML( req ) );
	return parseJSON( result );
    }

    public static boolean train( String engine, Map<String, Double> values) {
	String req = get_request_url( engine, "train", values );
	String response =  getHTML( req );
	return true;
    }

    public static boolean load( String engine ) {
	String req = get_request_url( engine, "load", null );
	String response =  getHTML( req );
	return true;
    }
}
