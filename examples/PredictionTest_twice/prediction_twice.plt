program prediction_twice;
	inputs
		a, c (t) using closest (t);
		b (t) using model(example, a);
	outputs
		o: c - b at every 1 sec;
end
