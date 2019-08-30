program ATR72_real;
	inputs
		w, w_real (t) using closest(t);
	outputs
		corrected_weight: w at every 1 sec;
	errors
		e: (w - w_real)/w;
  	signatures
  		s0(K): e = K, -0.035 < K, K < 0.035 		   "Normal";
		s1(K): e = K, K > 0.035 		   "over weight"
			estimate w = w_real;
		s2(K): e = K, K < -0.035 		"under weight"
			estimate w = w_real;
end