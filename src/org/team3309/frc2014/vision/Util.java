package org.team3309.frc2014.vision;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.highgui.Highgui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Util {

    public static BufferedImage getBufferedImage(Mat img) {
        //Imgproc.resize(img, img, new Size(640, 480));
        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            return bufImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public static double pixelsToInches(int pixels, double targetPixels) {
        double pixelsInchesRatio = targetPixels / VisionTarget.VERTICAL_LENGTH_INCHES;
        return pixels * pixelsInchesRatio;
    }

    private static final int ANGLE_THRESHOLD = 20;

    public static boolean isHorizontal(RotatedRect r) {
        double angle = Math.abs(r.angle % 90);
        if (angle < ANGLE_THRESHOLD || Math.abs(angle - 90) < ANGLE_THRESHOLD)
            return r.boundingRect().height < r.boundingRect().width;
        else return false;
    }

    public static boolean isVertical(RotatedRect r) {
        double angle = Math.abs(r.angle) % 90;
        if (angle < ANGLE_THRESHOLD || Math.abs(angle - 90) < ANGLE_THRESHOLD)
            return r.boundingRect().height > r.boundingRect().width;
        else return false;
    }

}
