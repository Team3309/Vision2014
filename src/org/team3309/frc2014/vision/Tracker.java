package org.team3309.frc2014.vision;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmagro on 1/5/14.
 */
public class Tracker {

    public static Mat hue, sat, val, erode, dilate, thresholded, result;

    /**
     * This method performs a threshold on the 3 separate channels of an HSV image.
     * Threshold values are retrieved from VisionConfig
     *
     * @param hsv
     * @param threshold
     * @param w
     * @return
     */
    private static Mat threshold(Mat hsv, TrackingConfig threshold, CalibrationWindow w) {
        ArrayList<Mat> split = new ArrayList<Mat>();
        Core.split(hsv, split);

        hue = split.get(0);
        sat = split.get(1);
        val = split.get(2);

        Mat hueBin = Mat.zeros(hue.size(), CvType.CV_8UC1);
        Mat satBin = Mat.zeros(val.size(), CvType.CV_8UC1);
        Mat valBin = Mat.zeros(val.size(), CvType.CV_8UC1);

        VisionConfig c = VisionConfig.getInstance();

        Core.inRange(hue, new Scalar(threshold.getHueMin()), new Scalar(threshold.getHueMax()), hueBin);
        Core.inRange(sat, new Scalar(threshold.getSatMin()), new Scalar(threshold.getSatMax()), satBin);
        Core.inRange(val, new Scalar(threshold.getValMin()), new Scalar(threshold.getValMax()), valBin);

        //combine all of the thresholded channels into a single Mat
        Mat bin = hueBin.clone();
        Core.bitwise_and(satBin, bin, bin);
        Core.bitwise_and(valBin, bin, bin);

        hue = hueBin;
        sat = satBin;
        val = valBin;

        if (w != null) {
            w.showHue(hueBin);
            w.showSat(satBin);
            w.showVal(valBin);
            w.showThreshold(bin);
        }

        return bin;
    }

    /**
     * This method performs an erosion and then a dilation on the specified binary image
     *
     * @param bin
     * @param t
     * @param w
     */
    private static void erodeAndDilate(Mat bin, TrackingConfig t, CalibrationWindow w) {
        VisionConfig c = VisionConfig.getInstance();

        Mat elementErosion = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                new Size(2 * t.getErosion() + 1, 2 * t.getErosion() + 1),
                new Point(t.getErosion(), t.getErosion()));
        // Apply the erosion operation
        // Erosion makes the white parts of the image smaller, which removes some small noise particles
        Imgproc.erode(bin, bin, elementErosion);
        erode = bin.clone();
        if (w != null)
            w.showErosion(bin);

