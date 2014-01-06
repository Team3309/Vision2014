package org.team3309.frc2014.vision;

import java.util.List;

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

    private static double ratioOneThird = 3.8 / 3;
    private static double ratioTwoThirds = 7.7 / 3;
    private static double ratioFull = 1;

    private double x, y;

    private Line top, bottom, side;

    public static Goal getGoal(List<Line> lines) {
        if (lines.size() < 3)
            return null;
        Line top = null, bottom = null, side = null;
        for (Line l : lines) {
            if (l.isHorizontal()) {
                if (top != null && top.getAverageY() > l.getAverageY())
                    bottom = l;
                else if (top != null && top.getAverageY() < l.getAverageY()) {
                    bottom = top;
                    top = l;
                } else
                    top = l;
            } else
                side = l;
        }
        return new Goal(top, bottom, side);
    }

    private Goal(Line top, Line bottom, Line side) {
        this.top = top;
        this.bottom = bottom;
        this.side = side;
    }

    public Line getTop() {
        return top;
    }

    public Line getBottom() {
        return bottom;
    }

    public Line getSide() {
        return side;
    }

    public Fill getFill() {
        double ratio = top.length() / side.length();
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

    public boolean isLeft() {
        return !isRight();
    }

    public boolean isRight() {
        return (side.getAverageX() < top.getAverageX() && side.getAverageX() < bottom.getAverageX());
    }

    public String toString() {
        String s = "Goal: ";
        if (isLeft())
            s += "left";
        else
            s += "right";
        s += " ";
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
