import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.atul.JavaOpenCV.Imshow;


public class GaussPrep {
	
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
	
	public static boolean compare(String file1, String file2) {
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

		m2 = blur(m1, 55);
		m2_out = blur(m1_out, 55);
		
		Imgproc.equalizeHist(m2, m3);
		Imgproc.equalizeHist(m2_out, m3_out);
		
		Core.normalize(m2, m4);
		Core.normalize(m2_out, m4_out);
		
		m5 = subtract(m3, m3_out);
		m5_out = subtract(m3_out, m3);
		
		Core.add(m5, m5_out, m6);
		
//		Imshow im = new Imshow("regular subtraction");
//		im.showImage(m5);
//		
//		Imshow im2 = new Imshow("fucky subtraction");
//		im2.showImage(m5_out);
//		
//		Imshow im3 = new Imshow("all mish-mashed togetha");
//		im3.showImage(m6);
			
		
		
		return true;
	}
}
