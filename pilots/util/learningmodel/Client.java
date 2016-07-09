package pilots.util.learningmodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import org.json.*;
public class Client {
	// simple implementation of java client, currently support only one variable, more will be added...
	public static void main(String[] args){
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++){
		Map<String, Double> map = new HashMap<>();
			map.put("a", 1.0);
			double[][] result = predict("1", map);
			if (result == null){
				System.out.println("not good");
			}else{
				for (int a = 0; a < result.length; a++){
					for (int j = 0; j < result[a].length; j++){
						System.out.println(result[a][j]);
					}
				}
			}
		}
		System.out.println("" + String.valueOf(System.currentTimeMillis() - startTime));
	}
	public static String getHTML(String url){		
		URL u;
		try {
			u = new URL(url);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuilder builder = new StringBuilder();
			while ((inputLine = in.readLine()) != null){
				builder.append(inputLine);
			}
			in.close();
			return builder.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static double[][] predict(String engine, Map<String, Double> values){
		String result = send_data(engine, values);
		// parse json object
		JSONObject obj = new JSONObject(result);
		JSONArray arr = obj.getJSONArray("value");
		// measure the size of matrix
		int rows = arr.length();
		if (rows == 0){
			return null; // exception
		}
		int columns = arr.getJSONArray(0).length();
		double[][] r_values = new double[rows][columns];
		for (int i = 0; i < arr.length(); i++){
			JSONArray item = arr.getJSONArray(i);
			for (int j = 0; j < item.length(); j++){
				double current_number = item.getDouble(j);
				r_values[i][j] = current_number;
			}
		}
		return r_values;
	}
	public static String send_data(String engine, Map<String, Double> values){
		StringBuilder builder = new StringBuilder("http://127.0.0.1:5000/?model="+String.valueOf(engine));
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
		builder.append(nameString.toString());
		builder.append(valueString.toString());
		return getHTML(builder.toString());
	}
}
