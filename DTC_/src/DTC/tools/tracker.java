package DTC.tools;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import DTC.tools.dataHandler.PointSerie;
import DTC.tools.individualData.Point;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.text.TextPanel;
import ij.text.TextWindow;

/**
 * This class handles tracking of individual point-like structures
 * @author fab
 *
 */
public class tracker {
	/** Maximum allowed travel distance between two frames, in pixels **/
	static float maxDistance=10;
	
	/** Minimum number of tracked frames to consider as a proper track **/
	static int minTrackedFrames=3;
	
	/** If set, will be used to tag the outputs with a channel number **/ 
	static int channel=0;
	
	/** If set, will be used to tag the outputs with color **/ 
	static Color color=null;
	
	
	/** If true, the image is zoomed in when clicking on one detection **/
	static boolean doZoomIn=false;
	
	/** If doZoomIn is true, the magnification to be used when zooming in **/
	static int zoomInValue=100;
	
	/** The line width for tracks display **/
	static int lineWidth=1;
	
	// The circular ROI's radius when displaying individual detections **/
	static int roiRadius=2;
	
	
	/**
	 * Resets all parameters to default (radius:2, tolerance: 16, enlarge: 0, tuning: false).
	 */
	public static void resetParameters() {
		maxDistance=5;
		minTrackedFrames=3;
		
		channel=0;
		color=null;
	}
	
	/**
	 * Sets all parameters:
	 * @param maxJump maximum allowed travel distance between two frames, in pixels (default: 5).
	 * @param minFrames minimum number of tracked frames to consider as a proper track (default: 3).
	 * @param C If set, will be used to tag the outputs with a channel number
	 * @param col If set, will be used to tag the outputs with color
	 */
	public static void setParameters(float maxJump, int minFrames, int C, Color col) {
		maxDistance=maxJump;
		minTrackedFrames=minFrames;
		
		channel=C;
		color=col;
	}
	
	/**
	 * Sets the maximum allowed travel distance between two frames, in pixels (default: 5).
	 * @param maxJump maximum allowed travel distance between two frames, in pixels.
	 */
	public static void setMaxDistance(float maxJump) {
		maxDistance=maxJump;
	}
	
