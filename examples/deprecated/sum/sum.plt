program Sum;
	inputs
		a, b, c (t) using closest(t);
		e_a (t) using closest(t);
	outputs
		o: c - b - a at every 1 sec;
    errors
		e: c - b - a;
    signatures
        s0(K): e = 0                           "No error"
        estimate e_a = c - b when s0 10 times;
        s1(K): e = K, K > 220                  "b failure"
		estimate b = c - e_a; 
        s2(K): e = K, K < -420                 "c failure"
		estimate c = e_a + b;
        s3(K): e = K, -200 < K, K < -10        "both b and c failure";
end
