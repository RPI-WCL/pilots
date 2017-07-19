from __future__ import print_function

import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt

# TODO: random order data feed

# Import data
D = np.genfromtxt('training_data/atr72/training.csv', delimiter=',', skip_header=1)
num_data = D.shape[0]
xs = D[:,[0,1,2,4]]
ys = D[:,3].reshape(num_data,1)

# Parameters
learning_rate = 0.01
training_epochs = 5000
batch_size = 100
display_step = 100

# Network Parameters
n_hidden_1 = 20 # 1st layer number of features
n_hidden_2 = 10 # 2nd layer number of features
n_input = 4
# n_classes = 10 # MNIST total classes (0-9 digits)

# tf Graph input
x = tf.placeholder("float", [None, n_input])
y_ = tf.placeholder("float", [None, 1])


# Create model
def multilayer_perceptron(x, weights, biases):
    # Hidden layer with RELU activation
    layer_1 = tf.add(tf.matmul(x, weights['h1']), biases['b1'])
    layer_1 = tf.nn.relu(layer_1)
    # Hidden layer with RELU activation
    layer_2 = tf.add(tf.matmul(layer_1, weights['h2']), biases['b2'])
    layer_2 = tf.nn.relu(layer_2)
    # Output layer with linear activation
    out_layer = tf.matmul(layer_2, weights['out']) + biases['out']
    return out_layer

# Store layers weight & bias
weights = {
    'h1': tf.Variable(tf.random_normal([n_input, n_hidden_1])),
    'h2': tf.Variable(tf.random_normal([n_hidden_1, n_hidden_2])),
    'out': tf.Variable(tf.random_normal([n_hidden_2, 1]))
}
biases = {
    'b1': tf.Variable(tf.random_normal([n_hidden_1])),
    'b2': tf.Variable(tf.random_normal([n_hidden_2])),
    'out': tf.Variable(tf.random_normal([1]))
}

# Construct model
y = multilayer_perceptron(x, weights, biases)

# Define loss and optimizer
# error = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=y, labels=y_))
error = tf.reduce_sum(tf.square(y_-y))/num_data
optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate).minimize(error)

# Initializing the variables
init = tf.global_variables_initializer()

# Launch the graph
sess = tf.InteractiveSession()
sess.run(init)

# Training cycle
for epoch in range(training_epochs):
    avg_error = 0.
    # total_batch = int(mnist.train.num_examples/batch_size)
    total_batch  = int(num_data/batch_size)
    # Loop over all batches
    for i in range(total_batch):
        # batch_x, batch_y = mnist.train.next_batch(batch_size)
        batch_x = xs[i*100:(i+1)*100]
        batch_y = ys[i*100:(i+1)*100].reshape(100,1)

        # Run optimization op (backprop) and error op (to get loss value)
        _, e = sess.run([optimizer, error], feed_dict={x: batch_x,
                                                      y_: batch_y})
        # Compute average loss
        avg_error += e / total_batch
    # Display logs per epoch step
    if epoch % display_step == 0:
        print("Epoch:", '%04d' % (epoch+1), "error=", \
            "{:.9f}".format(avg_error))

results = sess.run(y,feed_dict={x: xs, y_: ys})
# print ("------------------------------------------------------------")
# for i in range(num_data):
#     print (results[i], ys[i])

plt.plot(np.arange(0,num_data), results, 'r', ys, 'b')
plt.show()

# Test model
correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
