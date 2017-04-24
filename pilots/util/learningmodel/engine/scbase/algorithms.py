from sklearn.linear_model import LinearRegression
from sklearn.svm import SVR
from algo.bayes import Bayes
from algo.custom import CruiseAlgorithm
# all models are registered in this file as attribute
linear_regression = LinearRegression
linear_regression_twice = LinearRegression
svr = SVR
bayesonline = Bayes
bayes_prediction_test = Bayes
cruise_algorithm = CruiseAlgorithm
