program weight_tester;
    inputs
        v_a, prs, tmp, alt_i, curr_w (t) using closest (t);
	est_w (t) using model(weight, v_a, prs, tmp, alt_i);
    outputs
	v_a, curr_w, est_w at every 1 sec;
end;
