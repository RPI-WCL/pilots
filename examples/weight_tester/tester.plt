program weight_tester;
    inputs
        v_a, prs, tmp, alt, curr_w (t) using closest (t);
	est_w (t) using model(weight_model, v_a, prs, tmp, alt);
    outputs
	v_a, curr_w, est_w at every 200 msec;
end;
