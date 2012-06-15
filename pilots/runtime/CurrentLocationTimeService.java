package pilots.runtime;

import java.util.Date;

public interface CurrentLocationTimeService {
    
    public Date getTime();
    public double[] getLocation();

}