trainer weightcheck_mode_bayes; 
    / * v: true air speed, 
    a: angle of attack, 
    p: pressure, 
    t: temperature, 
    w: gross weight, 
    S: wing area, 
    R: Gas constant, 
    mode: labeled mode for training, 
    cl: coefficient of lift 
    * / 
    constants 
        S = 61.0; 
        R = 286.9; 
    data 
        v, p, t, w, a, mode using file(weightcheck.csv); 
        cl using model(cl_regression, a); 
    schema 
        unit(v:knot, p:in_Hg, t:celsius, w:force_pound, a:degree); 
    model 
        preprocess 
            changeunit using unit(v:m/s, p:pascal, t:kelvin, w:newton, a:radian); 
        features: w - 0.5 * vˆ2 * (p/R/t) * 2 * cl; 
        labels: mode; 
        algorithm: BayesianClassifier using DynamicBayesClassifier 
            (sigma_scale:2, threshold:100); 
        training: both;
end