package DTC.tools.dataHandler;

import java.awt.Color;
import java.util.ArrayList;

import DTC.tools.individualData.Point;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.measure.ResultsTable;
import ij.process.FloatPolygon;

/**
 * This class is designed to store detections made on temporal hyperstacks
 * @author fab
 *
 */
public class PointSerie {
	/** Stores the detections as individual points **/
	ArrayList<Point> points=new ArrayList<Point>();
	
	/** Timepoint **/
	int t=0;
	
	/** Channel **/
	int c=0;
	
	
	/** Color **/
	public Color color=Color.YELLOW;
	
	/** Name **/
	public String name="";
	
	/** Tag **/
	public String tag="";
	
	//--------------- Useful variables for stats ---------------
	/** Number of timepoints without a tag **/
	public int nbTPNoTag=0;
	
	/** Number of timepoints tagged as coloc **/
	public int nbTPColoc=0;
	
	/** Number of timepoints tagged as prox **/
	public int nbTPProx=0;
	
	/** Cumulative distance without a tag **/
	public float distNoTag=0;
	
	/** Cumulative distance tagged as coloc **/
	public float distColoc=0;
	
	/** Cumulative distance tagged as prox **/
	public float distProx=0;
	
	/** Number of sequences without a tag **/
	public int nbSeqNoTag=0;
	
	/** Number of sequences tagged as coloc **/
	public int nbSeqColoc=0;
	
	/** Number of sequences tagged as prox **/
	public int nbSeqProx=0;	
	
	
	/**
	 * Creates a new PointSerie object
	 */
	public PointSerie() {
	}
	
	/**
	 * Creates a new PointSerie object, based on an input PointRoi
	 * @param roi the input PointRoi
	 * @param intensities intensities associated to each point, as a float array
	 */
	public PointSerie(PointRoi roi, float[] intensities) {
		FloatPolygon fp=roi.getFloatPolygon();
		t=roi.getTPosition();
		c=roi.getCPosition();
		name=roi.getName();
		for(int i=0; i<roi.getNCoordinates(); i++) points.add(new Point(fp.xpoints[i], fp.ypoints[i], intensities[i]));
		buildStats();
	}
	
	/**
	 * Creates a new PointSerie object, starting from the input Point
	 * @param point the input PointRoi
	 */
	public PointSerie(Point point) {
		points.add(point);
		buildStats();
	}
	
	/**
	 * Sets the timepoint to the input value
	 * @param timepoint input value to set
	 */
	public void setTimepoint(int timepoint){
		this.t=timepoint;
	}
	
	/**
	 * Returns the timepoint associated to the current detections object
	 * @return the timepoint associated to the current detections object
	 */
	public int getTimepoint(){
		return t;
	}
	
	/**
	 * Sets the channel to the input value
	 * @param channel input value to set
	 */
	public void setChannel(int channel){
		this.c=channel;
	}
	
	/**
	 * Returns the channel associated to the current detections object
	 * @return the channel associated to the current detections object
	 */
	public int getChannel(){
		return c;
	}
	
	/**
	 * Sets the color to the input value
	 * @param color input value to set
	 */
	public void setColor(Color color){
		this.color=color;
	}
	
	/**
	 * Returns the color associated to the current detections object
	 * @return the color associated to the current detections object
	 */
	public Color getColor(){
		return color;
	}
	
	/**
	 * Sets the name to the input value
	 * @param name input value to set
	 */
	public void setName(String name){
		this.name=name;
	}
	
	/**
	 * Returns the name associated to the current detections object
	 * @return the name associated to the current detections object
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Set the input String as a tag to the current point
	 * @param tag a String to be used as a tag
	 */
	public void setTag(String tag){
		this.tag=tag;
	}
	
	/**
	 * Returns the tag associated to the current point, or null
	 * @return the tag associated to the current point, or null
	 */
	public String getTag(){
		return tag;
	}
	
	/**
	 * Returns the number of points within the PointSerie
	 * @return the number of points within the PointSerie
	 */
	public int getNPoints(){
		return points.size();
	}
	
	/**
	 * Returns the Point object at the input index
	 * @return the Point object at the input index
	 */
	public Point getPoint(int index){
		return points.get(index);
	}
	
	/**
	 * Adds a new point to the points list
	 * @param p the point to add
	 */
	public void add(Point p) {
		points.add(p);
		buildStats();
	}
	
