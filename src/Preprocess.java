import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class Preprocess {

	public static Mat multiChannelHistEq(Mat input){
		List<Mat> channels = new ArrayList<Mat>();
		Core.split(input, channels);
		
		Imgproc.equalizeHist(channels.get(0), channels.get(0));
		Imgproc.equalizeHist(channels.get(1), channels.get(1));
		Imgproc.equalizeHist(channels.get(2), channels.get(2));
		
		Mat merge = new Mat(input.size(), CvType.CV_8UC3);
		Core.merge(channels, merge);
		Mat output = new Mat(input.size(), CvType.CV_8UC1);
		Imgproc.cvtColor(merge, output, Imgproc.COLOR_BayerGR2GRAY);
		
		return output;
	}
	
}