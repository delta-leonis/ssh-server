package robocup.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import robocup.model.Ball;
import robocup.model.Enemy;
import robocup.model.FieldPoint;
import robocup.model.Goal;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.FieldZone;

@SuppressWarnings("serial")
public class PlayfieldFrame extends JPanel {

//	private static int FIELDWIDTH = 9000;
//	private static int FIELDHEIGHT = 6000;
	private static int FIELDWIDTH = World.getInstance().getField().getWidth();
	private static int FIELDHEIGHT = World.getInstance().getField().getHeight();
	private static double ratio = 0.10;
	
	public static void main(String [] args) {
		JFrame hai = new JFrame();	
		hai.setSize((int)(FIELDWIDTH * ratio) + 4, (int)(FIELDHEIGHT * ratio) + 28);
		hai.setContentPane(new PlayfieldFrame());
		hai.setVisible(true);
		//hai.setResizable(false);
		hai.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public PlayfieldFrame() {
		//zoneList = new EnumMap<FieldZone, Zone>(FieldZone.class);
		setSize((int)(FIELDWIDTH * ratio), (int)(FIELDHEIGHT * ratio));
		initFieldObjects();
		//World.getInstance().getReferee().setRightTeamByColor(TeamColor.BLUE);
		
		addMouseListener(new MouseAdapter() { 
	          public void mousePressed(MouseEvent me) { 
	        	  double x = (((me.getX() - getWidth()/2))*1/ratio);
	        	  double y = (((-1*me.getY() + getHeight()/2))*1/ratio);
	        	  World.getInstance().getBall().setPosition(new FieldPoint(x, y));
	        	  repaint();
	          } 
	        }); 

	}

	private void initFieldObjects(){
		ArrayList<Robot> robots = World.getInstance().getAllRobots();
		robots.get(0).setPosition(new FieldPoint(-2700,400));
		robots.get(1).setPosition(new FieldPoint(-2500, -120));
		//robots.get(2).setPosition(new Point(-1500, 0));

		World.getInstance().getBall().setPosition(new FieldPoint(100,-800));
	}
	
    public void paint(Graphics g) {
		super.paintComponents(g);
		//setRatio();
		
		//main playing field
		g.setColor(Color.GREEN.darker());
		g.fillRect(2, 2, getWidth() - 4, getHeight() - 4);

    	drawRaster(g);

    	for (FieldZone fieldZone : FieldZone.values())
    		drawZone(g, fieldZone);
    	
    	drawPlayfield(g);
    	drawFieldObjects(g);

	    Graphics2D g2 = (Graphics2D) g;
    	drawFreeShot(g);
	    g2.setColor(Color.white);
    }
    
    public void drawFreeShot(Graphics2D g2){
		g2.setStroke(new BasicStroke(1));

		FieldPoint hitmarker = hasFreeShot();
		Ball ball = World.getInstance().getBall();
		if(hitmarker != null)
			drawLine(g2, new Line2D.Double(hitmarker.toPoint2D(), ball.getPosition().toPoint2D()));
		else
			System.out.println("No solution");
    }
    
    public FieldPoint hasFreeShot(){
    	Ball ball = World.getInstance().getBall();
		//only proceed when we are the ballowner
		if(ball.getOwner() instanceof Enemy)
			return null;
		
		FieldZone[] zones = {FieldZone.EAST_NORTH_FRONT, FieldZone.EAST_CENTER, FieldZone.EAST_SOUTH_FRONT, FieldZone.EAST_MIDDLE, FieldZone.EAST_SOUTH_SECONDPOST, FieldZone.EAST_NORTH_SECONDPOST, 
							FieldZone.WEST_NORTH_FRONT, FieldZone.WEST_CENTER, FieldZone.WEST_SOUTH_FRONT, FieldZone.WEST_MIDDLE, FieldZone.WEST_SOUTH_SECONDPOST, FieldZone.WEST_NORTH_SECONDPOST};
		
		//check if the ball is in a zone from which we can actually make the angle
		if(!Arrays.asList(zones).contains(World.getInstance().locateFieldObject(ball)))
			return null;

		//get the enemy goal (checking which side is ours, and get the opposite 
		Goal enemyGoal = (World.getInstance().getReferee().isWestTeamColor(World.getInstance().getReferee().getAllyTeamColor())) ?  World.getInstance().getField().getEastGoal() : World.getInstance().getField().getWestGoal();

		ArrayList<Robot> obstacles = World.getInstance().getAllRobotsInArea(new FieldPoint[]{enemyGoal.getFrontSouth(), enemyGoal.getFrontNorth(), ball.getPosition()});

		//No obstacles?! shoot directly in the center of the goal;
		if(obstacles.size() == 0)
			return new FieldPoint(enemyGoal.getFrontSouth().getX(), 0.0);
		

		//make a list with all blocked areas.
		//Y is the same for all points, so key = x1, and value = x2
		//that way the map is automatically ordered in size, note that adding a new
		//point should check whether the key exist, and if the new value is bigger or smaller to
		//prevent loss of points
		TreeMap<Double, Double> obstructedArea = new TreeMap<Double, Double>();

		for(Robot obstacle : obstacles){
			double distance = obstacle.getPosition().getDeltaDistance(ball.getPosition()); 
			double divertAngle = Math.atan((Robot.DIAMETER/2) / distance);			
			
			double obstacleLeftAngle = Math.toRadians(ball.getPosition().getAngle(obstacle.getPosition()))  + divertAngle;
			double obstacleRightAngle = Math.toRadians(ball.getPosition().getAngle(obstacle.getPosition())) - divertAngle;
			
			double dx = enemyGoal.getFrontSouth().getX() - ball.getPosition().getX();
			double dyL = Math.tan(obstacleLeftAngle) * dx;
			double dyR = Math.tan(obstacleRightAngle) * dx;

			FieldPoint L = new FieldPoint((ball.getPosition().getX() + dx),
								(ball.getPosition().getY() + dyL));
			FieldPoint R = new FieldPoint((ball.getPosition().getX() + dx),
					(ball.getPosition().getY() + dyR));

			//is there a point with the same start X coordinate
			//if so, check whether it is bigger, and replace it if necessary
			if(!obstructedArea.containsKey(L.toPoint2D().getY()) ||
					(obstructedArea.get(L.toPoint2D().getY())) > R.toPoint2D().getY())
				obstructedArea.put(L.toPoint2D().getY(), R.toPoint2D().getY());
		}
		double minY = enemyGoal.getFrontSouth().getY();
		double maxY = enemyGoal.getFrontNorth().getY();
		obstructedArea = minMax(obstructedArea, minY, maxY);
		obstructedArea = mergeOverlappingValues(obstructedArea);

		TreeMap<Double, Double> availableArea = invertMap(obstructedArea);

		if(obstructedArea.firstKey() != minY)
			availableArea.put(minY, obstructedArea.firstKey());
		if(obstructedArea.lastEntry().getValue() != maxY)
			availableArea.put(obstructedArea.lastEntry().getValue(), maxY);

		double x = enemyGoal.getFrontSouth().getX();
		
		if(availableArea.size() <= 0)
			return null;
		
		//in this case size DOES matter
		Double biggestKey = availableArea.firstKey();
		for(Entry<Double, Double> entry : availableArea.entrySet())
			if(entry.getValue() - entry.getKey() > availableArea.get(biggestKey) - biggestKey)
				biggestKey = entry.getKey();

		System.out.println("Size: " + (availableArea.get(biggestKey) - biggestKey));
		//return point that lies in the center of the biggest point
		FieldPoint hitmarker = new FieldPoint(x, (biggestKey/2 + availableArea.get(biggestKey)/2));
		return hitmarker;
	}
    
	/**
	 * Checks whether a ally has a free shot, will only be checked
	 * if the robot is in one of the 6 center zones (due to accuracy)
	 * 
	 * @param executer
	 * @return
	 */
	public void drawFreeShot(Graphics g){
		Ball ball = World.getInstance().getBall();
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.RED);
		//only proceed when we are the ballowner
		if(ball.getOwner() instanceof Enemy)
			return;

		FieldZone[] zones = {FieldZone.EAST_NORTH_FRONT, FieldZone.EAST_CENTER, FieldZone.EAST_SOUTH_FRONT, FieldZone.EAST_MIDDLE, FieldZone.EAST_SOUTH_SECONDPOST, FieldZone.EAST_NORTH_SECONDPOST, 
							FieldZone.WEST_NORTH_FRONT, FieldZone.WEST_CENTER, FieldZone.WEST_SOUTH_FRONT, FieldZone.WEST_MIDDLE, FieldZone.WEST_SOUTH_SECONDPOST, FieldZone.WEST_NORTH_SECONDPOST};
		
		//check if the ball is in a zone from which we can actually make the angle
		if(!Arrays.asList(zones).contains(World.getInstance().locateFieldObject(ball))){
			System.out.println("Ball is in wrong zone (" + World.getInstance().locateFieldObject(ball) +".");
			return ;
		}

		//get the enemy goal (checking which side is ours, and get the opposite 
		Goal enemyGoal = (World.getInstance().getReferee().isWestTeamColor(World.getInstance().getReferee().getAllyTeamColor())) ?  World.getInstance().getField().getEastGoal() : World.getInstance().getField().getWestGoal();

		//scoreArea.add(new Line2D.Double(enemyGoal.getFrontSouth().toGUIPoint(ratio), enemyGoal.getFrontNorth().toGUIPoint(ratio)));
		//drawPolygon(g2, new Point[]{enemyGoal.getFrontSouth(), enemyGoal.getFrontNorth(), ball.getPosition()});
		drawLine(g2, new Line2D.Double(enemyGoal.getFrontSouth().toPoint2D(), ball.getPosition().toPoint2D()));
		drawLine(g2, new Line2D.Double(enemyGoal.getFrontNorth().toPoint2D(), ball.getPosition().toPoint2D()));
		ArrayList<Robot> obstacles = World.getInstance().getAllRobotsInArea(new FieldPoint[]{enemyGoal.getFrontSouth(), enemyGoal.getFrontNorth(), ball.getPosition()});

		//No obstacles?! shoot directly in the center of the goal;
		if(obstacles.size() == 0){
			System.out.println("No obstacles");
			drawOval(g2, (int) (enemyGoal.getFrontSouth().getX()), 0, 43);
			return;
		}
		System.out.println(obstacles.size() + " robots gevonden");
		

		//make a list with all blocked areas.
		//Y is the same for all points, so key = x1, and value = x2
		//that way the map is automatically ordered in size, note that adding a new
		//point should check whether the key exist, and if the new value is bigger or smaller to
		//prevent loss of points
		TreeMap<Double, Double> obstructedArea = new TreeMap<Double, Double>();

		for(Robot obstacle : obstacles){
			double distance = obstacle.getPosition().getDeltaDistance(ball.getPosition()); 
			double divertAngle = Math.atan((Robot.DIAMETER/2) / distance);			
			
			double obstacleLeftAngle = Math.toRadians(ball.getPosition().getAngle(obstacle.getPosition()))  + divertAngle;
			double obstacleRightAngle = Math.toRadians(ball.getPosition().getAngle(obstacle.getPosition())) - divertAngle;
			
			double dx = enemyGoal.getFrontSouth().getX() - ball.getPosition().getX();
			double dyL = Math.tan(obstacleLeftAngle) * dx;
			double dyR = Math.tan(obstacleRightAngle) * dx;

			FieldPoint L = new FieldPoint( (ball.getPosition().getX() + dx),
								 (ball.getPosition().getY() + dyL));
			FieldPoint R = new FieldPoint( (ball.getPosition().getX() + dx),
					 (ball.getPosition().getY() + dyR));

			g2.setColor(Color.blue);
			drawLine(g2, new Line2D.Double(L.toPoint2D(), ball.getPosition().toPoint2D()));
			g2.setColor(Color.yellow);
			drawLine(g2, new Line2D.Double(R.toPoint2D(), ball.getPosition().toPoint2D()));
			
			//is there a point with the same start X coordinate
			//if so, check whether it is bigger, and replace it if necessary
			if(!obstructedArea.containsKey(L.toPoint2D().getY()) ||
					(obstructedArea.get(L.toPoint2D().getY())) > R.toPoint2D().getY())
				obstructedArea.put(L.toPoint2D().getY(), R.toPoint2D().getY());
		}
		double minY = enemyGoal.getFrontSouth().getY();
		double maxY = enemyGoal.getFrontNorth().getY();
		obstructedArea = minMax(obstructedArea, minY, maxY);
		obstructedArea = mergeOverlappingValues(obstructedArea);

		TreeMap<Double, Double> availableArea = invertMap(obstructedArea);

		if(obstructedArea.firstKey() != minY)
			availableArea.put(minY, obstructedArea.firstKey());
		if(obstructedArea.lastEntry().getValue() != maxY)
			availableArea.put(obstructedArea.lastEntry().getValue(), maxY);

		double x =  enemyGoal.getFrontSouth().getX();
		for(Entry<Double, Double> entry : availableArea.entrySet()){
			drawLine(g2, new Line2D.Double(x, entry.getKey(), x, entry.getValue()));
		}

		if(availableArea.size() <= 0){
			System.out.println("No area left after processing field");
			return;
		}
		
		//in this case size DOES matter
		Double biggestKey = availableArea.firstKey();
		for(Entry<Double, Double> entry : availableArea.entrySet())
			if((entry.getValue() - entry.getKey() )> (availableArea.get(biggestKey) - biggestKey))
				biggestKey = entry.getKey();

		//return point that lies in the center of the biggest point
		g2.setColor(Color.CYAN);
		FieldPoint hitmarker = new FieldPoint(x, (biggestKey/2 + availableArea.get(biggestKey)/2));
		drawOval(g2, (int)hitmarker.getX(), ((int) hitmarker.getY()), 20);
		drawLine(g2, new Line2D.Double(ball.getPosition().toPoint2D(), hitmarker.toPoint2D()));
		return;
	}

