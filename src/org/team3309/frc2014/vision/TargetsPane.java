package org.team3309.frc2014.vision;

import java.awt.*;

/**
 * Created by vmagro on 1/7/14.
 */
public class TargetsPane extends Container {

    private TargetInfoPane leftTarget = new TargetInfoPane(), rightTarget = new TargetInfoPane();

    public TargetsPane() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;

        c.gridx = 0;
        add(leftTarget);

        c.gridx = 1;
        add(rightTarget);
    }

    public void addTarget(VisionTarget target) {
        if (target.isLeft())
            leftTarget.setTarget(target);
        else
            rightTarget.setTarget(target);
    }

}
