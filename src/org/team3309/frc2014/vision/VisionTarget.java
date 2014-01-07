package org.team3309.frc2014.vision;

import org.opencv.core.Point;

import java.util.List;

/**
 * Created by vmagro on 1/5/14.
 */
public class VisionTarget {

    public static final double VERTICAL_LENGTH_INCHES = 32;

    private Line vertical;
    private Line horizontal;
    private Point point;

    public static VisionTarget make(List<Line> lines) {
        Line vertical = null, horizontal = null;
        for (Line l : lines) {
            if (l.isHorizontal()) {
                if (horizontal != null) {
                    System.err.println("Multiple horizontal lines");
                    return null;
                } else {
                    horizontal = l;
                }
            }
            if (l.isVertical()) {
                if (vertical != null) {
                    System.err.println("Multiple vertical lines");
                    //return null;
                } else {
                    vertical = l;
                }
            }
        }
        VisionTarget target = new VisionTarget(vertical, horizontal);
        return target;
    }

    public VisionTarget(Line vertical, Line horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;

        if (vertical == null)
            System.err.println("No vertical line");
        if (horizontal == null)
            System.err.println("No horizontal line");

        if (horizontal != null && vertical != null) {
            point = new Point(vertical.getAverageX(), horizontal.getAverageY());
        } else if (vertical != null) {
            point = new Point(vertical.getAverageX(), vertical.getTop().y);
        }
    }

    public boolean isLeft() {
        return !isRight();
    }

    public boolean isRight() {
        if (horizontal != null && vertical != null)
            return horizontal.getAverageX() > vertical.getAverageX();
        return true;
    }

    public boolean isHot() {
        return horizontal != null && vertical != null;
    }

    public Point getPoint() {
        return point;
    }

    public double getTargetingX() {
        VisionConfig c = VisionConfig.getInstance();
        return (point.x - (c.getImageWidth() / 2)) / (c.getImageWidth() / 2);
    }

    public double getTargetingY() {
        VisionConfig c = VisionConfig.getInstance();
        return -1 * (point.y - (c.getImageHeight() / 2)) / (c.getImageHeight() / 2);
    }

    public Line getHorizontal() {
        return horizontal;
    }

    public Line getVertical() {
        return vertical;
    }

    public double distance() {
        VisionConfig c = VisionConfig.getInstance();
        double lengthPix = vertical.length();
        return VERTICAL_LENGTH_INCHES * c.getImageHeight() / (2 * lengthPix * Math.tan(Math.toDegrees(c.getVerticalFov())));
    }

    public double azimuth() {
        VisionConfig c = VisionConfig.getInstance();
        double dist = distance();
        //TODO actually compute stuff here
        return 0;
    }

    public String toString() {
        String s = "Target: ";
        if (isLeft())
            s += "left";
        else
            s += "right";
        s += " ";
        if (isHot())
            s += "hot";
        else
            s += "not hot";
        return s;
    }

}
