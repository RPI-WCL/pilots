package pilots.runtime;

import java.util.*;
import java.text.*;
import pilots.runtime.Dimension;


public class SpatioTempoData {
    public static String datePattern = "yyyy-MM-dd HHmmssSSSZ";
    public static String timeZoneID = "America/New_York";

    private static int currentId = 0;

    private int id;
    private double[][] locations;
    private Dimension dimension;
    private boolean isLocationInterval;
    private boolean hasLocations;

    private DateFormat dateFormat;     // not thread safe
    private Date[] times;
    private boolean isTimeInterval;
    private boolean hasTimes;

    private List<Double> values;

    private double dist; // used for calculating 1-D/euclidean distance

    public SpatioTempoData() {
        id = currentId++;

        locations = null;
        dimension = null;
        isLocationInterval = false;
        hasLocations = false;

        dateFormat = new SimpleDateFormat(datePattern);
        times = null;
        isTimeInterval = false;
        hasTimes = false;
        TimeZone.setDefault(TimeZone.getTimeZone(timeZoneID));

        values = new ArrayList<>();

        dist = 0.0;
    }

    public SpatioTempoData(String str) {
        id = currentId++;

        locations = null;
        dimension = null;
        isLocationInterval = false;
        hasLocations = false;

        dateFormat = new SimpleDateFormat(datePattern);
        times = null;
        isTimeInterval = false;
        hasTimes = false;
        TimeZone.setDefault(TimeZone.getTimeZone(timeZoneID));

        values = new ArrayList<>();

        dist = 0.0;

        if (!parse(str)) {
            System.err.println("parse failed");
        }
    }

    public double calcLocationDiff(int coord, double base) {
        // calculate the difference between the given base and locations

        double diff = 0.0;
        if (isLocationInterval) {
            diff = Math.min(Math.abs(locations[0][coord] - base),
                             Math.abs(locations[1][coord] - base));
        }
        else {
            diff = Math.abs(locations[0][coord] - base);
        }

        return diff;
    }

    public long calcTimeDiff(Date base) {
        // calculate the time difference between the given base and time

        long diff = 0;
        if (isTimeInterval) {
            diff = Math.min(Math.abs(times[0].getTime() - base.getTime()),
                             Math.abs(times[1].getTime() - base.getTime()));
        }
        else {
            diff = Math.abs(times[0].getTime() - base.getTime());
        }

        return diff;
    }


    // format
    // "(<Double>(,<Double>)?(,<Double>)?)?:<Date>(~<Date>)?:(<Double>(,<Double>)*)?"
    // e.g. <Date> -> yyyy-MM-dd HHmmssSSSZ
    public boolean parse(String str) {

        String[] data = str.split(":");

        // for (int i = 0; i < data.length; i++) 
        //     System.out.println(data[i]);

        // Spatio part
        if (0 < data[0].length()) {
            String[] locationStr = data[0].split("~");
            if (2 < locationStr.length) {
                System.err.println("Invalid location length: " + locationStr.length);
                return false;
            }
            isLocationInterval = (locationStr.length == 2) ? true : false;

            for (int i = 0; i < locationStr.length; i++) {
                // check spatial dimension
                String[] dimensionStr = locationStr[i].split(",");
                if (Dimension.MAX_SPATIAL_DIMENSION < dimensionStr.length) {
                    System.err.println("Invalid dimension length: " + dimensionStr.length);
                    return false;
                }
                dimension = new Dimension(dimensionStr.length);

                // alloc locations
                locations = (isLocationInterval) ? 
                    new double[2][dimensionStr.length] : 
                    new double[1][dimensionStr.length];

                for (int j = 0; j < dimensionStr.length; j++) {
                    locations[i][j] = Double.parseDouble(dimensionStr[j]);
                }
            }
            hasLocations = true;
        }

        // Temporal part
        if (0 < data[1].length()) {
            String[] timeStr = data[1].split("~");
            if (2 < timeStr.length) {
                System.err.println("Invalid time length: " + timeStr.length);
                return false;
            }
            isTimeInterval = (timeStr.length == 2) ? true : false;
        
            // alloc times
            times = (isTimeInterval) ? new Date[2] : new Date[1];

            for (int i = 0; i < timeStr.length; i++) {
                try {
                    // dateFormat = new SimpleDateFormat("yyyy-MM-dd HHMMZ");
                    times[i] = dateFormat.parse(timeStr[i]);
                } catch (ParseException e) {
                    System.out.println(e);
                }
            }
            hasTimes = true;
        }

        // Value part
        if (0 < data[2].length()) {
            String[] valueStr = data[2].split(",");
            for (int i = 0; i < valueStr.length; i++)
                values.add(Double.parseDouble(valueStr[i]));
        }

        return true;
    }


    public double getData(int varIndex) {
        Double d = values.get(varIndex);
        return d.doubleValue();
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }


    public void print() {
        System.out.println(marshal());
        System.out.println(" (dist=" + dist + ")");
        //System.out.println();
    }

    public Date[] getTimes() {
        return times;
    }

    public List<Double> getValues() {
        return values;
    }

    public boolean hasLocations() {
        return hasLocations;
    }

    public boolean hasTimes() {
        return hasTimes;
    }


    // marshal generates a string, which can be parsed back to the same spatio 
    // tempo data.
    public String marshal(){
        StringBuilder builder = new StringBuilder();
        if (locations != null) {
            switch(dimension.getDim()) {
            case Dimension.ONE_DIMENSION:
                builder.append(locations[0][0]);
                break;
            case Dimension.TWO_DIMENSION:
                builder.append(locations[0][0] + "," + locations[0][1]);
                break;
            case Dimension.THREE_DIMENSION:
                builder.append(locations[0][0] + "," + locations[0][1] + "," + locations[0][2]);
                break;
            }
            if (isLocationInterval) {
                builder.append("~");
                switch(dimension.getDim()) {
                case Dimension.ONE_DIMENSION:
                    builder.append(locations[0][0]);
                    break;
                case Dimension.TWO_DIMENSION:
                    builder.append(locations[0][0] + "," + locations[0][1]);
                    break;
                case Dimension.THREE_DIMENSION:
                    builder.append(locations[0][0] + "," + locations[0][1] + "," + locations[0][2]);
                    break;
                }
            }
        }
        
        if (times != null) {
            builder.append(":" + dateFormat.format(times[0]));
            if (isTimeInterval)
                builder.append("~" + dateFormat.format(times[1]));
        }

        builder.append(":");
        for (int i = 0; i < values.size(); i++) {
            Double d = values.get(i);
            builder.append(d);
            if ((1 < values.size()) && (i < (values.size() - 1))) 
                builder.append(",");
        }

        return builder.toString();
    }

    public String toString() {
        // skip locations for now
        
        String str = "";
        if (times.length == 2)
            str += times[0] + "~" + times[1] + ":";
        else if (times.length ==1)
            str += times[0] + ":";

        for (int i= 0; i < values.size(); i++) {
            if (i == values.size() - 1) 
                str += values.get(i);
            else
                str += values.get(i) + ",";
        }

        return str;
    }

}
    




