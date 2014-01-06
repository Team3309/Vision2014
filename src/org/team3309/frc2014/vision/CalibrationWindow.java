package org.team3309.frc2014.vision;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by vmagro on 1/5/14.
 */
public class CalibrationWindow extends JFrame implements SliderListener {

    private static CalibrationWindow instance;

    public static synchronized CalibrationWindow getInstance() {
        if (instance == null)
            instance = new CalibrationWindow();
        return instance;
    }

    private ArrayList<SliderListener> listeners = new ArrayList<SliderListener>();

    private ImageSlider hue, sat, val, erode, dilate, thresh, result;

    private CalibrationWindow() {
        setLayout(new GridBagLayout());

        hue = new ImageSlider("Hue", 0, 255);
        sat = new ImageSlider("Sat", 0, 255);
        val = new ImageSlider("Val", 0, 255);
        thresh = new ImageSlider("Threshold", 0, 0, 0);
        erode = new ImageSlider("Erosion", 1, 0, 25);
        dilate = new ImageSlider("Dilation", 1, 0, 25);
        result = new ImageSlider("Result", 0, 0, 0);

        hue.addListener(this);
        sat.addListener(this);
        val.addListener(this);
        erode.addListener(this);
        dilate.addListener(this);

        GridBagConstraints c = new GridBagConstraints();

        c.weightx = .33;
        c.weighty = .5;
        c.anchor = GridBagConstraints.NORTH;

        c.ipadx = 20;
        c.ipady = 10;

        c.gridy = 0;

        c.gridx = 0;
        add(hue, c);
        c.gridx = 1;
        add(sat, c);
        c.gridx = 2;
        add(val, c);
        c.gridx = 3;
        add(thresh, c);

        c.gridy = 1;

        c.gridx = 0;
        add(dilate, c);
        c.gridx = 1;
        add(erode, c);
        c.gridx = 2;
        add(result, c);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addListener(SliderListener l) {
        listeners.add(l);
    }

    public void showHue(Mat mat) {
        Mat tmp = mat.clone();
        Imgproc.resize(mat, tmp, new Size(320, 240));
        hue.show(tmp);
    }

    public void showSat(Mat mat) {
        Mat tmp = mat.clone();
        Imgproc.resize(mat, tmp, new Size(320, 240));
        sat.show(tmp);
    }

    public void showVal(Mat mat) {
        Mat tmp = mat.clone();
        Imgproc.resize(mat, tmp, new Size(320, 240));
        val.show(tmp);
    }

    public void showErosion(Mat mat) {
        Mat tmp = mat.clone();
        Imgproc.resize(mat, tmp, new Size(320, 240));
        erode.show(tmp);
    }

    public void showDilation(Mat mat) {
        Mat tmp = mat.clone();
        Imgproc.resize(mat, tmp, new Size(320, 240));
        dilate.show(tmp);
    }

    public void showThreshold(Mat mat) {
        Mat tmp = mat.clone();
        Imgproc.resize(mat, tmp, new Size(320, 240));
        thresh.show(tmp);
    }

    public void showResult(Mat mat) {
        Mat tmp = mat.clone();
        Imgproc.resize(mat, tmp, new Size(320, 240));
        result.show(tmp);
    }

    @Override
    public void sliderUpdated() {
        for (SliderListener l : listeners)
            l.sliderUpdated();
    }

    public int getHueMin() {
        return hue.getMin();
    }

    public int getHueMax() {
        return hue.getMax();
    }

    public int getSatMin() {
        return sat.getMin();
    }

    public int getSatMax() {
        return sat.getMax();
    }

    public int getValMin() {
        return val.getMin();
    }

    public int getValMax() {
        return val.getMax();
    }

    public int getErosionSize() {
        return erode.getSlider(0);
    }

    public int getDilationSize() {
        return dilate.getSlider(0);
    }

    public void setHueMin(int val) {
        hue.setMin(val);
    }

    public void setHueMax(int val) {
        hue.setMax(val);
    }

    public void setSatMin(int val) {
        sat.setMin(val);
    }

    public void setSatMax(int val) {
        sat.setMax(val);
    }

    public void setValMin(int val) {
        this.val.setMin(val);
    }

    public void setValMax(int val) {
        this.val.setMax(val);
    }

    public void setErosionSize(int val) {
        erode.setSlider(0, val);
    }

    public void setDilation(int val) {
        dilate.setSlider(0, val);
    }

}
