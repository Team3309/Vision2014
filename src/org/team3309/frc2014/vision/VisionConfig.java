package org.team3309.frc2014.vision;

/**
 * Created by vmagro on 1/5/14.
 */
public class VisionConfig {

    private static VisionConfig instance = null;

    public static VisionConfig getInstance() {
        if (instance == null)
            instance = new VisionConfig();
        return instance;
    }

    private int imageWidth = 640;
    private int imageHeight = 480;

    private TrackingConfig visionTargetThreshold = new TrackingConfig.Builder()
            .name("Vision Target")
            .hueMin(66)
            .hueMax(91)
            .satMin(161)
            .satMax(255)
            .valMin(86)
            .valMax(255)
            .erode(0)
            .dilate(2)
            .build();

    private TrackingConfig goalThreshold = new TrackingConfig.Builder()
            .name("Goal")
            .hueMin(66)
            .hueMax(196)
            .satMin(0)
            .satMax(255)
            .valMin(151)
            .valMax(255)
            .erode(0)
            .dilate(7)
            .build();

    //TODO find real values for these
    private double verticalFov = 14.24;
    private double horizontalFov = 37;

    private VisionConfig() {

    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public double getVerticalFov() {
        return verticalFov;
    }

    public void setVerticalFov(double verticalFov) {
        this.verticalFov = verticalFov;
    }

    public double getHorizontalFov() {
        return horizontalFov;
    }

    public void setHorizontalFov(double horizontalFov) {
        this.horizontalFov = horizontalFov;
    }

    public TrackingConfig getVisionTargetThreshold() {
        return visionTargetThreshold;
    }

    public TrackingConfig getGoalThreshold() {
        return goalThreshold;
    }
}
