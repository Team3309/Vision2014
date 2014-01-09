package org.team3309.frc2014.vision;

import org.opencv.core.Rect;

/**
 * Created by vmagro on 1/5/14.
 */
public class Goal {

    private static final int VERTICAL_LENGTH_INCHES = 37; //3 feet, 1 inch

    public static enum Fill {
        ONE_THIRD,
        TWO_THIRDS,
        FULL,
        ERROR
    }

    private static double ratioOneThird = 3.8 / 3;
    private static double ratioTwoThirds = 7.7 / 3;
    private static double ratioFull = 8 / 3;

    private Rect rect;

    public Goal(Rect rect) {
        this.rect = rect;
    }

    public double distance() {
        VisionConfig c = VisionConfig.getInstance();
        //the longer dimension is the height, so let's make sure that we get the longer dimension here
        double targetPx = rect.height;
        double distance = (VERTICAL_LENGTH_INCHES * (c.getImageHeight() / 2)) / (targetPx * Math.tan(Math.toRadians(c.getVerticalFov())));
        return distance;
    }

    public Fill getFill() {
        if (rect == null)
            return Fill.ERROR;
        double ratio = rect.width / rect.height;
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
        s += " distance = " + distance();
        return s;
    }

}
