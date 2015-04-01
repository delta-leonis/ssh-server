package robocup.model;

public class Zone {
	private String name;
	
	private FieldPoint[] relativePoints;
	private FieldPoint[] absolutePoints;
	
	private FieldPoint centerPoint;
	
	private double[] absoluteXPoints;
	private double[] absoluteYPoints;
	
	private int totalHeight;
	private int totalWidth;

	/**
	 * Constructor of the zone object
	 * @param name name of the zone
	 * @param points points of the polygon that define the zone
	 */
	public Zone(String name,  FieldPoint[] points, int totalFieldHeight, int totalFieldWidth) {
		this.name =name;

		this.relativePoints = points;
		this.absoluteXPoints = new double[getNPoints()];
		this.absoluteYPoints = new double[getNPoints()];
		this.absolutePoints = new FieldPoint[getNPoints()];
		
		calculateAbsolutePoints();
		
		totalHeight = totalFieldHeight;
		totalWidth = totalFieldWidth;
		
		// this is a temporary declaration, must be altered
		centerPoint = new FieldPoint(0, 0);
	}
	
	/**
	 * @return Name that identifies this 
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * probably an unused method, but it should not be removed before the system of zones is fully finished
	 */
	public void calculateAbsolutePoints() {
		int i=0;
		//x and y swapped for random
		for(FieldPoint coord:relativePoints) {
			absoluteXPoints[i] = coord.getX();
			absoluteYPoints[i] = coord.getY();

			absolutePoints[i] = new FieldPoint(absoluteXPoints[i], absoluteYPoints[i]);
			i++;
		}
	}

	/**
	 * gets the width of the polygon
	 * @return
	 */
	public double getWidth() {
		return getXDelta(relativePoints);
	}

	/**
	 * gets the height of the polygon
	 * @return
	 */
	public double getHeight() {
		return getYDelta(relativePoints);
	}

	/**
	 * getter function for the array with x points
	 * @return
	 */
	public double[] getAbsoluteXPoints() {
		return absoluteXPoints;
	}

	/**
	 * getter function for the array with y points
	 * @return
	 */
	public double[] getAbsoluteYPoints() {
		return absoluteYPoints;
	}
	
	/**
	 * getter function for the array with point objects, the used values of the object
	 * @return
	 */
	public FieldPoint[] getAbsolutePoints() {
		return absolutePoints;
	}
	
	/**
	 * method that takes a point, and calculates the distances between the point and all the vertexes of this zone.
	 * It will return the vertex point that is closest to the given point.
	 * @param argPoint the point to find the vertex with
	 * @return the vertex point that is closest to the argument
	 */
	public FieldPoint getClosestVertex(FieldPoint argPoint) {
		FieldPoint closestVertex = new FieldPoint(0,0);
		double shortestDistance = Double.MAX_VALUE;
		
		for (int iter = 0; iter < absoluteXPoints.length; iter++) {
			FieldPoint vertexPoint = new FieldPoint (absoluteXPoints[iter], absoluteYPoints[iter]);
			double calcDistance = argPoint.getDeltaDistance(vertexPoint);
			if (calcDistance < shortestDistance) {
				shortestDistance = calcDistance;
				closestVertex = vertexPoint;
			}
		}
		return closestVertex;
	}
	
	/**
	 * getter method that returns the Point object of the center of this zone
	 * @return the point object of the center
	 */
	public FieldPoint getCenterPoint() {
		return centerPoint;
	}
	
	/**
	 * setter method that overwrites the center of this zone
	 * @param argCenter the new center point
	 */
	public void setCenterPoint(FieldPoint argCenter) {
		centerPoint = argCenter;
	}
	
	/**
	 * a function that returns the absolute distance between the argument point and the declared center of the zone
	 * @param argPoint the point to calculate the distance with
	 */
	public void getDistanceFromCenter(FieldPoint argPoint) {
		argPoint.getDeltaDistance(centerPoint);
	}
	
	/**
	 * function that retrieves the relative points, without correlation of the set ratio
	 * @return
	 */
	public FieldPoint[] getRelativePoints() {
		return relativePoints;
	}

	/**
	 * a method that returns how many vertexes this zone has
	 * @return amount of vertexes
	 */
	public int getNPoints() {
		return relativePoints.length;
	}
	
	/**
	 * a method that checks if the given point falls within the polygon
	 * @param argPoint
	 * @return
	 */
    public boolean contains(FieldPoint argPoint) {
    	double pointX = (argPoint.getX() + (totalWidth / 2));
    	double pointY = (argPoint.getY() + (totalHeight / 2));		

        boolean result = false;
        for (int i = 0, j = absoluteXPoints.length - 1; i < absoluteXPoints.length; j = i++) {        	
          if ((absoluteYPoints[i] > pointY) != (absoluteYPoints[j] > pointY) &&
              (pointX < (absoluteXPoints[j] - absoluteXPoints[i]) * (pointY - absoluteYPoints[i]) / (absoluteYPoints[j] - absoluteYPoints[i]) + absoluteXPoints[i])) {
            result = !result;
           }
        }
        return result;
     }

	/**
	 * calculates the difference between the highest and lowest x points of the polygon
	 * @param pointArray an array with points that will be used in our calculation
	 * @return the difference
	 */
	private double getXDelta(FieldPoint[] pointArray) {
		double max = 0, min = 0;
		for(FieldPoint p: pointArray) {
			max = (max > p.getX() ? max : p.getX());
			min = (min < p.getX() ? min : p.getX());
		}
		return max - min;
	}
	
	/**
	 * calculates the difference between the highest and lowest y points of the polygon
	 * @param pointArray an array with points that will be used in our calculation
	 * @return the difference
	 */
	private double getYDelta(FieldPoint[] pointArray) {
		double max = 0, min = 0;
		for(FieldPoint p: pointArray){
			max = (max > p.getY() ? max : p.getY());
			min = (min < p.getY() ? min : p.getY());
		}
		return max - min;
	}
}