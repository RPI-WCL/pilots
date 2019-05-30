package pilots.runtime.errsig;

import java.util.*;
import pilots.runtime.errsig.Constraint;

public class ErrorSignature {
    public static final int CONST     = 0;
    public static final int LINEAR    = 1;

    private int type; // CONST or LINEAR
    private double value; // the value of a constant or slope
    private String desc;
    private List<Constraint> constraints;
    private List<Interval> intervals;

    public ErrorSignature(int type, double value, String desc) {
        this.type = type;
        this.value = value;
        this.desc = desc;
        this.constraints = null;
        this.intervals = null;
    }

    public ErrorSignature(int type, double value, String desc, 
                          List<Constraint> constraints) {
        this.type = type;
        this.value = value;
        this.desc = desc;
        this.constraints = constraints;
        this.intervals = new ArrayList<Interval>();
        for (int i = 0; i < constraints.size(); i++) {
            Constraint c = constraints.get(i);
            Interval interval1 = new Interval(c);
            boolean intersects = false;

            for (int j = 0; j < intervals.size(); j++) {
                Interval interval2 = intervals.get(j);
                if (interval1.intersects(interval2)) {
                    // System.out.println(interval1 + " intersects " + interval2);
                    interval2.merge(interval1);
                    // System.out.println("merged=" + interval2);
                    intersects = true;
                }
            }

            if (!intersects)
                intervals.add(interval1);
        }

        // for (int i = 0; i < intervals.size(); i++) {
        //     Interval interval = intervals.get(i);
        //     System.out.println(interval);
        // }
    }

    public boolean isConstrained() {
        return (constraints != null && 0 < constraints.size());
    }

    private boolean isInInterval(double point) {
        boolean flag = false;

        for (int i = 0; i < intervals.size(); i++) {
            Interval interval = intervals.get(i);
            if (interval.contains(point)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public double getClosestEndPoint(double point) {
        double minDist = Double.MAX_VALUE;
        double closest = 0;

        if (isInInterval(point)) {
            // the point is in one of the intervals
            closest = point;
        }
        else {
            // the point is NOT in the intervals
            for (int i = 0; i < intervals.size(); i++) {
                Interval interval = intervals.get(i);
                double[] endPoints = interval.getEndPoints();
                for (int j = 0; j < 2; j++) {
                    double dist = Math.abs(point - endPoints[j]);
                    if (dist < minDist) {
                        minDist = dist;
                        closest = endPoints[j];
                    }
                }
            }
        }

        return closest;
    }

    public int getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static void main(String[] args) {
        // List<Constraint> constraints = new ArrayList<>();
        // constraints.add(new Constraint(Constraint.GREATER_THAN, -10.0));
        // constraints.add(new Constraint(Constraint.LESS_THAN, 10.0));
        // constraints.add(new Constraint(Constraint.GREATER_THAN_OR_EQUAL_TO, -5.0));
        // constraints.add(new Constraint(Constraint.LESS_THAN_OR_EQUAL_TO,5.0));
        // ErrorSignature errSig = new ErrorSignature(ErrorSignature.CONST, 0.0, "Pitot tube + GPS failure", constraints);

        // List<Constraint> constraints1 = new ArrayList<>();
        // constraints1.add(new Constraint(Constraint.GREATER_THAN, -50.0));
        // constraints1.add(new Constraint(Constraint.LESS_THAN, 25.0));
        // ErrorSignature errSig = new ErrorSignature(ErrorSignature.CONST, 0.0, "Normal", constraints1);

        // List<Constraint> constraints2 = new ArrayList<>();
        // constraints2.add(new Constraint(Constraint.GREATER_THAN, 50.0));
        // constraints2.add(new Constraint(Constraint.LESS_THAN, 100.0));
        // ErrorSignature errSig = new ErrorSignature(ErrorSignature.CONST, 0.0, "Pitot tube failure", constraints2);

        // List<Constraint> constraints3 = new ArrayList<>();
        // constraints3.add(new Constraint(Constraint.GREATER_THAN, -150.0));
        // constraints3.add(new Constraint(Constraint.LESS_THAN, -100.0));
        // ErrorSignature errSig = new ErrorSignature(ErrorSignature.CONST, 0.0, "GPS failure", constraints3);

        List<Constraint> constraints4 = new ArrayList<>();
        constraints4.add(new Constraint(Constraint.GREATER_THAN, -100.0));
        constraints4.add(new Constraint(Constraint.LESS_THAN, -50.0));
        ErrorSignature errSig = new ErrorSignature(ErrorSignature.CONST, 0.0, "Pitot tube + GPS failure", constraints4);
        
        System.out.println("closest=" + errSig.getClosestEndPoint(7.20));
    }
}
