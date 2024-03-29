package org.team3309.frc2014.vision;

import org.opencv.core.Point;
import org.opencv.core.RotatedRect;

import java.util.List;

/**
 * Created by vmagro on 1/5/14.
 */
public class VisionTarget {

    public static final double VERTICAL_LENGTH_INCHES = 32;
    public static final double HORIZONTAL_LENGTH_INCHES = 23.5;

    private RotatedRect vertical;
    private RotatedRect horizontal;
    private Point point;

    private boolean overrideSide = false;
    private boolean overrideRight = false;

    public static VisionTarget make(List<RotatedRect> rects) {
        RotatedRect vertical = null, horizontal = null;
        for (RotatedRect r : rects) {
            if (Util.isHorizontal(r))
                horizontal = r;
            if (Util.isVertical(r))
                vertical = r;
        }
        VisionTarget target = new VisionTarget(vertical, horizontal);
        return target;
    }

    public VisionTarget(RotatedRect vertical, RotatedRect horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;

        if (horizontal != null && vertical != null) {
            point = new Point(vertical.center.x, horizontal.center.y);
        } else if (vertical != null) {
            point = new Point(vertical.center.x, vertical.boundingRect().tl().y);
        }
    }

    public boolean isLeft() {
        return !isRight();
    }

    public boolean isRight() {
        if (overrideSide)
            return overrideRight;
        if (horizontal != null && vertical != null)
            return horizontal.center.x > vertical.center.x;
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

    public RotatedRect getHorizontal() {
        return horizontal;
    }

    public RotatedRect getVertical() {
        return vertical;
    }

/*
    public double distance() {
        VisionConfig c = VisionConfig.getInstance();
        //the longer dimension is the height, so let's make sure that we get the longer dimension here
        double targetPx = vertical.boundingRect().height > vertical.boundingRect().width ?
                vertical.boundingRect().height : vertical.boundingRect().width;
        double distance = (VERTICAL_LENGTH_INCHES * (c.getImageHeight() / 2)) / (targetPx * Math.tan(Math.toRadians(c.getVerticalFov())));
        return distance;
    }
*/
    public double distance() {
        VisionConfig c = VisionConfig.getInstance();
    //the longer dimension is the height, so let's make sure that we get the longer dimension here
        double targetPx = vertical.boundingRect().height > vertical.boundingRect().width ?
            vertical.boundingRect().height : vertical.boundingRect().width;
        double frameLength = (VERTICAL_LENGTH_INCHES/targetPx)*c.getImageHeight();
        double distance = frameLength/(2*Math.tan((c.getVerticalFov()/2)*(Math.PI/180)));
        return distance;
    }

/*    public double azimuth() {
        VisionConfig c = VisionConfig.getInstance();
        double dist = distance();
        double targetPx = vertical.boundingRect().height > vertical.boundingRect().width ?
                vertical.boundingRect().height : vertical.boundingRect().width;
        double pixToIn = VERTICAL_LENGTH_INCHES / targetPx;
        double distFromCenter = (vertical.boundingRect().tl().x - (c.getImageWidth() / 2)) * pixToIn;
        return Math.atan2(dist, distFromCenter);
    }*/

    public double azimuth() {
        double targetAngle = 9999;
        double horizontalPx = 0;
        VisionConfig c = VisionConfig.getInstance();
        double verticalPx = vertical.boundingRect().height > vertical.boundingRect().width ?
                vertical.boundingRect().height : vertical.boundingRect().width;

        if(horizontal != null)
        {
            horizontalPx = horizontal.boundingRect().width > horizontal.boundingRect().height ?
                horizontal.boundingRect().width : horizontal.boundingRect().height;
        }

        if((horizontalPx != 0) && (verticalPx != 0))
        {
            double expectedHorizontalPx = verticalPx*HORIZONTAL_LENGTH_INCHES/VERTICAL_LENGTH_INCHES;
            targetAngle = Math.acos(horizontalPx/expectedHorizontalPx)*(180/Math.PI);

        }
        return targetAngle;
    }

    public void overrideLeft() {
        overrideSide = true;
        overrideRight = false;
    }

    public void overrideRight() {
        overrideSide = true;
        overrideRight = true;
    }

    public void clearSideOverride() {
        overrideSide = false;
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
