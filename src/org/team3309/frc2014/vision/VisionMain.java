package org.team3309.frc2014.vision;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import java.net.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

/**
 * Created by vmagro on 1/5/14.
 */
public class VisionMain implements SliderListener {

    public static void main(String[] args) {
        System.loadLibrary("opencv_java248");

        new VisionMain();
    }
    private CalibrationWindow w;

    private String imageName = "image_one_third.jpg";

    private TrackingConfig currentMode = VisionConfig.getInstance().getVisionTargetThreshold();

    public VisionMain() {
        w = CalibrationWindow.getInstance();


        Server server = new Server(8080);
        HandlerList handlers = new HandlerList();
        handlers.addHandler(new ResultHandler());
        handlers.addHandler(new ImageHandler());
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("web");
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        handlers.addHandler(resourceHandler);
        handlers.addHandler(new ConfigHandler());
        handlers.addHandler(new DefaultHandler());

        server.setHandler(handlers);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        }

        //sliderUpdated();

        try {
            URL imageUrl = new URL("http://10.33.9.121:80/image/jpeg.cgi");
            while (true) {
                Tracker.findTargets(Util.loadImage(imageUrl), w);
                Thread.sleep(33);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

/*        try {

            VideoCapture cam = new VideoCapture(0);
            int width = (int) cam.get(Highgui.CV_CAP_PROP_FRAME_WIDTH);
            int height = (int) cam.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT);

            while (cam.grab()) {

                Mat frame = new Mat(width, height, CvType.CV_32FC3);
                cam.retrieve(frame);
                Tracker.findTargets(frame, w);
                Thread.sleep(33);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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



        //Tracker.findTargets(Highgui.imread(imageName), w);

        /*Mat img = new Mat();
        capture.read(img);
        Imgproc.resize(img, img, new Size(640, 480));
        Tracker.findTargets(img, w);*/
    }
}
