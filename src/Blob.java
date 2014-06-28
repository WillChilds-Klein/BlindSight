import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.atul.JavaOpenCV.Imshow;


public class Blob {
	// cuts off lower right portion of image in case of non-squareness
	public static double[][] getSubmatrixSums(Mat input, int submatrixSize){
		int nRows = input.rows() / submatrixSize;
		int nCols = input.cols() / submatrixSize;
		
		System.out.println(nRows);
		System.out.println(nCols);
		
		double bigSum = sumVector(Core.sumElems(input).val);
		
		double[][] output = new double[nRows][nCols];
		
		Mat subMat;
		for(int i = 0; i < nRows; i++){
			for(int j = 0; j < nCols; j++){
				subMat = input.submat(i*submatrixSize, (i+1)*submatrixSize - 1, 
						j*submatrixSize, (j+1)*submatrixSize - 1);
				output[i][j] = (double) sumVector(Core.sumElems(subMat).val) / (double) bigSum;
			}
		}
		
		return output;
	}
	
	public static double sumVector(double[] vec){
		double sum = 0;
		for(double d : vec)
			sum += d;
		return sum;
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		
		Mat m1 = Highgui.imread("distrib/set1/changed/pair_0011_inbound.jpg",Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		Mat m1_out = Highgui.imread("distrib/set1/changed/pair_0011_outbound.jpg",Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		Mat m2 = new Mat(m1.size(), m1.type());
		Mat m2_out = new Mat(m1.size(), m1.type());
		Mat m3 = new Mat(m1.size(), m1.type());
		Mat m3_out = new Mat(m1.size(), m1.type());
		Mat m4 = new Mat(m1.size(), m1.type());		
		Mat m4_out = new Mat(m1.size(), m1.type());
		Mat m5 = new Mat(m1.size(), m1.type());
		Mat m5_out = new Mat(m1.size(), m1.type());
		Mat m6 = new Mat(m1.size(), m1.type());

		m2 = GaussPrep.blur(m1, 55);
		m2_out = GaussPrep.blur(m1_out, 55);
		
		Imgproc.equalizeHist(m2, m3);
		Imgproc.equalizeHist(m2_out, m3_out);
		
		Core.normalize(m2, m4);
		Core.normalize(m2_out, m4_out);
		
		m5 = GaussPrep.subtract(m3, m3_out);
		m5_out = GaussPrep.subtract(m3_out, m3);
		
		Core.add(m5, m5_out, m6);
		
//		Imshow im = new Imshow("regular subtraction");
//		im.showImage(m5);
//		
//		Imshow im2 = new Imshow("fucky subtraction");
//		im2.showImage(m5_out);
		
		Imshow im3 = new Imshow("all mish-mashed togetha");
		im3.showImage(m6);
		
		int pix = 20;
		double[][] test = getSubmatrixSums(m6, pix);
		System.out.println("[" + test.length + ", " + test[0].length + "]");
		for(int i = 0; i < test.length; i++){
			System.out.print("[");
			for(int j = 0; j < test[0].length; j++){
				System.out.print(test[i][j] + ", ");
			}
			System.out.print("]\n");
		}
	}
}	
