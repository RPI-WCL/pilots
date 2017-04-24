"""
Reads training definitions, run pipeline and generate the .estimator file
load_ prefix means change the inner states of the current object
stat_ prefix: static
step_ prefix: one step in transformation pipeline
"""

import json
import sys
import os

import scbase.baseenv as baseenv
import scbase.algorithms as algorithms
import scbase.parser as parser
import utils.sclogger as sclogger
from scbase.model import Model
from utils.sclogger import SCLOG
from interface.keys import TrainKey as K

# TODO: use absolute path with environment variables
class DataDef(object):
    """
    Definition of training data as inputs to model trainer
    """
    _stat_st_path = "file"
    _stat_st_type = "type"
    _stat_st_typehead = "header_type"
    _stat_st_path_sch = "schema"
    _stat_st_const = "constants"

    def __init__(self, dict_def=None):
        self.list_file = None
        self.str_type = None
        self.str_typehead = None
        self.str_path_sch = None
        self.dict_const = None
        if dict_def is not None:
            self.load_dict(dict_def)

    def load_dict(self, dict_def):
        """
        load dictionary into local fields.
        """
        self.list_file = dict_def.get(DataDef._stat_st_path)
        self.dict_const = dict_def.get(DataDef._stat_st_const)
        self.str_type = dict_def.get(DataDef._stat_st_type)
        self.str_typehead = dict_def.get(DataDef._stat_st_typehead)
        self.str_path_sch = dict_def.get(DataDef._stat_st_path_sch)

# TODO: Merge Preprocess with model into pipeline
# use string representation for invoking correct preprocessing method
class PreprocessDef(object):
    """
    Definition of Preprocessing field as first step in training pipeline
    """
    _stat_st_unit = "unit_transformation"
    def __init__(self, dict_def=None):
        self.step_preproc = None
        if dict_def is not None:
            self.load_dict(dict_def)

    def load_dict(self, dict_def):
        """
        load dictionary into local fields.
        """
        self.step_preproc = dict_def.get(PreprocessDef._stat_st_unit)

class ModelDef(object):
    """
    Definition of Model field as major learning model in training pipeline
    """
    _stat_st_features = "features"
    _stat_st_labels = "labels"
    _stat_st_algo = "algorithm"
    def __init__(self, dict_def=None):
        self.step_model = None
        self.list_features = None
        self.list_labels = None
        if dict_def is not None:
            self.load_dict(dict_def)

    def load_dict(self, dict_def):
        """
        load dictionary into local fields.
        """
        self.step_model = dict_def.get(ModelDef._stat_st_algo)
        self.list_features = dict_def.get(ModelDef._stat_st_features)
        self.list_labels = dict_def.get(ModelDef._stat_st_labels)

    def st_pathalgo(self):
        """
        get path to the output estimator
        """
        return self.step_model.get('save_file')

class TrainDef(object):
    """
    Data structure containing training definition
    """
    _stat_st_data = "data"
    _stat_st_preproc = "preprocessing"
    _stat_st_model = "model"
    def __init__(self, dict_def=None):
        self.datadef = None
        self.preprocessdef = None
        self.modeldef = None
        if dict_def is not None:
            self.load_dict(dict_def)

    def load_dict(self, dict_def):
        """
        load diction into local fields
        """
        self.datadef = DataDef(dict_def.get(TrainDef._stat_st_data))
        self.preprocessdef = PreprocessDef(dict_def.get(TrainDef._stat_st_preproc))
        self.modeldef = ModelDef(dict_def.get(TrainDef._stat_st_model))