        Mat elementDilation = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                new Size(2 * t.getDilation() + 1, 2 * t.getDilation() + 1),
                new Point(t.getDilation(), t.getDilation()));
        // Apply the dilation operation
        // Dilation makes the white parts of the image larger, which fixes some irregularities that may
        // have been caused by the erosion operation
        Imgproc.dilate(bin, bin, elementDilation);
        dilate = bin.clone();
        if (w != null)
            w.showDilation(bin);
    }

    private static List<MatOfPoint> findContours(Mat bin) {
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(bin, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private static void drawContours(List<MatOfPoint> contours, Mat img) {
        Scalar[] colors = new Scalar[]{new Scalar(255, 0, 0), new Scalar(0, 255, 255), new Scalar(0, 0, 255)};
        int colorIndex = 0;
        for (MatOfPoint contour : contours) {
            Point[] points = contour.toArray();
            System.out.println("Contour " + colorIndex + " has " + points.length + " points");
            for (int i = 0; i < points.length - 1; i++) {
                Core.line(img, points[i], points[i + 1], colors[colorIndex % colors.length], 5);
            }
            colorIndex++;
        }
    }

    private static void drawContour(MatOfPoint contour, Mat img) {
        Point[] points = contour.toArray();
        for (int i = 0; i < points.length - 1; i++) {
            Core.line(img, points[i], points[i + 1], new Scalar(255, 255, 255), 5);
        }
    }

    private static List<Line> getLines(MatOfPoint matOfPoint) {
        MatOfPoint2f points2f = new MatOfPoint2f(matOfPoint.toArray());

        MatOfPoint2f approx = new MatOfPoint2f();
        Imgproc.approxPolyDP(points2f, approx, 10, true);

        Point[] points = approx.toArray();
        ArrayList<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < points.length - 1; i++) {
            Line l = new Line(points[i], points[i + 1]);
            lines.add(l);
        }

        return lines;
    }

    private static void drawLine(Mat img, Line line, int thickness, Scalar color) {
        if (line != null)
            Core.line(img, line.getP1(), line.getP2(), color, thickness);
    }

    private static void drawGoal(Mat img, Goal goal) {
        /*Scalar color = goal.isLeft() ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255);
        drawLine(img, goal.getTop(), 5, color);
        drawLine(img, goal.getBottom(), 5, color);
        drawLine(img, goal.getSide(), 5, color);*/
    }

    private static void drawRectangle(Mat img, RotatedRect r, Scalar color) {
        Core.rectangle(img, r.boundingRect().tl(), r.boundingRect().br(), color, -1);
    }

    private static void drawRectangle(Mat img, Rect r, Scalar color) {
        Core.rectangle(img, r.tl(), r.br(), color, -1);
    }

    private static void drawTarget(Mat img, VisionTarget target) {
        Scalar color = target.isHot() ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255);
        drawRectangle(img, target.getVertical(), color);
        if (target.getHorizontal() != null)
            drawRectangle(img, target.getHorizontal(), color);
    }

    public static List<Goal> findGoals(Mat img, CalibrationWindow window) {
        Mat hsv = new Mat(img.size(), CvType.CV_8UC3);
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);
        Mat bin = threshold(hsv, VisionConfig.getInstance().getGoalThreshold(), window);
        window.showThreshold(bin.clone());

        erodeAndDilate(bin, VisionConfig.getInstance().getGoalThreshold(), window);

        List<MatOfPoint> contours = findContours(bin);
        System.out.println("Found " + contours.size() + " contours");

        ArrayList<Rect> hBoxes = new ArrayList<Rect>();
        ArrayList<Rect> vBoxes = new ArrayList<Rect>();

        //separate out the horizontal and vertical boxes, ignore everything else
        for (MatOfPoint contour : contours) {
            MatOfPoint2f points2f = new MatOfPoint2f(contour.toArray());
            RotatedRect rect = Imgproc.minAreaRect(points2f);
            if (rect.boundingRect().area() > 75) {
                if (Util.isHorizontal(rect)) {
                    hBoxes.add(rect.boundingRect());
                } else if (Util.isVertical(rect)) {
                    vBoxes.add(rect.boundingRect());
                } else {
                    drawRectangle(img, rect, new Scalar(255, 0, 0));
                }
            }
        }

        System.out.printf("Found %d horizontal boxes\n", hBoxes.size());
        System.out.printf("Found %d vertical boxes\n", vBoxes.size());

        Rect[] longestH = new Rect[2];
        if (hBoxes.size() > 1) {
            longestH[0] = hBoxes.get(0);
            longestH[1] = hBoxes.get(1);
        } else if (hBoxes.size() > 0)
            longestH[0] = hBoxes.get(0);

        for (Rect rect : hBoxes) {
            //bigger than the longest horizontal box
            if (rect.width > longestH[0].width) {
                longestH[1] = longestH[0];
                longestH[0] = rect;
                continue;
            }
            //not the longest but bigger than the second longest
            else if (rect.width > longestH[1].width)
                longestH[1] = rect;
        }

        ArrayList<Goal> goals = new ArrayList<Goal>();

        for (Rect r : longestH) {
            if (r != null) {
                drawRectangle(img, r, new Scalar(0, 255, 0));
                goals.add(new Goal(r));
            }
        }

        System.out.println("Found " + goals.size() + " goals");

        result = img;
        if (window != null)
            window.showResult(img);

        return goals;
    }

    public static List<VisionTarget> findTargets(Mat img, CalibrationWindow window) {
        Mat hsv = new Mat(img.size(), CvType.CV_8UC3);
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);
        thresholded = threshold(hsv, VisionConfig.getInstance().getVisionTargetThreshold(), window);
        Mat bin = thresholded.clone();

        Mat erodeAndDilate = bin.clone();
        erodeAndDilate(erodeAndDilate, VisionConfig.getInstance().getVisionTargetThreshold(), window);
        Core.bitwise_and(erodeAndDilate, bin, bin);

        List<MatOfPoint> contours = findContours(bin);

        ArrayList<RotatedRect> hBoxes = new ArrayList<RotatedRect>();
        ArrayList<RotatedRect> vBoxes = new ArrayList<RotatedRect>();

        //separate out the horizontal and vertical boxes, ignore everything else
        for (MatOfPoint contour : contours) {
            MatOfPoint2f points2f = new MatOfPoint2f(contour.toArray());
            RotatedRect rect = Imgproc.minAreaRect(points2f);
            if (rect.boundingRect().area() > 150) {
                if (Util.isHorizontal(rect)) {
                    hBoxes.add(rect);
                } else if (Util.isVertical(rect)) {
                    vBoxes.add(rect);
                } else {
                    drawRectangle(img, rect, new Scalar(255, 0, 0));
                }
            }
        }

        //System.out.println("Found " + hBoxes.size() + " horizontal boxes");
        //System.out.println("Found " + vBoxes.size() + " vertical boxes");

        ArrayList<VisionTarget> targets = new ArrayList<VisionTarget>();

        for (RotatedRect vrect : vBoxes) {
            //find the closest horizontal box to the current vertical box
            RotatedRect closestH = null;
            for (RotatedRect hrect : hBoxes) {
                if (closestH != null) {
                    if (Util.distance(vrect.center, hrect.center) < Util.distance(vrect.center, closestH.center))
                        closestH = hrect;
                } else {
                    closestH = hrect;
                }
            }

            //create a list to store the rectangles that make up the target
            ArrayList<RotatedRect> targetRects = new ArrayList<RotatedRect>();
            //add the vertical box to the list
            targetRects.add(vrect);
            //add the closest horizontal box only if it is within a certain distance
            if (closestH != null && Util.distance(vrect.center, closestH.center) < 300)
                targetRects.add(closestH);
            else if (closestH != null) {
            }

            VisionTarget target = VisionTarget.make(targetRects);
            targets.add(target);
        }

        //This can override a target as the left target if, for example, the left target is not hot and therefore the
        //built-in side detection will not work
        if (targets.size() > 1) {
            for (VisionTarget target : targets) {
                for (VisionTarget otherTarget : targets) {
                    if (!otherTarget.equals(target))
                        if (target.getPoint().x < otherTarget.getPoint().x)
                            target.overrideLeft();
                }
            }
        }

        //display info about the target (on the image and in the target info pane)
        for (VisionTarget target : targets) {
            drawTarget(img, target);
            if (window != null)
                window.showTargetInfo(target);
        }

        result = img;
        if (window != null) {
            window.showResult(img);
        }

        return targets;
    }

}
