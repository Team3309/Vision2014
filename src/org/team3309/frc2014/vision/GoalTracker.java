package org.team3309.frc2014.vision;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmagro on 1/5/14.
 */
public class GoalTracker {

    private static Mat threshold(Mat hsv, CalibrationWindow w) {
        ArrayList<Mat> split = new ArrayList<Mat>();
        Core.split(hsv, split);

        Mat hue = split.get(0);
        Mat sat = split.get(1);
        Mat val = split.get(2);

        VisionConfig c = VisionConfig.getInstance();

        Mat bin = new Mat(hsv.size(), CvType.CV_8UC1);

        Core.inRange(hue, new Scalar(c.getHueMin()), new Scalar(c.getHueMax()), hue);
        Core.inRange(sat, new Scalar(c.getSatMin()), new Scalar(c.getSatMax()), sat);
        Core.inRange(val, new Scalar(c.getValMin()), new Scalar(c.getSatMax()), val);

        w.showHue(hue);
        w.showSat(sat);
        w.showVal(val);

        Core.bitwise_or(hue, bin, bin);
        Core.bitwise_and(sat, bin, bin);
        Core.bitwise_and(val, bin, bin);

        return bin;
    }

    private static void erodeAndDilate(Mat bin, CalibrationWindow w) {
        VisionConfig c = VisionConfig.getInstance();

        Mat elementDilation = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                new Size(2 * c.getDilationSize() + 1, 2 * c.getDilationSize() + 1),
                new Point(c.getDilationSize(), c.getDilationSize()));
        /// Apply the erosion operation
        Imgproc.erode(bin, bin, elementDilation);

        w.showDilation(bin);

        Mat elementErosion = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                new Size(2 * c.getErosionSize() + 1, 2 * c.getErosionSize() + 1),
                new Point(c.getErosionSize(), c.getErosionSize()));
        /// Apply the erosion operation
        Imgproc.dilate(bin, bin, elementErosion);

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
        Scalar color = goal.isLeft() ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255);
        drawLine(img, goal.getTop(), 5, color);
        drawLine(img, goal.getBottom(), 5, color);
        drawLine(img, goal.getSide(), 5, color);
    }

    private static void drawTarget(Mat img, VisionTarget target) {
        Scalar color = target.isLeft() ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255);
        int thickness = target.isHot() ? 10 : 5;
        drawLine(img, target.getVertical(), thickness, color);
        if (target.getHorizontal() != null)
            drawLine(img, target.getHorizontal(), thickness, new Scalar(255, 0, 0));
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
        System.out.printf("Found %d vertical lines", vLines.size());

        for (Line l : hLines) {
            drawLine(img, l, 5, new Scalar(0, 0, 255));
        }
        for (Line l : vLines) {
            drawLine(img, l, 5, new Scalar(255, 0, 0));
        }

        window.showResult(img);

        return null;
    }

    public static VisionTarget findTarget(Mat img, CalibrationWindow window) {
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

        for (Line l : hLines) {
            drawLine(img, l, 5, new Scalar(255, 0, 0));
        }
        for (Line l : vLines) {
            drawLine(img, l, 5, new Scalar(0, 255, 0));
        }


        ArrayList<VisionTarget> targets = new ArrayList<VisionTarget>();

        for (Line vline : vLines) {
            Line closestH = null;
            for (Line hline : hLines) {
                if (closestH != null) {
                    if (hline.distance(vline) < closestH.distance(vline))
                        closestH = hline;
                } else {
                    closestH = hline;
                }
            }

            ArrayList<Line> targetLines = new ArrayList<Line>();
            targetLines.add(vline);
            if (closestH != null && Util.distance(closestH.getLeft(), vline.getTop()) < 50) {
                targetLines.add(closestH);
            } else if (closestH != null) {
            }

            VisionTarget target = VisionTarget.make(targetLines);
            targets.add(target);
        }

        System.out.println("Found " + targets.size() + " targets");

        if (targets.size() > 1) {
            for (int i = 0; i < targets.size(); i++) {
                VisionTarget target = targets.get(i);
                for (int j = i; j < targets.size(); j++) {
                    if (target.getPoint().x < targets.get(j).getPoint().x)
                        target.overrideLeft();
                }
            }
        }

        for (VisionTarget target : targets) {
            System.out.println(target);
            window.showTargetInfo(target);
            drawTarget(img, target);
        }

        window.showResult(img);

        return null;
    }

}
