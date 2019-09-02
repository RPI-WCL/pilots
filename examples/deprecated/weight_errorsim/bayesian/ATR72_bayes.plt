program ATR72_bayes;
	inputs
		v_a, a, p, t, w (t) using closest(t);
		cl (t) using predict( bayes, v_a, a, p, t, w );
	outputs
		signature: cl at every 1 sec;
end