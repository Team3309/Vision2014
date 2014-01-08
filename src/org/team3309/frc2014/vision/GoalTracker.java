package org.team3309.frc2014.vision;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmagro on 1/5/14.
 */
public class GoalTracker {

    /**
     * This method performs a threshold on the 3 separate channels of an HSV image.
     * Threshold values are retrieved from VisionConfig
     *
     * @param hsv
     * @param w
     * @return
     */
    private static Mat threshold(Mat hsv, CalibrationWindow w) {
        ArrayList<Mat> split = new ArrayList<Mat>();
        Core.split(hsv, split);

        Mat hue = split.get(0);
        Mat sat = split.get(1);
        Mat val = split.get(2);

        Mat hueBin = Mat.zeros(hue.size(), CvType.CV_8UC1);
        Mat satBin = Mat.zeros(val.size(), CvType.CV_8UC1);
        Mat valBin = Mat.zeros(val.size(), CvType.CV_8UC1);

        VisionConfig c = VisionConfig.getInstance();

        Mat bin = new Mat(hsv.size(), CvType.CV_8UC1);

        Core.inRange(hue, new Scalar(c.getHueMin()), new Scalar(c.getHueMax()), hueBin);
        Core.inRange(sat, new Scalar(c.getSatMin()), new Scalar(c.getSatMax()), satBin);
        Core.inRange(val, new Scalar(c.getValMin()), new Scalar(c.getSatMax()), valBin);

        w.showHue(hueBin);
        w.showSat(satBin);
        w.showVal(valBin);

        //combine all of the thresholded channels into a single Mat
        Core.bitwise_or(hueBin, bin, bin);
        Core.bitwise_and(satBin, bin, bin);
        Core.bitwise_and(valBin, bin, bin);

        w.showThreshold(bin);

        return bin;
    }

    private static void erodeAndDilate(Mat bin, CalibrationWindow w) {
        VisionConfig c = VisionConfig.getInstance();

        Mat elementErosion = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                new Size(2 * c.getErosionSize() + 1, 2 * c.getErosionSize() + 1),
                new Point(c.getErosionSize(), c.getErosionSize()));
        /// Apply the erosion operation
        Imgproc.erode(bin, bin, elementErosion);

        Mat elementDilation = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                new Size(2 * c.getDilationSize() + 1, 2 * c.getDilationSize() + 1),
                new Point(c.getDilationSize(), c.getDilationSize()));
        /// Apply the dilation operation
        Imgproc.dilate(bin, bin, elementDilation);

        w.showDilation(bin);

        w.showErosion(bin);
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

    private static void drawTarget(Mat img, VisionTarget target) {
        Scalar color = target.isLeft() ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255);
        int thickness = target.isHot() ? 15 : 5;
        drawRectangle(img, target.getVertical(), color);
        if (target.getHorizontal() != null)
            drawRectangle(img, target.getHorizontal(), color);
    }

    public static Goal findGoal(Mat img, CalibrationWindow window) {
        Mat hsv = new Mat(img.size(), CvType.CV_8UC3);
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);
        Mat bin = threshold(hsv, window);
        window.showThreshold(bin.clone());

        erodeAndDilate(bin, window);

        List<MatOfPoint> contours = findContours(bin);
        System.out.println("Found " + contours.size() + " contours");

        ArrayList<Line> hLines = new ArrayList<Line>();
        ArrayList<Line> vLines = new ArrayList<Line>();

        for (MatOfPoint contour : contours) {
            List<Line> lines = getLines(contour);
            for (Line l : lines) {
                if (l.isHorizontal())
                    hLines.add(l);
                if (l.isVertical())
                    vLines.add(l);
            }
        }

        System.out.printf("Found %d horizontal lines\n", hLines.size());
        System.out.printf("Found %d vertical lines\n", vLines.size());

        Line[] longestH = new Line[2];

        for (Line l : hLines) {
            //drawLine(img, l, 5, new Scalar(0, 0, 255));
            if (longestH[0] == null)
                longestH[0] = l;
            else if (longestH[1] == null)
                longestH[1] = l;
            else if (l.length() > longestH[0].length()) {
                longestH[1] = longestH[0];
                longestH[0] = l;
            } else if (l.length() > longestH[1].length())
                longestH[1] = l;
        }
        for (Line l : vLines) {
            //drawLine(img, l, 5, new Scalar(255, 0, 0));
        }

        Point center = new Point((longestH[0].getAverageX() + longestH[1].getAverageX()) / 2, (longestH[0].getAverageY() + longestH[1].getAverageY()) / 2);
        Core.circle(img, center, 10, new Scalar(0, 0, 255), -1);
        for (Line l : longestH) {
            drawLine(img, l, 5, new Scalar(0, 0, 255));
        }
        Goal goal = Goal.getGoal(longestH);
        System.out.println(goal);

        window.showResult(img);

        return null;
    }

    public static List<VisionTarget> findTarget(Mat img, CalibrationWindow window) {
        Mat hsv = new Mat(img.size(), CvType.CV_8UC3);
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);
        Mat bin = threshold(hsv, window);

        erodeAndDilate(bin, window);

        List<MatOfPoint> contours = findContours(bin);
        System.out.println("Found " + contours.size() + " contours");

        ArrayList<RotatedRect> hBoxes = new ArrayList<RotatedRect>();
        ArrayList<RotatedRect> vBoxes = new ArrayList<RotatedRect>();

        //separate out the horizontal and vertical boxes, ignore everything else
        for (MatOfPoint contour : contours) {
            MatOfPoint2f points2f = new MatOfPoint2f(contour.toArray());
            RotatedRect rect = Imgproc.minAreaRect(points2f);
            if (rect.boundingRect().area() > 75) {
                if (Util.isHorizontal(rect)) {
                    hBoxes.add(rect);
                } else if (Util.isVertical(rect)) {
                    vBoxes.add(rect);
                }
            }
        }

        System.out.printf("Found %d horizontal boxes\n", hBoxes.size());
        System.out.printf("Found %d vertical boxes\n", vBoxes.size());

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
            if (closestH != null && Util.distance(vrect.center, closestH.center) < 100)
                targetRects.add(closestH);

            VisionTarget target = VisionTarget.make(targetRects);
            targets.add(target);
        }

        System.out.println("Found " + targets.size() + " targets");

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
            System.out.println(target);
            window.showTargetInfo(target);
            drawTarget(img, target);
        }

        window.showResult(img);

        return targets;
    }

}
