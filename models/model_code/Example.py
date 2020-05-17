import numpy as np

class ExampleModel:
    def __init__(self, settings):
        # adjust settings
        # create model

    def is_live(self):
        return False

    def reset(self):
        # reset to prepare for new data
        pass

    def train(self):
        # called by module train
        return self

    def run(self):
        return 0.0

    def test(self):
        # test on testing data
        return self.score()

    def score(self):
        return 1.0

# Module API functions

def make_model(settings):
    model = Example(settings)
    return model

def train(model, dataset):
    # process data
    model = model.train()
    # test on training data
    acc = model.score()
    return acc, model
        
        
