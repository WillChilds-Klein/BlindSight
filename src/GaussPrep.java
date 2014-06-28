import java.util.BitSet;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

//import com.atul.JavaOpenCV.Imshow;



public class GaussPrep {
	public final static double MEAN = 25;
	public final double SCALE_PER_POINT = 5;
	// blurSize must be odd and < either dim of image
	public static Mat blur(Mat input, int blurSize){
		Mat output = new Mat(input.size(), input.type());
		Size size = new Size(blurSize, blurSize);
		
		Imgproc.GaussianBlur(input, output, size, 0);
		return output;
	}
	
	public static Mat RGBtoGrayscale(Mat input){
		Mat output = new Mat(input.size(), input.type());
		Imgproc.cvtColor(input, output, Imgproc.COLOR_RGB2GRAY);
		return output;
	}
	
	public static Mat subtract(Mat input1, Mat input2){
		Mat output = new Mat(input1.size(), input1.type());
		Core.subtract(input1, input2, output);
		return output;
	}
	
	public static double compare(String file1, String file2) {
System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		

	    
		Mat m1 = Highgui.imread(file1,Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		Mat m1_out = Highgui.imread(file2,Highgui.CV_LOAD_IMAGE_GRAYSCALE);	
		Mat m2 = new Mat(m1.size(), m1.type());
		Mat m2_out = new Mat(m1.size(), m1.type());
		Mat m3 = new Mat(m1.size(), m1.type());
		Mat m3_out = new Mat(m1.size(), m1.type());
		Mat m4 = new Mat(m1.size(), m1.type());		
		Mat m4_out = new Mat(m1.size(), m1.type());
		Mat m5 = new Mat(m1.size(), m1.type());
		Mat m5_out = new Mat(m1.size(), m1.type());
		Mat m6 = new Mat(m1.size(), m1.type());

		m1 = blur(m1, 41);
		m1_out = blur(m1_out, 41);
//		
		Imgproc.equalizeHist(m2, m2);
		Imgproc.equalizeHist(m2_out, m2_out);
		
		m2 = Normalize.convertBack(Normalize.normalize(m1));
		m2_out = Normalize.convertBack(Normalize.normalize(m1_out));
		
		m5 = subtract(m3, m3_out);
		m5_out = subtract(m3_out, m3);
		

//		m5 = m5.inv(); 
//		m5_out = m5_out.inv(); 
		
		Core.add(m5, m5_out, m6);
//		Imgproc.equalizeHist(m6, m6);
	//	m6 = Normalize.normalize(m6);
	//	m6 = Normalize.convertBack(m6);
		//System.out.println(m6.dump());
		
//		Imshow input = new Imshow("Input for image" + file1); 
//		Imshow input2 = new Imshow("Compare for image" + file1); 
//		Imshow im = new Imshow("regular subtraction for image " + file1);
//		input.showImage(m1);
//		input2.showImage(m1_out); 
//		im.showImage(m6);
//		
		Blob.getSubmatrixSums(m6, 20); 
//		
		
		Double bigSum = Blob.sumVector(Core.sumElems(m6).val)/m6.total(); 
//		System.out.println("Sum of pixels/numOfPixels = " + bigSum/m6.total()); 
		double confidence = 50+(bigSum-MEAN)*5;
		
//		Imshow im2 = new Imshow("fucky subtraction");
//		im2.showImage(m5_out);
//		
//		Imshow im3 = new Imshow("all mish-mashed togetha");
//		im3.showImage(m6);
			
		
//		matchFeatures(m2, m2_out);
		
		return confidence;
	}
	
	public static List<DMatch> goodMatches(MatOfDMatch matches) {
		List<DMatch> matchesList = matches.toList();
		double maxDistance = 0;
		double minDistance = 1000;

		int rowCount = matchesList.size();
		for (int i = 0; i < rowCount; i++) {
			double dist = matchesList.get(i).distance;
			if (dist < minDistance)
				minDistance = dist;
			if (dist > maxDistance)
				maxDistance = dist;
		}

		List<DMatch> goodMatchesList = new ArrayList<DMatch>();
		double upperBound = 1.5 * minDistance;
		for (int i = 0; i < rowCount; i++) {
			if (matchesList.get(i).distance < upperBound) {
				goodMatchesList.add(matchesList.get(i));
			}
		}
		return goodMatchesList;
	}
	
	public static void matchFeatures(Mat m2, Mat m2_out){
		Mat m3 = new Mat();
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		
		
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		MatOfKeyPoint keypoints_out = new MatOfKeyPoint();
		detector.detect(m2, keypoints);
		detector.detect(m2_out, keypoints_out);
		
		ArrayList<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();
		
		matches.add(new MatOfDMatch());
		
		
		Mat descriptor1 = new Mat();
		Mat descriptor2 = new Mat();
		
		extractor.compute(m2, keypoints, descriptor1);
		extractor.compute(m2_out, keypoints_out, descriptor2);
		
		matcher.match(descriptor1, descriptor2, matches.get(0));
		
		List<DMatch> matchesList2 = goodMatches(matches.get(0));
		
		MatOfDMatch match = new MatOfDMatch();
		match.fromList(matchesList2);
		
		//Imshow im3 = new Imshow("Matches");
		
		Features2d.drawMatches(m2, keypoints, m2_out, keypoints_out,
				match, m3, new Scalar(0, 255, 0),
				new Scalar(0, 0, 255), new MatOfByte(),
				Features2d.NOT_DRAW_SINGLE_POINTS);
		//im3.showImage(m3);

	}
}
