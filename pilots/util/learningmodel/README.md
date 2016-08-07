# PredictiveServer
### Author KeyboardNerd
A machine learning server running on Flask framework for internet connection.

## Installation
* install dependencies

~~~
pip install -r requirements.txt
~~~
## Usage
### Server Configuration
___
Configurations are saved in "server.config" json file.

1.  models: a list of models running on the server, this is the root of the json file
2. items in models list: every item has 4 fields: 
 * "id": a string identifies the model. 
 * "file_name": a string for the model file to be loaded, 
 * "json": bool, is loading method json? 
 * "initializer": the constructor to be used for initializing the object if it's unpickable 

#### example
~~~
{  
   "models":[  
      {  
         "id":"0",
         "file_name":"bayes.estimator",
         "json": true,
         "initializer": "bayes"
      },
      {  
         "id":"1",
         "file_name":"linearRegression.estimator",
         "initializer": "",
         "json": false
      }
   ]
}
~~~
### Run Server
___
You can run the learning server by running server.sh, which by default will run flask server on 127.0.0.1:5000

### URL Query
___
URL Query should identify these arguments:

* model: a model used for the prediction
* name: a list of fields the values of corresponding position represent, notice: name should match the fields name defined in feature transformation definition loaded in server. # this is subject to be revised
* value: a list of numbers

#### example
~~~
http://127.0.0.1:5000/?model=0&name=a,b&value=1.0,2.0
~~~

### Return
___
The server will return a string representation of the two dimensional matrix
e.g. [[1.0]] # subject to be revised

### Client
___
* Java Client: call Client.predict(String model_name, Map<String, Double> field_value_pairs) to get a double[] return value.

## Train Learning Model
### Load Training Configuration file

~~~
python jsonconf.py <train configuration file>
~~~

### Add learning model
in file learning_models.py, add line:

~~~
MODEL["model_name"] = LearningModel()
~~~

### Write Training Configuration file
create a json file with requested fields:

```javascript
{
	"data":{
		"file": "<data file>",
		"type": "<file parser ( defined in scparser.py ) >",
		"header_type": "<header parser ( defined in scparser.py ) >",
		"column_names": ["col_1 id", "col_2 id",...],
		(optional) "column_units": ["col_1 unit","col_2 unit",...],
		(optional) "constants": {"constant id": constant number value},
		"transformer":{
			(optional) "unit_transformation": ["col_1 unit","col_2 unit",...],
			"features": ["feature_1", "feature_2",...], 
			"labels": ["label_1", "label_2",...]
			// feature/labels format: comply to python format, each column id should be surrounded by { }.
		}
	},
	"trainer":{
		"model": "model name", // defined in learning_models.py
		"saveto": "file path to save the estimator",
		"savetojson": false // set true if the model should be saved in json format ( this will call model.tojson() )
	}
}
```

#### example:

```javascript
{
	"data":{
		"file": "data/thetrain.csv",
		"type": "csv",
		"header_type": "csvheader",
		"column_names": ["v","p","t","a","w"],
		"column_units": ["knot","in_Hg","celsius","degree","force_pound"],
		"constants": {"s": 61.0},
		"transformer":{
			"unit_transformation": ["m/s", "pascal","kelvin","radian","newton"],
			"features": ["{a}"],
			"labels": ["2*{w}/({v}**2*({p}/286.9/{t})*{s})"]
		}
	},
	"trainer":{
		"model": "linearregression",
		"saveto": "regression.estimator",
		"savetojson": false
	}
}
```

## Programming Tutorial

### steps:
* create base environment, each environment corresponds to one learning model.

~~~
env = BaseEnv()
~~~

* define training file schema 

~~~
column_name = ['v','p','t','a','w']
constants = {'s':61.0}
~~~

* load data from file and attach schema to the environment

~~~
data = env.load_file(file_name = "data/test.csv", map_method = csvfile, header_method = csvheader, header=column_name)
env.set_constant(**constants)
env.append_data(data)
~~~

* assign unit and perform unit transformation on data

~~~
env.update_variable_info(v={'unit': 'knot'}, p={'unit': 'in_Hg'}, t={'unit': 'celsius'}, a={'unit': 'degree'}, w={'unit': 'force_pound'}) 
cached_data = env.transform(env.unit_transformer, schema=env.get_schema_info(), units=dict(v='m/s',a='radian',w='newton',t='kelvin',p='pascal'))
env.data = cached_data.copy()
~~~

* set features and labels definitions, and then perform feature_label transformation to get the feature matrix and label matrix

~~~
feature_list = ["{a}"]
label_list = ["2*{w}/({v}**2*({p}/286.9/{t})*{s})"]
cached_data = env.transform(env.feature_label_transformer, schema=env.get_schema_info(), constant=env.get_constant(), features=feature_list, labels=label_list)
env.label = cached_data['label']
env.feature = cached_data['feature']
~~~

* instantiate a model to be trained

~~~
regression = LinearRegression()
~~~

* Train the model

~~~
trained_model = env.train_estimator(regression)
~~~

* save the trained model along with feature transformation definition, constant value in the environment. If the model requires special IO method, it should be wrapped in a class with `to_json()` and `load_json(string)` method implemented.

~~~
save_estimator("regression.model", trained_model, feature_list, env.constant_dict)
# for unpicklable object
save_estimator("model", trained_model, feature_list, constants, True)
~~~

## Prediction

* load estimator from file, for unpicklable object, it will call constructor, and then load_json(string) to load the object.

~~~
estimator, features, constants = load_estimator("model")
# for unpicklable object
load_estimator("model", True, Constructor_function)
~~~

* 