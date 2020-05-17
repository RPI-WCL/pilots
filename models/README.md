# Machine Learning Server

**Goal:**
Define a simple extensible interface to allow for custom machine learning algorithms to be trained, tested, and used in PILOTS programs. As well as build a server to handle the interfacing of the models.

Within this directory is where the implementation of each machine learning lagorithms are (model_code) and the serialized trained model (trained_models). All data for training and validation is stored in the [data](../data) folder

Defines APIs for creations, training, execution, and testing of machine learning models.

## Creation of Machine Learning Algorithms

See [model_code/Example.py](./model_code/Example.py) to see a template for the API functions calls.

## Installation (Linux/MacOS)
### Prerequisites

* python
* flask
* numpy
* sklearn


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
http://127.0.0.1:5000/train/LinearRegression

{
  "settings": {
               "OrdLeastSq": true,
	       "normalize": false
	     }
  "features": [[1,1,2,2], [1,2,2,3]]
  "labels": [[6, 8, 9, 11]]
}

### Response
~~~
{
  "success": true,
  "accuracy": 0.99
}
~~~
