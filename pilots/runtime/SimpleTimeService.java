package pilots.runtime;

import java.util.Date;
import java.util.Calendar;

public class SimpleTimeService implements CurrentLocationTimeService {
    private Date then;
    private long baseTime;

    public SimpleTimeService() {
        this.then = null;
        this.baseTime = 0;
    }
    

    public SimpleTimeService( Date base ) {
        this.then = new Date();
        this.baseTime = base.getTime();
    }
    
    public Date getTime() {
        Date now = new Date();
        
        if (0 < baseTime ) {
            // calculate time based on baseTime
            long diff = now.getTime() - then.getTime();
            now.setTime(diff + baseTime);
        }
        
        return now;
    }

    public double[] getLocation() {
        return null;
    }

    public void progressTime(long offset) {
        return;
    }

    public boolean isEndTime() {
        return false;
    }
}
