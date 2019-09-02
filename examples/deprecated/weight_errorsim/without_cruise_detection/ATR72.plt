program ATR72;
	inputs
		v_a, a, p, t, w (t) using closest(t);
		cl (t) using predict(linear_regression, a);
	outputs
		corrected_weight: w at every 1 sec;
	errors
		e: (w - p*(v_a*v_a)*61*cl/(2*286.9*t))/w;
  	signatures
  		s0(K): e = K, -0.035 < K, K < 0.035 		   "Normal";
		s1(K): e = K, K > 0.035 		   "over weight"
			estimate w = p*(v_a*v_a)*61*cl/(2*286.9*t);
		s2(K): e = K, K < -0.035 		"under weight"
			estimate  w = p*(v_a*v_a)*61*cl/(2*286.9*t);
end