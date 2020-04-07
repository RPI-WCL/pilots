
import pickle
import importlib

def get_model_module( model_name ):
    # import code of python model
    return importlib.import_module( "model_code." + model_name )

# === File I/O ===

def load_model( model_name ):
    # Unserialize model file
    model_filename = 'trained_models/' + model_name + '.model'
    m_in_file = open( model_filename, 'rb' )
    new_model = pickle.load( m_in_file )
    m_in_file.close()
    return new_model

def save_model( model_name, model ):
    # Serialize model into file
    model_filename = 'trained_models/' + str(model_name) + '.model'
    m_out_file = open( model_filename, 'wb' )
    s = pickle.dump( model, m_out_file )
    m_out_file.close()

# === Model functions ===

def reset( model ):
    model.reset()

def is_live_model( model ):
    # checks if python model updates while running
    return model.is_live()

def run_model( model_name, model, data ):
    return model.run( data )

def test_model( model_name, model, data ):
    return model.test( data )

# === Module functions ===

def create_model( algo_name, settings ):
    mm = get_model_module( algo_name )
    return mm.make_model( settings )

def train_model( algo_name, model, dataset ):
    mm = get_model_module( algo_name )
    return mm.train( model, dataset )
