import java.util.BitSet;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.atul.JavaOpenCV.Imshow;



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

		m2 = blur(m1, 25);
		m2_out = blur(m1_out, 25);
		
	//	m3 =  Normalize.normalize(m2); 
	//	m3_out = Normalize.normalize(m2_out); 
		
	//	m3 = Normalize.convertBack(m3);
	//	m3_out = Normalize.convertBack(m3_out);
		
		Imgproc.equalizeHist(m2, m3);
		Imgproc.equalizeHist(m2_out, m3_out);
		
		//Core.normalize(m2, m4);
		//Core.normalize(m2_out, m4_out);
		
		
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
			
		
		
		return confidence;
	}
}
