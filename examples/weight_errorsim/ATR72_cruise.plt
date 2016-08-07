program ATR72;
	inputs
		v_a, a, p, t, w (t) using closest(t);
		cl (t) using predict(1, a);
		cruise (t) using predict(2, p);
	outputs
		corrected_weight: w at every 1 sec;
	errors
		e: (w - p*(v_a*v_a)*61*cl/(2*286.9*t))/w*cruise;
  	signatures
  		s0(K): e = K, -0.035 < K < 0.035 		   "Normal";
		s1(K): e = K, K > 0.035 		   "over weight";
		s2(K): e = K, K < -0.035 		"under weight";
    correct
	    s1: w = p*(v_a*v_a)*61*cl/(2*286.9*t);
	    s2: w = p*(v_a*v_a)*61*cl/(2*286.9*t);
end