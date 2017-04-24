import numpy as np
from sklearn.naive_bayes import GaussianNB
from sklearn.preprocessing import RobustScaler

# implement fit(X), predict(X,Y), to_json(), load_json(string) function
class CruiseAlgorithm(object):
	# cruise algorithm is used to classify the cruise phase vs noncruise phase, it uses the differential change in data stream as the input matrix
	def __init__(self, testing=False):
		self.core = GaussianNB()
		self.scaler = RobustScaler()
		self.X_prev = None
		self.testing = testing
	def fit(self,X,Y): # Y should be the label of cruise or not
		X = self.prepare(X)
		self.core.fit(X,Y.ravel())
	def predict(self, X):
		if self.testing:
			X_t = self.prepare(X)
		else:
			if self.X_prev:
				X_t = X - self.X_prev
			else:
				X_t = X
			self.X_prev = X

		print repr(X_t)
		prediction_result = self.core.predict(X_t)
		return np.asmatrix(prediction_result)

	def prepare(self,X):
		a = np.zeros((X.shape[0],X.shape[1]))
		for i in xrange(X.shape[0]-1):
			a[i+1,:] = X[i+1] - X[i]
		return a
