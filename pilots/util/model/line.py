import numpy as np
from sklearn.linear_model import LinearRegression

def make_model():
    return LinearRegression()

def train( model, arg_map ):
    X = np.array( [[1,1], [2,2], [3,3], [4,4], [5,5]] )
    y = np.array( [2,4,6,8,10] )
    model = model.fit( X, y )

def run( model, data_map ):
    x = data_map[ 'x' ]
    result = model.predict( np.array([[x,x]]) )
    return result.tolist()
