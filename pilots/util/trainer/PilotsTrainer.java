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

	num_test_features = 0;
	num_test_labels = 0;
	
	algorithm = null;
	alg_args = new HashMap<>();
	
	data = new HashMap<>();
    }

    // ================== Data =======================

    protected DataVector get( String key ) {
	if ( !data.containsKey( key ) )
	    throw new NoSuchElementException(key);
	return data.get( key );
    }

    protected List<DataVector> collect( String type, int number ) {
	List<DataVector> all = new ArrayList<>();
	for ( int n = 1; n <= number; ++n ) {
	    String name = type + String.valueOf( f );
	    all.add( new DataVector( data.get( name ) ) );
	}
	return all;
    }

    
    protected void pullCSV( String filename, String column_names ) {
	try {
	    System.out.println( "Path: " + System.getProperty("user.dir") );
	    String filepath = System.getProperty("user.dir");
	    filepath += "/../../pilots/util/model/data/";
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
		System.out.println("Pulled " + csv_data.get(i).size() +
				   " rows: " + cols_to_keep.get(i));
		data.put( cols_to_keep.get(i), new DataVector( csv_data.get(i) ) );
	    }
	    
	    rd.close();
	} catch ( Exception e ) {
	    System.err.println( "Error while reading file: " + filename );
	    e.printStackTrace();
	}
	
    }

    protected void pullModel( String modelname, String output_names, DataVector[] model_args ) {
	try {
	    List<DataVector> results  = pilots.util.model.Client.predict( modelname,  );

	    List<String> all_out_names = Arrays.asList( output_names.split(",") );
	    
	    // add results to output names
	    if ( results.size() != all_out_names.size() ) {
		throw new Exception("Model outputs cannot be placed in output data streams,"+
				    " mismatching size" );
	    }

	    for ( int i = 0; i < all_out_names.size(); ++i ) {
		data.put( name, new DataVector( results.get(i) );
	    }
		
	    
	} catch ( Exception e ) {
	    System.err.println( "Error while pulling data from model: " + modelname );
	    e.printStackTrace();
	}
    }

    protected void createSequence( String args, String output_names ) {
	DataVector d = new DataVector();
	String[] tmp_args = args.split(",");
	Double start = Double.valueOf( tmp_args[0] );
	Double step = Double.valueOf( tmp_args[1] );
	Integer num = Integer.valueOf( tmp_args[2] );
	Double val = start;
	for ( int i = 0; i < num; ++i ) {
	    d.add( val );
	    val += step;
	}

	List<String> all_out_names = Arrays.asList( output_names.split(",") );
	for ( String name : all_out_names ) {
	    data.put( name, new DataVector( d ) );
	}
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
	} else if ( src[0].equals( "sequence" ) ) {
	    createSequence( src[1], outputs );
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

    protected void addTestFeature( DataVector f ) {
	num_test_features++;
	String fname = "test_feature" + String.valueOf( num_features );
	data.put( fname, f );
    }

    protected void addTestLabel( DataVector l ) {
	num_test_labels++;
	String lname = "test_label" + String.valueOf( num_labels );
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
    
    public void train() {
	// === Pull all features out ===
	List<DataVector> features = collect( "feature", num_features );
	
	// === Pull labels out ===
	List<DataVector> labels = collect( "label", num_labels );

	// === Pull all test features out ===
	List<DataVector> test_features = collect( "test_feature", num_test_features );
	
	// === Pull test labels out ===
	List<DataVector> test_labels = collect( "test_label", num_test_labels );

	try {
	    Double accuracy = pilots.util.model.Client.train( algorithm, alg_args,
							  features, labels );
	    
	    if ( accuracy != null ) {
		System.out.println( "Trained model: " + algorithm );
		System.out.println( "Final accuracy: " + String.valueOf( accuracy ) );
	    } else {
		System.err.println( "Non-fatal error while training model: " + algorithm );
	    }


	    if ( test_features.size() > 0 && test_labels.size() > 0 ) {
		Double test_accuracy = pilots.util.model.Client.test( algorithm,
								 test_features,
								 test_labels );

		if ( test_accuracy != null ) {
		    System.out.println( "Testing dataset accuracy: " + String.valueOf( test_accuracy ) );
		} else {
		    System.err.println( "Non-fatal error while testing model" );
		}
		
	    }

	    
	} catch ( Exception e ) {
	    System.err.println( "Fatal Error while training" );
	    e.printStackTrace();
	}


    }

    
}
