program PredictionTest;
	inputs
		a (t) using closest (t);
		b (t) using predict(linear_regression_twice, a);
	outputs
		B: b at every 1 sec;
end