import LearnModel as lm
import numpy as np
import math
class LeastSquare(lm.SupervisedLearningModel):
	def _error(self,x,y,param):
		r = y - np.dot(x,param)
		se = np.dot(r.transpose(), r)
		rmse = math.sqrt(se/x.shape[0])
		return {'RMSE': rmse, 'SE': se}
	def _train(self,x,y):
		q, r = np.linalg.qr(x)
		b = np.dot(q.transpose(),y)
		weight = np.linalg.lstsq(r[:3,:3],b[:3])
		return weight[0]
	def _eval(self, x, param):
		return np.dot(x, param)

# special case of the linear transformation ( for this test... )
class SpecialLinearTransformation(lm.DataTransformer):
	def setWeight(self, weight):
		self.w = weight;
	def _YTrans(self, Y):
		r = np.array([Y[:,:].sum(axis=1) + self.w]).transpose()
		return r
	def _XTrans(self, X):
		# place holder
		x1 = np.divide(np.multiply(np.power(X[:,0],2), X[:,1]),X[:,2]);
		x2 = np.multiply(x1,X[:,3])
		x3 = np.multiply(x2, X[:,3])
		A = np.ones((X.shape[0],3))
		A[:,1] = x1
		A[:,2] = x2
		return A
