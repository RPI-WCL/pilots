# Learning Model, database and unit converter

# Usage:
## Cautious:
1. Beta Version, Some corner cases are not covered and will terminate the process directly.
2. Suppose only 2D visualization, and supervised learning in undefined constraints.
## Load data to database:
```python
database = DataBase('path-to-json-config-file', [ignore case])
parser = db.DataParser('path-to-data-file', deliminator)
database.addData(parser) # use the parser to parse the data
uc = UnitConverter.loadFile('path-to-json-file')
data.loadConfiguration('path-to-json-config-file', uc)
```
## visualize data
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
# Implement Datatransformer (function _YTrans, _XTrans)
class YourTransformation(DataTransformer):
    def _YTrans(self, Y):
        ... # return transformed Y
	def _XTrans(self, X):
        ... # return transformed X
```
## Train learning mode:
```python
    data <- loaded database
	TrainField = ['col4', 'col3', ... ] # columns in data for X transformation
	TargetField = [ 'col1', 'col2', ... ] # columns in data for Y transformation
	trans = YourTransformation(TrainField, TargetField)
	model = Yourmodel(trans)
	start = 0; end = 100
	model.readData(data, start, end) # read database from start row to end row, columns are defined by transformation
	model.train() # run training algorithm
	model.inSampleError() # return in sample error, you can send parameter to visualize the error
	model.outSampleError(data, start, end) # return out sample error, you can send parameter True at last to visualize the error
```
