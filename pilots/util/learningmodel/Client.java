package pilots.util.learningmodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

public class Client {
	// simple implementation of java client, currently support only one variable, more will be added...
	public static void main(String[] args){
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++){
		Map<String, Double> map = new HashMap<>();
		map.put("aoa", 1.0);		
		predict(0, map);
		}
		System.out.println("" + String.valueOf(System.currentTimeMillis() - startTime));
	}
	public static void printArray(double[] t){
		for (double i : t){
			System.out.println(i);
		}
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
	public static double[] predict(int engine, Map<String, Double> values){
		String result = send_data(engine, values);
		String[] strings = result.split(",", -1);
		double[] double_result = new double[strings.length];
		for (int i = 0; i < strings.length; i++){
			double_result[i] = Double.valueOf(strings[i].trim()).doubleValue();
		}
		return double_result;
	}
	public static String send_data(int engine, Map<String, Double> values){
		StringBuilder builder = new StringBuilder("http://127.0.0.1:5000/?model="+String.valueOf(engine)+'&');
		List<String> keys = new ArrayList<>();
		keys.addAll(values.keySet());
		builder.append("name=");
		for( int i = 0; i < keys.size(); i++){
			builder.append(keys.get(i));
			if (i != keys.size() - 1){
				builder.append(",");
			}
		}
		builder.append("&value=");
		for (int i = 0; i < keys.size(); i++){
			builder.append(String.valueOf(values.get(keys.get(i))));
			if (i != keys.size() - 1){
				builder.append(",");
			}
		}
		return getHTML(builder.toString());
	}
}