	/**
	 * Removes a point from the points list
	 * @param index the index of the point to remove
	 */
	public void remove(int index) {
		points.remove(index);
	}
	
	/**
	 * Generates a PointRoi from the current detections
	 * @return a PointRoi representation of the current detections
	 */
	public PointRoi toPointRoi() {
		PointRoi.setColor(color);
		PointRoi roi=new PointRoi();
		
		for(int i=0; i<points.size(); i++) roi.addPoint(points.get(i).x, points.get(i).y);
		roi.setName(name);
		roi.setPosition(c, 1, t);
		
		return roi;
	}
	
	/**
	 * Generates a PolygonRoi from the current detections
	 * @return a PolygonRoi representation of the current detections
	 */
	public PolygonRoi toPolyline() {
		FloatPolygon pol=new FloatPolygon();
		for(int i=0; i<points.size(); i++) pol.addPoint(points.get(i).x, points.get(i).y);
		
		PolygonRoi.setColor(color);
		PolygonRoi roi=new PolygonRoi(pol, PolygonRoi.POLYLINE);
		roi.setName(name);
		roi.setPosition(c, 1, t);
		
		return roi;
	}
	
	
	/**
	 * Builds the statistics block for the current PointSerie:
	 * nbTPNoTag: number of timepoints without a tag
	 * nbTPProx: number of timepoints with proximity tag
	 * nbTPColoc: number of timepoints with colocalization tag
	 * distNoTag: distance traveled while carrying no tag
	 * distProx: distance traveled while carrying the proximity tag
	 * distColoc: distance traveled while carrying the colocalization tag
	 * nbSeqNoTag: number of sequences (successive timepoints carrying the same tag) carrying no tag
	 * nbSeqProx: number of sequences (successive timepoints carrying the same tag) carrying the proximity tag
	 * nbSeqColoc: number of sequences (successive timepoints carrying the same tag) carrying the colocalization tag
	 */
	public void buildStats() {
		nbTPNoTag=0;
		nbTPProx=0;
		nbTPColoc=0;
		distNoTag=0;
		distProx=0;
		distColoc=0;
		nbSeqNoTag=0;
		nbSeqProx=0;
		nbSeqColoc=0;
		
		String currTag=points.get(0).getTag().split("_").length==2?points.get(0).getTag().split("_")[0]:"";
		
		for(int i=0; i<points.size(); i++) {
			Point p=points.get(i);
			String tag=p.getTag().split("_").length==2?p.getTag().split("_")[0]:"";
			double distanceToPreviousPoint=i==0?Double.NaN:p.getDistance(points.get(i-1));
			
			if(i==0) currTag=tag;

			switch(tag) {
				case "Prox":
					nbTPProx++;
					if(!((Double)distanceToPreviousPoint).isNaN()) distProx+=distanceToPreviousPoint;
					break;
					
				case "Coloc":
					nbTPColoc++;
					if(!((Double)distanceToPreviousPoint).isNaN()) distColoc+=distanceToPreviousPoint;
					break;
					
				case"":
					nbTPNoTag++;
					if(!((Double)distanceToPreviousPoint).isNaN()) distNoTag+=distanceToPreviousPoint;
					break;
			}
			
			if(!currTag.equals(tag)) {
				switch(currTag) {
					case "Prox":
						nbSeqProx++;
						break;
						
					case "Coloc":
						nbSeqColoc++;
						break;
						
					case "":
						nbSeqNoTag++;
						break;
				}
			}
			
			if(i==points.size()-1) {
				switch(tag) {
					case "Prox":
						nbSeqProx++;
						break;
						
					case "Coloc":
						nbSeqColoc++;
						break;
						
					case "":
						nbSeqNoTag++;
						break;
				}
			}
			
			currTag=tag;
		}
	}
	
