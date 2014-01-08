package org.team3309.frc2014.vision;

import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import java.util.List;

/**
 * Created by vmagro on 1/5/14.
 */
public class VisionMain implements SliderListener {

    public static void main(String[] args) {
        System.loadLibrary("opencv_java247");

        new VisionMain();
    }

    private CalibrationWindow w;

    private VideoCapture capture;

    private String imageName = "image_one_third.jpg";

    public VisionMain() {
        //w = CalibrationWindow.getInstance();
        capture = new VideoCapture();
        //capture.open(0);

        VisionConfig c = VisionConfig.getInstance();
        if (w != null) {
            w.setHueMin(c.getHueMin());
            w.setHueMax(c.getHueMax());
            w.setSatMin(c.getSatMin());
            w.setSatMax(c.getSatMax());
            w.setValMin(c.getValMin());
            w.setValMax(c.getValMax());

            w.setErosionSize(c.getErosionSize());
            w.setDilation(c.getDilationSize());

            w.addListener(this);

            sliderUpdated();
        }

        long start = System.currentTimeMillis();

        List<VisionTarget> targets = GoalTracker.findTarget(Highgui.imread(imageName), null);

        long end = System.currentTimeMillis();

        for (VisionTarget t : targets) {
            if (t.isHot()) {
                if (t.isRight())
                    System.out.println("Right is hot");
                else
                    System.out.println("Left is hot");
            }
        }

        double elapsed = end - start;

        System.out.println("Took " + elapsed + "ms");

    }

    @Override
    public void sliderUpdated() {
        VisionConfig c = VisionConfig.getInstance();
        System.out.println(c);

        c.setHueMax(w.getHueMax());
        c.setHueMin(w.getHueMin());
        c.setSatMax(w.getSatMax());
        c.setSatMin(w.getSatMin());
        c.setValMax(w.getValMax());
        c.setValMin(w.getValMin());

        c.setErosionSize(w.getErosionSize());
        c.setDilationSize(w.getDilationSize());

        //GoalTracker.findGoal(Highgui.imread(imageName), w);
        GoalTracker.findTarget(Highgui.imread(imageName), null);

        /*Mat img = new Mat();
        capture.read(img);
        Imgproc.resize(img, img, new Size(640, 480));
        GoalTracker.findTarget(img, w);*/
    }
}
