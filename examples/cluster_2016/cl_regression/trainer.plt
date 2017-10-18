trainer cl_regression; 
    / * 
    v: true air speed, 
    a: angle of attack, 
    p: pressure, 
    t: temperature, 
    w: gross weight, 
    S: wing area, 
    R: Gas constant 
    * / 
    constants 
        R = 286.9; 
        S = 61.0; 
    data 
        v, p, t, w, a using file(weightcheck.csv ); 
        schema 
            unit(v:knot, p:in_Hg, t:celsius, w:force_pound, a:degree); 
    model 
        preprocess 
            changeunit using unit(v:m/s, p:pascal, t:kelvin, w:newton, a:radian); 
        features: a; 
        labels: 2 * w/(vˆ2 * (p/R/t) * S); 
        algorithm: LinearRegression using PseudoInverse(); 
        training: offline;
end