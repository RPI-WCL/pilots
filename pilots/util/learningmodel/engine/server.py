from flask import Flask
from flask import request
import numpy as np
import json
import InterfaceIO

config = 'server.json'
app = Flask(__name__)

def make_json(npmatrix):
    '''
        @param: npmatrix ( a ndarray, column matrix )
        @return: a json_string representing the npmatrix, key is column index, value is corresponding column matrix
    '''    
    return json.dumps({'value': npmatrix.tolist()})
interface = InterfaceIO.InterfaceIO(config)

@app.route("/")
def hello():
    model_id = request.args['model']
    data = map(lambda(x): float(x.strip()), request.args['value'].split(',')) # parse to data
    schema = map(unicode.strip, request.args['name'].split(','))
    return make_json(interface.predictSingleValue(model_id, InterfaceIO.makedict(data, schema)))
