program AoaSpeedCheck1;
  inputs
    va, vg, vw, aa, ag, aw, aoa (t) using closest(t);
  constants
    V_CRUISE            =    110;
    SPEED_NORMAL_L      =   -0.1 * V_CRUISE; 
    SPEED_NORMAL_H      =   0.33 * V_CRUISE;
    SPEED_PITOT_L       =   0.34 * V_CRUISE;
    SPEED_PITOT_H       =  16.10 * V_CRUISE;
    SPEED_GPS_L         = -13.83 * V_CRUISE;
    SPEED_GPS_H         =  -0.67 * V_CRUISE;
    SPEED_GPS_PITOT_L   =  -0.66 * V_CRUISE;
    SPEED_GPS_PITOT_H   =   -0.1 * V_CRUISE;
    AOA_NORMAL          =   0.10 * V_CRUISE;
    PI                  = 3.141592;
    MPS2KNOT            = 1.94384;
    K1                  = 2.90094;
    K2                  = 0.00024;
    K3                  = 0.00108;
  outputs
    va, vg, aoa, mode at every 1 sec;
  errors
    e1: vg - sqrt(va^2 + vw^2 + 2*va*vw*cos((PI/180)*(aw-aa)));
    e2: va - MPS2KNOT * sqrt(K1/(K2*aoa + K3));
  modes
    m0: SPEED_NORMAL_L < e1 and e1 < SPEED_NORMAL_H and abs(e2) < AOA_NORMAL    "Normal";
    m1: SPEED_NORMAL_L < e1 and e1 < SPEED_NORMAL_H and AOA_NORMAL <= abs(e2)   "AoA sensor failure"
        estimate aoa = (1/K2)*((MPS2KNOT^2/va^2)*K1 - K3);      
    m9: SPEED_PITOT_L < e1 and e1 < SPEED_PITOT_H
        "Pitot tube + (AoA sensor) failure"
        estimate va = sqrt(vg^2 + vw^2 - 2*vg*vw*cos((PI/180)*(ag-aw)))
        estimate aoa = (1/K2)*((MPS2KNOT^2/va^2)*K1 - K3);
    m4: SPEED_GPS_L < e1 and e1 < SPEED_GPS_H and abs(e2) < AOA_NORMAL          "GPS failure"
        estimate vg = sqrt(va^2 + vw^2 + 2*va*vw*cos((PI/180)*(aw-aa)));
    m5: SPEED_GPS_L < e1 and e1 < SPEED_GPS_H and AOA_NORMAL <= abs(e2)
        "GPS failure + AoA sensor failure"
        estimate vg = sqrt(va^2 + vw^2 + 2*va*vw*cos((PI/180)*(aw-aa)))
        estimate aoa = (1/K2)*((MPS2KNOT^2/va^2)*K1 - K3);      
   m11: SPEED_GPS_PITOT_L < e1 and e1 < SPEED_GPS_PITOT_H 
        "GPS + Pitot tube + (AoA sensor) failure";
end;
