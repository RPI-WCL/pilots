import numpy as np
from sklearn.neural_network import MLPRegressor

class NeuralNetwork:
    def __init__(self, settings):
        # custom user settings
        if "steps" in settings:
            self._steps = settings["steps"]
        # MLP settings
        _hidden_layer_sizes = (100,)
        if "hidden_layer_sizes" in settings:
            _hidden_layer_sizes = settings["hidden_layer_sizes"]
        _activation = "identity"
        if "activation" in settings:
            _activation = settings["activation"]
        _solver = "lbfgs"
        if "solver" in settings:
            _solver = settings["solver"]
        _alpha = 0.0001
        if "alpha" in settings:
            _alpha = settings["alpha"]
        _batch_size = "auto"
        if "batch_size" in settings:
            _batch_size = settings["batch_size"]
        _learning_rate = "constant"
        if "learning_rate" in settings:
            _learning_rate = settings["learning_rate"]
        _learning_rate_init = 0.001
        if "learning_rate_init" in settings:
            _learning_rate_init = settings["learning_rate_init"]
        _power_t = 0.5
        if "power_t" in settings:
            _power_t = settings["power_t"]
        _max_iter = 1000
        if "max_iter" in settings:
            _max_iter = settings["max_iter"]
        _shuffle = True
        if "shuffle" in settings:
            _shuffle = settings["shuffle"]
        _random_state = None
        if "random_state" in settings:
            _random_state = settings["random_state"]
        _tol = 1e-6
        if "tol" in settings:
            _tol = settings["tol"]
        _verbose = True
        if "verbose" in settings:
            _verbose = settings["verbose"]
        _warm_start = False
        if "warm_start" in settings:
            _warm_start = settings["warm_start"]
        _momentum = 0.9
        if "momentum" in settings:
            _momentum = settings["momentum"]
        _nesterovs_momentum = True
        if "nesterovs_momentum" in settings:
            _nesterovs_momentum = settings["nesterovs_momentum"]
        _early_stopping = False
        if "early_stopping" in settings:
            _early_stopping = settings["early_stopping"]
        _validation_fraction = 0.1
        if "validation_fraction" in settings:
            _validation_fraction = settings["validation_fraction"]
        _beta_1 = 0.9
        if "beta_1" in settings:
            _beta_1 = settings["beta_1"]
        _beta_2 = 0.999
        if "beta_2" in settings:
            _beta_2 = settings["beta_2"]
        _epsilon = 1e-8
        if "epsilon" in settings:
            _epsilon = settings["epsilon"]
        _n_iter_no_change = 150
        if "n_iter_no_change" in settings:
            _n_iter_no_change = settings["n_iter_no_change"]
        _max_fun = 15000
        if "max_fun" in settings:
            _max_fun = settings["max_fun"]
        # Create model using all settings
        self._model = MLPRegressor(hidden_layer_sizes=_hidden_layer_sizes, activation=_activation,
                                   solver=_solver, alpha=_alpha, batch_size=_batch_size,
                                   learning_rate=_learning_rate, learning_rate_init=_learning_rate_init,
                                   power_t=_power_t, max_iter=_max_iter, shuffle=_shuffle,
                                   random_state=_random_state, tol=_tol, verbose=_verbose,
                                   warm_start=_warm_start, momentum=_momentum,
                                   nesterovs_momentum=_nesterovs_momentum,
                                   early_stopping=_early_stopping,
                                   validation_fraction=_validation_fraction,
                                   beta_1=_beta_1, beta_2=_beta_2,
                                   epsilon=_epsilon, n_iter_no_change=_n_iter_no_change )
        
    def run( self, data ):
        result = self._model.predict( data )
        return result.tolist()
    
    def train( self, X, y ):
        self._model.fit(X,y)
        return self
    
    def score( self, X, y):
        return self._model.score(X,y)
    
def is_live():
    return False

def make_model( settings ):
    model = NeuralNetwork( settings )
    return model

def train( model, dataset ):
    feat = np.array(dataset["features"]).transpose()
    labels = np.array(dataset["labels"]).transpose()
    model = model.train( X, y )
    acc = model.score( X, y )
    return acc, model

def run( model, data ):
    result = model.run( data )
    return result.tolist()
    
    
