
Specs for a model.py file:

def make_model():
    returns something that stores all the information about the model

def train( model, settings, data ):
    model is the model object
    settings is a map of settings to their values
    data is a map containing features and labels
      features is a list of list of input data (X)
      labels is a list of list of correct output data (y)

def run( model, data ):
    model is the model object
    data is the list of list of input data
    
      