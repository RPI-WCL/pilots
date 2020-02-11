import math
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.mlab as mlab
import matplotlib
from scipy.stats import norm
from matplotlib.legend_handler import HandlerLine2D
import operator
import json

''' Wennan's Code '''
class State(object):
    def __init__(self, state_id):
        self.id = state_id
        self.n = 0
        self.mean = 0.0
        self.std = 0.0
        self.sum = 0.0
        self.sqsum = 0.0
        self.prior = 0.0

    def update(self, input_data):
        self.n += 1
        self.sum += input_data
        self.sqsum += input_data ** 2
        self.mean = self.sum/self.n
        self.std = math.sqrt(self.sqsum/self.n - (self.sum/self.n) ** 2)

class Bayes(object):

    def __init__(self, sigma_scale=2, threshold=100):
        self.data = {}
        self.states = {}
        self.minor_states = {}
        self.total_num = 0
        self.sigma_scale = sigma_scale
        self.threshold = threshold

    def __str__(self):
        return "(Bayes threshold=%d, sigma_scale=%d, num_states=%d, num_minor_states=%d, num_data=%d)"%(self.threshold, self.sigma_scale, len(self.states), len(self.minor_states), self.total_num)
    def __repr__(self):
        return self.__str__()

    def print_states(self):
        for item in self.states.values():
            print 'id',item.id
            print 'n',item.n
            print 'mean',item.mean
            print 'std',item.std
            print 'sum',item.sum
            print 'sqsum',item.sqsum
            print 'prior',item.prior

    def print_minor_states(self):
        for item in self.minor_states.values():
            print 'id',item.id
            print 'n',item.n
            print 'mean',item.mean
            print 'std',item.std
            print 'sum',item.sum
            print 'sqsum',item.sqsum
            print 'prior',item.prior

    # Get the next available state id
    def get_next_id(self):
        current_ids = self.states.keys() + self.minor_states.keys()
        for i in range(1, 1000):
            if i not in current_ids:
                return i
        return "error"

    def create_minor_state(self, item):
        state_id = self.get_next_id()
        self.minor_states[state_id] = State(state_id)
        self.minor_states[state_id].update(item)
        return state_id

    def update_states_prior(self):
        for item in self.states.values():
            item.prior = (1.0*item.n)/self.total_num

    def save_training_para(self, filename):
        output_string = "{'states':["
        count = 0
        for item in self.states.values():
            output_string += "{'id':" + str(item.id) + ","
            output_string += "'n':" + str(item.n) + ","
            output_string += "'mean':" + str(item.mean) + ","
            output_string += "'std':" + str(item.std) + ","
            output_string += "'sum':" + str(item.sum) + ","
            output_string += "'sqsum':" + str(item.sqsum) + ","
            output_string += "'prior':" + str(item.prior) + "}"
            count += 1
            if count < len(self.states):
                output_string += ","
        output_string += "]}\r\n"
        output_string = output_string.replace("'", "\"");
        print output_string
        with open(filename, 'w') as outfile:
            outfile.write(output_string)
    def to_json(self):
        json_dict = {}
        json_dict['states'] = map(lambda(item): {'id': item.id, 'n': item.n,'mean':item.mean,'std':item.std,'sum':item.sum,'sqsum':item.sqsum,'prior':item.prior}, self.states.values())
        json_dict['config'] = {'sigma_scale': self.sigma_scale, 'threshold': self.threshold}
        return json.dumps(json_dict)

    def load_json(self, json_string):
        self.states = {}
        self.total_num = 0
        temp = json.loads(json_string)
        for item in temp["states"]:
            item_id = item["id"]
            self.states[item_id] = State(item_id)
            self.states[item_id].n = item["n"]
            self.states[item_id].mean = item["mean"]
            self.states[item_id].std = item["std"]
            self.states[item_id].sum = item["sum"]
            self.states[item_id].sqsum = item["sqsum"]
            self.states[item_id].prior = item["prior"]
            self.total_num += item["n"]
        self.sigma_scale = temp['config']['sigma_scale']
        self.threshold = temp['config']['threshold']
        
    def load_training_para(self, filename):
        self.states = {}
        self.total_num = 0
        with open(filename, 'r') as inputfile:
            temp = json.load(inputfile)
            for item in temp["states"]:
                item_id = item["id"]
                self.states[item_id] = State(item_id)
                self.states[item_id].n = item["n"]
                self.states[item_id].mean = item["mean"]
                self.states[item_id].std = item["std"]
                self.states[item_id].sum = item["sum"]
                self.states[item_id].sqsum = item["sqsum"]
                self.states[item_id].prior = item["prior"]
                self.total_num += item["n"]

    # Code from Sida  https://github.com/KeyboardNerd/SpatioTemporalLearning
    def load_data(self, filename):
        reader = filter(lambda(x): bool(x.strip()), open(filename,"rU").readlines())
        headline = reader[0].strip()
        if headline[0] != '#':
            raise Exception("Not a valid PILOTS file")
        heading = headline[1:].split(',')
        length = len(heading)
        self.data = {}
        for item in heading:
            self.data[item] = []
        for row in reader[1:]:
            d = row.split(":")
            location = d[0]
            time = d[1]
            items = map(float, d[2].split(','))
            if len(items) != length:
                raise Exception("data is not filled, aborted")
            for i in range(length):
                if (heading[i] == "type"):
                    self.data[heading[i]].append(int(items[i]))
                else:
                    self.data[heading[i]].append(items[i])

    def histo_plot(self, data):
        (mu, sigma) = norm.fit(data)
        # the histogram of the data
        n, bins, patches = plt.hist(data, 30, normed=1, facecolor='green', alpha=0.75)

        # add a 'best fit' line
        y = mlab.normpdf( bins, mu, sigma)
        l = plt.plot(bins, y, 'r--', linewidth=2)

    def plot_training(self, input_data, input_type):
        # Plot the histogram of b/a
        self.histo_plot(input_data[:10000])
        self.histo_plot(input_data[10000:20000])
        plt.xlabel('b/a')
        plt.ylabel('Probability')
        plt.title('Histogram of the training set b/a')
        plt.grid(True)
        plt.show()

        input_type_mean = []
        for item in input_type:
            input_type_mean.append(self.states[item].mean)
        # Plot b/a, mean value of the input type
        plt.plot(input_data)
        plt.plot(input_type_mean, marker='o', markersize=5, label='Type')
        plt.legend()
        plt.title("Training Set b/a, and types")
        plt.ylabel("b/a")
        plt.xlabel("time (s)")
        plt.show()

    def fit(self, input_data, input_type):
        input_data = input_data.transpose().tolist()[0]
        input_type = input_type.transpose().tolist()[0]
        if (len(input_data) != len(input_type)):
            raise Exception("Learning data and type length doesn't match")
        for i in range(len(input_data)):
            self.total_num += 1
            if not input_type[i] in self.states.keys():
                self.states[input_type[i]] = State(input_type[i])
            self.states[input_type[i]].update(input_data[i])
            self.update_states_prior()

    def predict(self, input_data, no_learning=False):
        # input_data is a numpy column matrix, 
        input_data = input_data.transpose().tolist()[0]
        output_type = []
        for item in input_data:
            if (self.in_major_states(item)):
                self.total_num += 1
                item_state = self.get_state(item)
                self.states[item_state].update(item)
                self.update_states_prior()
                output_type.append(item_state)
            else:
                item_state = self.in_minor_states(item)
                if (item_state):
                    self.minor_states[item_state].update(item)
                    output_type.append(item_state)
                    # If item number in a minor state > 1000, change it to a major state.
                    if (self.minor_states[item_state].n >= self.threshold):
                        self.states[item_state] = self.minor_states[item_state]
                        del(self.minor_states[item_state])
                        self.total_num += self.states[item_state].n
                        self.update_states_prior()
                else:
                    item_state = self.create_minor_state(item)
                    output_type.append(item_state)
        return np.asmatrix(output_type).transpose()

    def training(self, input_data, input_type):
        # Training data: a and b
        # b is twice, three times of a

        # Bayes Classifier
        if (len(input_data) != len(input_type)):
            raise Exception("Learning data and type length doesn't match")
        for i in range(len(input_data)):
            self.total_num += 1
            if not input_type[i] in self.states.keys():
                self.states[input_type[i]] = State(input_type[i])
            self.states[input_type[i]].update(input_data[i])
            self.update_states_prior()
        self.plot_training(input_data, input_type)

    def get_state(self, item):
        post_prob = {}
        evidence = 0.0
        for state in self.states.values():
            evidence += norm.pdf(item, state.mean, state.std) * state.prior
        for i in self.states:
            post_prob[i] = norm.pdf(item, self.states[i].mean, self.states[i].std) * self.states[i].prior / evidence
        # Get the state of the highest post prob.
        return max(post_prob.iteritems(), key=operator.itemgetter(1))[0]

    def testing(self, input_data):
        # Testing data, c and d
        output_type = []
        for item in input_data:
            output_type.append(self.get_state(item))
        print output_type

    # Check if the item is within mean +- 3 sigma of any major state
    def in_major_states(self, item):
        for state in self.states.values():
            if item >= state.mean - self.sigma_scale * state.std and item <= state.mean + self.sigma_scale * state.std:
                return True
        return False

    # Get average value of std divided by mean
    def avg_std(self):
        result = 0.0
        for state in self.states.values():
            result += state.std
        return result / len(self.states)

    # Check if the item is within mean +- 3 sigma of any minor state
    def in_minor_states(self, item):
        for state in self.minor_states.values():
            est_std = state.std
            # If item in a minor state < 100, use estimated std
            if state.n < 100:
                est_std = self.avg_std()
            if item >= state.mean - self.sigma_scale * est_std and item <= state.mean + self.sigma_scale * est_std:
                return state.id
        return False

    def plot_testing(self, input_data, output_type):
        # Plot the histogram of d/c
        self.histo_plot(input_data[:10000])
        self.histo_plot(input_data[10000:20000])
        self.histo_plot(input_data[20000:30000])
        plt.xlabel('d/c')
        plt.ylabel('Probability')
        plt.title('Histogram of the testing set d/c')
        plt.grid(True)
        plt.show()

        output_type_mean = []
        for item in output_type:
            if item in self.states.keys():
                output_type_mean.append(self.states[item].mean)
            else:
                output_type_mean.append(0)
        # Plot d/c, mean value of the output type
        plt.plot(input_data)
        plt.plot(output_type_mean, marker='o', markersize=5, label='Type')
        plt.legend()
        plt.title("Testing Set d/c, and types")
        plt.ylabel("d/c")
        plt.xlabel("time (s)")
        plt.show()

    # Online training and testing
    def online_learning(self, input_data):
        output_type = []
        self.print_states()
        for item in input_data:
            if (self.in_major_states(item)):
                self.total_num += 1
                item_state = self.get_state(item)
                self.states[item_state].update(item)
                self.update_states_prior()
                output_type.append(item_state)
            else:
                item_state = self.in_minor_states(item)
                if (item_state):
                    self.minor_states[item_state].update(item)
                    output_type.append(item_state)
                    # If item number in a minor state > 1000, change it to a major state.
                    if (self.minor_states[item_state].n >= self.threshold):
                        self.states[item_state] = self.minor_states[item_state]
                        del(self.minor_states[item_state])
                        self.total_num += self.states[item_state].n
                        self.update_states_prior()
                else:
                    item_state = self.create_minor_state(item)
                    output_type.append(item_state)
                    print item_state
        print output_type
        self.print_states()
        self.print_minor_states()

        self.plot_testing(input_data, output_type)

# Public interface functions
        
def is_live():
    return True

def make_model():
    bay = Bayes()
    return bay

def train( model, dataset ):
    X = np.array(dataset["features"]).transpose()
    y = np.array(dataset["labels"]).transpose()
    return model.predict( dataset )

def run( model, data ):
    return
