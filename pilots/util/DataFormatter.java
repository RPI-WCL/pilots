package pilots.util;

import java.io.*;
import java.text.*;
import java.util.*;
import pilots.runtime.*;

public class DataFormatter {
    // assuming '[time]\SP[data]' input

    private String varName;
    private String inputFile;
    private DateFormat dateFormat;
    private Date now;
    private long baseTime;
    // private int outputFrequencyMsec;

    public DataFormatter(String varName, String inputFile, String baseDateStr) {
        this.varName = varName;
        this.inputFile = inputFile;
        this.dateFormat = new SimpleDateFormat(SpatioTempoData.datePattern);
        try {
            this.now = this.dateFormat.parse(baseDateStr);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        baseTime = this.now.getTime();
        // outputFrequencyMsec = outputFrequencyMsec;
    } 


    public void format() {
        String outputFile = varName + ".txt";

		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
            out.println("#" + varName);
            
            String str = null;
            while ((str = in.readLine()) != null) {
                String[] data = str.split(",");
                double timeSec = Double.parseDouble(data[0]);  // assuming data[0] is time
                long timeMsec = (long)(timeSec * 1000);
                now.setTime(baseTime + timeMsec);

                String timeStr = dateFormat.format(now);
                out.print(":" + timeStr + ":");
                for (int i = 1; i < data.length; i++) {
                    if (i == data.length - 1)
                        out.println(data[i]);
                    else
                        out.print(data[i] + ",");
                }
            }

            out.close();
            in.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }


    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java DataFormatter <var name> <input file> <base date>");
            return;
        }

        DataFormatter starter = new DataFormatter(args[0], args[1], args[2]);
        starter.format();
    }
}
