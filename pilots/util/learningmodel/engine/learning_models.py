from sklearn.linear_model import LinearRegression
from bayes import Bayes
# all models are registered in this file
MODEL = {}
MODEL['linearregression'] = LinearRegression();
MODEL['bayesonline'] = Bayes();
def get(name):
	return MODEL[name]