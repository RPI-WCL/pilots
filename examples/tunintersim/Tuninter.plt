/* Weight from speed example **Most current ** */
program Tuninter;
	inputs
		airspeed,weight (t) using closest(t);
	outputs
		weight_out: weight at every 1 sec;
	errors
		e: weight - 18199 + 0.0431*airspeed*airspeed;
	signatures
		s0(K): e = K, -50 < K, K < 50			"No error";
		s1(K): e = K,  K < -1000		"Underpowered or Overweight";
		s2(K): e = K,  1000 < K			"Overpowered or Underweight";
	correct
		s1: weight = -0.0431*airspeed*airspeed + 18199; 
		s2: weight = -0.0431*airspeed*airspeed + 18199; 
end 

/*Notes:

Limits for weight are variable, here we chose 20% of the total fuel capacity difference to notify the pilot.
*/