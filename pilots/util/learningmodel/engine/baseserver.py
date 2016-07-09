import baseenv as base
import bayes
from flask import Flask
from flask import request
import numpy as np
import json

file_name = 'server.config'
app = Flask(__name__)


# globals
INITIALIZERS = {'bayes': bayes.Bayes}
ESTIMATOR = {}
LAMBDA_CALCULATOR = {}
SCHEMA = {}
CONSTANT = {}
SCHEMA_TRANSLATION = {}
def make_json(npmatrix):
    '''
        @param: npmatrix ( a ndarray, column matrix )
        @return: a json_string representing the npmatrix, key is column index, value is corresponding column matrix
    '''    
    return json.dumps({'value': npmatrix.tolist()})

def load_config(file_name):
    import json
    raw = json.load(open(file_name,'rU'))
    models = raw['models']
    for model in models:
        id_ = model['id']
        json = model['json']
        f = model['file_name']
        init = model['initializer']
        to_json = f
        if init:
            initializer = INITIALIZERS[init]
        else:
            initializer = None
        ESTIMATOR[id_] = base.load_estimator(f, to_json, initializer)
        SCHEMA_TRANSLATION[id_] = model['dictionary']
    print "configuration loaded: SCHEMA TRANSLATION = " + str(SCHEMA_TRANSLATION) + "\nSCHEMA = " + str(ESTIMATOR)
def accept_schema(request, model, estimator_dict, schema_dict, lambda_dict, schema_translation):
    estimator, features,constant = estimator_dict[model]
    translation = schema_translation[model]
    to_parse = False
    if model in schema_dict and schema_dict[model]['name'] != request.args['name']:
        to_parse = True
    elif model not in schema_dict:
        schema_dict[model] = {}
        schema_dict[model]['name'] = request.args['name']
        to_parse = True
    if to_parse:
        current_schema = {}
        name = map(lambda(x): x.strip(), request.args['name'].split(',')) # parse to schema
        for i in xrange(len(name)):
            current_name = name[i]
            if name[i] in translation:
                current_name = translation[name[i]]
            current_schema[current_name] = {'index': i}
        schema_dict[model]['schema'] = current_schema
        schema_dict[model]['name'] = request.args['name']
        print schema_dict
        print
        lambda_dict[model] = base.BaseEnv.generate_transformer(features, schema_dict[model]['schema'], constant)
        print "lambda expression updated! "

load_config(file_name)
@app.route("/")
def hello():
    model = request.args['model']
    estimator, features,constant = ESTIMATOR[model] # retrieve model
    data = [map(lambda(x): float(x.strip()), request.args['value'].split(','))] # parse to data
    accept_schema(request, model, ESTIMATOR, SCHEMA, LAMBDA_CALCULATOR, SCHEMA_TRANSLATION)
    feature_transformer = LAMBDA_CALCULATOR[model]
    X = np.apply_along_axis(feature_transformer, 1, data)
    print 'to predict =' + str(X)
    return make_json(estimator.predict(X))
