import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

//import com.atul.JavaOpenCV.Imshow;

public class BlindSight{
	
	public final static double MEAN = 25;
	
	public static void main(String[] args) throws FileNotFoundException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		//System.out.println("Correct\tWrong");
		
		/** /
		String input1 = args[0];
		String input2 = args[1];
		
		double countConf = GaussPrep.compare(input1, input2); 
		double ratio = Blob.doBlob(input1, input2); 
		double confidence = countConf;
		if (confidence < 0)
			confidence = 0; 
		else if (confidence > 100)
			confidence = 100; 
		if (ratio > 7)
			confidence = 100; 
		System.out.println(confidence); 
		/**/
		
		testAll(); 
	}
	
	public static double compare(String file1, String file2) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		
		Mat m1 = Highgui.imread(file1,Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		Mat m1_out = Highgui.imread(file2,Highgui.CV_LOAD_IMAGE_GRAYSCALE);	
		Mat m2 = new Mat(m1.size(), m1.type());
		Mat m2_out = new Mat(m1.size(), m1.type());
		Mat m3 = new Mat(m1.size(), m1.type());
		Mat m3_out = new Mat(m1.size(), m1.type());
		Mat m5 = new Mat(m1.size(), m1.type());
		Mat m5_out = new Mat(m1.size(), m1.type());
		Mat m6 = new Mat(m1.size(), m1.type());

		m1 = Preprocess.blur(m1, 41);
		m1_out = Preprocess.blur(m1_out, 41);

		Imgproc.equalizeHist(m2, m2);
		Imgproc.equalizeHist(m2_out, m2_out);
		
		m2 = Preprocess.convertBack(Preprocess.normalize(m1));
		m2_out = Preprocess.convertBack(Preprocess.normalize(m1_out));
		
		m5 = subtract(m3, m3_out);
		m5_out = subtract(m3_out, m3);
		
		Core.add(m5, m5_out, m6);
		
		Blob.getSubmatrixSums(m6, 20); 
		
		Double bigSum = Blob.sumVector(Core.sumElems(m6).val)/m6.total(); 
		double confidence = 50+(bigSum-MEAN)*5;
		
		return confidence;
	}
	
	public static Mat subtract(Mat input1, Mat input2){
		Mat output = new Mat(input1.size(), input1.type());
		Core.subtract(input1, input2, output);
		return output;
	}

	public static void testAll(){
		for(int i = 1; i <=3; i++){
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

			compare(file1, file2); 
//			if(GaussPrep.compare(file1, file2) == changed)
				numTrue++;
//			else
				numFalse++;
		}
		System.out.println(numTrue+"\t"+numFalse);
	
	}
}
