package robocup.model;

import java.awt.Color;
import java.awt.Dimension;

public class Zone {
	private int x, y;
	private double ratio =1;
	private String name;
	private int[] relativeYPoints;
	private int[] relativeXPoints;
	private int[] absoluteXPoints;
	private int[] absoluteYPoints;
	private double xOffset; //just for visualizing and calculating absolute X/Y points
	private double yOffset; //just for visualizing and calculating absolute X/Y points
	private Color color;

	public Zone(Dimension dimension, Color teamColor, String name, int x, int y, double ratio, int[] yPoints, int[] xPoints){
		if(yPoints.length != xPoints.length){
			System.err.println("Point arrays aren't equal in length");
			return; 
		}
		this.color=teamColor;
		this.name=name;
		this.xOffset = dimension.getWidth()/2.0;
		this.yOffset = dimension.getHeight()/2.0;
		this.x=x;
		this.y=y;
		this.ratio = ratio;
		this.relativeYPoints=yPoints;
		this.relativeXPoints=xPoints;
		this.absoluteXPoints = new int[getNPoints()];
		this.absoluteYPoints = new int[getNPoints()];
		calculateAbsolutePoints();

	}
	public Zone(String name, Color teamColor, int x, int y, int[] yPoints, int[] xPoints){
		this(new Dimension(0,0), teamColor, name, x, y, 1, yPoints, xPoints);
	}
	
	public String getName(){
		return name;
	}
	
	public void setOffset(Dimension dimension, double ratio){
		this.xOffset = dimension.getWidth()/2.0;
		this.yOffset = dimension.getHeight()/2.0;
		this.ratio = ratio;
	}
	
	public void calculateAbsolutePoints(){
		int i=0;
		//x and y swapped for random
		for(int coord:relativeXPoints){
			absoluteYPoints[i] = (int) (yOffset - (y + coord) * ratio);
			i++;
		}
		i=0;
		for(int coord:relativeYPoints){
			absoluteXPoints[i] = (int) (xOffset + ( x +coord) * ratio);
			i++;
		}
	}
	
	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}

	public int getX(){
		return (int) (x + xOffset);
	}
	
	public int getY(){
		return (int) (y + yOffset);
	}
	
	public void setCoords(int x, int y){
		this.x = x;
		this.y = y;
		calculateAbsolutePoints();
	}

	public int getWidth(){
		return getDelta(relativeXPoints);
	}

	public int getHeight(){
		return getDelta(relativeYPoints);
	}

	public int[] getAbsoluteXPoints(){
		return absoluteXPoints;
	}
	public int[] getAbsoluteYPoints(){
		return absoluteYPoints;
	}
	
	public int[] getRelativeXPoints(){
		return relativeXPoints;
	}
	
	public int[] getRelativeYPoints(){
		return relativeYPoints;
	}
	
	public int getNPoints(){
		return relativeYPoints.length;
	}

	private int getDelta(int[] pointArray) {
		int max =0
		   ,min =0;
		for(int x: pointArray){
			max = (max > x ? max : x);
			min = (min < x ? min : x);
		}
		return max - min;
	}
}
