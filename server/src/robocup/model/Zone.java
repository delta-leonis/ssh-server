package robocup.model;

public class Zone {
	private String name;
	
	private Point[] relativePoints;
	private Point[] absolutePoints;
	
	private Point centerPoint;
	
	private int[] absoluteXPoints;
	private int[] absoluteYPoints;
	
	private int totalHeight;
	private int totalWidth;

	/**
	 * Constructor of the zone object
	 * @param name name of the zone
	 * @param points points of the polygon that define the zone
	 */
	public Zone(String name,  Point[] points, int totalFieldHeight, int totalFieldWidth) {
		this.name =name;

		this.relativePoints = points;
		this.absoluteXPoints = new int[getNPoints()];
		this.absoluteYPoints = new int[getNPoints()];
		this.absolutePoints = new Point[getNPoints()];
		
		calculateAbsolutePoints();
		
		totalHeight = totalFieldHeight;
		totalWidth = totalFieldWidth;
		
		// this is a temporary declaration, must be altered
		centerPoint = new Point(0, 0);
	}

	@SuppressWarnings("unused")
	private void print(){
		for (int a = 0; a < absoluteXPoints.length; a++) {
			System.out.println(absoluteXPoints[a]+" "+absoluteYPoints[a]);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public void calculateAbsolutePoints() {
		int i=0;
		//x and y swapped for random
		for(Point coord:relativePoints) {
			absoluteXPoints[i] = (int) coord.getX();
			absoluteYPoints[i] = (int) coord.getY();

			absolutePoints[i] = new Point(absoluteXPoints[i], absoluteYPoints[i]);
			i++;
		}
	}

	/**
	 * gets the width of the polygon
	 * @return
	 */
	public int getWidth() {
		return getXDelta(relativePoints);
	}

	/**
	 * gets the height of the polygon
	 * @return
	 */
	public int getHeight() {
		return getYDelta(relativePoints);
	}

	/**
	 * getter function for the array with x points
	 * @return
	 */
	public int[] getAbsoluteXPoints() {
		return absoluteXPoints;
	}

	/**
	 * getter function for the array with y points
	 * @return
	 */
	public int[] getAbsoluteYPoints() {
		return absoluteYPoints;
	}
	
	/**
	 * getter function for the array with point objects, the used values of the object
	 * @return
	 */
	public Point[] getAbsolutePoints() {
		return absolutePoints;
	}
	
	public Point getClosestVertex(Point argPoint) {
		Point closestVertex = new Point(0,0);
		double shortestDistance = Double.MAX_VALUE;
		
		for (int iter = 0; iter < absoluteXPoints.length; iter++) {
			Point vertexPoint = new Point (absoluteXPoints[iter], absoluteYPoints[iter]);
			double calcDistance = argPoint.getDeltaDistance(vertexPoint);
			if (calcDistance < shortestDistance) {
				shortestDistance = calcDistance;
				closestVertex = vertexPoint;
			}
		}
		return closestVertex;
	}
	
	public Point getCenterPoint() {
		return centerPoint;
	}
	
	public void setCenterPoint(Point argCenter) {
		centerPoint = argCenter;
	}
	
	public void getDistanceFromCenter(Point argPoint) {
		argPoint.getDeltaDistance(centerPoint);
	}
	
	/**
	 * function that retrieves the relative points, without correlation of the set ratio
	 * @return
	 */
	public Point[] getRelativePoints() {
		return relativePoints;
	}

	
	public int getNPoints() {
		return relativePoints.length;
	}
	
	/**
	 * a method that checks if the given point falls within the polygon
	 * @param argPoint
	 * @return
	 */
    public boolean contains(Point argPoint) {
    	int pointX = (int) (argPoint.getX() + (totalWidth / 2));
    	int pointY = (int) (argPoint.getY() + (totalHeight / 2));		

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
	private int getXDelta(Point[] pointArray) {
		int max = 0, min = 0;
		for(Point p: pointArray) {
			max = (max > (int)p.getX() ? max : (int)p.getX());
			min = (min < (int)p.getX() ? min : (int)p.getX());
		}
		return max - min;
	}
	
	/**
	 * calculates the difference between the highest and lowest y points of the polygon
	 * @param pointArray an array with points that will be used in our calculation
	 * @return the difference
	 */
	private int getYDelta(Point[] pointArray) {
		int max = 0, min = 0;
		for(Point p: pointArray){
			max = (max > (int)p.getY() ? max : (int)p.getY());
			min = (min < (int)p.getY() ? min : (int)p.getY());
		}
		return max - min;
	}
}