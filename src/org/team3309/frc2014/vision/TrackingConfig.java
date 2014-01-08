package org.team3309.frc2014.vision;

/**
 * Created by vmagro on 1/8/14.
 */
public class TrackingConfig {

    private String name;
    private int hueMin, hueMax, satMin, satMax, valMin, valMax;
    private int erosion, dilation;

    private TrackingConfig() {
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
        return name + " hue:" + hueMin + "-" + hueMax + " sat:" + satMin + "-" + satMax + " val:" + valMin + "-" + valMax + " erosion:" + erosion + " dilation:" + dilation;
    }

    public int getErosion() {
        return erosion;
    }

    public void setErosion(int erosion) {
        this.erosion = erosion;
    }

    public int getDilation() {
        return dilation;
    }

    public void setDilation(int dilation) {
        this.dilation = dilation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Builder {

        private TrackingConfig config = new TrackingConfig();

        public Builder hueMin(int val) {
            config.setHueMin(val);
            return this;
        }

        public Builder hueMax(int val) {
            config.setHueMax(val);
            return this;
        }

        public Builder satMin(int val) {
            config.setSatMin(val);
            return this;
        }

        public Builder satMax(int val) {
            config.setSatMax(val);
            return this;
        }

        public Builder valMin(int val) {
            config.setValMin(val);
            return this;
        }

        public Builder valMax(int val) {
            config.setValMax(val);
            return this;
        }

        public Builder erode(int val) {
            config.setErosion(val);
            return this;
        }

        public Builder dilate(int val) {
            config.setDilation(val);
            return this;
        }

        public Builder name(String name) {
            config.setName(name);
            return this;
        }

        public TrackingConfig build() {
            return config;
        }
    }
}
