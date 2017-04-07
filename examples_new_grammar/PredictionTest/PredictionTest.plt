program PredictionTest;
	inputs
		a, c (t) using closest (t);
		b (t) using predict(linear_regression_twice, a);
	outputs
		o: c - b at every 1 sec;
end