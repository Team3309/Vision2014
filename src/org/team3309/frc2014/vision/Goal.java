package org.team3309.frc2014.vision;

/**
 * Created by vmagro on 1/5/14.
 */
public class Goal {

    public static enum Fill {
        ONE_THIRD,
        TWO_THIRDS,
        FULL,
        ERROR
    }

    private static double ratioOneThird = 3.8 * 2 / 3;
    private static double ratioTwoThirds = 7.7 * 2 / 3;
    private static double ratioFull = 8 * 2 / 3;

    private double x, y;

    private Line top, bottom;

    public static Goal getGoal(Line[] lines) {
        Line top = lines[0].getAverageY() < lines[1].getAverageY() ? lines[0] : lines[1];
        Line bottom = lines[0].getAverageY() > lines[1].getAverageY() ? lines[0] : lines[1];
        return new Goal(top, bottom);
    }

    private Goal(Line top, Line bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public Line getTop() {
        return top;
    }

    public Line getBottom() {
        return bottom;
    }

    public Fill getFill() {
        if (top == null || bottom == null)
            return Fill.ERROR;
        double ratio = top.length() / Math.abs(top.getAverageY() - bottom.getAverageY());
        System.out.println(ratio);
        if (Math.abs(ratio - ratioOneThird) < Math.abs(ratio - ratioTwoThirds) && Math.abs(ratio - ratioOneThird) < Math.abs(ratio - ratioFull)) {
            return Fill.ONE_THIRD;
        }
        if (Math.abs(ratio - ratioTwoThirds) < Math.abs(ratio - ratioOneThird) && Math.abs(ratio - ratioTwoThirds) < Math.abs(ratio - ratioFull)) {
            return Fill.TWO_THIRDS;
        }
        if (Math.abs(ratio - ratioFull) < Math.abs(ratio - ratioOneThird) && Math.abs(ratio - ratioFull) < Math.abs(ratio - ratioTwoThirds)) {
            return Fill.FULL;
        }
        return Fill.ERROR;
    }

    public String toString() {
        String s = "Goal: ";
        switch (getFill()) {
            case ERROR:
                s += "invalid";
                break;
            case ONE_THIRD:
                s += "1/3";
                break;
            case TWO_THIRDS:
                s += "2/3";
                break;
            case FULL:
                s += "full";
                break;
        }
        return s;
    }

}
