import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class Normalize {

	public static Mat normalize(Mat m) {
		Mat norm1 = new Mat(m.rows(), m.cols(), m.type()); 
		m.convertTo(norm1, CvType.CV_32FC3, 1.0/255.5);
        Core.normalize(norm1, norm1);
		return norm1; 
	}
	
	public static Mat convertBack(Mat m) {
	
		
		Mat convert = new Mat(m.rows(), m.cols(), m.type()); 
		m.convertTo(convert, CvType.CV_8U, 255*255);
		//Imgproc.cvtColor(convert, convert, Imgproc.COLOR_GRAY2RGBA,  4); 
		return convert; 
	}
}
