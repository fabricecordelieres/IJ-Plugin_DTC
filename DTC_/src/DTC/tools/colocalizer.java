package DCT.tools;

import java.awt.Color;

import DCT.tools.dataHandler.PointSerie;
import DCT.tools.utilities.utilities;
import ij.WindowManager;
import ij.gui.Overlay;
import ij.gui.Roi;

/**
 * This class handles co-localization of individual point-like tracked structures
 * @author fab
 *
 */
public class colocalizer {
	/** Maximum allowed proximity distance, in pixels **/
	static float maxProximity=6;
	
	/** Maximum allowed co-localization distance, in pixels **/
	static float maxColoc=3;

	/**
	 * Sets all parameters:
	 * @param proximity maximum allowed proximity distance, in pixels
	 * @param coloc maximum allowed co-localisation distance, in pixels
	 */
	public static void setParameters(float proximity, float coloc) {
		maxProximity=proximity;
		maxColoc=coloc;
	}
	
	/**
	 * Sets the maximum allowed proximity distance, in pixels
	 * @param proximity maximum allowed proximity distance, in pixels
	 */
	public static void setProximityThreshold(float proximity) {
		maxProximity=proximity;
	}

	/**
	 * Sets the maximum allowed co-localization distance, in pixels
	 * @param coloc maximum allowed co-localization distance, in pixels
	 */
	public static void setColocalizationThreshold(float coloc) {
		maxColoc=coloc;
	}
	
	/**
	 * Tags all detections with Coloc_ or Prox_ keyword and the distance to the colocalized/proximal element
	 * @param detections a PointSerie 2D array, first coordinate being the channel and second coordinate being the timepoint
	 */
	public static void tag(PointSerie[][] detections){
		//Loop for all timepoints
		for(int i=0; i<detections[0].length; i++) {
			//For each point, compare distances and stores only prox/coloc points
			tag(detections[0][i], detections[1][i]);
		}
	}
	
	/**
	 * Tags all detections from channel 1 and 2 depending on the proximity/co-localization parameters
	 * @param channel1 detections for channel1
	 * @param channel2 detections for channel2
	 */
	public static void tag(PointSerie channel1, PointSerie channel2) {
		if(channel1!=null && channel2!=null) {
			for(int i=0; i<channel1.getNPoints(); i++) {
				for(int j=0; j<channel2.getNPoints(); j++) {
					float distance=channel2.getPoint(j).getDistance(channel1.getPoint(i));
					
					if(distance<=maxColoc) {
						channel1.getPoint(i).setTag("Coloc_"+distance);
						channel2.getPoint(j).setTag("Coloc_"+distance);
					}
					
					if(distance>maxColoc && distance<=maxProximity) {
						if(!(channel1.getPoint(i).getTag().startsWith("Coloc"))) channel1.getPoint(i).setTag("Prox_"+distance);
						if(!(channel2.getPoint(j).getTag().startsWith("Coloc"))) channel2.getPoint(j).setTag("Prox_"+distance);
					}
				}
			}
		}	
	}
	
	/**
	 * Generates a preview of colocalization 
	 * @param channel1 detections on channel 1
	 * @param channel2 detections on channel 2
	 * @param radius radius for display, in pixels, a a 2D array
	 */
	public static void previewColoc(PointSerie channel1, PointSerie channel2, double[] radius) {
		tag(channel1, channel2);
		if(channel1!=null && channel2!=null) {
			PointSerie[] channels=new PointSerie[] {channel1, channel2};
			
			Overlay ov=WindowManager.getCurrentImage().getOverlay();
			
			for(int i=0; i<channels.length; i++) {
				for(int j=0; j<channels[i].getNPoints(); j++) {
					Object tag=channels[i].getPoint(j).getTag();
					if(tag!=null) {
						Roi roi=utilities.getCross(channels[i].getPoint(j).x, channels[i].getPoint(j).y, (float) radius[i]);
						
						if((""+tag).startsWith("Coloc")) roi.setStrokeColor(Color.CYAN);
						if((""+tag).startsWith("Prox")) roi.setStrokeColor(Color.MAGENTA);
						
						ov.add(roi);
					}
				}
			}
		}
	}
}
