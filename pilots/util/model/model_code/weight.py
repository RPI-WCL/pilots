import numpy as np

class WeightModel:
    def __init__(self, settings):
        # Good data has non-zero velocities
        self.is_good_data = False
        # Each data point ((density altitude, [slope, y-intercept]), weight)
        self.current_data = []
        self.remembered_data = []
        # delta
        self.delta_da = 0.0
        self.delta_slope = 0.0

    def is_live(self):
        return True

    def reset(self):
        self.current_data = []

    def calc_deltas(self):
        # Find pair of close weights
        n = len(self.remembered_data)
        d_da = []
        d_sl = []
        for i in range(n):
            for j in range(n-i):
                w1 = self.remembered_data[i]['w']
                w2 = self.remembered_data[j]['w']
                da1 = self.remembered_data[i]['da']
                da2 = self.remembered_data[j]['da']
                slope1 = self.remembered_data[i]['line'][0]
                slope2 = self.remembered_data[j]['line'][0]
                # calculate delta da
                if abs(w1 - w2) < 10.0:
                    if abs(da1 - da2) >= 0.1:
                        delta = (slope1 - slope2) / (da1 - da2)
                        d_da.append(delta)
                # calculate delta slope
                if abs(da1 - da2) < 10:
                    if abs(slope1 - slope2) >= 0.1:
                        delta = (w1 - w2) / (slope1 - slope2)
                        d_sl.append(delta)
        if len(d_da) == 0 or len(d_sl) == 0:
            #print(self.remembered_data)
            print("BAD DATA: too sparse")
            self.delta_da = 0.0
            self.delta_slope = 0.0
        else:
            self.delta_da = np.average(d_da)
            self.delta_slope = np.average(d_sl)
            print("Calculated deltas:", self.delta_da, self.delta_slope)

    def interpolate(self, unknown, known):
        e_da = (known['da'] - unknown['da'])
        e_slope = (known['line'][0] - unknown['line'][0])
        value = known['w'] - (self.delta_slope * (e_slope - self.delta_da * e_da))
        print(":>", e_da, e_slope, value)
        return value

    def run(self, data):
        # Input data: [airspeed, pressure, temp, altitude]
        if len(self.current_data) > 0 and self.current_data[-1] == data:
            # Repeated data point
            data = np.array( self.current_data ).transpose()[0]
            return [[self.run_all( data )]]
        self.current_data.append( data )
        if len(self.current_data) > 3:
            data = np.array( self.current_data ).transpose()[0]
            return [[self.run_all( data )]]
        else:
            return [[0.0]]
        
    def run_all(self, data):
        # Input data: [[airspeed, pressure, temp, altitude], ...]
        avg_prs = np.average(data[1])
        avg_tmp = np.average(data[2])
        avg_alt = np.average(data[3])
        dens_alt = self.density_altitude( avg_prs, avg_tmp, avg_alt )
        # Calculate slope
        times = range(len(data[0]))
        velocities = np.array(data[0])
        #print("POLYFIT: ", times, velocities)
        z = np.polyfit(times, velocities, 1)
        # Find closest remembered line
        min_error = None
        closest_line = None
        for r_line in self.remembered_data:
            # Calculate error between lines' slopes
            error_da = (r_line['da'] - dens_alt) ** 2
            error_slope = (r_line['line'][0] - z[0]) ** 2
            error = error_da + error_slope
            if min_error == None or error < min_error:
                min_error = error
                closest_line = r_line
        if closest_line == None:
            return 0.0
        else:
            # Interpolate from closest
            print(dens_alt, z, "::", closest_line)
            print()
            current = {'da': dens_alt, 'line': z}
            result = self.interpolate( current, closest_line)
            print( result )
            return result
            #return closest_line['w']
        
    def train(self, X, y):
        for i in range(len(X)):
            # Loop over all trials
            trial = np.array(X[i]).transpose()
            avg_prs = np.average(trial[1])
            avg_tmp = np.average(trial[2])
            avg_alt = np.average(trial[3])
            dens_alt = self.density_altitude( avg_prs, avg_tmp, avg_alt )
            # Calculate slope
            times = range(len(trial[0]))
            velocities = np.array(trial[0])
            z = np.polyfit(times, velocities, 1)
            # Calculate average weight
            weight = np.average( y[i] )
            # Store data_point
            datapoint = {'da': dens_alt, 'line': z, 'w': weight}
            self.remembered_data.append( datapoint )
        self.calc_deltas()
        return self

    def score(self, X, y):
        all_acc = []
        for i in range(len(X)):
            # Loop over all trials
            trial = np.array(X[i]).transpose()
            tmp_result = self.run_all( trial )
            result = np.average( y[i] )
            print("--->", result)
            all_acc.append( (tmp_result - result) ** 2 )
        return np.average( all_acc )

    def density_altitude( self, prs, tmp, alt_i ):
        prs_alt = (29.92 - prs) * 1000 + alt_i
        isa = 15 - (1.98 * (alt_i / 1000))
        dens_alt = prs_alt + (118.8 * (tmp - isa))
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

    n = len(feat)
    for i in range(n):
        if feat[i][-1] < 0.001 or i == n-1:
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

    model = model.train( X, y )
    acc = model.score( X, y )
    return acc, model

def run( model, data ):
    result = model.run( data )
    return [[result]]

def is_live():
    return False
