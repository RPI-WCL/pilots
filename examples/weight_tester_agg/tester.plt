program weight_tester;
    inputs
        v_a, prs, tmp, alt, curr_w (t) using closest (t);
	est_w_kalb (t) using model(weight_model_kalb, v_a, prs, tmp, alt);
	est_w_krno (t) using model(weight_model_krno, v_a, prs, tmp, alt);
	est_w (t) using model(wagg_model, prs, tmp, alt, est_w_kalb, est_w_krno );
    outputs
	v_a, curr_w, est_w at every 200 msec;
end;
