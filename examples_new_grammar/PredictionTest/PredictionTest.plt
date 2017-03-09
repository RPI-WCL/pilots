program PredictionTest;
	inputs
		a,b (t) using closest (t);
		c (t) using predict(linear, a);
	outputs
		difference: b - c at every 1 sec;
end