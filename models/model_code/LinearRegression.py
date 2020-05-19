import numpy as np
from sklearn.linear_model import LinearRegression

class LinearRegressionModel:
    def __init__(self, settings):
        if "OrdLeastSq" in settings:
            if not settings["OrdLeastSq"]:
                println("Error: no other methods implemented")
                # Settings from sklearn model
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
        # ---------------------------------------------------
        self.model = LinearRegression( fit_intercept=_fit_intercept,
                                  normalize=_normalize,
                                  copy_X=_copy_X,
                                  n_jobs=_n_jobs)

    def is_live(self):
        return False

    def reset(self):
        pass

    def train(self, X, y):
        self.model.fit(X,y)
        acc = self.model.score(X,y)
        return acc, self

    def run(self, data ):
        _data = np.array(data).transpose()
        result = self.model.predict( _data )
        return result.tolist()

    def test(self, data):
        X = np.array(dataset["test_features"]).transpose()
        y = np.array(dataset["test_labels"]).transpose()
        acc = self.model.score( X, y )
        return acc

# ------------------------------------------------------
    
def make_model(settings):
    model = LinearRegressionModel(settings)
    return model

def train(model, dataset ):
    X = np.array(dataset["features"]).transpose()
    y = np.array(dataset["labels"]).transpose()
    acc, model = model.train( X, y )
    return acc, model

