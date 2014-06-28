import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.atul.JavaOpenCV.Imshow;

public class BlindSight{
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		
		Mat m1 = Highgui.imread("/Users/childskl/Desktop/lena.jpg",Highgui.CV_LOAD_IMAGE_COLOR);
//		Mat m2 = new Mat(m1.size(), m1.type());
		
		
		
//		Imshow im = new Imshow("Original Image"); 
//		im.showImage(m1);
		
		Mat[] arr = grayscaleToThreshold(RGBtoGrayscale(m1), 5);
		
		Mat m2 = arr[2];
		
		Imshow im2 = new Imshow("middle threshold");
		im2.showImage(m2);
	}
	
	public static Mat[] grayscaleToThreshold(Mat input, int numThresholds){
		int threshold_value = 0;
		int threshold_type = Imgproc.THRESH_BINARY;
		int max_value = 255;
		
		Mat[] output = new Mat[numThresholds];
		
		for(int i = 0; i < numThresholds; i++){
			output[i] = new Mat(input.size(), input.type());
			threshold_value = (int) (((float) i / (float) numThresholds) * (float) max_value);
			Imgproc.threshold(input, output[i], threshold_value, max_value, threshold_type);
			System.out.println(" i = " + threshold_value);
		}
		
		return output;
	}
	
	public static Mat RGBtoGrayscale(Mat input){
		Mat output = new Mat(input.size(), input.type());
		Imgproc.cvtColor(input, output, Imgproc.COLOR_RGB2GRAY);
		return output;
	}
}