	private TreeMap<Double, Double> minMax(
			TreeMap<Double, Double> map, double min, double max) {
		TreeMap<Double, Double> newMap = new TreeMap<Double, Double>();
		for(Entry<Double, Double> entry : map.entrySet()){
			Double newKey, newValue;
			newKey = Math.min(Math.max(entry.getKey(), min), max);
			newValue = Math.min(Math.max(entry.getValue(), min), max);
			newMap.put(newKey, newValue);
		}
		return newMap;
	}

	private TreeMap<Double, Double> invertMap(
			TreeMap<Double, Double> map) {
		TreeMap<Double, Double> invertedMap = new TreeMap<Double, Double>();
		Double prevY1 = null,
				prevY2 = null;
		for(Entry<Double, Double> entry : map.entrySet()){
			if(prevY1 == null){
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
				continue;
			}
			Double Y1 = entry.getKey();
			invertedMap.put(prevY2, Y1);
		}
		return invertedMap;
	}

	@SuppressWarnings("unused")
	private void printMap(TreeMap<Double, Double> map){
		System.out.println("  Y1 |   Y2");
		System.out.println("-------------");
		for(Entry<Double, Double> entry : map.entrySet())
			System.out.println(entry.getKey().intValue() + " | " + entry.getValue().intValue());
	}
	
