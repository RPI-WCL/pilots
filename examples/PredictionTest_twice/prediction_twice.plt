program prediction_twice;
	inputs
		a, c (t) using closest (t);
		b (t) using model(lin_reg, a);
	outputs
		o: c - b at every 1 sec;
end
