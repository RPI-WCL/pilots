from flask import Flask
from flask import request
import json
import model_ops

app = Flask(__name__)
server_model = None

def make_json( l ):
    s = json.dumps({'value': l})
    print(s)
    return s

@app.route("/load")
def server_load():
    model_id = request.args['model']
    global server_model
    server_model = model_ops.load_model( model_id )
    return "Model Loaded"

@app.route("/train")
def server_train():
    model_id = request.args['model']
    mm = model_ops.get_model( model_id )
    return "Training"

@app.route("/")
def main():
    global server_model
    if server_model == None:
        return "Model not loaded", 404
    model_id = request.args['model']
    data = map( lambda x: float(x.strip()),
                request.args['value'].split(','))
    schema = map( str.strip, request.args['name'].split(',') )
    result = model_ops.run_model( server_model, model_id,
                                  dict(zip( schema, data )) )
    return make_json( result )

