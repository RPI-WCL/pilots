import json
import sys
import time
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error

import scbase.baseenv as baseenv
import scbase.algorithms as algorithms
import scbase.parser as parser
from interface.keys import TrainKey as K
from scbase.model import Model
def train_model(json_file_name):
	return _load(json_file_name)

def test_model(json_file_name):
	env = baseenv.BaseEnv()
	f = open(json_file_name, 'rU')
	json_dict = json.load(f)
	read_data(env, json_dict['data'])
	if 'preprocess' in json_dict:
		read_preprocess(env, json_dict['preprocess'])
	model = load_model(json_dict['test'])
	env.feature = env.transform(baseenv.BaseEnv.data_transformer, schema=env.get_schema_info(), constant=env.get_constant(), definition=model.features)
	if json_dict['label']:
		env.label = env.transform(baseenv.BaseEnv.data_transformer, schema=env.get_schema_info(), constant=env.get_constant(), definition=model.labels)
	pr = model.predict(env.feature)
	return (pr, env.label)

def load_model(estimator_file_name):
	return Model.load_file(estimator_file_name, algorithms)

def read_data(env, json_dict):
	""" read data defined by json_dict to env, and return the env"""
	file_names = json_dict[K.D_FILE]
	schema = json.load(open(json_dict[K.D_S],'rU'))
	for i in file_names:
		data = env.load_file(i, getattr(parser,json_dict[K.D_T]), getattr(parser, json_dict[K.D_HT]), header=schema[K.S_N])
		env.append_data(data)
	if K.D_C in json_dict:
		env.set_constant(**json_dict[K.D_C])
	if K.S_U in schema:
		unit_dict = makedict(schema[K.S_N], map(lambda(name): {'unit': name}, schema[K.S_U]))
		env.update_variable_info(**unit_dict)
def read_preprocess(env, json_dict):
	# this part could be modulized
	if K.P_UT in json_dict: 
		env.set_data(env.transform(baseenv.BaseEnv.unit_transformer, schema=env.get_schema_info(), units=json_dict[K.P_UT]))
	if 'differential' in json_dict:
		env.set_data(env.transform(baseenv.BaseEnv.differential, order=json_dict['differential']))
def read_model(env, json_dict):
	feature_symbol = None
	label_symbol = None
	if K.A_FEATURES in json_dict:
		env.feature = env.transform(baseenv.BaseEnv.data_transformer, schema=env.get_schema_info(), constant=env.get_constant(), definition=json_dict[K.A_FEATURES])
		feature_symbol = json_dict[K.A_FEATURES]

	if K.A_LABELS in json_dict:
		env.label = env.transform(baseenv.BaseEnv.data_transformer, schema=env.get_schema_info(), constant=env.get_constant(), definition=json_dict[K.A_LABELS])
		label_symbol = json_dict[K.A_LABELS]

	if K.A in json_dict:
		json_dict_a = json_dict[K.A]
		algorithm = getattr(algorithms, json_dict_a[K.A_ID])(**json_dict_a[K.A_PARAM])
		env.train_model(algorithm)
		if K.A_FILE in json_dict_a:
			serialize_function = ""
			deserialize_function = ""
			if K.A_SERIALIZE in json_dict_a:
				serialize_function = json_dict_a[K.A_SERIALIZE]
			if K.A_DSERIALIZE in json_dict_a:
				deserialize_function = json_dict_a[K.A_DSERIALIZE]
			model = Model(algorithm, feature_symbol, label_symbol, env.get_constant(), serialize_function, deserialize_function, json_dict_a[K.A_ID])
			model.save(json_dict_a[K.A_FILE])

def _load(json_file_name):
	env = baseenv.BaseEnv()
	f = open(json_file_name, 'rU')
	json_dict = json.load(f)
	if K.D in json_dict:
		read_data(env, json_dict[K.D])
	if K.P in json_dict:
		read_preprocess(env, json_dict[K.P])
	if K.M in json_dict:
		read_model(env, json_dict[K.M])
	return env

def makedict(list1, list2):
	result = {}
	if len(list1) != len(list2):
		return {}
	for i in xrange(len(list1)):
		result[list1[i]] = list2[i]
	return result

if __name__ == '__main__':
	json_file = sys.argv[1]
	env = train_model("definitions/cruise.json")
	# predicted, real = test_model("definitions/bayes_config_test.json")
	# train_model("definitions/bayes_config.json")
	# train_model("definitions/regression_config.json")

	# load_model("regression.estimator")
	# load_model("bayes_online.estimator")
