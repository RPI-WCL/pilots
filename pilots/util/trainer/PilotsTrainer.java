package pilots.util.trainer;

import java.util.*;

import pilots.util.model.*;

public class PilotsTrainer {

    protected int num_features;
    protected int num_labels;
    protected String algorithm;
    protected Map<String, Double> alg_args; // TODO: more arg types

    protected Map<String, DataVector> data;

    public PilotsTrainer() {
	num_features = 0;
	num_labels = 0;
	
	algorithm = null;
	alg_args = new HashMap<>();
	
	data = new HashMap<>();
    }

    protected void pullData( String source, String outputs, DataVector ... model_args ) {
	// If file:
	//   Open file
	//   Find correct columns
	//   Save columns into data
	// If model:
	//   ... TODO
    }

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
	alg_args.put( arg_name, d );
    }

    protected void addAlgArg( String arg_name, int i ) {
	alg_args.put( arg_name, new Double(i) );
    }

    protected void addAlgArg( String arg_name, boolean b ) {

    }
    
    void train() {
	return;
    }

    
}
