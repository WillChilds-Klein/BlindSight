import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.atul.JavaOpenCV.Imshow;


public class EdgyAF {
	
	public static int getEdgeConfidence(Mat image1, Mat image2) {
		int confidence = 0; 
		
		Mat norm1 = Normalize.normalize(image1); 
		Mat norm2 = Normalize.normalize(image2); 
		
		norm1 = Normalize.convertBack(norm1); 
		norm2 = Normalize.convertBack(norm2); 
		
//		Mat norm1 = new Mat(image1.rows(), image1.cols(), image1.type()); 
//		Mat norm2 = new Mat(image2.rows(), image2.cols(), image2.type());
//		image1.convertTo(norm1, CvType.CV_32FC3, 1.0/255.5);
//		image2.convertTo(norm2, CvType.CV_32FC3, 1.0/255.5);
//		
//        Core.normalize(norm1, norm1);
//        Core.normalize(norm2,norm2);
        double[] r = Core.sumElems(norm2).val; 
        float sum = 0; 
        for (double x : r) {
        	sum += x; 
        }
        System.out.println("X = " + sum); 
        
//		Core.normalize(image2, norm2);
		System.out.println(norm1.dump()); 
		//System.out.println(norm2.dump()); 
		
		Imshow imnorm1 = new Imshow("Image1 norm"); 
		Imshow imnorm2 = new Imshow("Image2 norm"); 
		
		Mat edge1 = GetEdges.getEdges(norm1); 
		Mat edge2 = GetEdges.getEdges(norm2); 
		
		int white1 = Core.countNonZero(edge1); 
		int white2 = Core.countNonZero(edge2); 
		
		Imshow imedge1 = new Imshow("Image1 edge"); 
		Imshow imedge2 = new Imshow("Image2 edge"); 
		
		imnorm1.showImage(norm1);
		imnorm2.showImage(norm2);
		imedge1.showImage(edge1);
		imedge2.showImage(edge2);
		
		//System.out.println("white1 = " + white1 + " white 2 =" + white2); 
		
		return confidence; 
	}
	
}
