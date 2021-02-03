package DTC.tools;


import java.awt.Color;
import java.awt.Polygon;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import DTC.tools.dataHandler.PointSerie;
import DTC.tools.utilities.utilities;
import ij.CompositeImage;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.plugin.RoiEnlarger;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.MaximumFinder;
import ij.plugin.filter.RankFilters;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

/**
 * This class handles detection of individual point-like structures
 * @author fab
 *
 */
public class detector {
	/** Colors to be used for channels **/
	public static final Color[] COLORS=new Color[] {new Color(127, 0, 0), new Color(0, 127, 0), new Color(0, 0, 127), Color.CYAN, Color.MAGENTA, Color.YELLOW};
	
	/** Radius for filtering, in pixels **/
	public static double radius=2;
	
	/** Tolerance for local maxima detection **/
	static double tolerance=16;
	
	/** If false, only local maxima detection will be performed. If true, center of mass will be retrieved
	 * within the filtering area (on the original image) **/ 
	static boolean doTuning=false;
	
	/** If set, will be used to tag the outputs with a channel number **/ 
	static int channel=0;
	
	/** If set, will be used to tag the outputs with color **/ 
	static Color color=null;
	
	/** If true, enlarges output ROIs of a radius equal to the filtering radius **/ 
	public static boolean doEnlarge=false;
	
	/** Setting to add ROIs to RoiManager as crosses **/
	public static final int CROSS=0;
	
	/** Setting to add ROIs to RoiManager as circles **/
	public static final int CIRCLE=1;
	
	/**
	 * Resets all parameters to default (radius:2, tolerance: 16, enlarge: 0, tuning: false).
	 */
	public static void resetParameters() {
		radius=2;
		tolerance=16;
		doTuning=false;
		
		channel=0;
		color=null;
		doEnlarge=false;
	}
	
	/**
	 * Sets all parameters:
	 * @param rad radius for filtering (default: 2).
	 * @param tol tolerance for local maxima detection (default: 16).
	 * @param doTune If false, only local maxima detection will be performed. If true, center of mass will be retrieved within the filtering area (on the original image, default: false).
	 * @param C If set, will be used to tag the outputs with a channel number.
	 * @param col If set, will be used to tag the outputs with color.
	 * @param doEnl If true, enlarges output ROIs of a radius equal to the filtering radius.
	 */
	public static void setParameters(double rad, double tol, boolean doTune, int C, Color col, boolean doEnl) {
		radius=rad;
		tolerance=tol;
		doTuning=doTune;
		channel=C;
		color=col;
		doEnlarge=doEnl;
	}
	
	/**
	 * Sets the radius for filtering (default: 2).
	 * @param rad filter radius, in pixels.
	 */
	public static void setRadius(double rad) {
		radius=rad;
	}
	
	/**
	 * Sets the tolerance for local maxima detection (default: 16).
	 * @param tolerance tolerance for local maxima detection.
	 */
	public static void setTolerance(double tol) {
		tolerance=tol;
	}
	
	/**
	 * If false, only local maxima detection will be performed. If true, center of mass will be retrieved within the filtering area (on the original image, default: false).
	 * @param doTune set to true to retrieve centre of mass instead of local maxima.
	 */
	public static void setDoTuning(boolean doTune) {
		doTuning=doTune;
	}
	
	/**
	 * Sets channel number for output (default: 0).
	 * @param C the channel number for output.
	 */
	public static void setChannel(int C) {
		channel=C;
	}
	
	/**
	 * Sets the color for output (default: null).
	 * @param col color for output.
	 */
	public static void setColor(Color col) {
		color=col;
	}
	
	/**
	 * If true, enlarges output ROIs of a radius equal to the filtering radius.
	 * @param doTune set to true to enlarge the displayed ROIs.
	 */
	public static void setDoEnlarge(boolean doEnl) {
		doEnlarge=doEnl;
	}
	
