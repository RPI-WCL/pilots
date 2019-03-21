program AoaCheck;
	inputs
		aoa, v (t) using closest(t);
	outputs
        aoa_out: aoa at every 1 sec;
	errors
        /* K1 = 11.34796, K2 = 0.00076, K3 = 0.00367 */
        e: v - 1.94384 * sqrt(11.34796 / (0.00076 * aoa + 0.00367));
	signatures
        s0(K): e = K, K < 1    "Normal";
        s1(K): e = K, K > 13   "AoA sensor failure"
            estimate aoa = ((1.94384 * 1.94384 * 11.34796/(v * v)) - 0.00076) / 0.00367;
end;
