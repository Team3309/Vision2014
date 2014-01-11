package org.team3309.frc2014.vision;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by vmagro on 1/10/14.
 */
public class ConfigHandler extends AbstractHandler {

    TrackingConfig currentMode = VisionConfig.getInstance().getVisionTargetThreshold();

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        if (target.startsWith("/config")) {
            baseRequest.setHandled(true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String input = "";
            String line = null;
            while ((line = reader.readLine()) != null)
                input += line;

            System.out.println(input);
            JSONObject json = new JSONObject(input);

            currentMode.setHueMin(json.optInt("hueMin", currentMode.getHueMin()));
            currentMode.setHueMax(json.optInt("hueMax", currentMode.getHueMax()));
            currentMode.setSatMin(json.optInt("satMin", currentMode.getSatMin()));
            currentMode.setSatMax(json.optInt("satMax", currentMode.getSatMax()));
            currentMode.setValMin(json.optInt("valMin", currentMode.getValMin()));
            currentMode.setValMax(json.optInt("valMax", currentMode.getValMax()));
            currentMode.setErosion(json.optInt("erosion", currentMode.getErosion()));
            currentMode.setDilation(json.optInt("dilation", currentMode.getDilation()));
        }
    }

}
