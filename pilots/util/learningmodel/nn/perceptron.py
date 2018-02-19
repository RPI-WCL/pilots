import tensorflow as tf
import numpy as np
from sklearn.preprocessing import StandardScaler
import math
import pandas as pd
from sklearn.metrics import mean_squared_error
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
import sys

def preprocess(x):
    scaler = StandardScaler()
    x = scaler.fit_transform(x)
    return x, scaler

def split_label(d):
    return d[:,1:], d[:,0].reshape(d.shape[0], 1)

def run(config, layers):
    learning_rate = config['learning_rate']
    training_epochs = config['training_epochs']
    train_data_files = config['train_data_files']
    test_data_files = config['test_data_files']

    D = load_data(*train_data_files)
    x, y = split_label(D)

    x_train, x_valid, y_train, y_valid = train_test_split(x, y)
    x_train, scaler = preprocess(x_train)

    # construct neuron network
    output_layer, feature_placeholder, label_placeholder = make_network(x_train.shape[1], y_train.shape[1], layers)
    error = tf.losses.mean_squared_error(output_layer, label_placeholder)
    optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate).minimize(error)

    # Initializing the variables
    init = tf.global_variables_initializer()

    # Launch the graph
    sess = tf.InteractiveSession()
    sess.run(init)

    errors = []
    for epoch in range(training_epochs):
        a, e = sess.run([optimizer, error], feed_dict={feature_placeholder: x_train, label_placeholder: y_train})
        errors.append(e)
        if epoch % 100 == 0:
            print "Epoch:", '%04d' % (epoch+1), "error=", "{:.9f}".format(e)
    plt.plot(errors)
    plt.yscale('log')
    plt.show()

    # validation error
    x_valid = scaler.transform(x_valid)
    e = sess.run(error, feed_dict={feature_placeholder: x_valid, label_placeholder: y_valid})
    print 'validation error RMSE = ', e**0.5
    results = sess.run(output_layer, feed_dict={feature_placeholder: x_valid})
    plt.plot(np.arange(0,x_valid.shape[0]), results, 'r', y_valid, 'b')
    plt.show()

    # in sample error 
    D = load_data(*train_data_files)
    x_insample, y_insample = split_label(D)
    x_insample = scaler.transform(x_insample)
    e = sess.run(error, feed_dict={feature_placeholder: x_insample, label_placeholder: y_insample})
    print 'validation error RMSE = ', e**0.5
    results = sess.run(output_layer, feed_dict={feature_placeholder: x_insample})
    plt.plot(np.arange(0,x_insample.shape[0]), results, 'r', y_insample, 'b')
    plt.show()

    # testing error
    D_test = load_data(*test_data_files)
    x_test, y_test = split_label(D_test)
    x_test = scaler.transform(x_test)

    results = sess.run(output_layer, feed_dict={feature_placeholder: x_test})
    e = sess.run(error, feed_dict={feature_placeholder: x_test, label_placeholder: y_test})
    print 'testing error RMSE = ', e**0.5
    plt.plot(np.arange(0,x_test.shape[0]), results, 'r', y_test, 'b')
    plt.show()
    return output_layer, feature_placeholder, label_placeholder, scaler, error

# hyper parameter tuning by grid search
def make_network(input_size, output_size, hidden_layers_activation_fn):
    x = tf.placeholder("float", [None, input_size])
    y_ = tf.placeholder("float", [None, 1])
    layers = []
    prev = x
    prev_num_neuron = input_size
    for i, layer in enumerate(hidden_layers_activation_fn):
        num_neuron, activation_func = layer
        current_layer = tf.add(tf.matmul(prev, tf.Variable(tf.random_normal([prev_num_neuron, num_neuron]))), tf.Variable(tf.random_normal([num_neuron])))
        if activation_func:
            current_layer = activation_func(current_layer)
        layers.append(current_layer)
        prev = current_layer
        prev_num_neuron = num_neuron
    # make the last layer
    if layers:
        layers.append(tf.add(tf.matmul(layers[-1], tf.Variable(tf.random_normal([prev_num_neuron, output_size]))), tf.Variable(tf.random_normal([output_size]))))
    else:
        layers.append(tf.add(tf.matmul(x, tf.Variable(tf.random_normal([prev_num_neuron, output_size]))), tf.Variable(tf.random_normal([output_size]))))
    return layers[-1], x, y_

def load_data(*file_names):
    D = []
    for file_name in file_names:
        df = pd.read_csv(file_name)
        D.append(np.matrix(df))
    return np.vstack(D)

def main():
    features = ['_roll__deg', 'pitch__deg', 'alpha__deg', '__altftmsl', '_Vind_kias', 'y_velocity', 'y_acceleration']
    label = ['curnt___lb']
    config = {
        'train_data_files': ['train/20160713_all.csv', 'train/20160715_all.csv'],
        'test_data_files': ['train/20160712_cruise_phase.csv'],
        'learning_rate': 0.005, 
        'training_epochs': 50000,
        'batch_size': 10000
    }
    layers = [(20, tf.nn.sigmoid), (10, tf.nn.relu)]
    return run(config, layers)

main()