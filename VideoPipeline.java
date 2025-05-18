//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.videoio.VideoCapture;
//import org.openftc.easyopencv.OpenCvPipeline;
//
//public class VideoReader {
//
//    public static void main(String[] args) {
//        // Load the OpenCV native library
//        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
//
//        // Create a VideoCapture object
//        // 0 represents the default camera, you can specify a video file path instead
//        VideoCapture videoCapture = new VideoCapture("path/to/your/video.mp4");
//
//        // Check if the video capture is opened successfully
//        if (!videoCapture.isOpened()) {
//            System.err.println("Error opening video file");
//            return;
//        }
//
//        // Create a Mat object to store the frame
//        Mat frame = new Mat();
//
//        // Loop through the video frames
//        while (videoCapture.read(frame)) {
//            // Process the frame (e.g., display it, analyze it, etc.)
//            // You can use EasyOpenCV or other OpenCV functions here
//
//            // Display the frame (optional, requires a GUI setup)
//            // Imgproc.resize(frame, frame, new Size(640, 480)); // Resize if needed
//            // HighGui.imshow("Video Frame", frame);
//            // int key = HighGui.waitKey(1);
//            // if (key == 27) break; // Exit on ESC key
//
//            // Release the frame for the next iteration
//            frame.release();
//        }
//
//        // Release the video capture
//        videoCapture.release();
//        // HighGui.destroyAllWindows(); //Close all windows
//
//        System.out.println("Video processing complete.");
//    }
//}

package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import org.openftc.easyopencv.OpenCvPipeline;

public class VideoPipeline extends OpenCvPipeline {

    Mat mat = new Mat();
    java.util.List<Point> midpoints = new java.util.ArrayList<>();

    public Mat yellowDetect(Mat input) {
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
            if (area > 25000) { // Only process contours if the area is big enough (reduces noise)
                Rect boundingRect = Imgproc.boundingRect(contours.get(i));
                boolean add = midpoints.add(new Point(boundingRect.x + (double) boundingRect.width /2, boundingRect.y +  (double) boundingRect.height/2));
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
    @Override
    public Mat processFrame(Mat input) {
        // Runs functions for detecting the colors and displaying the rectangles
        input = yellowDetect(input);
        Scalar lineColor = new Scalar(255, 255, 255); // White color
        int thickness = 2; // Line thickness

        for (int i = 0; i < midpoints.size() - 1; i++) {
            Point start = midpoints.get(i);
            Point end = midpoints.get(i + 1);
            Imgproc.line(input, start, end, lineColor, thickness);
        }

        return input;
    }
}