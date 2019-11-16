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

def collect_dict( what ):
    names = map( str.strip, request.json[what].split(',') )
    ret = []
    for n in names:
        ret[n] = request.json[ n ]
    return ret

def collect_list( what ):
    names = map( str.strip, request.json[what].split(',') )
    ret = []
    for n in names:
        ret.append(request.json[ n ])
    return ret

# === Endpoints =====

@app.route("/load/<model_id>", methods = ['GET'])
def server_load( model_id ):
    try:
        #model_id = request.args['model']
        global server_models
        model_name = str( model_id )
        server_models[model_name] = model_ops.load_model( model_name )
        return json.dumps( {"success": True} )
    except:
        return json.dumps( {"success": False} )

@app.route("/train/<model_id>", methods = ['POST'])
def server_train( model_id ):
    try:
        # === Make sure model is loaded ===
        model_id = request.args['model']
        global server_models
        if not server_models.has_key( model_id ):
            return "ERROR"
        # === Load model ===
        curr_model = server_models[ model_id ]
        # === Collect settings ===
        settings = collect_dict( 'settings' )
        # === Collect inputs ===
        features = collect_list( 'features' )
        labels = collect_list( 'labels' )
        tr_dataset = { "features": features, "labels": labels }
        # === Train model ===
        accuracy = model_ops.train( model_id, curr_model, settings, tr_dataset )
        # === Save updated model ===
        model_ops.save_model( model_id, curr_model )
        return "{ \"success\": true, \"accuracy\":" + str(accuracy) + " }"
    except:
        return json.dumps( {"success": False} )

@app.route("/run/<model_id>", methods = ['POST'])
def server_run( model_id ):
    #try:
        # === Make sure model is loaded ===
        print("RUNNING")
        model_name = str( model_id )
        global server_models
        if not model_name in server_models.keys():
            print( "MODEL NOT FOUND" )
            return json.dumps( {"success": False} )
        print( "Running model", model_name )
        # === Load model ===
        curr_model = server_models[ model_name ]
        # === Collect inputs ===
        data = collect_list( "data" )
        # === Return results ===
        result = model_ops.run_model( model_id, curr_model, data )
        # === Save if live model ===
        #if model_ops.is_live_model():
            #model_ops.save_model( model_name, curr_model )
        return json.dumps( {"value": result} )
    #except:
        #return "{ \"success\": false }"
    
@app.route("/")
def main():
    return "This is the PILOTS model server"
           

