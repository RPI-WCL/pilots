import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt


def main():
    # Import data
    D = np.genfromtxt('training_data/atr72/training.csv', delimiter=',', skip_header=1)
    num_data = D.shape[0]

    R = 286.9
    s = 61.0

    xs = D[:,4].reshape(num_data,1)
    ys = 2*R*R/s* np.divide(np.multiply(D[:,3],(D[:,2]+273.15)),
                            np.multiply(D[:,1], np.square(D[:,0])))
    ys = (ys/np.average(ys)).reshape(num_data,1)

    # print xs[:30]
    # print ys[:30]
    # print "---------------"

    # Create the model
    x = tf.placeholder(tf.float32, [None, 1])
    # W = tf.Variable(tf.zeros([1,1]))
    W = tf.Variable(tf.constant(0.0, shape=[1,1]))
    # b = tf.Variable(tf.zeros([1,1]))
    b = tf.Variable(tf.constant(0.1, shape=[1,1]))

    y = tf.matmul(x, W) + b

    y_ = tf.placeholder(tf.float32, [None, 1])

    mean_squared_error = tf.reduce_sum(tf.square(y_-y))/num_data
    train_step = tf.train.GradientDescentOptimizer(6.4).minimize(mean_squared_error)

    sess = tf.InteractiveSession()
    tf.global_variables_initializer().run()

    # Train
    for i in range(40):
        batch_xs = xs[i*100:(i+1)*100].reshape(100,1)
        batch_ys = ys[i*100:(i+1)*100].reshape(100,1)
        sess.run(train_step, feed_dict={x: batch_xs, y_: batch_ys})
        # print(sess.run([W[0,0],b[0,0]]))

    # Test trained model
    test_error_sum = tf.reduce_sum(tf.square(y_-y))
    print("in sample error:",
            sess.run(test_error_sum/4000, feed_dict={x: xs[:4000].reshape(4000,1),
                                        y_: ys[:4000].reshape(4000,1)}))
    print("out of sample error:",
            sess.run(test_error_sum/(num_data-4000), feed_dict={x: xs[4000:].reshape(num_data-4000,1),
                                        y_: ys[4000:].reshape(num_data-4000,1)}))

    results = sess.run(y,feed_dict={x: xs.reshape(num_data,1),
                                        y_: ys.reshape(num_data,1)})
    # print "------------------------------------------------------------"
    # for i in range(num_data):
    #     print results[i], ys[i]
    plt.plot(np.arange(0,num_data), results, 'r', ys, 'b')
    plt.show()

if __name__ == '__main__':
    main()