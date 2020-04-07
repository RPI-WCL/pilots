program  ensemble;
  constants
    w1 = 0.2;
    w2 = 0.5;
    w3 = 0.3;
  inputs
    // Pressure, temperature, and  altitude
    prs, tmp, alt (t) using  closest (t);
    // Get  prediction  from  each  model
    da1  using  model(da_model1, prs, tmp, alt);
    da2  using  model(da_model2, prs, tmp, alt);
    da3  using  model(da_model3, prs, tmp, alt);
    //  Combine  results
    A using  average(da1, da2, da3);
    M using  majority(da1, da2, da3, none:A);
    W using  weighted(w1:da1, w2:da2, w3:da3);
    D using  decision( (prs < 29.00):da1,
              (29.00  < prs < 30.00):da2,
                      (30.00  < prs):da3 );
  outputs
    A, M, W, D at every 1 sec;
end;
