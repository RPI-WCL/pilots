program ATR72;
	inputs
		speed, aoa, pressure, temperature, weight (t) using closest(t);
		cl (t) using predict(1, aoa);
	outputs
		measured_weight: weight at every 1 sec;
	errors
		e: abs(weight - pressure*(speed*speed)*61*cl/(2*286.9*temperature))/weight;
  signatures
  	s0(K): e = K, K < 0.035 		   "No error";
	s1(K): e = K, K > 0.035 		   "weight error";
    correct
	      s1: weight = pressure*(speed*speed)*61*cl/(2*286.9*temperature);
end
