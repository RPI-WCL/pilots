import numpy as np

class Weight_Aggregator:
    def __init__(self, settings):
        self.midpoint = 0.0
        if "midpoint" in settings:
            self.midpoint = settings["midpoint"]

    def is_live(self):
        return False

    def reset(self):
        pass

    def density_altitude( self, prs, tmp, alt_i ):
        prs_alt = (29.92 - prs) * 1000 + alt_i
        isa = 15 - (1.98 * (alt_i / 1000))
        dens_alt = prs_alt + (118.8 * (tmp - isa))
        return dens_alt
    
    def run(self, data):
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
    model = Weight_Aggregator(settings)
    return model

def train( model, dataset ):
    return 1.0, model
