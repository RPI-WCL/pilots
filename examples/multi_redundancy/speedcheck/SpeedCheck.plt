program SpeedCheck;
  /* va: airspeed,     aa: airspeed angle,
     vw: wind speed,   aw: wind speed angle,
     vg: ground speed, ag: ground angle */
  inputs
    va, vg, vw (t) using closest(t);
    aa, ag, aw (t) using closest(t);
  constants /* For Cessna 172 SP */
    V_CRUISE    =   110;
    NORMAL_L    =  -0.1 * V_CRUISE; 
    NORMAL_H    =   0.1 * V_CRUISE;
    PITOT_L     =  0.57 * V_CRUISE;
    PITOT_H     =   0.9 * V_CRUISE;
    GPS_L       =  -1.1 * V_CRUISE;
    GPS_H       =  -0.9 * V_CRUISE;
    GPS_PITOT_L = -0.43 * V_CRUISE;
    GPS_PITOT_H =  -0.1 * V_CRUISE;
    PI          = 3.141592;
  outputs
    va, vg, mode at every 1 sec;
  errors
    e1: vg - sqrt(va^2 + vw^2 + 
                  2*va*vw*cos((PI/180)*abs(aw-aa)));
  signatures
    s0(k): e1 = k, NORMAL_L < k, k < NORMAL_H "Normal";
    s1(k): e1 = k, PITOT_L  < k, k < PITOT_H
      "Pitot tube failure"
      estimate va = sqrt(vg^2 + vw^2 -
        2*vg*vw*cos((PI/180)*abs(ag-aw)));
    s2(k): e1 = k, GPS_L    < k, k < GPS_H "GPS failure"
      estimate vg = sqrt(va^2 + vw^2 + 
        2*va*vw*cos((PI/180)*abs(aw-aa)));
    s3(k): e1 = k, GPS_PITOT_L < k, k < GPS_PITOT_H
      "GPS + Pitot tube failure";
end;
