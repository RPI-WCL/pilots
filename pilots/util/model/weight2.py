import numpy as np

class WeightModel:
    def __init__(self, settings):
        # Good data has non-zero velocities
        self.is_good_data = False
        # Each data point ((density altitude, [slope, y-intercept]), weight)
        self.current_data = []
        self.remembered_data = []
        
        
    def run(self, data):
        # Input data: [time, airspeed, pressure, temp, altitude]
        return 0
        
    def run_all(self, data):
        # Input data: [[time, airspeed, pressure, temp, altitude], ...]
        avg_prs = np.average(data[2])
        avg_tmp = np.average(data[3])
        avg_alt = np.average(data[4])
        dens_alt = density_altitude( avg_prs, avg_tmp, avg_alt )
        # Calculate slope
        times = np.array(data[0]) - data[0][0]
        velocities = np.array(data[1])
        z = np.polyfit(times, velocities, 1)
        # Find closest remembered line
        min_error = None
        closest_line = None
        for r_line in self.remembered_data:
            print( "RL:", r_line )
            # Calculate error between lines' slopes
            error = (r_line['line'][0] - z[0]) ** 2
            if min_error == None or error < min_error:
                min_error = error
                closest_line = r_line
        if closest_line == None:
            return 0
        else:
            return r_line['w']

        
    def train(self, X, y):
        for i in range(len(X)):
            # Loop over all trials
            trial = np.array(X[i]).transpose()
            avg_prs = np.average(trial[2])
            avg_tmp = np.average(trial[3])
            avg_alt = np.average(trial[4])
            dens_alt = density_altitude( avg_prs, avg_tmp, avg_alt )
            # Calculate slope
            times = np.array(trial[0]) - trial[0][0]
            velocities = np.array(trial[1])
            z = np.polyfit(times, velocities, 1)
            # Calculate average weight
            weight = np.average( y[i] )
            # Store data_point
            datapoint = {'da': dens_alt, 'line': z, 'w': weight}
            self.remembered_data.append( datapoint )
        return self

    def score(self, X, y):
        all_acc = []
        for i in range(len(X)):
            # Loop over all trials
            tmp_result = self.run_all( X[i] )
            result = np.average( y[i] )
            all_acc.append( (tmp_result - result) ** 2 )
        return np.average( all_acc ) / 100

def density_altitude( prs, tmp, alt_i ):
    prs_alt = (29.92 - prs) * 1000 + alt_i
    isa = 15 - ((2*alt_i) // 1000)
    dens_alt = prs_alt + (120 * (tmp - isa))
    return dens_alt

# ============================================================

def make_model( settings ):
    model = WeightModel( settings )
    return model

def train( model, dataset ):
    feat = np.array(dataset["features"]).transpose()
    labels = np.array(dataset["labels"]).transpose()

    X = []
    y = []

    X_tmp = []
    y_tmp = []

    for i in range(len(feat)):
        if feat[i-1][-1] < 0.001:
            # Ran into division between trials
            if len(X_tmp) == 0:
                continue
            X.append( X_tmp )
            y.append( y_tmp )
            X_tmp = []
            y_tmp = []
            continue
        X_tmp.append( feat[i].tolist() )
        y_tmp.append( labels[i].tolist() )

    print("------")
    #print( "X:", X )
    print("------")
    #print( "y:", y )
        
    model = model.train( X, y )
    acc = 0 #model.score( X, y )
    return acc, model

def run( model, data ):
    result = model.run( data )
    return result.tolist()

def is_live():
    return False
