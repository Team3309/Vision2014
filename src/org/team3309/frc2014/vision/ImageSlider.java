package org.team3309.frc2014.vision;

import org.opencv.core.Mat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by vmagro on 1/5/14.
 */
public class ImageSlider extends Container implements ChangeListener {

    private ArrayList<SliderListener> listeners = new ArrayList<SliderListener>();

    private JLabel imgLabel = new JLabel();
    private JSlider[] sliders;

    public ImageSlider(String name, int numSliders, int min, int max) {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();

        c.gridy = 0;

        add(new JLabel(name), c);

        c.fill = GridBagConstraints.HORIZONTAL;

        imgLabel.setPreferredSize(new Dimension(320, 240));

        c.gridy = 1;
        add(imgLabel, c);

        sliders = new JSlider[numSliders];
        for (int i = 0; i < numSliders; i++) {
            sliders[i] = new JSlider(min, max);
            sliders[i].addChangeListener(this);
            c.gridy++;
            add(sliders[i], c);
        }
    }

    public ImageSlider(String name, int min, int max) {
        this(name, 2, min, max);
    }

    public void show(Mat mat) {
        if (mat != null)
            imgLabel.setIcon(new ImageIcon(Util.getBufferedImage(mat)));
    }

    public int getSlider(int slider) {
        return sliders[slider].getValue();
    }

    public int getMin() {
        return getSlider(0);
    }

    public int getMax() {
        return getSlider(1);
    }

    public void setSlider(int slider, int val) {
        sliders[slider].setValue(val);
    }

    public void setMin(int val) {
        setSlider(0, val);
    }

    public void setMax(int val) {
        setSlider(1, val);
    }

    public void addListener(SliderListener l) {
        listeners.add(l);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        for (SliderListener l : listeners) {
            l.sliderUpdated();
        }
    }
}
