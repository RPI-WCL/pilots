import numpy as np
from pint import UnitRegistry
from sklearn.linear_model import LinearRegression
import json
import matplotlib.pyplot as plt
import re
import pickle

TRAINER = {}
PREDICTOR = {}
# basic functions
def readcsv(file_name):
	raw = open(file_name,'rU').readlines()
	head = map(lambda(word): word.strip(), raw[0].split(','))
	body = raw[1:]
	raw_data = np.asmatrix(map(lambda(row): map( float, row.split(',')), body))
	return (head, raw_data)

def transform_unit(raw_data, units, tounits,copy=False):
	if copy:
		data = raw_data.copy()
	else:
		data = raw_data # shallow copy
	ureg = UnitRegistry()
	Q_ = ureg.Quantity
	for i in xrange(data.shape[1]):
		data[:,i] = Q_(data[:,i], units[i]).to(tounits[i]).magnitude
	return data

def generate_transformer(features, name, constant, constant_value):
	def position_lookup(requested_name, name, constant, matrix_name, constant_value):
		if requested_name in name:
			return {requested_name: "%s[%d]"%(matrix_name, name.index(requested_name))}
		elif requested_name in constant:
			return {requested_name: str(constant_value[constant.index(requested_name)])}
		else:
			raise Exception("position lookup failed" + requested_name)
	regex = re.compile('\{[0-9A-Za-z]+\}')
	indexes = {}
	find_var_function = lambda(string): map(lambda(requested_name): position_lookup(requested_name, name, constant, 'x', constant_value), map(lambda(word):word[1:-1], regex.findall(i)))
	string_vars = map(lambda(one): (reduce(lambda a,b: dict(a,**b), one)), [ find_var_function(i) for i in features])
	string = []
	for i in xrange(len(features)):
		string.append(features[i].format(**string_vars[i]))
	transformer = eval('lambda(x): np.asarray(['+reduce(lambda a,b: a+','+b, string)+'])')
	return transformer

def test_basic():
	# read a file and assign tags (dictionary) to each column
	head, raw_data = readcsv("thetrain.csv")
	# set header information
	units = ['knot', 'in_Hg', 'celsius', 'degree', 'force_pound']
	tounits = ['m/s', 'pascal', 'kelvin', 'radian', 'newton']
	name = ['v','p','t','a','w']
	# set constant value
	constant = ['s']
	constant_value = [61.0]
	# transform unit
	transformed_data = transform_unit(raw_data, units, tounits, True)
	# stringified transformation method
	label_transformer = generate_transformer(["2*{w}/({v}**2*({p}/286.9/{t})*{s})"], name, constant, constant_value)
	feature_transformer = generate_transformer(["{a}"], name, constant, constant_value)
	# for scikit:
		# transform into Y and X
	Y = np.apply_along_axis(label_transformer, 1, transformed_data)
	X = np.apply_along_axis(feature_transformer, 1, transformed_data)
	reg = LinearRegression()
	reg.fit(X,Y)

	# prediction: load thetest.csv
	head, raw_data_test = readcsv("thetest.csv")
	# transform unit
	transformed_data_test = transform_unit(raw_data, units, tounits, True)
	X_test = np.apply_along_axis(feature_transformer, 1, transformed_data_test)
	Y_test = np.apply_along_axis(label_transformer, 1, transformed_data_test)
	plt.plot(X_test, reg.predict(X))
	plt.scatter(X_test, Y_test)
	plt.show()
	# prediction using dictionary
	prediction_name = ['a']
	prediction_data = [[1]]
	prediction_feature_transformer = generate_transformer(['{a}'], prediction_name, constant, constant_value)
	X_test = np.apply_along_axis(prediction_feature_transformer, 1, X_test)
def generate_estimator():
	head, raw_data = readcsv("thetrain.csv")
	# set header information
	units = ['knot', 'in_Hg', 'celsius', 'degree', 'force_pound']
	tounits = ['m/s', 'pascal', 'kelvin', 'radian', 'newton']
	name = ['v','p','t','a','w']
	# set constant value
	constant = ['s']
	constant_value = [61.0]
	# transform unit
	transformed_data = transform_unit(raw_data, units, tounits, True)
	# stringified transformation method
	label_transformer = generate_transformer(["2*{w}/({v}**2*({p}/286.9/{t})*{s})"], name, constant, constant_value)
	feature_transformer = generate_transformer(["{a}"], name, constant, constant_value)
	Y = np.apply_along_axis(label_transformer, 1, transformed_data)
	X = np.apply_along_axis(feature_transformer, 1, transformed_data)
	reg = LinearRegression()
	reg.fit(X,Y)
	# save the estimator
	obj = {"transformer": {"features": ["{aoa}"], "name": name, "constant": constant, "constant_value": constant_value}, "model": reg}
	pickle.dump(obj, open("linear_regression.model", "w+b"))

def load_estimator(file_name):
	obj = pickle.load(open(file_name, "rb"))
	reg = obj['model']
	return (obj['transformer'], reg)

if __name__ == '__main__':
	generate_estimator()
	print load_estimator("linear_regression.model")