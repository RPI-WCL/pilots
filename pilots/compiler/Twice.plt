program twice;
	inputs
		a: (t) using closest(t;x;y);
		b: (t) using closest(t;x);
	outputs;
    errors
		e: b - func(b - 2 * a) at every 1 sec;
end;
