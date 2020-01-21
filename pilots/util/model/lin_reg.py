import numpy as np
from sklearn.linear_model import LinearRegression

def is_live():
    return False

def make_model( settings ):
    _fit_intercept = True
    if "fit_intercept" in settings:
        _fit_intercept = settings["fit_intercept"]
    _normalize = False
    if "normalize" in settings:
        _normalize = settings["normalize"]
    _copy_X = True
    if "copy_X" in settings:
        _copy_X = settings["copy_X"]
    _n_jobs = None
    if "n_jobs" in settings:
        _n_jobs = settings["n_jobs"]
    model = LinearRegression( fit_intercept=_fit_intercept,
                              normalize=_normalize,
                              copy_X=_copy_X,
                              n_jobs=_n_jobs)
    return model

def train( model, dataset ):
    X = np.array(dataset["features"]).transpose()
    y = np.array(dataset["labels"]).transpose()
    print("X:", X)
    print("y:", y)
    model = model.fit( X, y )
    acc = model.score( X, y )
    return acc, model

def run( model, data ):
    _data = np.array(data).transpose()
    result = model.predict( _data )
    return result.tolist()
