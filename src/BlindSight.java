import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.atul.JavaOpenCV.Imshow;

public class BlindSight{
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		System.out.println("Correct\tWrong");
		testAll();
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
	
	public static void testAll(){
		
		for(int i = 1; i <=3 ; i++){
			testMethods(i, true);
			testMethods(i, false);
		}
	}
	
	public static void testMethods(int setNum, boolean changed){
		int numTrue = 0, numFalse = 0;
		int count = 50, i = 0;
		
		DecimalFormat formatter = new DecimalFormat("0000");
		String beg = "set"+ setNum + "/" + (changed? "changed" : "unchanged") + "/pair_", end1, end2;
		
		if(setNum == 2){
			end1 = "_before.jpg";
			end2 = "_later.jpg";
			i = changed? 1 : 0;
			
			count = i == 1? 51 : 50;
		}else {
			end1 = "_inbound.jpg";
			end2 = "_outbound.jpg";
			if(setNum == 3 && !changed){
				i = 932; 
				count = 932 + 51;
			} else if (setNum == 1 && changed){
				i = 1;
				count = 51;
			}
			
		}
		
		for( ; i < count; i+=2){
			String file1 = beg + formatter.format(i) + end1;
			String file2 = beg + formatter.format(i) + end2;

			if(GaussPrep.compare(file1, file2) == changed){
				numTrue++;
			}
			else
				numFalse++;
		}
		System.out.println(numTrue+"\t"+numFalse);
	
	}
}