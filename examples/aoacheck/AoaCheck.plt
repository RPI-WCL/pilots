program AoaCheck; /* Model 3 */
  /* aoa: angle of attack, va: airspeed */
  inputs aoa, va (t) using closest(t);
  constants
    V_CRUISE = 110;
    NORMAL_L = -0.10 * V_CRUISE;
    NORMAL_H =  0.10 * V_CRUISE;
    MPS2KNOT = 1.94384;
    A        = 0.0881;
    B        = 0.3143;
    L        = 1156.6;
    S        = 16.2;
    RHO      = 1.225;
    G        = 9.80665;
  outputs
    aoa_estimated, aoa, e, mode, va at every 1 sec;
  errors
    e: va - MPS2KNOT * sqrt((2*L*G) / ((A*aoa + B)*S*RHO));
  signatures
    s0: e = k, NORMAL_L < k, k < NORMAL_H   "Normal";
    s1: e = k, k < NORMAL_L, NORMAL_H < k   "AoA sensor failure"
      estimate aoa_estimated = ((2*L*MPS2KNOT^2*G)/(A*S*RHO*va^2)) - B/A;
end;
