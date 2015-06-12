package robocup.model;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

public class Obstruction extends FieldObject {
	private int width,
				length;
	private double orientation;

	public Obstruction(int _width, int _length){
		width = _width;
		length = _length;
	}

	public int getWidth(){
		return width;
	}

	public int getLength(){
		return length;
	}
	
	public double getOrientation(){
		return orientation;
	}

	public void setOrientation(int newOrientation) {
		orientation = newOrientation;
	}
	
	public Rectangle2D getRectangle(){
		return new Rectangle2D.Double(position.getX()-length/2, position.getY()-width/2, length, width);
	}
	
	public Polygon toPolygon(){
		Rectangle2D rect = getRectangle();
		AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(90 - orientation), rect.getCenterX(), rect.getCenterY());

        Polygon p = new Polygon(); 

        PathIterator i = rect.getPathIterator(at);
        while (!i.isDone()) {
            double[] xy = new double[2];
            i.currentSegment(xy);
            i.next();
            if(i.isDone()){
            	break;
            }
            p.addPoint((int) xy[0], (int) xy[1]);
            System.out.println("[" + xy[0] + "," + xy[1] + "]");
        }
        System.out.println();
		return p;
	}
}
