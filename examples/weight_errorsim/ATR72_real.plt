program ATR72_real;
	inputs
		w, w_real (t) using closest(t);
	outputs
		corrected_weight: w at every 1 sec;
	errors
		e: (w - w_real)/w;
  	signatures
  		s0(K): e = K, -0.035 < K < 0.035 		   "Normal";
		s1(K): e = K, K > 0.035 		   "over weight";
		s2(K): e = K, K < -0.035 		"under weight";
    correct
	    s1: w = w_real;
	    s2: w = w_real;
end