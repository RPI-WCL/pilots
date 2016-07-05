import base
import numpy
import json
from flask import Flask
from flask import request
import numpy as np
app = Flask(__name__)
param, reg = base.load_estimator("linear_regression.model")
ESTIMATOR = {}
ESTIMATOR['0'] = (param, reg)
@app.route("/")
def hello():
    model = request.args['model']
    value = [map(lambda(x): float(x.strip()), request.args['value'].split(','))]
    name = map(lambda(x): x.strip(), request.args['name'].split(','))
    param, reg = ESTIMATOR[model]
    param['name'] = name
    feature_transformer = base.generate_transformer(**param)
    X = np.apply_along_axis(feature_transformer, 1, value)
    return str(reg.predict(X))[2:-2]