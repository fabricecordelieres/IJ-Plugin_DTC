package DCT.tools.individualData;

import ij.gui.PointRoi;

/**
 * This class handles simple 2D+tcoordinates. 
 * @author fab
 *
 */
public class Point {
	/** x coordinate **/
	public float x=Float.NaN;
	
	/** y coordinate **/
	public float y=Float.NaN;
	
	/** Intensity **/
	float intensity=Float.NaN;
	
	
	/** Tag **/
	public String tag="";
	
	/**
	 * Creates a new point, based on input coordinates
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param intensity associated to the point
	 */
	public Point(float x, float y, float intensity){
		this.x=x;
		this.y=y;
		this.intensity=intensity;
	}
	
	/**
	 * Creates a new point, based on an input PointRoi
	 * @param roi the input PointRoi (only the first element will be used)
	 * @param intensity associated to the point
	 */
	public Point(PointRoi roi, float intensity) {
		if(roi!=null) {
			x=roi.getFloatPolygon().xpoints[0];
			y=roi.getFloatPolygon().ypoints[0];
			this.intensity=intensity;
		}
	}
	
	/**
	 * Sets the input String as a tag to the current point
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
	 * Moves the current point by adding the x, y, t coordinates from the input vector.
	 * @param vector
	 */
	public void translate(Point vector){
		x+=vector.x;
		y+=vector.y;
	}
	
	/**
	 * Computes and returns the distance between the current point and the input point
	 * @param p a point from where to compute the distance
	 * @return the distance between the current point and the input point
	 */
	public float getDistance(Point p){
		return (float) Math.sqrt((p.x-x)*(p.x-x)+(p.y-y)*(p.y-y));
	}

	/**
	 * Generates a PointRoi from the current point
	 * @return a PointRoi representation of the current point
	 */
	public PointRoi toPointRoi() {
		return new PointRoi(x, y);
	}
	
	@Override
	public String toString(){
		return " x="+x+" y="+y+" intensity="+intensity+" tag="+tag;
	}
	
	public Point clone() {
		Point out=new Point(x, y, intensity);
		out.setTag(tag);
		
		return out;
	}
}
