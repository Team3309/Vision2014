package org.team3309.frc2014.vision;

import org.opencv.core.Point;

/**
 * Created by vmagro on 1/5/14.
 */
public class Line {

    private Point p1, p2;

    public Line(Point p1, Point p2) {
        if (p1.x < p2.x) {
            this.p1 = p1;
            this.p2 = p2;
        } else {
            this.p1 = p2;
            this.p2 = p1;
        }
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public double getAverageY() {
        return (p1.y + p2.y) / 2;
    }

    public double getAverageX() {
        return (p1.x + p2.x) / 2;
    }

    public double getAngle() {
        return Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x));
    }

    public boolean isHorizontal() {
        return isHorizontal(10);
    }

    public boolean isHorizontal(double threshold) {
        return Math.abs(getAngle() % 180) < threshold;
    }

    public boolean isVertical() {
        return isVertical(10);
    }

    public boolean isVertical(double threshold) {
        return Math.abs(Math.abs(getAngle() % 180) - 90) < threshold;
    }

    public double length() {
        return Util.distance(p1, p2);
    }

    public Point center() {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    public double distance(Line other) {
        return Util.distance(center(), other.center());
    }

    public String toString() {
        return p1.toString() + " -> " + p2.toString();
    }

}
