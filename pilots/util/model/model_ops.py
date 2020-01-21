
import pickle
import importlib

def is_live_model( model_name ):
    mm = get_model_module( model_name )
    return mm.is_live()
    
def get_model_module( model_name ):
    """
    """
    return importlib.import_module( model_name )

def create_model( model_name, settings ):
    """
    """
    mm = get_model_module( model_name )
    return mm.make_model( settings )

def load_model( model_name ):
    """
    """
    model_filename = 'models/' + model_name + '.model'
    m_in_file = open( model_filename, 'rb' )
    new_model = pickle.load( m_in_file )
    m_in_file.close()
    return new_model


def save_model( model_name, model ):
    """
    """
    model_filename = 'models/' + str(model_name) + '.model'
    m_out_file = open( model_filename, 'wb' )
    s = pickle.dump( model, m_out_file )
    m_out_file.close()

def run_model( model_name, model, data ):
    """
    """
    mm = get_model_module( model_name )
    return mm.run( model, data )

def train_model( model_name, model, dataset ):
    """
    """
    mm = get_model_module( model_name )
    return mm.train( model, dataset )