    private TreeMap<Double, Double> mergeOverlappingValues(TreeMap<Double, Double> map){
		TreeMap<Double, Double> mergedMap = new TreeMap<Double, Double>();
		Double prevY1 = null,
				prevY2 = null;
		for(Entry<Double, Double> entry : map.entrySet()){
			if(prevY1 == null){
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
				continue;
			}
			Double Y1 = entry.getKey();
			Double Y2 = entry.getValue();
			if(prevY2 >= Y1){
				//meergeeee
				prevY1 = (prevY1 > Y1 ? Y1 : prevY1);
				prevY2 = (prevY2 > Y2 ? prevY2 : Y2);
			}else {
				mergedMap.put((prevY1 < prevY2 ? prevY1 : prevY2), (prevY1 > prevY2 ? prevY1 : prevY2));
				//no merge
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
			}
		}
		mergedMap.put((prevY1 < prevY2 ? prevY1 : prevY2), (prevY1 > prevY2 ? prevY1 : prevY2));
		return mergedMap;
    }
   
	private void drawLine(Graphics2D g2, Line2D.Double line){
		Point2D.Double p1 = new FieldPoint(line.getP1().getX(), line.getP1().getY()).toGUIPoint(ratio);
		Point2D.Double p2 = new FieldPoint(line.getP2().getX(), line.getP2().getY()).toGUIPoint(ratio);
		g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
	}
	
