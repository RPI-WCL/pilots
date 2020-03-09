import numpy as np

class Weight_Aggregator:
    def __init__(self, settings):
        self.midpoint = 0.0
        if "midpoint" in settings:
            self.midpoint = settings["midpoint"]
        print("trained")

    def is_live(self):
        return False

    def reset(self):
        x = 0

    def density_altitude( self, prs, tmp, alt_i ):
        prs_alt = (29.92 - prs) * 1000 + alt_i
        isa = 15 - (1.98 * (alt_i / 1000))
        dens_alt = prs_alt + (118.8 * (tmp - isa))
        return dens_alt
    
    def run(self, data):
        print("RUNNING")
        prs = np.average(data[0])
        tmp = np.average(data[1])
        alt = np.average(data[2])
        da = self.density_altitude(prs, tmp, alt)
        if da < self.midpoint:
            return [data[3]]
        else:
            return [data[4]]

# ======================

def make_model( settings ):
    print("Creating")
    model = Weight_Aggregator(settings)
    return model

def train( model, dataset ):
    print("traingin")
    return 0, model

def run( model, data ):
    print("Running?")
    return model.run(data)

def is_live():
    return False
