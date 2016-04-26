# Learning Model, database and unit converter
These models uses learning model to estimate the target function. DataBase will load data from file to the memory, by the method defined in Data Configuration files. UnitConverter will take in any available unit and convert it to the unit with same type ( the unit database is defined in
*unit.json*). You can implement SupervisedLearningModel and DataTransformer for new learning models. LeastSquare and SpecialLinearTransformation classes are made for illustration of how this works, which implements the parameter learning using lift function, with QR Factorization for regularization.

# Usage:
You need to first define Data Configuration files for databases (what columns will be used in the learning model and what is the units of them), [optional the data conversion files for databases ( what new name you want to assign to each columns, and what is the new units of them)],  the main configuration file for running the learning model.
```
python main.py [main configuration file]
```
## main configuration file:
```
 {
     "Unit Converter File": "Path to the Unit Converter File",
     "Training Data Files": [{
         "name": "PATH TO DATA FILE",
         "deliminator": "DELIMINATOR OF THE DATA FILE",
         "cruise phase": [
             [CRUISE PHASE START LINE NUMBER, CRUISE PHASE END LINE NUMBER], ...
         ]
     }, ...(A list of {name,deliminator,cruise phase})],
     "Training Data Inital Configuration": "PATH TO DATA INITIAL CONFIGURATION FILE"
     "Training Data Unit Conversion Configuration": "PATH TO DATA CONVERSION CONFIGURATION FILE",
     "Training Data Visualization": true or false (to enable visualization of training data),
     "Training Data Result Visualization": true or false (to enable fitting result visualization of training data),
     "Testing Data Files": [{
         "name": "PATH TO DATA FILE",
         "deliminator": "DELIMINATOR OF THE DATA FILE",
         "cruise phase": [
             [CRUISE PHASE START LINE NUMBER, CRUISE PHASE END LINE NUMBER], ...
         ]
     }, ...(A list of {name,deliminator, cruise phase)],
     "Testing Data Inital Configuration": "PATH TO DATA INITIAL CONFIGURATION FILE"
 ",
 "Testing Data Unit Conversion Configuration": "PATH TO DATA CONVERSION CONFIGURATION FILE",
 "Testing Data Visualization": true or false (to enable visualization of test data),
     "Testing Data Result Visualization": true or false(to enable fitting result visualization of test data),
     "X Fields": ["columns you want to use for training set (named by latest configuration file)", ...],
     "Y Fields": ["columns you want to use for target set (named by latest configuration file)", ...],
     "Automatic Cruise Phase": true or false (to enable the automatic cruise phase finder),
     "Cruise Phase Time Field": "column name of time",
     "Cruise Phase Speed Field": "column name of speed",
     "Cruise Phase Maximum Acceleration": number (maximum acceleration to find cruise phase),
     "Cruise Phase Minimum Length": number (minimum cruise phase time period to find cruise phase),
     "Cruise Phase Minimum Speed": number (minimum cruise phase speed),
     "Plane Empty Weight": number (the empty weight of the plane)
 }
```
## Example Main Configuration file:
```
{
    "Unit Converter File": "unit.json",
    "Training Data Files": [{
        "name": "../data/Hight Power King Air_2.txt",
        "deliminator": "|",
        "cruise phase": [
            [766, 1256],
            [1788, 2300],
            [2884, 3736]
        ]
    }, {
        "name": "../data/Medium Power King Air.txt",
        "deliminator": "|",
        "cruise phase": [
            [945, 1725],
            [2225, 2782],
            [3450, 4284]
        ]
    }],
    "Training Data Inital Configuration": "initConfig.json",
    "Training Data Unit Conversion Configuration": "afterConfig.json",
    "Training Data Visualization": false,
    "Training Data Result Visualization": false,
    "Testing Data Files": [{
        "name": "../data/Low Power King Air.txt",
        "deliminator": "|",
        "cruise phase": [
            [630, 1335],
            [1706, 2337],
            [2670, 3487]
        ]
    }],
    "Testing Data Inital Configuration": "initConfig.json",
    "Testing Data Unit Conversion Configuration": "afterConfig.json",
    "Testing Data Visualization": false,
    "Testing Data Result Visualization": false,
    "X Fields": ["true air speed", "ambient pressure", "ambient temperature", "angle of attack"],
    "Y Fields": ["fuel 1", "fuel 2", "fuel 3", "fuel 4", "fuel 5", "fuel 6", "fuel 7", "fuel 8"],
    "Automatic Cruise Phase": false,
    "Cruise Phase Time Field": "time",
    "Cruise Phase Speed Field": "true air speed",
    "Cruise Phase Maximum Acceleration": 10,
    "Cruise Phase Minimum Length": 10,
    "Cruise Phase Minimum Speed": 10,
    "Plane Empty Weight": 37836.5732
}
```
## Data Configuration File:
For Data Initial Configuration File, **identify the column names in data file and maintain the consistence between the "name" values in this file and column names. Each column should have different "key" values.**
For Data Conversion Configuration File, **The changed column information is applied by the key values**, so track back to what the 'key' value is referring to and write down the new value for that column. The columns not covered in the Data Conversion Configuration File will remain unchanged.
```
{
    "columns": [{
        "key": number,
        "type": "type of the unit/column semantic meaning",
        "name": "name of the column",
        "unit": "unit of the column"
    }, ...(each one should have different key value)]
}
```
## Example Data Initial Configuration File:
```
{
    "columns": [{
        "key": 0,
        "type": "speed",
        "name": "Vtrue,_ktas",
        "unit": "knot"
    }, {
        "key": 1,
        "type": "pressure",
        "name": "AMprs,_inHG",
        "unit": "inches of mercury"
    }, {
        "key": 2,
        "type": "temperature",
        "name": "AMtmp,_degC",
        "unit": "celsius"
    }, {
        "key": 3,
        "type": "angle",
        "name": "alpha,__deg",
        "unit": "degree"
    }, {
        "key": 4,
        "type": "force",
        "name": "_fuel,_1_lb",
        "unit": "pound"
    }]
}
```
## Example Data Conversion Configuration File:
```
{
  "columns": [
    {
      "key": 0,
      "type": "speed",
      "name": "true air speed",
      "unit": "meter per second"
    },
    {
      "key": 1,
      "type": "pressure",
      "name": "ambient pressure",
      "unit": "pascal"
    },
    {
      "key": 2,
      "type": "temperature",
      "name": "ambient temperature",
      "unit": "kelvin"
    },
    {
      "key": 3,
      "type": "angle",
      "name": "angle of attack",
      "unit": "radian"
    },
    {
      "key": 4,
      "type": "force",
      "name": "fuel 1",
      "unit": "newton"
    }
  ]
}
```
## Format of the data file ( limitation of the DataParser ):
1. it should have delimiter
2. it should start with a row defining the names for each columns, I call it name row
3. each row contains exactly same number of columns
4. every cell except the name row should contains exactly one number

# Extend the model:
## Define Supervised learning mode:
```python
# Implement SupervisedLearningModel (function _error, _train, _eval)
class YourModel(SupervisedLearningModel):
	def _error(self,x,y,param): # how to find error  
        ... # return error
	def _train(self,x,y): # how to train and get parameters
        ... # return learned parameters
	def _eval(self, x, param): # how to get result from x and parameters
        ... # return result
  *other functions*
# Implement Datatransformer (function _YTrans, _XTrans)
class YourTransformation(DataTransformer):
  def _YTrans(self, Y):
        ... # return transformed Y
	def _XTrans(self, X):
        ... # return transformed X
  *other functions*
```
