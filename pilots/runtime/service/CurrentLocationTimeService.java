package pilots.runtime.service;

import java.util.Date;

public interface CurrentLocationTimeService {
    
    public Date getTime();
    public double[] getLocation();
    
    // for simulation
    public void progressTime( long offset ); // offset in msec
    public boolean isEndTime();
}