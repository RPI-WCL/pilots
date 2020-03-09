from flask import Flask
from flask import request
import json
import model_ops

app = Flask(__name__)
server_models = {}

# === Endpoints =====

@app.route("/load/<model_id>", methods = ["GET"])
def server_load( model_id ):
    # Load a model
    global server_models
    server_models[model_id] = model_ops.load_model( str( model_id ) )
    model_ops.reset( server_models[model_id] )
    return json.dumps( {"success": True} )


@app.route("/train/<algo_model>", methods = ["POST"])
def server_train( algo_model ):
    # Create and train a model
    # === Make sure model is NOT loaded ===
    algo_id, model_id = algo_model.split(":")
    global server_models
    if model_id in server_models.keys():
        print( "ERROR: Model already loaded" )
        x = input( "Would you like to continue? [Y/n]" )
        x = x.lower()
        if x == 'n' or x == 'no':
            return json.dumps( {"success": False} )

    print(request.json)
    # === Collect settings ===
    settings = request.json["settings"]
    # === Collect inputs ===
    features = request.json["features"]
    labels = request.json["labels"]
    tr_dataset = { "features": features, "labels": labels }
    # === Create new model ===
    curr_model = model_ops.create_model( algo_id, settings )
    # === Train model ===
    accuracy, curr_model = model_ops.train_model( algo_id, curr_model, tr_dataset )
    print("Finished training")
    # === Save updated model ===
    server_models[ model_id ] = curr_model
    model_ops.save_model( model_id, curr_model )
    return json.dumps( {"success": True, "accuracy": accuracy } )


@app.route("/run/<model_id>", methods = ["POST"])
def server_run( model_id ):
    # Run a loaded model
    # === Make sure model is loaded ===
    model_name = str( model_id )
    global server_models
    if not model_name in server_models.keys():
        return json.dumps( {"success": False} )
    # === Load model ===
    curr_model = server_models[ model_name ]
    # === Collect inputs ===
    data = request.json["data"]
    # === Return results ===
    result = model_ops.run_model( model_name, curr_model, data )
    # === Save if live model ===
    if curr_model.is_live():
        model_ops.save_model( model_name, curr_model )
    return json.dumps( {"success": True, "value": result} )

@app.route("/test/<model_id>", methods = ["POST"])
def server_test( model_id ):
    # Test a loaded model
    # === Make sure model is loaded ===
    model_name = str( model_id )
    global server_models
    if not model_name in server_models.keys():
        return json.dumps( {"success": False} )
    # === Load model ===
    curr_model = server_models[ model_name ]
    # === Collect inputs ===
    tfeatures = request.json["test_features"]
    tlabels = request.json["test_labels"]
    tr_dataset = { "test_features": tfeatures, "test_labels": tlabels }
    # === Return results ===
    result = model_ops.test_model( model_name, curr_model, tr_dataset )
    # === Save if live model ===
    if curr_model.is_live():
        model_ops.save_model( model_name, curr_model )
    return json.dumps( {"success": True, "accuracy": result} )

    
@app.route("/")
def main():
    return "This is the PILOTS model server"
           

