program twice;
	inputs
		a: (t) using closest(t);
		b: (t) using closest(t);
	outputs;
    errors
		e: b - 2 * a at every 1 sec;
end;
