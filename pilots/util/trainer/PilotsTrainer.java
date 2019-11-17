package pilots.util.trainer;

import java.io.*;
import java.util.*;

import pilots.util.model.*;

public class PilotsTrainer {

    protected int num_features;
    protected int num_labels;
    protected String algorithm;
    protected Map<String, ModelArg> alg_args;

    protected Map<String, DataVector> data;

    public PilotsTrainer() {
	num_features = 0;
	num_labels = 0;
	
	algorithm = null;
	alg_args = new HashMap<>();
	
	data = new HashMap<>();
    }

    // ================== Data =======================
    
    private void pullCSV( String filename, String column_names ) {
	try {
	    String filepath = "pilots/util/model/data/";
	    BufferedReader rd = new BufferedReader( new FileReader( filepath + filename ) );
	    // === Find which columns to store ===
	    List<String> cols_to_keep = Arrays.asList(column_names.split(","));
	    List<Integer> col_indices = new ArrayList<>();
	    String header = rd.readLine();
	    int index = 0;
	    for ( String col : header.split(",") ) {
		if ( cols_to_keep.contains( col ) ) {
		    col_indices.add( index );
		}
		++index;
	    }

	    // ======
	    List<DataVector> csv_data = new ArrayList<>();
	    for ( int i = 0; i < col_indices.size(); ++i ) {
		csv_data.add( new DataVector() );
	    }
	    
	    // === Save data from csv ===
	    String row = null;
	    while ( (row = rd.readLine()) != null ) {
		String[] tmp = row.split(",");
		// Add all entries from correct columns into correspoding DataVectors
		for ( int i = 0; i < col_indices.size(); ++i ) {
		    Double val = Double.valueOf( tmp[ col_indices.get(i) ] );
		    csv_data.get(i).add( val );
		}
	    }

	    // === Push data into data map ===
	    for ( int i = 0; i < cols_to_keep.size(); ++i ) {
		data.put( cols_to_keep.get(i), csv_data.get(i) );
	    }
	    
	    rd.close();
	} catch ( Exception e ) {
	    System.err.println( "Error while reading file: " + filename );
	    e.printStackTrace();
	}
	
    }

    private void pullModel( String modelname, String output_names, DataVector[] model_args ) {
	// TODO
    }

    protected void pullData( String source, String outputs, DataVector ... model_args ) {
	String[] src = source.split(":");
	if ( src.length > 2 ) {
	    System.err.println( "Malformed source name" );
	    return;
	}
	if ( src[0].equals( "file" ) ) {
	    pullCSV( src[1], outputs );
	} else if ( src[0].equals( "model" ) ) {
	    pullModel( src[1], outputs, model_args );
	} else {
	    System.err.println( "Unknown source type" );
	}
    }

    // ================= Model =====================

    protected void addFeature( DataVector f ) {
	num_features++;
	String fname = "feature" + String.valueOf( num_features );
	data.put( fname, f );
    }

    protected void addLabel( DataVector l ) {
	num_labels++;
	String lname = "label" + String.valueOf( num_labels );
	data.put( lname, l );
    }

    protected void addAlgArg( String arg_name, Double d ) {
	alg_args.put( arg_name, new ModelArg( d ) );
    }

    protected void addAlgArg( String arg_name, int i ) {
	alg_args.put( arg_name, new ModelArg( i ) );
    }

    protected void addAlgArg( String arg_name, boolean b ) {
	alg_args.put( arg_name, new ModelArg( b ) );
    }

    // =================== Train ======================
    
    void train() {
	// === Pull all features out ===
	List<DataVector> features = new ArrayList<>();
	for ( int f = 0; f < num_features; ++f ) {
	    String feat_name = "feature" + String.valueOf( f );
	    features.add( data.get( feat_name ) );
	}
	
	// === Pull labels out ===
	List<DataVector> labels = new ArrayList<>();
	for ( int l = 0; l < num_labels; ++l ) {
	    String label_name = "label" + String.valueOf( l );
	    features.add( data.get( label_name ) );
	}
	
	Double accuracy = pilots.util.model.Client.train( algorithm, alg_args,
							  features, labels );

	if ( accuracy != null ) {
	    System.out.println( "Trained model: " + algorithm );
	    System.out.println( "Final accuracy: " + String.valueOf( accuracy ) );
	} else {
	    System.err.println( "Error while training model: " + algorithm );
	}
    }

    
}
