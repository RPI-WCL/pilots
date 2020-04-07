import numpy as np

class WeightModel:
    def __init__(self, settings):
        # print progress messages
        self.verbose = False
        if "verbose" in settings:
            self.verbose = settings["verbose"]
        
        # Check that data is above cutoff
        self.cutoff = 0.0
        if "cutoff" in settings:
            self.cutoff = settings["cutoff"]

        # Percent difference in values to estimate delta_acc with
        self.da_close = 0.1
        if "da_close" in settings:
            self.da_close = settings["da_close"]

        # Percent difference in values to estimate delta_da with
        self.w_close = 0.1
        if "w_close" in settings:
            self.w_close = settings["w_close"]

        # Each data point: ((density altitude, [slope, y-intercept]), weight)
        self.current_data = []
        self.remembered_data = []
        # delta
        self.delta_da = 0.0
        self.delta_acc = 0.0

    # -----------------------------------------
    # API functions 
        
    def is_live(self):
        return True

    def reset(self):
        self.current_data = []

    def run(self, datapoint):
        # Input data: [airspeed, pressure, temp, altitude]
        if len(self.current_data) > 0 and self.current_data[-1] == datapoint:
            # Repeated data point, do not add
            _data = np.array( self.current_data ).transpose()[0]
            return [[self.run_all( _data )]]
        else:
            # Add data point
            if datapoint[0][0] > self.cutoff:
                self.current_data.append( datapoint )
            if len(self.current_data) > 3:
                _data = np.array( self.current_data ).transpose()[0]
                return [[self.run_all( _data )]]
            else:
                return [[0.0]]


    def train(self, data):
        X,y = self.process_data(data, False)
        for i in range(len(X)):
            # Loop over all trials
            trial = np.array(X[i]).transpose()
            trial = self.cutoff_trial( trial )
            dens_alt, z_prime = self.compute_trial(trial)
            # Calculate average weight
            weight = np.average( y[i] )
            # Store data_point
            datapoint = {'da': dens_alt, 'acc': z_prime, 'w': weight}
            self.remembered_data.append( datapoint )
        self.calc_deltas()
        return self

    def test(self, data):
        X,y = self.process_data(data, True)
        acc = self.score(X,y)
        return acc

    # -----------------------------------------
    # Training functions

    def calc_deltas(self):
        # Find pair of closest weights
        n = len(self.remembered_data)
        d_da = []
        d_acc = []
        for i in range(n):
            for j in range(n-i):
                w1 = self.remembered_data[i]['w']
                w2 = self.remembered_data[j]['w']
                da1 = self.remembered_data[i]['da']
                da2 = self.remembered_data[j]['da']
                acc1 = self.remembered_data[i]['acc']
                acc2 = self.remembered_data[j]['acc']
                # calculate delta da
                if abs(w1 - w2)/max(w1,w2) < (self.w_close / 100.0):
                    if abs(da1 - da2) > 0.0:
                        delta = (acc1 - acc2) / (da1 - da2)
                        d_da.append(delta)
                # calculate delta acc
                if abs(da1 - da2)/max(da1,da2) < (self.da_close / 100.0):
                    if abs(acc1 - acc2) > 0.0:
                        delta = (w1 - w2) / (acc1 - acc2)
                        d_acc.append(delta)
        if len(d_da) == 0 or len(d_acc) == 0:
            print("BAD DATA: too sparse")
            self.delta_da = 0.0
            self.delta_acc = 0.0
        else:
            self.delta_da = np.average(d_da)
            self.delta_acc = np.average(d_acc)
            if self.verbose:
                print("Calculated deltas:", self.delta_da, self.delta_acc)

    def interpolate(self, unknown, known):
        e_da = (known['da'] - unknown['da'])
        e_acc = (known['acc'] - unknown['acc'])
        value = known['w'] + np.sum(self.delta_acc * (self.delta_da * e_da - e_acc))
        if self.verbose:
            print("Interpolation results:>", e_da, e_acc, value)
        return value

    # -----------------------------------------
        
    def run_all(self, data):
        # Input data: [[airspeed, pressure, temp, altitude], ...]
        dens_alt, z_prime = self.compute_trial( data )
        # Find closest remembered line
        min_error = None
        closest_line = None
        for r_line in self.remembered_data:
            error = self.compute_error( [dens_alt, z_prime], r_line )
            if min_error == None or error < min_error:
                min_error = error
                closest_line = r_line
        if closest_line == None:
            return 0.0
        else:
            if self.verbose:
                print(dens_alt, z_prime, "::", closest_line)
            current = {'da': dens_alt, 'acc': z_prime}
            # Interpolate from closest
            result = self.interpolate( current, closest_line)
            if self.verbose:
                print("final result:", result)
            return result

    # -----------------------------------------
    # Helper functions
        
    def score(self, data):
        X,y = self.process_data(data, False)
        all_acc = []
        for i in range(len(X)):
            # Loop over all trials
            trial = np.array(X[i]).transpose()
            trial = self.cutoff_trial( trial )
            # estimate value
            tmp_result = self.run_all( trial )
            # real value
            result = np.average( y[i] )
            #print("--->", result)
            e = self.score_error( tmp_result, result )
            all_acc.append( e )
        return np.average( all_acc )

    def compute_trial(self, trial):
        # compute density altitude
        avg_prs = np.average(trial[1])
        avg_tmp = np.average(trial[2])
        avg_alt = np.average(trial[3])
        dens_alt = self.density_altitude( avg_prs, avg_tmp, avg_alt )
        if self.verbose:
            print("DA ----> ",dens_alt)
        # compute acceleration curve
        times = range(len(trial[0]))
        velocities = np.array(trial[0])
        z = np.polyfit(times, velocities, 1)
        z_prime = np.polyder(z)
        # ---------------------
        return dens_alt, z_prime

    def compute_error(self, x, t):
        x_da = x[0]
        x_acc = x[1]
        error_da = (t['da'] - x_da) ** 2
        error_acc = np.linalg.norm(t['acc'] - x_acc)
        error = error_da + error_acc

    def score_error(self, est_w, curr_w):
        return 1.0 - np.abs(est_w - curr_w) / curr_w

    def density_altitude( self, prs, tmp, alt_i ):
        prs_alt = (29.92 - prs) * 1000 + alt_i
        isa = 15 - (1.98 * (alt_i / 1000))
        dens_alt = prs_alt + (118.8 * (tmp - isa))
        return dens_alt

    def cutoff_trial(self, trial):
        count = 0
        for i in range(len(trial[0])):
            if trial[0][i] < self.cutoff:
                count += 1
            else:
                break
        new_trial = []
        for j in range(len(trial)):
            new_trial.append(trial[j][count:])
        return new_trial

    def process_data(self, dataset, is_test):
        feats = []
        labels = []
        if not is_test:
            feat = np.array(dataset["features"]).transpose()
            labels = np.array(dataset["labels"]).transpose()
        else:
            feats = np.array(dataset["test_features"]).transpose()
            labels = np.array(dataset["test_labels"]).transpose()

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

        return X,y

# ============================================================

def make_model( settings ):
    model = WeightModel( settings )
    return model

def train( model, dataset ):
    model = model.train( dataset )
    acc = model.score( dataset )
    return acc, model
