program ATR72_cruise;
	inputs
		v_a, a, p, t, w, h (t) using closest(t);
		cl (t) using predict(1, a);
		cruise (t) using predict(cruise, h);
	outputs
		corrected_weight: w at every 1 sec;
	errors
		e: atan((w - (p*(v_a*v_a)*61*cl/(2*286.9*t)))/w)*cruise+(1-cruise)*10;
  	signatures
  		s0(K): e = K, -0.05 < K, K < 0.05 		   "Normal";
		s1(K): e = K, 1.57079632679 > K, K > 0.05 		   "over weight";
		s2(K): e = K, -1.57079632679 < K, K < -0.05 		"under weight";
		s3(K): e = K, K > 1.57079632679						"Not Applicable";
        correct
	        s1: w = p*(v_a*v_a)*61*cl/(2*286.9*t);
	        s2: w = p*(v_a*v_a)*61*cl/(2*286.9*t);
end