	/**
	 * Exports the content of the current PointSerie to the results table
	 * @param trackNb a number to be displayed in the table to identify the track
	 * @param channel a channel number to be displayed in the table to identify the track
	 * @param rt the ResultsTable to which data should be exported
	 */
	public void toResultsTable(int trackNb, int channel, ResultsTable rt) {
		for(int i=0; i<points.size(); i++) {
			int lineNb=rt.getCounter();
			Point p=points.get(i);
			String[] tag=p.getTag().split("_");
			
			
			rt.setValue("Track", lineNb, trackNb);
			rt.setValue("TimePoint", lineNb, i+1);
			rt.setValue("Channel", lineNb, channel);
			rt.setValue("X", lineNb, p.x);
			rt.setValue("Y", lineNb, p.y);
			rt.setValue("Intensity", lineNb, p.intensity);
			rt.setValue("Distance", lineNb, i==0?Double.NaN:p.getDistance(points.get(i-1)));
			
			rt.setValue("Status", lineNb, tag.length==2?tag[0]:"");
			rt.setValue("Distance_To_Other", lineNb, tag.length==2?tag[1]:"");
		}
	}
	
	/**
	 * Exports the content of stats for the current PointSerie to the results table
	 * @param trackNb a number to be displayed in the table to identify the track
	 * @param channel a channel number to be displayed in the table to identify the track
	 * @param rt the ResultsTable to which data should be exported
	 */
	public void statsToResultsTable(int trackNb, int channel, ResultsTable rt) {
		int lineNb=rt.getCounter();
		
		rt.setValue("Track", lineNb, trackNb);
		rt.setValue("Channel", lineNb, channel);
		rt.setValue("Nb_timePoints", lineNb, points.size());
		rt.setValue("Nb_timePoints_without_tag", lineNb, nbTPNoTag);
		rt.setValue("Nb_timePoints_prox", lineNb, nbTPProx);
		rt.setValue("Nb_timePoints_coloc", lineNb, nbTPColoc);
		rt.setValue("Distance_without_tag", lineNb, distNoTag);
		rt.setValue("Distance_prox", lineNb, distProx);
		rt.setValue("Distance_coloc", lineNb, distColoc);
		rt.setValue("Nb_sequences_without_tag", lineNb, nbSeqNoTag);
		rt.setValue("Nb_sequences_prox", lineNb, nbSeqProx);
		rt.setValue("Nb_sequences_coloc", lineNb, nbSeqColoc);
	}
	
	@Override
	public PointSerie clone() {
		PointSerie out=new PointSerie();
		for(int i=0; i<points.size(); i++) out.add(points.get(i).clone());
		out.setTimepoint(t);
		out.setChannel(c);
		out.setColor(color);
		out.setName(name);
		out.setTag(tag);
		
		out.buildStats();
		
		return out;
	}
	
	@Override
	public String toString() {
		//JSON-like formatted output
		String out="{\n";
		out+="\t\"name\": \""+name+"\",\n";
		out+="\t\"tag\": \""+tag.replace("\t", " ")+"\",\n";
		out+="\t\"Timepoints\":[";
		
		for(int i=0; i<points.size(); i++) {
			Point p=points.get(i);
			String[] tag=p.getTag().split("_");
			out+="\n";
			out+="\t\t{\n";
			out+="\t\t\t\"Time\": "+(i+1)+",\n";
			out+="\t\t\t\"Distance\": "+(i==0?"\"NaN\"":p.getDistance(points.get(i-1)))+",\n";
			out+="\t\t\t\"Status\": \""+(tag.length==2?tag[0]:"")+"\",\n";
			out+="\t\t\t\"Distance_To_Other\": "+(tag.length==2?tag[1]:"\"NaN\"")+",\n";
			out+=p.toString();
			out+="\t\t}";
			if(i<points.size()-1) out+=",";
		}
		
		out+="\n\t],\n";
		
		out+="\t\"Stats\":{\n";
		
		out+="\t\t\"nbTPNoTag\": "+nbTPNoTag+",\n";
		out+="\t\t\"nbTPProx\": "+nbTPProx+",\n";
		out+="\t\t\"nbTPColoc\": "+nbTPColoc+",\n";
		out+="\t\t\"distNoTag\": "+distNoTag+",\n";
		out+="\t\t\"distProx\": "+distProx+",\n";
		out+="\t\t\"distColoc\": "+distColoc+",\n";
		out+="\t\t\"nbSeqNoTag\": "+nbSeqNoTag+",\n";
		out+="\t\t\"nbSeqProx\": "+nbSeqProx+",\n";
		out+="\t\t\"nbSeqColoc\": "+nbSeqColoc+"\n";
		
		out+="\t}";
		out+="\n}";
		
		return out;
	}
}
