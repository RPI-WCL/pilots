program PredictionTest_mode;
	inputs
		a, b (t) using closest (t);
		mode (t) using predict(bayes_prediction_test, a, b);
	outputs
		mode at every 1 sec;
end
