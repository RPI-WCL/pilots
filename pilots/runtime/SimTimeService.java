package pilots.runtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.Logger;

import pilots.runtime.*;


public class SimTimeService implements CurrentLocationTimeService {
    private static Logger LOGGER = Logger.getLogger(SimTimeService.class.getName());
    
    private Date now, end;

    public SimTimeService() {
        String timeRange = System.getProperty("timeRange");
        if (timeRange == null) {
            // should throw an exception here
            LOGGER.severe("Need \"timeRange\" property to be set!!!");
            return;
        }

        String[] timeRanges = timeRange.split("~");

        String datePattern = "yyyy-MM-dd HHmmssZ";
        DateFormat dateFormat = new SimpleDateFormat(datePattern);

        Date[] time = new Date[2];
        for (int i = 0; i < 2; i++) {
            if (timeRanges[i] != null) {
                time[i] = new Date();
                try {
                    time[i] = dateFormat.parse(timeRanges[i]);
                    LOGGER.finest("timeRanges[" + i + "]=" + timeRanges[i]
                                  + ", time[" + i + "]=" + time[i]);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        }
        now = time[0];
        end  = time[1];
    }
    
    public Date getTime() {
        return now;
    }

    public double[] getLocation() {
        String[] varNames = { "x", "y", "z" };
        double[] locations = new double[3];
        Method[] methods = { new Method(Method.INTERPOLATE, "t", "2") };

        for (int i = 0; i < 3; i++) {
            DataStore store = DataStore.findStore(varNames[i]);
            if (store != null) {
                locations[i] = store.getData(varNames[i], methods);
            }
            else {
                LOGGER.warning("No matching variable stored for \"" + varNames[i] + "\"");
            }
        }

        return locations;
    }

    public void progressTime(long offset) { // offset in msec
        now.setTime(now.getTime() + offset);
    }

    public boolean isEndTime() {
        return (0 < now.compareTo(end));
    }        
}
