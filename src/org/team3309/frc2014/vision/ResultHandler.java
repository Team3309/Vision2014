package org.team3309.frc2014.vision;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by vmagro on 1/10/14.
 */
public class ResultHandler extends AbstractHandler {

    private TrackingConfig currentMode = VisionConfig.getInstance().getVisionTargetThreshold();

    private URL imageUrl;

    public ResultHandler() {
        try {
            imageUrl = new URL("http://192.168.0.120/image/jpeg.cgi");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (target.equals("/result")) {
            baseRequest.setHandled(true);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);

            //Mat img = Util.loadImage(imageUrl);
            Mat img = Highgui.imread("image_one_third.jpg");

            long start = System.currentTimeMillis();

            List<VisionTarget> targets = Tracker.findTargets(img, null);

            long end = System.currentTimeMillis();

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("time", (end - start));

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