	/**
	 * Sets the minimum number of tracked frames to consider as a proper track (default: 3).
	 * @param minFrames minimum number of tracked frames to consider as a proper track.
	 */
	public static void setMinTrackedFrames(int minFrames) {
		minTrackedFrames=minFrames;
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
	 * Performs nearest neighbor-based tracking on the input array of PointSerie (one time point per cell).
	 * Tracking is performed cell by cell, for each cell in turn trying to grab as many time points as possible in one shot.
	 * @param detections detections to link between frames, as an array of PointSerie (one time point per cell).
	 * @param maxRounds number of successive tracking sequences to be applied: increasing the number may help catch static structures in a crowded environment
	 * @return an array list of PointSerie, each element being a track.
	 */
	public static ArrayList<PointSerie> doNearestNeighborMaximizeTrack(PointSerie[] detections, int maxRounds) {
		//Required to keep the content of "detections" untouched: direct cloning doesn't work...
		PointSerie[] toAnalyze=new PointSerie[detections.length];
		for(int i=0; i<detections.length; i++) toAnalyze[i]=detections[i].clone();
		
		
		ArrayList<PointSerie> tracks=new ArrayList<PointSerie>();
		boolean stopRound=false;
		
		
		for(int nRounds=1; nRounds<=maxRounds; nRounds++) {
		
			for(int i=0; i<toAnalyze.length-1; i++) {
				//Get all points from the current timepoint
				PointSerie currPool=toAnalyze[i];
				
				for(int j=0; j<currPool.getNPoints(); j++) {
					stopRound=false;
					
					//Get the current point to test
					Point currPoint=currPool.getPoint(j);
					
					//Add it to a temporary track
					PointSerie tmpTrack=new PointSerie(currPoint);
					tmpTrack.setTag(currPoint.getTag());
					
					for(int k=i+1; k<toAnalyze.length; k++) {
						PointSerie nextPool=toAnalyze[k];
						
						//Get the closest point
						float[] data=getClosestPoint(currPoint, nextPool);
						
						//If found, add to the tmp track and removes it from the pool
						if(data!=null) {
							if(data[1]<=maxDistance) {
								//Set the current PointRoi to the one we have just found
								currPoint=nextPool.getPoint((int) data[0]);
								
								//Add it to a temporary track
								tmpTrack.add(currPoint);
								
								//Tag the track
								tmpTrack.setTag(tmpTrack.getTag()+"\t"+currPoint.getTag());
								
								//Remove the closest point from t+1 (nextPool, but on original data) and reset the counter !!!
								toAnalyze[k].remove((int) data[0]);
							}else {
								stopRound=true;
								k=toAnalyze.length;
							}
						}else {
							stopRound=true;
							k=toAnalyze.length;
						}
					}
					
					//Adds the tmpTrack to tracks if long enough
					if(tmpTrack.getNPoints()>minTrackedFrames) {
						tmpTrack.setName("Track_"+(tracks.size()+1));
						tracks.add(tmpTrack);
						toAnalyze[i].remove(j); //removes the first point as it also belongs to the track
					}
					
					//
					if(stopRound)  j=currPool.getNPoints();
				}
			}
		}
		
		return tracks;
	}
	
	/**
	 * Isolates the closest point from the input point within the input list, returns its index and distance.
	 * @param point the reference point.
	 * @param pointSerie the list of points to compare to.
	 * @return the index of the closest point and its distance .
	 */
	public static float[] getClosestPoint(Point point, PointSerie pointSerie) {
		float minDist=Float.MAX_VALUE;
		int index=-1;
		
		for(int i=0; i<pointSerie.getNPoints(); i++) {
			float currDist=point.getDistance(pointSerie.getPoint(i));
			
			if(currDist<minDist) {
				index=i;
				minDist=currDist;
			}
		}
		
		if(index==-1) return null;
		
		return new float[] {index, minDist};
	}
	
	/**
	 * Pushes all the tracks to the ROI Manager and returns them as a arrayList of PointSerie.
	 * @param detections detections to link between frames, as an array of PointSerie (one time point per cell).
	 * @param maxRounds number of successive tracking sequences to be applied: increasing the number may help catch static structures in a crowded environment
	 * @return an array list of PointSerie, each element being a track.
	 */
	public static ArrayList<PointSerie> sendTracksToRoiManager(PointSerie[] detections, int maxRounds) {
		getPrefs();
		
		ArrayList<PointSerie> tracks=doNearestNeighborMaximizeTrack(detections, maxRounds);
		
		RoiManager rm=RoiManager.getInstance();
		if(rm==null) {
			rm=new RoiManager();
			rm.setVisible(true);
		}
		
		for(int i=0; i<tracks.size(); i++) {
			if(tracks.get(i)!=null) {
				Roi roi=tracks.get(i).toPolyline();
				
				if(color!=null) roi.setStrokeColor(color);
				roi.setName("Tracking_"+(channel==-1?"":"Channel "+channel+" ")+"Frame "+(i+1));
				roi.setPosition(channel==-1?0:channel, 1, 0);
				roi.setStrokeWidth(lineWidth/10.0);
				
				rm.add((ImagePlus) null, roi, -1);
			}
		}
		
		return tracks;
	}
	
	/**
	 * Pushes selected tracks to the ROI Manager
	 * @param tracks the tracks to push
	 * @param tag a tag to filter the ROIs to select: either Coloc, Prox or NonProxColoc
	 */
	public static void sendTracksToRoiManager(ArrayList<ArrayList<PointSerie>> tracks, String tag) {
		getPrefs();
		
		RoiManager rm=RoiManager.getInstance();
		if(rm==null) {
			rm=new RoiManager();
			rm.setVisible(true);
		}
		
		for(int channel=0; channel<tracks.size(); channel++) {
			for(int i=0; i<tracks.get(channel).size(); i++) {
				if(tracks.get(channel).get(i)!=null) {
					
					String currTag=tracks.get(channel).get(i).getTag();
					
					Roi roi=tracks.get(channel).get(i).toPolyline();
					
					if(color!=null) roi.setStrokeColor(detector.COLORS[channel]);
					roi.setName("Track_"+(i+1)+(channel==-1?"":" Channel "+(channel+1)+" "));
					roi.setPosition(channel==-1?0:channel, 1, 0);
					roi.setStrokeWidth(lineWidth/10.0);
					
					if(!tag.equals("All")) {
						if((currTag.indexOf("Coloc")!=-1 || currTag.indexOf("Prox")!=-1) && tag.equals("NonProxColoc")) roi=null;

						if(currTag.indexOf("Prox")==-1 && tag.equals("Prox")) roi=null;
						
						if(currTag.indexOf("Coloc")==-1 && tag.equals("Coloc")) roi=null;
						
						if((currTag.indexOf("Prox")==-1 || currTag.indexOf("Coloc")!=-1) && tag.equals("ProxOnly")) roi=null;
						
						if((currTag.indexOf("Coloc")==-1 || currTag.indexOf("Prox")!=-1) && tag.equals("ColocOnly")) roi=null;
					}
					
					if(roi!=null) rm.add((ImagePlus) null, roi, -1);
				}
			}
		}
	}
	
	/**
	 * Pushes selected tracks to the ResultsTable
	 * @param tracks the tracks to push
	 * @param tag a tag to filter the ROIs to select: either All, Coloc, Prox, NonProxColoc or Stats
	 */
	public static void sendTracksToResultsTable(ArrayList<ArrayList<PointSerie>> tracks, String tag) {
		getPrefs();
		
		ResultsTable rt=new ResultsTable();
		
		for(int channel=0; channel<tracks.size(); channel++) {
			for(int i=0; i<tracks.get(channel).size(); i++) {
				if(tracks.get(channel).get(i)!=null) {
					boolean doLog=true;
					String currTag=tracks.get(channel).get(i).getTag();
					
					
					if(!tag.equals("All")) {
						if((currTag.indexOf("Coloc")!=-1 || currTag.indexOf("Prox")!=-1) && tag.equals("NonProxColoc")) doLog=false;
						
						if(currTag.indexOf("Prox")==-1 && tag.equals("Prox")) doLog=false;
						
						if(currTag.indexOf("Coloc")==-1 && tag.equals("Coloc")) doLog=false;
						
						if((currTag.indexOf("Prox")==-1 || currTag.indexOf("Coloc")!=-1) && tag.equals("ProxOnly")) doLog=false;
						
						if((currTag.indexOf("Coloc")==-1 || currTag.indexOf("Prox")!=-1) && tag.equals("ColocOnly")) doLog=false;
						
						if(tag.equals("Stats")) {
							tracks.get(channel).get(i).statsToResultsTable(i+1, channel+1, rt);
							doLog=false;
						}
					}
					
					if(doLog) tracks.get(channel).get(i).toResultsTable(i+1, channel+1, rt);
				}
			}
		}
		rt.show(tag);
		
		//Handle event
		TextWindow tw=(TextWindow) WindowManager.getWindow(tag);
		final TextPanel tp = tw.getTextPanel();
		
		tp.addMouseListener(new MouseAdapter() {
			@Override
            public void mouseReleased(final MouseEvent e){
                if (!e.isConsumed()){
                	tableEvent(tp, tag, tracks);
                	e.consume();
                }
                
            }
		});
		
		tp.addKeyListener(new KeyListener() {
					
					@Override
					public void keyTyped(final KeyEvent e) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void keyReleased(final KeyEvent e) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void keyPressed(final KeyEvent e) {
						// TODO Auto-generated method stub
						if (!e.isConsumed()){
							int line = tp.getSelectionStart();
							int lastLine=tp.getText().split("\n").length-1;
							if(e.getKeyCode()==KeyEvent.VK_U) tp.setSelection(Math.max(0,  line-1), Math.max(0,  line-1));
							if(e.getKeyCode()==KeyEvent.VK_D) tp.setSelection(Math.min(lastLine,  line+1), Math.min(lastLine,  line+1));
							tableEvent(tp, tag, tracks);
		                	e.consume();
						}
					}
		});
	}
	
	public static void tableEvent(TextPanel tp, String tableName, ArrayList<ArrayList<PointSerie>> tracks) {
		getPrefs();
		
		Roi roi=null;
		
		int line = tp.getSelectionStart();
        String selected_line=tp.getLine(line);
        
        String[] results_columns = selected_line.split("\\t+");
        
        int timepoint=tableName.indexOf("Stats")!=-1?1:Integer.parseInt(results_columns[1]);
        int channel=tableName.indexOf("Stats")!=-1?Integer.parseInt(results_columns[1]):Integer.parseInt(results_columns[2]);
        
        double x=Double.parseDouble(results_columns[3]);
        double y=Double.parseDouble(results_columns[4]);
        
        Color color=Color.RED;
        if(channel==2) color=Color.GREEN;
       
        if(tableName.indexOf("Stats")!=-1) {
        	int trackNb=Integer.parseInt(results_columns[0]);
        	roi=tracks.get(channel-1).get(trackNb-1).toPolyline();
        }else {
	        if(selected_line.contains("Coloc")) color=Color.yellow;
	        
	        if(selected_line.contains("Prox")) {
	        	if(channel==1) color=Color.MAGENTA;
	        	if(channel==2) color=Color.CYAN;
	        }
	        
	        roi=new OvalRoi(x-roiRadius, y-roiRadius, 2*roiRadius+1, 2*roiRadius+1);
        }
        
        roi.setStrokeColor(color);
        roi.setStrokeWidth(lineWidth/10.0);
        
        ImagePlus ip=WindowManager.getCurrentImage();
        if(ip!=null) {
        	ip.setPosition(channel, 1, timepoint);
        	ip.updatePosition(channel, 1, timepoint);
        	ip.setRoi(roi);
        	if(doZoomIn) {
        		if(tableName.indexOf("Stats")!=-1) {
        			IJ.run("To Selection", "");
        		}else{
        			IJ.run("Set... ", "zoom="+zoomInValue+" x="+x+" y="+y);
        		}
        	}
        }
	}
	
	/**
	 * Get saved preferences from the output GUI
	 */
	public static void getPrefs() {
		doZoomIn=Prefs.get("Coloc_And_Track_doZoomIn.boolean", false);
		zoomInValue=(int) Prefs.get("Coloc_And_Track_ZoomInValue.double", 400);
		lineWidth=(int) Prefs.get("Coloc_And_Track_lineWidth.double", 1);
		roiRadius=(int) Prefs.get("Coloc_And_Track_roiRadius.double", 2);
	}
}
