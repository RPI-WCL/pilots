program weight_tester_err;
    inputs
        v_a, prs, tmp, alt, curr_w (t) using closest (t);
	est_w_kalb (t) using model(weight_model_kalb, v_a, prs, tmp, alt);
	est_w_krno (t) using model(weight_model_krno, v_a, prs, tmp, alt);
	est_w (t) using model(wagg_model, prs, tmp, alt, est_w_kalb, est_w_krno );
    constants
        PERCENT = 100;
        CONVERGENCE = 10;
    outputs
	v_a, curr_w, est_w, e at every 200 msec;
    errors
        e: (est_w-curr_w)/curr_w * PERCENT;
    signatures
        s0: e = 0  "Perfect";
        s1(K): e = K, abs(K) < CONVERGENCE "Converge";
end;
