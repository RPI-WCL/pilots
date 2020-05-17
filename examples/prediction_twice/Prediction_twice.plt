program Prediction_twice;
	inputs
		a, b (t) using closest (t);
		b_prime (t) using model(twice_model, a);
	outputs
		o: b - b_prime at every 1 sec;
end
