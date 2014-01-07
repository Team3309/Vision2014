package org.team3309.frc2014.vision;

import javax.swing.*;
import java.awt.*;

/**
 * Created by vmagro on 1/6/14.
 */
public class TargetInfoPane extends Container {

    private JLabel pixels = new JLabel(), hot = new JLabel(), distance = new JLabel(), azimuth = new JLabel();

    public TargetInfoPane() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.ipady = 10;

        c.gridy = 0;
        add(new JLabel("Target"), c);

        c.gridy = 1;
        add(distance, c);

        c.gridy = 2;
        add(azimuth, c);

        c.gridy = 3;
        add(hot, c);

        c.gridy = 4;
        add(pixels, c);
    }

    public void setTarget(VisionTarget target) {
        distance.setText(Math.round(target.distance()) + "in");
        azimuth.setText(Math.round(target.azimuth()) + "°");
        hot.setText(target.isHot() ? "Hot" : "Not hot");
        pixels.setText(Math.round(target.getVertical().length()) + "px");
    }

}
