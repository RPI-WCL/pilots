from flask import Flask
from flask import request
import json
import model_ops

app = Flask(__name__)
server_models = {}

# === Helper functions =====

def make_json( l ):
    s = json.dumps({'value': l})
    print(s)
    return s

# === Endpoints =====

@app.route("/load/<model_id>", methods = ["GET"])
def server_load( model_id ):
    try:
        global server_models
        server_models[model_name] = model_ops.load_model( str( model_id ) )
        return json.dumps( {"success": True} )
    except:
        return json.dumps( {"success": False} )

@app.route("/train/<model_id>", methods = ["POST"])
def server_train( model_id ):
    try:
        # === Make sure model is loaded ===
        global server_models
        if not server_models.has_key( model_id ):
            return json.dumps( {"success": False} )
        # === Load model ===
        curr_model = server_models[ model_id ]
        # === Collect settings ===
        settings = request.json( "settings" )
        # === Collect inputs ===
        features = request.json( "features" )
        labels = request.json( "labels" )
        tr_dataset = { "features": features, "labels": labels }
        # === Train model ===
        accuracy = model_ops.train( model_id, curr_model, settings, tr_dataset )
        # === Save updated model ===
        model_ops.save_model( model_id, curr_model )
        return json.dumps( {"success": True, "accuracy": accuracy } )
    except:
        return json.dumps( {"success": False} )

@app.route("/run/<model_id>", methods = ["POST"])
def server_run( model_id ):
    try:
        # === Make sure model is loaded ===
        model_name = str( model_id )
        global server_models
        if not model_name in server_models.keys():
            return json.dumps( {"success": False} )
        # === Load model ===
        curr_model = server_models[ model_name ]
        # === Collect inputs ===
        data = request.json( "data" );
        # === Return results ===
        result = model_ops.run_model( model_name, curr_model, data )
        # === Save if live model ===
        if model_ops.is_live():
            model_ops.save_model( model_name, curr_model )
        return json.dumps( {"value": result} )
    except:
        return jon.dumps( {"success": False} )
    
@app.route("/")
def main():
    return "This is the PILOTS model server"
           

