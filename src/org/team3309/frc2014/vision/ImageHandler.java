package org.team3309.frc2014.vision;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vmagro on 1/10/14.
 */
public class ImageHandler extends AbstractHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (target.startsWith("/image")) {
            baseRequest.setHandled(true);

            response.setContentType("image/jpeg");

            String imgName = target.substring("/image/".length()).toLowerCase();
            Mat img = null;
            if (imgName.equals("hue"))
                img = Tracker.hue;
            if (imgName.equals("sat"))
                img = Tracker.sat;
            if (imgName.equals("val"))
                img = Tracker.val;
            if (imgName.equals("thresh"))
                img = Tracker.thresholded;
            if (imgName.equals("erode"))
                img = Tracker.erode;
            if (imgName.equals("dilate"))
                img = Tracker.dilate;
            if (imgName.equals("result"))
                img = Tracker.result;

            if (img != null) {
                img = img.clone();
                Imgproc.resize(img, img, new Size(320, 240));

                MatOfByte matOfByte = new MatOfByte();
                Highgui.imencode(".jpg", img, matOfByte);
                response.setStatus(200);
                response.getOutputStream().write(matOfByte.toArray());
            } else {
                response.setStatus(404);
            }
        }
    }
}
