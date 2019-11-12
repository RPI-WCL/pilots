
def make_model():
    return False

def train( model, arg_map ):
    model = make_model()
    # do training HERE
    trained_model = True
    return trained_model

def run( model, data_map ):
    a = data_map['a']
    b = data_map['b']
    return [ a+b ]
