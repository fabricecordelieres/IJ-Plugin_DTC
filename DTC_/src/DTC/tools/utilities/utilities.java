package DTC.tools.utilities;

import ij.gui.PolygonRoi;
import ij.gui.Roi;

/**
 * This class encompasses misc useful static methods
 * @author fab
 *
 */
public class utilities {
	/**
	 * Creates a Roi shaped as a cross
	 * @param x x coordinate of the mid-point
	 * @param y y coordinate of the mid-point
	 * @param radius half-width/height of the cross
	 * @return a cross, as a Roi
	 */
	public static Roi getCross(float x, float y, float radius) {
		float xL=x-radius;
		float xR=x+radius;
		float yB=y-radius;
		float yT=y+radius;
		
		float[] xPoints=new float[] {x, xR, x, x, x, xL, x, x, x};
		float[] yPoints=new float[] {y, y, y, yB, y, y, y, yT, y};
		
		
		return new PolygonRoi(xPoints, yPoints, PolygonRoi.POLYGON);
	}
	
	/**
	 * Creates a Roi shaped as a cross, branches being orientated at 45°
	 * @param x x coordinate of the mid-point
	 * @param y y coordinate of the mid-point
	 * @param radius half-width/height of the cross
	 * @return a rotated cross, as a Roi
	 */
	public static Roi getSideCross(float x, float y, float radius) {
		float side=(float) (radius/Math.sqrt(2));
		
		float xL=x-side;
		float xR=x+side;
		float yB=y-side;
		float yT=y+side;
		
		float[] xPoints=new float[] {x, xR, x, xR, x, xL, x, xL, x};
		float[] yPoints=new float[] {y, yT, y, yB, y, yB, y, yT, y};
		
		
		return new PolygonRoi(xPoints, yPoints, PolygonRoi.POLYGON);
	}
}
