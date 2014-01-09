package org.team3309.frc2014.vision;

import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

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

    private TrackingConfig currentMode = VisionConfig.getInstance().getVisionTargetThreshold();

    public VisionMain() {
        w = CalibrationWindow.getInstance();
        capture = new VideoCapture();
        //capture.open(0);

        VisionConfig c = VisionConfig.getInstance();
        if (w != null) {
            w.setHueMin(currentMode.getHueMin());
            w.setHueMax(currentMode.getHueMax());
            w.setSatMin(currentMode.getSatMin());
            w.setSatMax(currentMode.getSatMax());
            w.setValMin(currentMode.getValMin());
            w.setValMax(currentMode.getValMax());

            w.setErosionSize(currentMode.getErosion());
            w.setDilation(currentMode.getDilation());

            w.addListener(this);

            sliderUpdated();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sliderUpdated();
        }

        /*while (true) {
            Mat img = new Mat();
            capture.read(img);
            Imgproc.resize(img, img, new Size(640, 480));
            Tracker.findTargets(img, w);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

    }

    @Override
    public void sliderUpdated() {
        currentMode.setHueMax(w.getHueMax());
        currentMode.setHueMin(w.getHueMin());
        currentMode.setSatMax(w.getSatMax());
        currentMode.setSatMin(w.getSatMin());
        currentMode.setValMax(w.getValMax());
        currentMode.setValMin(w.getValMin());

        currentMode.setErosion(w.getErosionSize());
        currentMode.setDilation(w.getDilationSize());

        System.out.println(currentMode);

        /*List<Goal> goals = Tracker.findGoals(Highgui.imread(imageName), w);
        for(Goal g : goals)
            System.out.println(g);*/
        Tracker.findTargets(Highgui.imread(imageName), w);

        /*Mat img = new Mat();
        capture.read(img);
        Imgproc.resize(img, img, new Size(640, 480));
        Tracker.findTargets(img, w);*/
    }
}
