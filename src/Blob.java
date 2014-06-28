import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

//import com.atul.JavaOpenCV.Imshow;

public class Blob {
	
	// cuts off lower right portion of image in case of non-squareness
	public static double[][] getSubmatrixSums(Mat input, int submatrixSize){
		int nRows = input.rows() / submatrixSize;
		int nCols = input.cols() / submatrixSize;

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
	
	// returns array: {maxRatio,maxRow,maxCol}
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
	
	public static double doBlob(String file1, String file2) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		
		Mat m1 = Highgui.imread(file1,Highgui.CV_LOAD_IMAGE_COLOR);
		Mat m1_out = Highgui.imread(file2,Highgui.CV_LOAD_IMAGE_COLOR);
		Mat m2 = new Mat(m1.size(), CvType.CV_8UC1);
		Mat m2_out = new Mat(m1.size(), CvType.CV_8UC1);
		Mat m3 = new Mat(m2.size(), m2.type());
		
		m2 = Preprocess.multiChannelHistEq(m1);
		m2_out = Preprocess.multiChannelHistEq(m1_out);
		
		m2 = Preprocess.blur(m2, Preprocess.BLUR_PARAM);
		m2_out = Preprocess.blur(m2_out, Preprocess.BLUR_PARAM);
		
		Mat[] hope = Preprocess.matchFeatures(m2, m2_out);
		m2 = hope[0];
		m2_out = hope[1];
		
		m3 = BlindSight.subtract(m2_out, m2);
		
//		Imshow im = new Imshow("da truth");
//		im.showImage(m3);
		
		int pix = 40;
		double[][] test = getSubmatrixSums(m3, pix);
		double ratio = maxToAvgRatio(test)[0]; 
		
//		System.out.println("[" + test.length + ", " + test[0].length + "]");
//		for(int i = 0; i < test.length; i++){
//			System.out.print("[");
//			for(int j = 0; j < test[0].length; j++){
//				System.out.print(test[i][j] + ", ");
//			}
//			System.out.print("]\n");
//		}
		
		return ratio; 
	}
	
}	
