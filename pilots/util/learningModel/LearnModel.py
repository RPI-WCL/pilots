import matplotlib.pyplot as plt
import numpy as np
import UnitConverter as uc
import DataBase as db
import math
import sys
import abc


class DataTransformer(object):
	__metaclass__ = abc.ABCMeta
	def __init__(self, XFields, YFields):
		self.XFields = XFields
		self.YFields = YFields
	def trans(self, database, start, end):
		length = end - start
		X = np.zeros((length, len(self.XFields)))
		index = 0
		for f in self.XFields: # dirty
			r = np.array([database.select(f, start, end)])
			X[:,index] = r
			index += 1
		Y = np.zeros((length, len(self.YFields)))
		index = 0
		for f in self.YFields: # dirty
			Y[:,index] = np.array([database.select(f, start, end)])
			index += 1
		# by definition:
		return (self._XTrans(X), self._YTrans(Y))
	@abc.abstractmethod
	def _YTrans(self, Y):
		pass
	@abc.abstractmethod
	def _XTrans(self, X):
		pass

class SupervisedLearningModel(object):
	__metaclass__ = abc.ABCMeta
	def __init__(self, dataTransformer):
		self.param = None
		self.X = None
		self.Y = None
		self.transformer = dataTransformer
	def resetData(self):
		self.X = None
		self.Y = None
	def resetParam(self):
		self.param = None
	def readData(self, dataset, start, end):
		x,y = self.transformer.trans(dataset, start, end)
		if self.X is None or self.Y is None:
			self.X, self.Y = x,y
		else:
			self.X = np.vstack((self.X, x))
			self.Y = np.vstack((self.Y, y))
	def inSampleError(self, visual=False):
		if visual:
			self._visualize(self.X, self.Y, self.param)
		return self._error(self.X, self.Y, self.param)
	def outSampleError(self, dataset, start, end, visual=False):
		x,y = self.transformer.trans(dataset, start, end)
		if visual:
			self._visualize(x, y, self.param)
		return self._error(x,y,self.param)
	def train(self):
		self.param = self._train(self.X, self.Y)
		return self.param
	@abc.abstractmethod
	def _error(self, x, y, param):
		pass
	@abc.abstractmethod
	def _train(self, x, y):
		pass
	@abc.abstractmethod
	def _eval(self, x, param):
		pass
	def _visualize(self,X,Y,W):
		eY = self._eval(X, W)
		f, graphs = plt.subplots(2)
		graphs[0].set_title("blue is measured, red is estimated")
		graphs[0].plot(np.linspace(0,Y.shape[0], Y.shape[0]), Y, color="blue")
		graphs[0].plot(np.linspace(0,Y.shape[0], Y.shape[0]), eY, color="red")
		graphs[1].set_title("measured - estimated")
		graphs[1].plot(np.linspace(0,Y.shape[0], Y.shape[0]), Y - eY, color="red")
		f.subplots_adjust(hspace=0.5)
		plt.show()


# we only need part of the data to train the model
# currently a place holder for more general interface
# only for test and fun ...
def getCruisePhase(database, timeField, speedField, change, minspeed, window):
	# we define cruise phase as acceleration = 0 +- 0.01 ( about 500 N) and speed >= 10m/s
	speed = database.select(timeField)
	time = database.select(speedField)
	# calculate acceleration
	a = [0]*(len(speed) - 1)
	result = []
	start = -1
	end = -1
	for i in xrange(len(a)):
		period = time[i+1]-time[i]
		if (time[i+1] - time[i]) == 0:
			a[i] = sys.maxint
		else:
			a[i] = (speed[i+1] - speed[i])/(time[i+1] - time[i])
		if (abs(a[i]) < change and speed[i] > minspeed):
			if start != -1:
				end += 1
			else:
				start = i
				end = i+1
		else:
			if start != -1:
				result.append((start, end, period))
				start = -1
				end = -1
	if start != -1:
		result.append((start, end))
	r = filter(lambda(t): t[2] > window, result)
	return r
