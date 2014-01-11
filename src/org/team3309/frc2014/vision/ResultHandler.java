package org.team3309.frc2014.vision;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.highgui.Highgui;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by vmagro on 1/10/14.
 */
public class ResultHandler extends AbstractHandler {

    private TrackingConfig currentMode = VisionConfig.getInstance().getVisionTargetThreshold();

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (target.equals("/result")) {
            baseRequest.setHandled(true);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);

            List<VisionTarget> targets = Tracker.findTargets(Highgui.imread("image_one_third.jpg"), null);

            JSONObject jsonResponse = new JSONObject();

            JSONArray array = new JSONArray();
            for (VisionTarget t : targets) {
                JSONObject json = new JSONObject();
                json.put("side", t.isLeft() ? "left" : "right");
                json.put("left", t.isLeft());
                json.put("right", t.isRight());
                json.put("hot", t.isHot());
                json.put("distance", t.distance());
                json.put("azimuth", t.azimuth());
                array.put(json);
            }

            jsonResponse.put("targets", array);

            JSONObject config = new JSONObject();
            config.put("hueMin", currentMode.getHueMin());
            config.put("hueMax", currentMode.getHueMax());
            config.put("satMin", currentMode.getSatMin());
            config.put("satMax", currentMode.getSatMax());
            config.put("valMin", currentMode.getValMin());
            config.put("valMax", currentMode.getValMax());
            config.put("erosion", currentMode.getErosion());
            config.put("dilation", currentMode.getDilation());

            jsonResponse.put("config", config);

            response.getWriter().print(jsonResponse.toString());
        }
    }

}
