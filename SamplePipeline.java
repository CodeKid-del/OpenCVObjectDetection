package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SamplePipeline extends OpenCvPipeline {

    Mat mat = new Mat();

    @Override
    public Mat processFrame(Mat input) {
        // Converts image color space to HSV for color detection
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);

        // Color detection: isolates objects of the color in the range from the lower and upper bounds
        Scalar yellowLowerBound = new Scalar(15, 100, 100); // yellow
        Scalar yellowUpperBound = new Scalar(37, 255, 300);
        Core.inRange(mat, yellowLowerBound, yellowUpperBound, mat); // Puts a binary mask on the image to find the areas that are within the color range

        // Find contours
        java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Calculates bounding box of objects
        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > 50) { // Only process contours if the area is big enough (reduces noise)
                Rect boundingRect = Imgproc.boundingRect(contours.get(i));
                // Draw a rectangle around the detected object
                Imgproc.rectangle(input, boundingRect, new Scalar(255, 255, 0), 1); // yellow rectangle

            }
        }
        // returns processed image
        mat.release();
        hierarchy.release();
        for (MatOfPoint contour : contours) {
            contour.release();
        }
        return input;
    }
}