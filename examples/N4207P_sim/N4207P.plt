program weight_tester_err;
    inputs
        v_a, prs, tmp, alt, curr_w (t) using closest (t);
	est_w (t) using model(weight_model_exp, v_a, prs, tmp, alt);
    outputs
	v_a, curr_w, est_w, e at every 200 msec;
    errors
        e: (est_w-curr_w)/curr_w * 100;
end;