class LearningEngine(object):
    """
    Initialized with an training definition and train the model based on the training definition
    """
    def __init__(self, st_pathtraindef):
        self.st_pathtraindef = st_pathtraindef
        self.env = baseenv.BaseEnv() # may be changed later
        try:
            with open(self.st_pathtraindef, 'rU') as file_:
                dict_def = json.load(file_)
                self.traindef = TrainDef(dict_def)
        except EnvironmentError as e:
            SCLOG.error("engineIO: Unable to load file" + self.st_pathtraindef)
            raise e

    @staticmethod
    def makedict(list1, list2):
        """
        make dictionary with items in list1 as keys and those in list2 as values
        """
        result = {}
        if len(list1) != len(list2):
            return {}
        for i in xrange(len(list1)):
            result[list1[i]] = list2[i]
        return result

    def train(self):
        """ use training definition to train the model"""
        self.read_data(self.traindef.datadef) # load data
        self.read_preprocess(self.traindef.preprocessdef) # preprocess the data
        self.read_model(self.traindef.modeldef) # train the model
        print ("engineIO: Finished Training")

    def read_data(self, datadef):
        """ read data defined by json_dict to env, and return the env"""
        list_pathfile = datadef.list_file
        try:
            str_abspath_sch = datadef.str_path_sch
            with open(str_abspath_sch, 'rU') as file_:
                schema = json.load(file_)
                for st_pathfile in list_pathfile:
                    st_pathfile = st_pathfile
                    parser_ = getattr(parser, datadef.str_type)
                    parser_header = getattr(parser, datadef.str_typehead)
                    data = self.env.load_file(st_pathfile, parser_, parser_header, header=schema[K.S_N])
                    self.env.append_data(data)
                if datadef.dict_const is not None:
                    self.env.set_constant(**datadef.dict_const)
                if K.S_U in schema:
                    unit_dict = LearningEngine.makedict(schema[K.S_N], map(lambda(name): {'unit': name}, schema[K.S_U]))
                    self.env.update_variable_info(**unit_dict)
        except EnvironmentError as e:
            SCLOG.error("LearningEngine: Unable to load schema file" + datadef.str_path_sch)
            raise e

    def read_preprocess(self, preprocessdef):
        """ run preprocess step based on training definition"""
        if preprocessdef.step_preproc is not None:
            schema = self.env.get_schema_info()
            units = preprocessdef.step_preproc
            matrix_ = self.env.transform(baseenv.BaseEnv.unit_transformer, schema=schema, units=units)
            self.env.set_data(matrix_)

    def read_model(self, modeldef):
        """ run model training step based on training definition"""
        if modeldef.list_features is not None:
            schema_info = self.env.get_schema_info()
            self.env.feature = self.env.transform(baseenv.BaseEnv.data_transformer,
                                                  schema=schema_info, constant=self.env.get_constant(),
                                                  definition=modeldef.list_features)
        if modeldef.list_labels is not None:
            self.env.label = self.env.transform(baseenv.BaseEnv.data_transformer,
                                                schema=self.env.get_schema_info(),
                                                constant=self.env.get_constant(),
                                                definition=modeldef.list_labels)
        if modeldef.step_model is not None: # this should be enforced
            # TODO: refactor the model to be generic step in pipe
            dict_stepmodel = modeldef.step_model
            algorithm = getattr(algorithms, dict_stepmodel['id'])(**dict_stepmodel['param'])
            self.env.train_model(algorithm)
            if 'save_file' in dict_stepmodel:
                serialize_function = ""
                deserialize_function = ""
                if 'serialize_function' in dict_stepmodel:
                    serialize_function = dict_stepmodel['serialize_function']
                if 'deserialize_function' in dict_stepmodel:
                    deserialize_function = dict_stepmodel['deserialize_function']
                model = Model(algorithm, modeldef.list_features, modeldef.list_labels,
                              self.env.get_constant(), serialize_function, deserialize_function, dict_stepmodel[K.A_ID])
                if 'save_file' in dict_stepmodel:
                    model.save(dict_stepmodel['save_file'])

def main():
    """
    Read first argument as the training definition file
    train the model, save the output estimator
    """
    sclogger.init()
    sclogger.SCLOG.info("LearningEngine started\n")
    if len(sys.argv) != 2:
        sclogger.SCLOG.error("training definition path is not provided")
        return
    st_pathtraindef = os.path.join(sys.argv[1])
    learningengine = LearningEngine(st_pathtraindef)
    learningengine.train()

if __name__ == '__main__':
    main()
