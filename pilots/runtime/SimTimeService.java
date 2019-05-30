package pilots.runtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;

import pilots.runtime.*;


public class SimTimeService extends DebugPrint implements CurrentLocationTimeService {
    private Date now, end;

    public SimTimeService() {
        String timeSpan = System.getProperty("timeSpan");
        if (timeSpan == null) {
            // should throw an exception here
            System.err.println("Need \"timeSpan\" property to be defined!!!");
            return;
        }

        String[] timeSpans = timeSpan.split("~");

        String datePattern = "yyyy-MM-dd HHmmssZ";
        DateFormat dateFormat = new SimpleDateFormat(datePattern);

        Date[] time = new Date[2];
        for (int i = 0; i < 2; i++) {
            if (timeSpans[i] != null) {
                time[i] = new Date();
                try {
                    time[i] = dateFormat.parse(timeSpans[i]);
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
                dbgPrint("SimTimeService: no matching variable stored for \"" + varNames[i] + "\"");
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
