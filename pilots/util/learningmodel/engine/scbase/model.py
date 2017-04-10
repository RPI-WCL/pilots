import pickle
import numpy as np
class Model(object):
	""" Model class provides a wrapper for learning models, including the equation definition of features and labels, saving and loading functions"""
	""" It's a general class for various machine learning algorithms and hypothesis"""
	""" This class could be refactored using simplier relation"""
	def __init__(self, estimator=None, features=None, labels=None, constants=None, serialize_function=None, deserialize_function=None, init_function=None):
		self.estimator = estimator
		self.features = features
		self.labels = labels
		self.constants = constants
		self.serialize_function = serialize_function
		self.deserialize_function = deserialize_function
		self.init_function = init_function

	def predict(self, X):
		return self.estimator.predict(X)

	def save(self, filename):
		estimator = self.estimator
		if self.serialize_function:
			estimator = getattr(self.estimator, self.serialize_function)()
		pickle.dump((estimator, self.features, self.labels, self.constants, self.serialize_function, self.deserialize_function, self.init_function), open(filename, 'w+b'))
		print ("generated " + filename)
	
	def load(self, filename, algorithms):
		pending_estimator, features, labels, constants, serialize_function, deserialize_function, estimator_init_id = pickle.load(open(filename, 'rb'))
		estimator = pending_estimator
		if deserialize_function: # should be initialized by estimator_init 
			algorithm = getattr(algorithms, estimator_init_id)()
			getattr(algorithm, deserialize_function)(pending_estimator) # initialize the estimator
			estimator = algorithm
		self.__init__(estimator, features, labels, constants, serialize_function, deserialize_function, estimator_init_id)

	@staticmethod
	def load_file(filename, algorithms):
		m = Model()
		m.load(filename, algorithms)
		return m

class OnlineModel(object):
	""" OnlineModel is a class specifically designed for ServerInterface, it may be changed later on"""
	def __init__(self, model, translation={}):
		self.model = model
		self.translation = translation

	def predictSingleValue(self, feature_dict):
		# translate the feature dict
		self.translate(feature_dict)
		feature_dict.update(self.model.constants) # inject environment to the dictionary
		mx = map(lambda(feature): eval(feature.format(**feature_dict)), self.model.features)
		return self.model.predict(np.asmatrix(mx))

	def translate(self, feature_dict):
		for i in feature_dict:
			if i in self.translation:
				feature_dict[self.translation[i]] = feature_dict[i]
				del feature_dict[i]
	def save(self,filename):
		self.model.save(filename)