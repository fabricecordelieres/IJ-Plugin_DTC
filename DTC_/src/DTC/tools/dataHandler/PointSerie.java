package DCT.tools.dataHandler;

import java.awt.Color;
import java.util.ArrayList;

import DCT.tools.individualData.Point;
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
	}
	
	/**
	 * Creates a new PointSerie object, starting from the input Point
	 * @param point the input PointRoi
	 */
	public PointSerie(Point point) {
		points.add(point);
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
	
	public void toResultsTable(int trackNb, int channel, ResultsTable rt) {
		for(int i=0; i<points.size(); i++) {
			int lineNb=rt.getCounter();
			Point p=points.get(i);
			String[] tag=p.getTag().split("_");
			
			
			rt.setValue("Track", lineNb, trackNb);
			rt.setValue("Channel", lineNb, channel);
			rt.setValue("X", lineNb, p.x);
			rt.setValue("Y", lineNb, p.y);
			rt.setValue("Distance", lineNb, i==0?Double.NaN:p.getDistance(points.get(i-1)));
			
			rt.setValue("Status", lineNb, tag.length==2?tag[0]:"");
			rt.setValue("Distance_To_Other", lineNb, tag.length==2?tag[1]:"");
		}
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
		
		return out;
	}
}
