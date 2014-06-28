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
	
	// returns array: {[maxRatio],[maxRow],[maxCol]}
	public static double[] maxToAvgRatio(double[][] arr){
		double max = -1, sum = 0, ratio, avg;
		double output[] = new double[3];
		for(int i = 0; i < arr.length; i++){
			for(int j = 0; j < arr[0].length; j++){
				if(arr[i][j] > max){
					max = arr[i][j];
					output[1] = i;
					output[2] = j;
				}
				sum += arr[i][j];
			}
		}
		avg = sum / (arr.length * arr[0].length);
		ratio = max / avg;
		
		output[0] = ratio;
		return output;
	}
	
	// array @param max is array output by maxToAvgRatio
	// returns ratio of maxVal to avg of 8(ish) surrounding squares
	public static double maxToAvgOfSurroundingLayer(double[][] arr, double[] max){
		if(max.length != 3)
			return -1;
		int maxRow = (int) max[1], maxCol = (int) max[2];
		double layerSum = 0;
		int nSquaresSurrounding = 0;
		
		if(isValid(maxRow-1, maxCol-1, arr.length, arr[0].length)){
			layerSum += arr[maxRow-1][maxCol-1];
			nSquaresSurrounding++;
		} if(isValid(maxRow-1, maxCol, arr.length, arr[0].length)){
			layerSum += arr[maxRow-1][maxCol];
			nSquaresSurrounding++;
		} if(isValid(maxRow-1, maxCol+1, arr.length, arr[0].length)){
			layerSum += arr[maxRow-1][maxCol+1];
			nSquaresSurrounding++;
		} if(isValid(maxRow, maxCol+1, arr.length, arr[0].length)){
			layerSum += arr[maxRow][maxCol+1];
			nSquaresSurrounding++;
		} if(isValid(maxRow+1, maxCol+1, arr.length, arr[0].length)){
			layerSum += arr[maxRow+1][maxCol+1];
			nSquaresSurrounding++;
		} if(isValid(maxRow+1, maxCol, arr.length, arr[0].length)){
			layerSum += arr[maxRow+1][maxCol];
			nSquaresSurrounding++;
		} if(isValid(maxRow+1, maxCol-1, arr.length, arr[0].length)){
			layerSum += arr[maxRow+1][maxCol-1];
			nSquaresSurrounding++;
		} if(isValid(maxRow, maxCol-1, arr.length, arr[0].length)){
			layerSum += arr[maxRow][maxCol-1];
			nSquaresSurrounding++;
		}
			
		System.out.println("!!!" + nSquaresSurrounding + "!!" + maxRow + " " + maxCol + " " + arr[maxRow][maxCol]);
		
		return max[0] / (layerSum / nSquaresSurrounding);
		
	}
	
	public static boolean isValid(int row, int col, int nRows, int nCols){
		return (row >= 0 && col >= 0) && (row < nRows && col < nCols);
	}  
	
	public static double sumVector(double[] vec){
		double sum = 0;
		for(double d : vec)
			sum += d;
		return sum;
	}
	
	public static void main(String[] args) {
System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		
		String file1 = "distrib/set1/changed/pair_0009_inbound.jpg";
		String file2 = "distrib/set1/changed/pair_0009_outbound.jpg";

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
//		
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
		
		double[] max = maxToAvgRatio(test);
		test[0][0] = 1; max[1] = 6; max[2] = 0;
		System.out.println(maxToAvgOfSurroundingLayer(test, max));
	}
}	
