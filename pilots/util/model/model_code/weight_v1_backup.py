import numpy as np
from sklearn.neural_network import MLPRegressor

class WeightModel:
    def __init__(self, settings):
        self.all_data = []
        self._steps = 20 # How many time steps the model looks at
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
        if len(self.all_data) < self._steps:
            self.all_data.append( data )
            return [-1.0]
        else:
            self.all_data.append( data )
            self.all_data = self.all_data[1:]
            #tmp = np.append( self.all_data[0], self.all_data[1:] )
            tmp = compress_data( self.all_data )
            result = self._model.predict( [tmp] ) * 100
            return result.tolist()
    def train( self, X, y ):
        self._model.fit(X,y)
        return self
    def score( self, X, y):
        return self._model.score(X,y)
    def get_steps( self ):
        return self._steps

def compress_data( data ):
    # Takes [[v_a], [prs], [tmp], [alt_i]]
    # Converts to [acc, alt]
    flipped = np.array( data ).transpose()
    #print( "Flipped", flipped )
    # get averaged data
    prs = np.average( flipped[1] )
    tmp = np.average( flipped[2] )
    alt = np.average( flipped[3] )
    # calculate pressure altitude
    prs_alt = (29.92 - prs) * 1000 + alt
    # calculate density altitude
    isa = 15 - (alt // 1000) * 2
    dens_alt = prs_alt + (120 * (tmp - isa))

    # calculate acceleration
    xs = np.array(range(flipped[0].size))
    vel_line = np.polyfit( xs, flipped[0], 3)
    acc = [vel_line[0], vel_line[1], vel_line[2]]

    print( "Acceleration: ", acc, "Density Alt: ", dens_alt )    
    return [acc[1], acc[2], dens_alt]
    
def is_live():
    return True

def make_model( settings ):
    model = WeightModel( settings )
    return model

def train( model, dataset ):
    np.seterr(all='warn')
    print("Starting training")
    feat = np.array(dataset["features"]).transpose()
    labels = np.array(dataset["labels"]).transpose()

    X = []
    y = []

    s = model.get_steps()

    i = s

    while i <= 3341:
        if feat[i-1][-1] < 0.001:
            i += s
            continue
        #tmp = np.append( feat[i-s], feat[i-s+1:i] ).tolist()
        tmp = feat[i-s:i]
        if i < 10:
            print(tmp)
        X.append( compress_data(tmp) )
        y.append( labels[i-s][0] / 100 )
        i += 1

    X = np.array(X, dtype=np.float64)
    y = np.array(y, dtype=np.float64)
    print( "X: ", X )
    print()
    print( "y: ", y )
    
    model = model.train( X, y )
    acc = model.score( X, y )

    # Test
    print("--- Test 1---")
    for i in range(50):
        if i > 20:
            print(i, model.run( feat[i] ) )
    print("--- Test 2---")
    for i in range(870,920):
        if i > 890:
            print(i, model.run( feat[i] ) )
    print("--- Test 3---")
    for i in range(2030, 2080):
        if i > 2050:
            print(i, model.run( feat[i] ) )
    
    return acc, model

def run( model, data ):
    result = model.run( data )
    return result.tolist()
    
    
