import org.opencv.core.*;
//import org.opencv.highgui.*;

//import com.atul.JavaOpenCV.Imshow;

public class GetEdges {
   
    	
    	public static Mat getEdges(Mat inputImage)
    	{
    	   Mat croppedInput = inputImage.submat(0, inputImage.rows(), 0, inputImage.cols()-1);
    	   Mat shiftedMat = new Mat(inputImage.size(), inputImage.type());
    	  
    	  
    	   //shift up 
    	   Mat subMatInput = inputImage.submat(1, inputImage.rows(),0, inputImage.cols());
    	   Mat firstSectorRows = inputImage.submat(inputImage.rows()-1, inputImage.rows(), 0, inputImage.cols());
    	   subMatInput.copyTo(shiftedMat);
    	   shiftedMat.push_back(firstSectorRows);
    	  
    	   
     
    	  
    	   
    	   //shift right
    	   Mat subMatShifted = shiftedMat.submat(0, shiftedMat.rows(), 1, shiftedMat.cols());
    	   Mat shiftedMat2 = new Mat(subMatShifted.size(), subMatShifted.type());
    	   subMatShifted.copyTo(shiftedMat2);
    	 
    	  
    	   Mat diffImage1 = new Mat(croppedInput.size(), croppedInput.type());
    	   Mat diffImage2 = new Mat(croppedInput.size(), croppedInput.type());
    	   Mat outputImage = new Mat(croppedInput.size(), croppedInput.type());
    	   
    	  Core.subtract(shiftedMat2, croppedInput, diffImage1);
    	  Core.subtract(croppedInput, shiftedMat2, diffImage2);
    	  Core.add(diffImage1, diffImage2, outputImage);

    	   //Imshow im = new Imshow("outputImage");
    	   //im.showImage(outputImage);
    	   return outputImage;
    	
    	  
    	}
    	  
    	 
    	       
    
}