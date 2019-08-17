program TwiceCorrect;
    inputs
        a(t) using closest(t);
        b(t) using closest(t);
    outputs
        e, mode at every 1 sec;
    errors
        e: b - 2 * a;
    signatures
        s0: e = 0                 "Normal mode";
        s1(K): e = 2 * t + K      "A failure"
		estimate a = b / 2;
        s2(K): e = -2 * t + K     "B failure"
		estimate b = a * 2;
        s3(K): e = K, abs(K) > 20 "Out-of-sync";
end;
