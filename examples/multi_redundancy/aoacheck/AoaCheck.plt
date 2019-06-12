program AoaCheck; /* Model 3 */
  /* aoa: angle of attack, va: airspeed */
  inputs aoa, va (t) using closest(t);
  constants
    V_CRUISE = 110;
    NORMAL_L = -0.10 * V_CRUISE;
    NORMAL_H =  0.10 * V_CRUISE;
    MPS2KNOT = 1.94384;
    K1       = 2.90094;
    K2       = 0.00024;
    K3       = 0.00108;
  outputs
    aoa, mode at every 1 sec;
  errors
    e2: va - MPS2KNOT * sqrt(K1/(K2*aoa + K3));
  signatures
    s0(k): e2 = k, NORMAL_L < k, k < NORMAL_H   "Normal";
    s1(k): e2 = k, k < NORMAL_L, NORMAL_H < k   "AoA sensor failure"
      estimate aoa = (1/K2)*((MPS2KNOT^2/va^2)*K1 - K3);
end;
