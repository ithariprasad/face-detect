package com.virtusapolaris.cam;

import org.bytedeco.javacv.*;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualRecognitionOptions;

import static org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;

import java.io.File;

/**
 * 
 */
public class App implements Runnable {
	final int INTERVAL = 100;/// you may use interval
	CanvasFrame canvas = new CanvasFrame("Face detect");

	public App() {
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}

	public void run() {

		FrameGrabber grabber = new VideoInputFrameGrabber(0); // 1 for next
																// camera
		OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
		IplImage img;
		int i = 0;
		try {
			grabber.start();
			while (i < 1) {
				Frame frame = grabber.grab();

				img = converter.convert(frame);

				// the grabbed frame will be flipped, re-flip to make it right
				cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise

				// save
				cvSaveImage("c:/temp/" + (i++) + "-aa.jpg", img);
				
				System.out.println("saving ");

				canvas.showImage(converter.convert(img));

				Thread.sleep(INTERVAL);
	
			}
			canvas.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		
		System.setProperty("javax.net.ssl.trustStore",
				"C:/Users/hariprasadm/.keystore");
		
		App gs = new App();
		Thread th = new Thread(gs);
		th.start();
		
		Thread.sleep(2000);
		
		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		service.setApiKey("18bf2b647455222f1e13d04292a874078d5fab39");

		System.out.println("Face detecting an image");
		
		VisualRecognitionOptions options1 = new VisualRecognitionOptions.Builder()
				.images(new File("c:/temp/0-aa.jpg"))
				.build();
		
		service.detectFaces(options1);
		DetectedFaces result = service.detectFaces(options1).execute();
		System.out.println(result);
		
	}
}