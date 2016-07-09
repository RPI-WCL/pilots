#PILOTS Machine Learning Extension
## Usage
### Server
Defined in util/learningmodel/README.md
### PILOTS Client
PILOTS calls util/learningmodel/Client.java to get result from predictive server

The Extension language is defined as:

~~~
var_name (t) predict (model_id, feature_var_1, feature_var_2,...)
~~~

* model_id: the identifier string of the requested model stored in http server.
* featuer_var\_*: the variable used for prediction. variable name should match the feature name in transformation method of requested model or at least match one of the feature names in "dictionary" field for requested model. **Feature variables are retrieved by Closest(t) Method.**

## Future work
* Redesign variable name dependency between server and client.
* Allow people control get data method for each variables.
