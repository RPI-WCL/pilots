
Specs for a model.py file:

def make_model():
    Returns something that stores all the information about the model.
    Can be a class or an sklearn model or many other things.
    Objects that can not be deserialized using the pickle library
    can not be used.

def train( model, settings, data ):
    model is the model object.
    settings is a map of settings to their values.
    data is a map containing features and labels:
      features is a list of list of input data (X)
      labels is a list of list of correct output data (y)
    

def run( model, data ):
    model is the model object.
    data is the list of list of input data.
    

# Custom Model Server
Goal:
Define a simple extensible interface to allow for custom machine learning algorithms to be trained, tested, and used in PILOTS programs. As well as build a server to handle the interfacing of the models.

APIs:

1. Load: Input: Name of model to load Output: Success or Failure
2. Train: Input: Name of model, model settings, and training dataset Output: Success and accuracy after training, or Failure
3. Run: Input: Name of model, values to run model on Output: Result of running model on given input, or Failure


Service:

1. Prediction service: Input: some homogeneous datastreams; Output: some predicted values using requested model
2. Training service: Input: training definition file; output: trained estimator
3. Specified for PILOTS project: produce Java Client for streaming data communication between PILOTS main program (Data Selection) and Predictive Server.

## Installation (Linux/MacOS)
### Prerequisites

TODO

## Model

Write a python file with specific functions:

~~~

"""
Says whether the model is updated during the run operation
"""
def is_live():
    return bool

"""
Gives a model object that will be serialized
"""
def make_model():
    return model

"""
Returns accuracy of training on that dataset
model_obj - deserialized model object
settings - map of settings
dataset - { "features": [[]+], "labels": [[]+] }
"""
def train( model_obj, settings, dataset ):
    return accuracy_percent

"""
Returns 
"""
def run( model_obj, data ):
    return [[]+]
~~~


## Server

### Load Request:
HTTP GET URL:
~~~
http://hostname:port/load/<model_name>
~~~
### Load Response:
JSON:
~~~
{"success": True/False}
~~~
### Train Request:

HTTP POST URL:
~~~
http://hostname:port/train/<model_name>
~~~
POST Data:
~~~
{
  "settings": { STRING: (BOOL|INT|DOUBLE) },
  "features": [],
  "labels": []
}
~~~

### Run Request:
HTTP POST URL:
~~~
http://hostname:port/run/<model_name>
~~~
POST Data:
~~~
{ "data": [[]+] }
~~~

## Example TRAIN
### Request
~~~
http://127.0.0.1:5000/train/lin_reg

{
  "settings": {
               "fit_intercept": true,
	       "normalize": false,
	       "copy_X", true
	     }
  "features": [[1,1,2,2], [1,2,2,3]]
  "labels": [[6, 8, 9, 11]]
}

### Response
~~~
{
  "success": true,
  "accuracy": 1.0
}
~~~