	/**
	 * Performs detection for all slices of the stack, using multithreading
	 * @param ip the ImagePlus on which detection has to be performed
	 * @param roi roi in which analysis should be performed or null for the full image
	 * @param radius radius of the circular ROI around each point in which to quantify fluorescence (in pixels)
	 * @return an array of PointSerie
	 */
	public static PointSerie[] detect(ImagePlus ip, Roi roi, int radius) {
		//Checks
		if(ip.getNSlices()!=1 || ip.getNChannels()!=1) throw new IllegalArgumentException("Detector requires a one channel 2D+t hyperstack");
		
		//Prepare threads array
		int nProcessors=Runtime.getRuntime().availableProcessors();
		Thread[] threads=new Thread[nProcessors];
		
		AtomicInteger ai=new AtomicInteger(0); //Hypertack position, starting at 1 (i+1 when calling the processor, later in the code)
		int nImages=ip.getStackSize();
		
		//Prepare output data container
		PointSerie[] det=new PointSerie[nImages];
		
		//Each thread will process part of the full hyperstack images
		for(int ithread=0; ithread<nProcessors; ithread++) {
			threads[ithread]=new Thread() {
				public void run() {
					for(int i=ai.getAndIncrement(); i<nImages; i=ai.getAndIncrement()) {
						ImageProcessor imp=ip.getStack().getProcessor(i+1);
						PointRoi pointRoi=detect(imp, roi);
						float intensities[]=new float[pointRoi.size()];
						
						//Fluorescence quantification
						for(int j=0; j<pointRoi.size(); j++) {
							imp.setRoi(new OvalRoi(pointRoi.getXCoordinates()[j]-radius, pointRoi.getYCoordinates()[j]-radius, 2*radius-1, 2*radius-1));
							intensities[j]=(float) imp.getStatistics().mean;
						}
						
						
						det[i]=new PointSerie(pointRoi, intensities);
					}
				}
			};
			
		}
		
		//Run the threads
		for(int i=0; i<threads.length; i++) {
			threads[i].setPriority(Thread.NORM_PRIORITY);
			threads[i].start();
		}
		
		//Join the threads
		try {
			for(int i=0; i<threads.length; i++) {
				threads[i].join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ip.setPosition(1);
		
		return det;
	}
	
	/**
	 * Performs detection and returns the result as a unique ROI, either made of individual points or circles. The input ImageProcessor is first 
	 * duplicated then the copy is subjected to a median filter, rolling-ball background subtraction and gaussian blur (same radius for all).
	 * Local maxima are retrieved as a ROI (tolerance set according to the input parameter). Depending on the enlarge parameter, the output is set
	 * to multiple points (enlarge<=0) or circles. Null is returned when no maximum has been found.
	 * @param iproc input ImageProcessor to analyze.
	 * @param roi roi in which analysis should be performed or null for the full image
	 * @return detected structures as a unique ROI or null
	 */
	public static PointRoi detect(ImageProcessor iproc, Roi roi) {
		iproc.resetRoi();
		ImageProcessor proc=iproc.duplicate();
		if(roi!=null) proc.setRoi(roi);
		
		proc.setRoi(roi);
		//To make sure the outside pixels are not taken into account
		proc.setColor(Color.black);
		proc.fillOutside(roi);
		
		RankFilters rf=new RankFilters();
		rf.rank(proc, radius, RankFilters.MEDIAN);
		
		BackgroundSubtracter bs=new BackgroundSubtracter();
		bs.rollingBallBackground(proc, radius, false, false, false, false, false);
		
		proc.blurGaussian(radius);
		
		MaximumFinder mf=new MaximumFinder();
		PointRoi pointRoi=new PointRoi(mf.getMaxima(proc, tolerance, false));
		
		proc=null;
		if(pointRoi.getNCoordinates()==0)  return null;
		if(doTuning) pointRoi=tuneDetections(iproc, roi);
		return pointRoi;
	}
	
	/**
	 * Generates a preview of detections by fully processing the current frame
	 */
	public static PointSerie previewDetections(ImagePlus img, Roi roi) {
		if(img==null) throw new IllegalArgumentException("An ImagePlus should be opened first !");
		Overlay ov=img.getOverlay();
		if(ov==null) ov=new Overlay();
		
		Roi pointRoi=detect(((CompositeImage) img).getChannelProcessor(), roi);
		PointSerie out=null;
		
		if(pointRoi!=null) {
			float[] intensities=new float[pointRoi.size()];
			Arrays.fill(intensities, Float.NaN);
			
			out=new PointSerie((PointRoi) pointRoi, intensities);
			if(color!=null) pointRoi.setStrokeColor(color);
			pointRoi=RoiEnlarger.enlarge(pointRoi, radius*1.1);
			ov.add(pointRoi);
		}
		
		img.setOverlay(ov);
		
		return out;
	}
	
	/**
	 * Performs tuning on the detections by placing a circle around the current detection, and defining the location
	 * of its centre of mass. Detections are updated with those new coordinates.
	 * @param iproc input ImageProcessor to analyze.
	 * @param roi roi to analyze.
	 * @return a PointRoi object containing the new location or null if the input ROI and/or ImageProcessor is null.
	 */
	public static PointRoi tuneDetections(ImageProcessor iproc, Roi roi) {
		if(roi!=null && iproc!=null) {
			Polygon pol=roi.getPolygon();
			int[] xPol=pol.xpoints;
			int[] yPol=pol.ypoints;
			float[] x=new float[pol.npoints];
			float[] y=new float[pol.npoints];
			
			for(int i=0; i<pol.npoints; i++) {
				iproc.setRoi(RoiEnlarger.enlarge(new PointRoi(xPol[i], yPol[i]), radius));
				x[i]=(float) iproc.getStatistics().xCenterOfMass;
				y[i]=(float) iproc.getStatistics().yCenterOfMass;
			}
			
			return new PointRoi(x, y);
		}else {
			return null;
		}
	}
	
	/**
	 * Pushes all the detections to the ROI Manager and returns them as an array of PointSerie.
	 * @param ip the ImagePlus on which detection has to be performed.
	 * @param roi roi in which analysis should be performed or null for the full image
	 * @param radius radius of the circular ROI around each point in which to quantify fluorescence (in pixels)
	 * @return an array of PointSerie.
	 */
	public static PointSerie[] sendDetectionsToRoiManager(ImagePlus ip, Roi roi, int radius) {
		PointSerie[] det=detect(ip, roi, radius);
		
		RoiManager rm=RoiManager.getInstance();
		if(rm==null) {
			rm=new RoiManager();
			rm.setVisible(true);
		}
		
		for(int i=0; i<det.length; i++) {
			if(det[i]!=null) {
				Roi pointRoi=det[i].toPointRoi();
				if(pointRoi!=null && doEnlarge) pointRoi=RoiEnlarger.enlarge(det[i].toPointRoi(), radius);
				
				if(color!=null) pointRoi.setStrokeColor(color);
				pointRoi.setName("Detection_"+(channel==-1?"":"Channel "+channel+" ")+"Frame "+(i+1));
				pointRoi.setPosition(channel==-1?0:channel, 1, i+1);
				
				rm.add((ImagePlus) null, pointRoi, -1);
			}
		}
		
		return det;
	}
	
	/**
	 * Pushes all the detections to the ROI Manager and returns them as an array of PointSerie.
	 * @param ip the ImagePlus on which detection has to be performed.
	 * @return an array of PointSerie.
	 */
	
	/**
	 * Pushes selected detections to the ROI Manager
	 * @param detections the detections to push
	 * @param params the parameters for ROI creation
	 * @param type the type of ROI to push: crosses or circles
	 * @param tag a tag to filter the ROIs to select: either Coloc, Prox or NonProxColoc
	 */
	public static void sendFilteredDetectionsToRoiManager(PointSerie[][] detections, int[][] params, int type, String tag) {
		RoiManager rm=RoiManager.getInstance();
		if(rm==null) {
			rm=new RoiManager();
			rm.setVisible(true);
		}
		
		for(int i=0; i<2; i++) {
			for(int j=0; j<detections[i].length; j++) {
				for(int k=0; k<detections[i][j].getNPoints(); k++) {
					
					String currTag=detections[i][j].getPoint(k).getTag();
					
					Roi roi=null;
					
					switch(type) {
						case CROSS:
							roi=utilities.getCross(detections[i][j].getPoint(k).x, detections[i][j].getPoint(k).y, (float) params[i][0]);
							break;
						case CIRCLE:
							roi=detections[i][j].getPoint(k).toPointRoi();
							if(roi!=null && doEnlarge) roi=RoiEnlarger.enlarge(roi, radius);
							break;
					}
					
					
					String roiName="Detection_"+tag+" Channel "+(i+1)+" Frame "+(j+1)+" Detection "+(k+1)+"/"+detections[i][j].getNPoints();
					roi.setName(roiName);
					roi.setPosition(i+1, 1, j+1);
					
					if(currTag.startsWith("Coloc")) {
						roi.setStrokeColor(Color.CYAN);
						if(!tag.equals("Coloc")) roi=null;
					}
					
					if(currTag.startsWith("Prox")) {
						roi.setStrokeColor(Color.MAGENTA);
						if(!tag.equals("Prox")) roi=null;
					}
					
					if(currTag=="" && !tag.equals("NonProxColoc")) roi=null;
					
					if(roi!=null) rm.add((ImagePlus) null, roi, -1);
				}
			}
		}
	}
}
