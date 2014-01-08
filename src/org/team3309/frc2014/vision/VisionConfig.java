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

    private int hueMin = 66;
    private int hueMax = 91;
    private int satMin = 161;
    private int satMax = 255;
    private int valMin = 106;
    private int valMax = 255;

    private int erosionSize = 0;
    private int dilationSize = 2;

    private double verticalFov = 14.24;

    private VisionConfig() {

    }

    public int getHueMin() {
        return hueMin;
    }

    public void setHueMin(int hueMin) {
        this.hueMin = hueMin;
    }

    public int getHueMax() {
        return hueMax;
    }

    public void setHueMax(int hueMax) {
        this.hueMax = hueMax;
    }

    public int getSatMin() {
        return satMin;
    }

    public void setSatMin(int satMin) {
        this.satMin = satMin;
    }

    public int getSatMax() {
        return satMax;
    }

    public void setSatMax(int satMax) {
        this.satMax = satMax;
    }

    public int getValMin() {
        return valMin;
    }

    public void setValMin(int valMin) {
        this.valMin = valMin;
    }

    public int getValMax() {
        return valMax;
    }

    public void setValMax(int valMax) {
        this.valMax = valMax;
    }

    public String toString() {
        return "hue:" + hueMin + "," + hueMax + " sat:" + satMin + "," + satMax + " val:" + valMin + "," + valMax + " dilation:" + dilationSize + " erosion:" + erosionSize;
    }

    public int getErosionSize() {
        return erosionSize;
    }

    public void setErosionSize(int erosionSize) {
        this.erosionSize = erosionSize;
    }

    public int getDilationSize() {
        return dilationSize;
    }

    public void setDilationSize(int dilationSize) {
        this.dilationSize = dilationSize;
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
}
