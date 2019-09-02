program AoaSpeedCheck3;
  inputs
    aoa, SpeedCheck.va, SpeedCheck.vg, SpeedCheck.mode (t) using closest(t);
  constants
    V_CRUISE    = 110;
    AOA_NORMAL  = 0.10 * V_CRUISE;
    MPS2KNOT    = 1.94384;
    K1          = 2.90094;
    K2          = 0.00024;
    K3          = 0.00108;
  outputs
    SpeedCheck.va, SpeedCheck.vg, aoa, mode at every 1 sec;
  errors
    e2: SpeedCheck.va - MPS2KNOT * sqrt(K1/(K2*aoa + K3));  
  modes
    m0: SpeedCheck.mode == 0 and abs(e2) <  AOA_NORMAL  "Normal";
    m1: SpeedCheck.mode == 0 and abs(e2) >= AOA_NORMAL  "AoA sensor failure"
        estimate aoa = (1/K2)*((MPS2KNOT^2/SpeedCheck.va^2)*K1 - K3);      
    m2: SpeedCheck.mode == 1 and abs(e2) <  AOA_NORMAL  "Pitot tube failure";
    m3: SpeedCheck.mode == 1 and abs(e2) >= AOA_NORMAL  "Pitot tube + AoA sensor failure"
        estimate aoa = (1/K2)*((MPS2KNOT^2/SpeedCheck.va^2)*K1 - K3);            
    m4: SpeedCheck.mode == 2 and abs(e2) <  AOA_NORMAL  "GPS failure";
    m5: SpeedCheck.mode == 2 and abs(e2) >= AOA_NORMAL  "GPS + AoA sensor failure"
        estimate aoa = (1/K2)*((MPS2KNOT^2/SpeedCheck.va^2)*K1 - K3);                  
   m11: SpeedCheck.mode == 3                            "GPS + Pitot tube (+ AoA sensor) failure";
   m12: SpeedCheck.mode == -1 and abs(e2) <  AOA_NORMAL "(GPS) failure";
   m13: SpeedCheck.mode == -1 and abs(e2) >= AOA_NORMAL "Unknown failure";
end;
