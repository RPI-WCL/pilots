import numpy as np

class EnsembleMethods:
    def __init__(self, settings):
        self.method = "average"
        if "average" in settings:
            self.method = "average"
        if "majority" in settings:
            self.method = "majority"
        if "weighted" in settings:
            self.method = "weighted"
        if "decision" in settings:
            self.method = "decision"
        return self

    def is_live(self):
        return False

    def reset(self):
        pass

    def train(self):
        return 1.0, self

    def run(self):
        if self.method == "average":
            0.0
        elif self.method == "majority":
            0.0
        elif self.method == "weighted":
            0.0
        elif self.method == "decision":
            0.0
        
        return 0.0

    def test(self):
        return 1.0

# ------------------------------------

def make_model(settings):
    model = EnsembleMethods(settings)
    return model

def train(model, dataset):
    return model.train()

        
        
