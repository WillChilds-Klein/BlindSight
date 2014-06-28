import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
//import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.atul.JavaOpenCV.Imshow;

public class Preprocess {
	
	public static int BLUR_PARAM = 21;
	public final double SCALE_PER_POINT = 5;
	
	public static Mat multiChannelHistEq(Mat input) {
		List<Mat> channels = new ArrayList<Mat>();
		Core.split(input, channels);

		Imgproc.equalizeHist(channels.get(0), channels.get(0));
		Imgproc.equalizeHist(channels.get(1), channels.get(1));
		Imgproc.equalizeHist(channels.get(2), channels.get(2));

		Mat merge = new Mat(input.size(), CvType.CV_8UC3);
		Core.merge(channels, merge);
		Mat output = new Mat(input.size(), CvType.CV_8UC1);
		Imgproc.cvtColor(merge, output, Imgproc.COLOR_BGR2GRAY);

		return output;
	}

	public static Mat[] matchFeatures(Mat m2, Mat m2_out){
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
		
//		Imshow im3 = new Imshow("Matches");

		Features2d.drawMatches(m2, keypoints, m2_out, keypoints_out,
				match, m3, new Scalar(0, 255, 0),
				new Scalar(0, 0, 255), new MatOfByte(),
				Features2d.NOT_DRAW_SINGLE_POINTS);
//		im3.showImage(m3);

		// obj => m2, scene => m2_out
		List<Point> obj_list = new ArrayList<Point>();
		List<Point> scene_list = new ArrayList<Point>();
			
		List<KeyPoint> keypoints_list = keypoints.toList();
		List<KeyPoint> keypoints_out_list = keypoints_out.toList();
		
		for( int i = 0; i < matchesList2.size(); i++ ){
			// Get the keypoints from the good matches
		    obj_list.add(keypoints_list.get(matchesList2.get(i).queryIdx).pt);
		    scene_list.add(keypoints_out_list.get(matchesList2.get(i).trainIdx).pt);
		}
		MatOfPoint2f obj = new MatOfPoint2f();
		MatOfPoint2f scene = new MatOfPoint2f();
		obj.fromList(obj_list);
		scene.fromList(scene_list);

		Mat m2_out_warped = null;
		if(matchesList2.size() >= 4){
			Mat H = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, 1);
			
			m2_out_warped = new Mat(m2_out.size(), m2_out.type());
			Imgproc.warpPerspective(m2_out, m2_out_warped, H, m2_out_warped.size());
			/** /
			Imshow im5 = new Imshow("changed and rotated");
			im5.showImage(m2_out_warped);
			Imshow im6 = new Imshow("subtraction");
			im6.showImage(BlindSight.subtract(m2_out_warped, m2));
			/**/
			Mat[] output = {m2, m2_out_warped};
			return output;
		}
		
//		Imshow im4 = new Imshow("original");
//		im4.showImage(m2);
		
		Mat[] output = {m2, m2_out};
		return output;
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
	
	public static Mat normalize(Mat m) {
		Mat norm1 = new Mat(m.rows(), m.cols(), m.type()); 
		m.convertTo(norm1, CvType.CV_32FC3, 1.0/255.5);
        Core.normalize(norm1, norm1);
		return norm1; 
	}
	
	public static Mat convertBack(Mat m) {
		Mat convert = new Mat(m.rows(), m.cols(), m.type()); 
		m.convertTo(convert, CvType.CV_8U, 255*10);
		return convert; 
	}
	
	public static Mat[] grayscaleToThreshold(Mat input, int numThresholds){
		int threshold_value = 0;
		int threshold_type = Imgproc.THRESH_BINARY;
		int max_value = 255;
		
		Mat[] output = new Mat[numThresholds];
		
		for(int i = 0; i < numThresholds; i++){
			output[i] = new Mat(input.size(), input.type());
			threshold_value = (int) (((float) (i+1) / ((float) numThresholds+1) )* (float) max_value);
//			System.out.println("Threshhold val = " + threshold_type); 
			Imgproc.threshold(input, output[i], threshold_value, max_value, threshold_type);
//			System.out.println(" i = " + threshold_value);
		}
		
		return output;
	}
	
//	public static void main(String[] args) {
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
//	    
//		String file1 = "distrib/set3/unchanged/pair_0938_inbound.jpg";
//		String file2 = "distrib/set3/unchanged/pair_0938_outbound.jpg";
//		
//		Mat m = Highgui.imread(file1,Highgui.CV_LOAD_IMAGE_COLOR);
//		Mat m_out = Highgui.imread(file2,Highgui.CV_LOAD_IMAGE_COLOR);
//		Mat m1 = new Mat(m.size(), CvType.CV_8UC1);
//		Mat m1_out = new Mat(m_out.size(), CvType.CV_8UC1);
//
//		m1 = multiChannelHistEq(m);
//		m1_out = multiChannelHistEq(m_out);
//		
//		m1 = blur(m1, BLUR_PARAM);
//		m1_out = blur(m1_out, BLUR_PARAM);
//		
//		matchFeatures(m1, m1_out);
//		
//		return;
//	}
	
}