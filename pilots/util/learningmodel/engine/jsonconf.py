import json
import baseenv
import learning_models
import scparser
import sys
import time
def makedict(list1, list2):
	result = {}
	if len(list1) != len(list2):
		return {}
	for i in xrange(len(list1)):
		result[list1[i]] = list2[i]
	return result

def load(json_file):
	init_start = time.time()
	env = baseenv.BaseEnv()
	f = open(json_file, "rU")
	json_dict = json.load(f);
	sys.stdout.write("configuration file loaded %s\n"%json_file)
	model = learning_models.get(json_dict["trainer"]['model'])
	sys.stdout.write("learning model loaded %s\n"%str(model))

	data_conf = json_dict['data']
	trans_conf = data_conf["transformer"]
	# load matrix data
	sys.stdout.write("training file loading...\n")
	start_time = time.time();
	data = env.load_file(data_conf['file'], scparser.get(data_conf["type"]), scparser.get(data_conf['header_type']), header=data_conf['column_names'])
	env.append_data(data)
	sys.stdout.write("training file finished loading ( %.2f )\n"%(time.time() - start_time))

	if "constants" in data_conf:
	# set constant in the environment
		env.set_constant(**data_conf["constants"])
	# load unit information
	if "column_units" in data_conf:
		unit_dict = makedict(data_conf["column_names"], map(lambda(name): {"unit": name}, data_conf["column_units"]))
		env.update_variable_info(**unit_dict)
	# unit transformation ( if applicable )
	if "unit_transformation" in trans_conf:
		sys.stdout.write("units transformation started...\n")
		start_time = time.time()
		new_unit_dict = makedict(data_conf['column_names'], trans_conf["unit_transformation"])
		cached_data = env.transform(env.unit_transformer, schema=env.get_schema_info(), units=new_unit_dict);
		env.data = cached_data.copy()
		sys.stdout.write("units transformation ended ( %.2f )\n"%(time.time() - start_time))
	# feature transformation ( if applicable )
	sys.stdout.write("feature, label transformation started...\n")
	start_time = time.time()
	features = trans_conf["features"]
	labels = trans_conf["labels"]
	cached_data = env.transform(env.feature_label_transformer, schema=env.get_schema_info(), constant=env.get_constant(), features=features, labels=labels)
	env.label = cached_data["label"]
	env.feature = cached_data["feature"]
	sys.stdout.write("feature, label transformation ended ( %.2f )\n"%(time.time() - start_time))
	# train the model
	start_time = time.time()
	sys.stdout.write("estimator training started...")
	estimator = env.train_estimator(model)
	sys.stdout.write("Estimator training finished ( %.2f )\n"%(time.time() - start_time));
	# if applicable, save the result
	if json_dict["trainer"]["saveto"]:
		model_output = json_dict["trainer"]["saveto"]
		baseenv.save_estimator(model_output, estimator, features, env.constant_dict, json_dict['trainer']['savetojson'])
		sys.stdout.write("Estimator saved to file %s ( json = %s )\n"%(model_output,str(json_dict["trainer"]["savetojson"])))
	sys.stdout.write("total time = %.2f\n"%(time.time() - init_start))
if __name__ == '__main__':
	if len(sys.argv) != 2:
		print "usage: <training file_name>"
	else:
		load(sys.argv[1])
