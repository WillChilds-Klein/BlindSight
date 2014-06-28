import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

//import com.atul.JavaOpenCV.Imshow;

public class BlindSight{
	
	public static void main(String[] args) throws FileNotFoundException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		//System.out.println("Correct\tWrong");
		
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
		
		//testAll();
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		Mat input1 = Highgui.imread("/Users/austinstone/Desktop/inbound1.jpg",Highgui.CV_LOAD_IMAGE_GRAYSCALE);
//		Mat input2 = Highgui.imread("/Users/austinstone/Desktop/outbound1.jpg",Highgui.CV_LOAD_IMAGE_GRAYSCALE);
//		//EdgyAF.getEdgeConfidence(input1, input2); 
//		GaussPrep.compare("/Users/austinstone/Desktop/inbound1.jpg", "/Users/austinstone/Desktop/outbound1.jpg");
//		System.out.println("heeeere"); 
	}
	
	public static void doSubtract() {
		/*
		Mat m1 = Highgui.imread(args[1],Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		Mat m2 = Highgui.imread(args[2],Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		*/
		Mat input1 = Highgui.imread("/Users/austinstone/Desktop/inbound1.jpg",Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		Mat input2 = Highgui.imread("/Users/austinstone/Desktop/outbound1.jpg",Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		Mat[] threshholdArrayInput1 = grayscaleToThreshold(input1, 5);
		Mat[] threshholdArrayInput2 = grayscaleToThreshold(input2, 5);
		
		for (int i = 0; i < threshholdArrayInput1.length; i++) {
			Mat bwInput1 = threshholdArrayInput1[i]; 
			Mat bwInput2 = threshholdArrayInput2[i]; 
			
			Mat diffInput1Input2BW = new Mat(bwInput1.size(), bwInput1.type());
			Mat diffInput1Input2BWTemp = new Mat(bwInput1.size(), bwInput1.type());
		    Core.subtract(bwInput1, bwInput2, diffInput1Input2BW);
		    Core.subtract(bwInput2, bwInput1, diffInput1Input2BWTemp);
		    Core.add(diffInput1Input2BW, diffInput1Input2BWTemp , diffInput1Input2BW);
		   // Imshow bwDiff = new Imshow("bwDiff for threshold" + i);
		   // Imshow bwInput1Im = new Imshow("bwInput1");
		   // Imshow bwInput2Im = new Imshow("bwInput2");
		   // Imshow input1Raw = new Imshow("Input1Raw"); 
		   // Imshow input2Raw = new Imshow("Input2Raw"); 
		   // bwDiff.showImage(diffInput1Input2BW);
		    
			
			
		}
		
//		Mat bwInput1 = threshholdArrayInput1[0];
//		Mat bwInput2 = threshholdArrayInput2[0];
//		Mat diffInput1Input2BW = new Mat(bwInput1.size(), bwInput1.type());
//		Mat diffInput1Input2BWTemp = new Mat(bwInput1.size(), bwInput1.type());
//	    Core.subtract(bwInput1, bwInput2, diffInput1Input2BW);
//	    Core.subtract(bwInput2, bwInput1, diffInput1Input2BWTemp);
//	    Core.add(diffInput1Input2BW, diffInput1Input2BWTemp , diffInput1Input2BW);
//	    Imshow bwDiff = new Imshow("bwDiff");
//	    Imshow bwInput1Im = new Imshow("bwInput1");
//	    Imshow bwInput2Im = new Imshow("bwInput2");
//	    Imshow input1Raw = new Imshow("Input1Raw"); 
//	    Imshow input2Raw = new Imshow("Input2Raw"); 
//	    bwDiff.showImage(diffInput1Input2BW);
//	    bwInput1Im.showImage(bwInput1);
//	    bwInput2Im.showImage(bwInput2);
//	    input1Raw.showImage(input1); 
//	    input2Raw.showImage(input2);
	    
	    
//		Mat m2 = new Mat(m1.size(), m1.type());
		/*
		Mat loadAsColorEdges = GetEdges.getEdges(m1);
		
//		Imshow im = new Imshow("Original Image"); 
//		im.showImage(m1);
		
		Mat[] arr = grayscaleToThreshold(m1, 5);
		Mat[] arrEdgesThenBW = grayscaleToThreshold(loadAsColorEdges, 5);
		Mat m2 = arr[2];
		Mat arrEdgesThenBWM0 = arrEdgesThenBW[1];
		Imshow im2 = new Imshow("middle threshold");
		im2.showImage(m2);
		Mat mWithEdges = GetEdges.getEdges(m2);
		Imshow edges = new Imshow("LoadAsBWThenEdges");
		Imshow edgesThenBWImShow = new Imshow("edgesThenBW");
		Imshow edgesOnly = new Imshow("Edges only loaded as color");
		edgesOnly.showImage(loadAsColorEdges);
		edgesThenBWImShow.showImage(arrEdgesThenBWM0);
		edges.showImage(mWithEdges);*/
	}
	
	public static Mat[] grayscaleToThreshold(Mat input, int numThresholds){
		int threshold_value = 0;
		int threshold_type = Imgproc.THRESH_BINARY;
		int max_value = 255;
		
		Mat[] output = new Mat[numThresholds];
		
		for(int i = 0; i < numThresholds; i++){
			output[i] = new Mat(input.size(), input.type());
			threshold_value = (int) (((float) (i+1) / ((float) numThresholds+1) )* (float) max_value);
			System.out.println("Threshhold val = " + threshold_type); 
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
		
		for(int i = 1; i <=3; i++){
			testMethods(i, true);
			testMethods(i, false);
		}
	}
	
	public static void testMethods(int setNum, boolean changed){
		int numTrue = 0, numFalse = 0;
		int count = 50, i = 0;
		
		DecimalFormat formatter = new DecimalFormat("0000");
		//String beg = "set"+ setNum + "/" + (changed? "changed" : "unchanged") + "/pair_", end1, end2;
		String beg = "set"+ setNum + "/" + "changed" + "/pair_", end1, end2;

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

			GaussPrep.compare(file1, file2); 
			//if(GaussPrep.compare(file1, file2) == changed){
				numTrue++;
			//}
			//else
				numFalse++;
		}
		System.out.println(numTrue+"\t"+numFalse);
	
	}
}
