import scbase.algorithms
import scbase.model as M
import numpy as np
import json
import scbase.model
from interface.keys import InterfaceKey as K

def makedict(data, schema):
	if len(data) != len(schema):
		raise Exception("schema, data length mismatch error:"+str(data)+str(schema))
	r = {}
	for i in xrange(len(schema)):
		r[schema[i]] = data[i]
	return r
class InterfaceIO(object):
	def __init__(self, json_file_name):
		self.models, self.config = self._load(json_file_name)
	def read_models(self, json_dict):
		models = {}
		config = {}
		for model in json_dict:
			models[model[K.M_ID]] = M.OnlineModel(M.Model.load_file(model[K.M_F], scbase.algorithms), model[K.M_DIC])
			if K.M_UPDATE in model:
				config[model[K.M_ID]] = ( model[K.M_UPDATE], model[K.M_F] )
		return (models, config)
	def close(self):
		for i in self.models:
			if self.config[i][0]:
				self.models[i].save(self.config[i][1])
	def _load(self, json_file_name):
		f = open(json_file_name, 'rU')
		json_dict = json.load(f)
		return self.read_models(json_dict['models'])

	def predictSingleValue(self, id_, input_dict):
		return self.models[id_].predictSingleValue(input_dict)


if __name__ == '__main__':
	interface = InterfaceIO("server.json")
	print interface.models['bayes'].model.estimator.print_states()
	
	interface.close()