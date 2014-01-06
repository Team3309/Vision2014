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

    public static Goal findGoal(Mat img, CalibrationWindow window) {
        Mat hsv = new Mat(img.size(), CvType.CV_8UC3);
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);
        Mat bin = threshold(hsv, window);
        window.showThreshold(bin.clone());

        erodeAndDilate(bin, window);

        List<MatOfPoint> contours = findContours(bin);
        System.out.println("Found " + contours.size() + " contours");

        //drawContours(contours, img);
        //drawContour(contours.get(1), img);

        for (MatOfPoint contour : contours) {
            List<Line> lines = getLines(contour);

            for (int i = 1; i < lines.size(); i++) {
                lines.remove(i);
            }

            for (Line l : lines) {
                drawLine(img, l, 5, new Scalar(255, 255, 0));
            }

            Goal goal = Goal.getGoal(lines);
            //drawGoal(img, goal);
            System.out.println(goal);
        }

        window.showResult(img);

        return null;
    }

}
