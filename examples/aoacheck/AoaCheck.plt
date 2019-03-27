program AoaCheck;
	inputs
		aoa, v (t) using closest(t);
	outputs
        aoa_out: aoa at every 1 sec;
	errors
        /* MPS2KNOT=1.94384, A=0.0881, B=0.3143, L=1156.6, S=16.2, RHO=1.225, G=9.80665 */
        e: v - 1.94384 * sqrt((2*1156.6*9.80665) / ((0.0881*aoa + 0.3143)*16.2*1.225));
	signatures
        s0(K): e = K, -10 < K, K < 10  "Normal";
        s1(K): e = K, K > 20           "AoA higher-than-actual"
            estimate aoa = ((2*1156.6*1.94384*1.94384*9.80665)/(0.0881*16.2*1.225*v*v)) - 0.3143/0.0881;
        s1(K): e = K, K <-13           "AoA lower-than-actual"
            estimate aoa = ((2*1156.6*1.94384*1.94384*9.80665)/(0.0881*16.2*1.225*v*v)) - 0.3143/0.0881;
end;

b
