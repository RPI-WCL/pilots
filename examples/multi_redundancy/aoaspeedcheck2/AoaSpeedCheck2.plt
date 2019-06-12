program AoaSpeedCheck2;
  inputs
    SpeedCheck.va, SpeedCheck.vg, SpeedCheck.mode, 
    AoaCheck.aoa, AoaCheck.mode (t) using closest(t);
  constants
    MPS2KNOT    = 1.94384;
    K1          = 2.90094;
    K2          = 0.00024;
    K3          = 0.00108;
  outputs
    SpeedCheck.va, SpeedCheck.vg, AoaCheck.aoa, mode at every 1 sec;
  modes
    m0: SpeedCheck.mode == 0 and AoaCheck.mode == 0
      "Normal";
    m1: SpeedCheck.mode == 0 and AoaCheck.mode == 1
      "AoA sensor failure";
    m8: SpeedCheck.mode == 0 and AoaCheck.mode == -1
      "(AoA sensor) failure"
      estimate AoaCheck.aoa = (1/K2)*((MPS2KNOT^2/SpeedCheck.va^2)*K1 - K3);            
    m9: SpeedCheck.mode == 1
      "Pitot tube + (AoA sensor) failure"
      estimate AoaCheck.aoa = (1/K2)*((MPS2KNOT^2/SpeedCheck.va^2)*K1 - K3);                  
    m4: SpeedCheck.mode == 2 and AoaCheck.mode == 0
      "GPS failure";
    m5: SpeedCheck.mode == 2 and AoaCheck.mode == 1
      "GPS + AoA sensor failure";
   m10: SpeedCheck.mode == 2 and AoaCheck.mode == -1
      "GPS + (AoA sensor) failure"
      estimate AoaCheck.aoa = (1/K2)*((MPS2KNOT^2/SpeedCheck.va^2)*K1 - K3);                        
   m11: SpeedCheck.mode == 3
      "GPS + Pitot tube + (AoA sensor) failure";
   m12: SpeedCheck.mode == -1 and AoaCheck.mode == 0
      "(GPS) failure";
   m13: SpeedCheck.mode == -1 and AoaCheck.mode == 1
      "Unknown failure";
end;