	private void drawOval(Graphics2D g2,int _x, int _y, int _width){
		//g2.setStroke(new BasicStroke(1));
		int width = (int) (_width*ratio);
		int x = (int) (_x*ratio + getWidth()/2 - width/2);
		int y = (int) (-1*_y*ratio + getHeight()/2 - width/2);
		g2.drawOval(x, y, width, width);
	}
    
    private void drawFieldObjects(Graphics g) {
    	Graphics2D g2 = (Graphics2D)g;
    	g2.setColor(Color.cyan);
		for(Robot robot : World.getInstance().getAllRobots()){
			drawOval(g2, (int) robot.getPosition().getX(), (int) robot.getPosition().getY(), Robot.DIAMETER);
		}

		Ball ball = World.getInstance().getBall();
		g2.setColor(Color.orange);
		drawOval(g2, (int) ball.getPosition().getX(), (int) ball.getPosition().getY(), 42);
	}

	private void drawRaster(Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));

    	g2.setColor(Color.LIGHT_GRAY);
    	for(double y=- getHeight() / 20; y < getWidth(); y += getHeight() / 10)
    		g2.drawLine((int) y, 0, (int) y, getHeight());

    	for(double x = 0; x < getHeight(); x += getHeight() / 10)
    		g2.drawLine(0, (int)x, getWidth(), (int)x);
    	
    	g2.setColor(Color.WHITE);
    	g2.drawLine(getHeight() / 20 + 5, 15, getHeight() / 20 + getHeight() / 10 - 5, 15);
    	g2.drawLine(getHeight() / 20 + 5, 10, getHeight() / 20 + 5, 20);
    	g2.drawLine(getHeight() / 20 + getHeight() / 10 - 5, 10, getHeight() / 20 + getHeight() / 10 - 5, 20);
    	String sizeDesc = String.format("%.1fcm",  (double)FIELDWIDTH / (double)(FIELDWIDTH / (FIELDHEIGHT / 10)) / 10);
    	g2.drawString(sizeDesc, getHeight() / 10 + 5 - sizeDesc.length() * 7 / 2, 30);
    }
    
    // must  be redone, zones now exist within the model.
    private void drawZone(Graphics g, FieldZone fieldZone) {
	    Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));
		//zone.setOffset(getSize(), ratio);
		//zone.calculateAbsolutePoints();

		int[] scaledX = new int[fieldZone.getNumberOfVertices()];
		int[] scaledY = new int[fieldZone.getNumberOfVertices()];
		int index =0;
		for(FieldPoint point : fieldZone.getVertices()){
			scaledX[index] = (int) (point.toGUIPoint(ratio).getX());
			scaledY[index] = (int) (point.toGUIPoint(ratio).getY());
			index++;
		}
		
		g2.setColor(Color.BLUE);
		g2.drawPolygon(scaledX, scaledY, fieldZone.getNumberOfVertices());
		g2.drawString(fieldZone.name(), (int) (fieldZone.getCenterPoint().toGUIPoint(ratio).getX() - fieldZone.name().length()*4), (int) (fieldZone.getCenterPoint().toGUIPoint(ratio).getY()));
    }

	private void drawPlayfield(Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));

		//middle line
		g.setColor(Color.WHITE);
		g2.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
		
		//middle circle
		g2.drawArc(getWidth() / 2 - getWidth() / 18, getHeight() / 2 - getWidth() / 18, getWidth() / 9, getWidth() / 9, 0, 360);
		
		//draw left goaly space
		g2.drawArc(-getWidth() / 9, getHeight() / 2 - getWidth() / 9 - getWidth() / 18, (int) (getWidth() / 9.0 * 2), (int) (getWidth() / 9.0 * 2), 0, 90);
		g2.drawArc(-getWidth() / 9, getHeight() / 2 - getWidth() / 9 + getWidth() / 18, (int) (getWidth() / 9.0 * 2), (int) (getWidth() / 9.0 * 2), 0, -90);
		g2.drawLine(getWidth() / 9 + 1, getHeight() / 2 - getWidth() / 18, 
					getWidth() / 9 + 1, getHeight() / 2 + getWidth() / 18);

		//draw right goaly area thingy <insert footbalterm>
		g2.drawArc((int) (getWidth() / 9.0 * 8), getHeight() / 2 - getWidth() / 9 - getWidth() / 18, (int) (getWidth() / 9.0 * 2), getWidth() / 9 * 2, 180, -90);
		g2.drawArc((int) (getWidth() / 9.0 * 8), getHeight() / 2 - getWidth() / 9 + getWidth() / 18, (int) (getWidth() / 9.0 * 2), getWidth() / 9 * 2, 180, 90);
		g2.drawLine((int) (getWidth() / 9.0 * 8), getHeight() / 2 - getWidth() / 18, 
					(int) (getWidth() / 9.0 * 8), getHeight() / 2 + getWidth() / 18);
		g2.setStroke(new BasicStroke(5));
		drawLine(g2, new Line2D.Double(World.getInstance().getField().getEastGoal().getFrontSouth().toGUIPoint(ratio),
				World.getInstance().getField().getEastGoal().getFrontNorth().toGUIPoint(ratio)));
		drawLine(g2, new Line2D.Double(World.getInstance().getField().getWestGoal().getFrontSouth().toGUIPoint(ratio),
				World.getInstance().getField().getWestGoal().getFrontNorth().toGUIPoint(ratio)));
		
	}
}