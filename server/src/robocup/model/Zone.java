package robocup.model;

public class Zone {
	private String name;
	
	private Point[] relativePoints;
	private Point[] absolutePoints;
	
	//private int[] relativeYPoints;
	//private int[] relativeXPoints;
	private int[] absoluteXPoints;
	private int[] absoluteYPoints;

	// teamcolor has to be deleted because it aint an indicator for the correct side, maybe an isleft bool
	public Zone(String name,  Point[] points) {
		//this.color=teamColor;
		this.name =name;

		//-3000 tot 3000, -2000 tot 2000
		this.relativePoints = points;
		this.absoluteXPoints = new int[getNPoints()];
		this.absoluteYPoints = new int[getNPoints()];
		this.absolutePoints = new Point[getNPoints()];
		
		//this.relativeYPoints=yPoints;
		//this.relativeXPoints=xPoints;
		calculateAbsolutePoints();
		
		check (400, 200);
		check (500, 400);
	}
	
	private void check(int xarg, int yarg) {
		boolean a = contains(new Point(xarg,yarg));
		if (a){
			System.out.println(xarg+", "+yarg+" lies in: "+name);
			print();
		}

	}
	
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

	public int getWidth() {
		return getXDelta(relativePoints);
	}

	public int getHeight() {
		return getYDelta(relativePoints);
	}

	// to be changed
	public int[] getAbsoluteXPoints() {
		return absoluteXPoints;
	}
	// to be changed
	public int[] getAbsoluteYPoints() {
		return absoluteYPoints;
	}
	
	public Point[] getAbsolutePoints() {
		return absolutePoints;
	}
	
	public Point[] getRelativePoints() {
		return relativePoints;
	}

	
	public int getNPoints() {
		return relativePoints.length;
	}
	
    public boolean contains(Point argPoint) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = absoluteXPoints.length - 1; i < absoluteXPoints.length; j = i++) {        	
          if ((absoluteYPoints[i] > argPoint.getY()) != (absoluteYPoints[j] > argPoint.getY()) &&
              (argPoint.getX() < (absoluteXPoints[j] - absoluteXPoints[i]) * (argPoint.getY() - absoluteYPoints[i]) / (absoluteYPoints[j] - absoluteYPoints[i]) + absoluteXPoints[i])) {
            result = !result;
           }
        }
        return result;
     }
    

	private int getXDelta(Point[] pointArray) {
		int max = 0, min = 0;
		for(Point p: pointArray) {
			max = (max > (int)p.getX() ? max : (int)p.getX());
			min = (min < (int)p.getX() ? min : (int)p.getX());
		}
		return max - min;
	}
	
	private int getYDelta(Point[] pointArray) {
		int max = 0, min = 0;
		for(Point p: pointArray){
			max = (max > (int)p.getY() ? max : (int)p.getY());
			min = (min < (int)p.getY() ? min : (int)p.getY());
		}
		return max - min;
	}
}