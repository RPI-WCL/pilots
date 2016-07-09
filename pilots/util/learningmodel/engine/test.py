from baseenv import *
from scparser import *
import bayes
def generate_linearRegression():
    units = ['knot', 'in_Hg', 'celsius', 'degree', 'force_pound']
    tounits = ['m/s', 'pascal', 'kelvin', 'radian', 'newton']
    name = ['v','p','t','a','w']
    env = BaseEnv()
    data = env.load_file("data/thetrain.csv", csvfile, csvfile_header, header=['v','p','t','a','w'])
    env.append_data(data)
    env.set_constant(s=61.0)
    env.update_variable_info(v={'unit': 'knot'}, p={'unit': 'in_Hg'}, t={'unit': 'celsius'}, a={'unit': 'degree'}, w={'unit': 'force_pound'})
    cached_data = env.transform(env.unit_transformer, schema=env.get_schema_info(), units=dict(v='m/s',a='radian',w='newton',t='kelvin',p='pascal'))
    env.data = cached_data.copy()
    cached_data = env.transform(env.feature_label_transformer, schema=env.get_schema_info(), constant=env.get_constant(), features=["{a}"], labels=["2*{w}/({v}**2*({p}/286.9/{t})*{s})"])
    env.label = cached_data['label']
    env.feature = cached_data['feature']
    estimator = env.train_estimator(LinearRegression())
    # for estimation, the input should be: schematic meaning of the matrix columns, feature transformation method
    features = ['{a}']
    save_estimator("linearRegression.estimator", estimator, features, env.constant_dict)
    load_estimator("linearRegression.estimator")

def generate_Bayes():
    name = ['a','b','type']
    env = BaseEnv()
    pilotfile = lambda(row): map(float, row.split(':')[-1].split(','))
    pilot_header = lambda(row): map(lambda(word): word.strip(), row[1:].split(','))
    data = env.load_file("data/bayes_training.txt", pilotfile, pilot_header, header=name)
    env.append_data(data)
    cached_data = env.transform(env.feature_label_transformer, schema=env.get_schema_info(), constant=env.get_constant(), features=['{b}/{a}'], labels=["{type}"])
    env.label = cached_data['label']
    env.feature = cached_data['feature']
    estimator = env.train_estimator(bayes.Bayes())
    data = env.load_file('data/bayes_testing.txt', pilotfile, pilot_header, header=name)
    transformer = BaseEnv.generate_transformer(feature_list=['{b}/{a}'], schema=env.get_schema_info(), constant=env.get_constant())
    save_estimator("bayes.estimator", estimator, ['{b}/{a}'], env.constant_dict, True)
    (estimator, features, constants) = load_estimator("bayes.estimator", True, bayes.Bayes)

def test_estimator():
    (estimator, features, constants) = load_estimator("linearRegression.estimator")
    # load test matrix
    env = BaseEnv()
    data = env.load_file("data/thetest.csv", csvfile, csvfile_header)
    # set input matrix schema:
    schema = {'a': {'index': 3}}
    feature_transfromer = BaseEnv.generate_transformer(features, schema, {})
    testdata = np.apply_along_axis(feature_transfromer, 1, data)
#    plt.plot(testdata, estimator.predict(testdata))
#    plt.show()
    (estimator, features, constants) = load_estimator("bayes.estimator", True, bayes.Bayes)
    data = env.load_file("data/bayes_testing.txt", pilotfile, pilot_header)
    schema = {'a': {'index': 0}, 'b': {'index': 1}, 'type': {'index': 2}}
    feature_transfromer_bayes = BaseEnv.generate_transformer(features, schema, {})
    label_transformer = BaseEnv.generate_transformer(['{type}'], schema, {})
    testdata = np.apply_along_axis(feature_transfromer_bayes, 1, data)
    testdata_label = np.apply_along_axis(label_transformer, 1, data)
    testresult = estimator.predict(testdata)
    print testdata.shape
    print testresult.shape
    print '----------'
    plt.scatter(testdata.tolist(), testresult.tolist(), marker='.')
    plt.scatter(testdata.tolist(), testdata_label.tolist(), marker='x')
    plt.show()
if __name__ == '__main__':
    generate_Bayes()
    generate_linearRegression()
    test_estimator()