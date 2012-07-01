package pilots.runtime;

import java.util.Date;
import java.util.Calendar;

public class SimpleTimeService implements CurrentLocationTimeService {
    private Date then_;
    private long baseTime_;

    public SimpleTimeService() {
        then_ = null;
        baseTime_ = 0;
    }
    

    public SimpleTimeService( Date base ) {
        then_ = new Date();
        baseTime_ = base.getTime();
    }
    
    public Date getTime() {
        Date now = new Date();
        
        if (0 < baseTime_ ) {
            // calculate time based on baseTime_
            long diff = now.getTime() - then_.getTime();
            now.setTime( diff + baseTime_ );
        }
        
        return now;
    }

    public double[] getLocation() {
        return null;
    }

    public void progressTime( long offset ) {
        return;
    }

    public boolean isEndTime() {
        return false;
    }
}