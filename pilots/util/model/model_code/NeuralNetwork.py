import numpy as np
from sklearn.neural_network import MLPRegressor

class NeuralNetwork:
    def __init__(self, settings):
        # custom user settings
        _class_or_regress = True
        if "classifier" in settings:
            _class_or_regress = True
        elif "regressor" in settings:
            _class_or_regress = False
        # MLP settings
        # Hidden layers
        _hidden_layer_sizes = (100,)
        h_layers = None
        h_nodes = None
        if "hidden_layers" in settings:
           h_layers = settings["hidden_layers"]
        if "hidden_nodes" in settings:
            h_nodes = settings["hidden_nodes"]
        if h_layers != None and h_nodes != None:
            _hidden_layer_sizes = (h_nodes,)*h_layers

        # Activation
        _activation = "relu"
        if "identity" in settings:
            _activation = "identity"
        elif "logistic" in settings:
            _activation = "logistic"
        elif "tanh" in settings:
            _activation = "tanh"
        elif "relu" in settings:
            _activation = "identity"

        # Solver
        _solver = "adam"
        if "lbfgs" in settings:
            _solver = "lbfgs"
        elif "sgd" in settings:
            _solver = "sgd"
        elif "adam" in settings:
            _solver = "adam"


        # Learning Rate
        _learning_rate = "constant"
        _learning_rate_init = 0.001
        if "learning_rate" in settings:
            _learning_rate_init = settings["learning_rate"]


        _verbose = True
            
        # Locked settings
        _alpha = 0.0001
        _batch_size = "auto"
        _power_t = 0.5
        _max_iter = 200
        _shuffle = True
        _random_state = None
        _tol = 1e-4
        _warm_start = False
        _momentum = 0.9
        _nesterovs_momentum = True
        _early_stopping = False
        _validation_fraction = 0.1
        _beta_1 = 0.9
        _beta_2 = 0.999
        _epsilon = 1e-8
        _n_iter_no_change = 10
        _max_fun = 15000
        # Create model using all settings
        if _class_or_regress:
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
        else:
            self._model = MLPClassifier(hidden_layer_sizes=_hidden_layer_sizes, activation=_activation,
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
            
    # API Functions
    def is_live( self ):
        return False

    def reset( self ):
        pass

    def train( self, X, y ):
        self._model = self._model.fit(X,y)
        return self
        
    def run( self, data ):
        result = self._model.predict( data )
        return result.tolist()

    def test( self, data ):
        tfeat = np.array(data["test_features"]).transpose()
        tlabels = np.array(data["test_labels"]).transpose()
        return self._model.score(tfeat,tlabels)
    
    def score( self, X, y):
        return self._model.score(X,y)
    

# Static API Functions

def make_model( settings ):
    model = NeuralNetwork( settings )
    return model

def train( model, dataset ):
    feat = np.array(dataset["features"]).transpose()
    labels = np.array(dataset["labels"]).transpose()
    model = model.train( feat, labels )
    acc = model.score( feat, labels )
    return acc, model
    
