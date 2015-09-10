package robocup.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

public class Obstruction extends FieldObject implements Drawable{
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
		if(position == null)
			return null;
		
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
        }
		return p;
	}
	
	@Override
	public void paint(Graphics2D g2){
		g2.setColor(Color.GRAY);
		g2.setStroke(new BasicStroke(30));
		AffineTransform oldTransform = g2.getTransform();
		FieldPoint position = (this.position == null ? new FieldPoint(0,0) : this.position ).mirrorY();
		
		double theta = Math.toRadians(orientation);
 
		// create rect centred on the point we want to rotate it about
		Rectangle2D rect = new Rectangle2D.Double(-width/2., -length/2., width, length);
	
		AffineTransform transform = new AffineTransform();
		transform.translate(position.getX()/2, position.getY()/2);
		transform.rotate(theta);
		transform.createTransformedShape(rect);
		g2.transform(transform);
		
		g2.fill(rect);
		g2.setColor(g2.getColor().darker());
		g2.draw(rect);
		g2.transform(oldTransform);
	}
}
