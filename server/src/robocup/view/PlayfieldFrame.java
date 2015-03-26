package robocup.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;

import robocup.model.Ball;
import robocup.model.Enemy;
import robocup.model.Goal;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.Zone;
import robocup.model.enums.FieldZone;

@SuppressWarnings("serial")
public class PlayfieldFrame extends JPanel {
	private static int FIELDWIDTH = 6050;
	private static int FIELDHEIGHT = 4050;
	private static double ratio = 0.15;
	
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
		
		addMouseListener(new MouseAdapter() { 
	          public void mousePressed(MouseEvent me) { 
	        	  float x = (float) (((float)(me.getX() - getWidth()/2))*1/ratio);
	        	  float y = (float) (((float)(-1*me.getY() + getHeight()/2))*1/ratio);
	        	  World.getInstance().getBall().setPosition(new Point(x, y));
	        	  repaint();
	          } 
	        }); 

	}

	private void initFieldObjects(){
		ArrayList<Robot> robots = World.getInstance().getAllRobots();
		robots.get(0).setPosition(new Point(-2700,400));
		robots.get(1).setPosition(new Point(-2500, -120));
		//robots.get(2).setPosition(new Point(-1500, 0));

		World.getInstance().getBall().setPosition(new Point(100,-800));
	}
	
    public void paint(Graphics g) {
		super.paintComponents(g);
		//setRatio();
		
		//main playing field
		g.setColor(Color.GREEN.darker());
		g.fillRect(2, 2, getWidth() - 4, getHeight() - 4);

    	drawRaster(g);

    	for (final Map.Entry<FieldZone, Zone> zone : World.getInstance().getField().getZones().entrySet())
    		drawZone(g, zone.getValue());
    	
    	drawPlayfield(g);
    	drawFieldObjects(g);

	    Graphics2D g2 = (Graphics2D) g;
    	drawFreeShot(g2);
	    g2.setColor(Color.white);
    }
    
    public void drawFreeShot(Graphics2D g2){
		g2.setStroke(new BasicStroke(1));

		Point hitmarker = null;
		int i = 0;
		long beginTime = System.currentTimeMillis();
		long endTime = beginTime + (1 * 1000);
		while(System.currentTimeMillis() < endTime){
			hitmarker = hasFreeShot();
			i++;
		}
		System.out.println(i);
		Ball ball = World.getInstance().getBall();
		if(hitmarker != null)
			drawLine(g2, new Line2D.Double(hitmarker.toPoint2D(), ball.getPosition().toPoint2D()));
		else
			System.out.println("No solution");
    }
    
    public Point hasFreeShot(){
    	Ball ball = World.getInstance().getBall();
		//only proceed when we are the ballowner
		if(ball.getOwner() instanceof Enemy)
			return null;
		
		FieldZone[] zones = {FieldZone.EAST_RIGHT_FRONT, FieldZone.EAST_CENTER, FieldZone.EAST_LEFT_FRONT, FieldZone.EAST_MIDDLE, FieldZone.EAST_LEFT_SECOND_POST, FieldZone.EAST_RIGHT_SECOND_POST, 
							FieldZone.WEST_RIGHT_FRONT, FieldZone.WEST_CENTER, FieldZone.WEST_LEFT_FRONT, FieldZone.WEST_MIDDLE, FieldZone.WEST_LEFT_SECOND_POST, FieldZone.WEST_RIGHT_SECOND_POST};
		
		//check if the ball is in a zone from which we can actually make the angle
		if(!Arrays.asList(zones).contains(World.getInstance().locateFieldObject(ball)))
			return null;

		//get the enemy goal (checking which side is ours, and get the opposite 
		Goal enemyGoal = (World.getInstance().getReferee().getDoesTeamPlaysWest(World.getInstance().getReferee().getOwnTeamColor())) ?  World.getInstance().getField().getEastGoal() : World.getInstance().getField().getWestGoal();

		ArrayList<Robot> obstacles = World.getInstance().getAllRobotsInArea(new Point[]{enemyGoal.getFrontLeft(), enemyGoal.getFrontRight(), ball.getPosition()});

		//No obstacles?! shoot directly in the center of the goal;
		if(obstacles.size() == 0)
			return new Point(enemyGoal.getFrontLeft().getX(), 0.0f);
		

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
			
			double dx = enemyGoal.getFrontLeft().getX() - ball.getPosition().getX();
			double dyL = Math.tan(obstacleLeftAngle) * dx;
			double dyR = Math.tan(obstacleRightAngle) * dx;

			Point L = new Point((float) (ball.getPosition().getX() + dx),
								(float) (ball.getPosition().getY() + dyL));
			Point R = new Point((float) (ball.getPosition().getX() + dx),
					(float) (ball.getPosition().getY() + dyR));

			//is there a point with the same start X coordinate
			//if so, check whether it is bigger, and replace it if necessary
			if(!obstructedArea.containsKey(L.toPoint2D().getY()) ||
					(obstructedArea.get(L.toPoint2D().getY())) > R.toPoint2D().getY())
				obstructedArea.put(L.toPoint2D().getY(), R.toPoint2D().getY());
		}
		double minY = (double)enemyGoal.getFrontLeft().getY();
		double maxY = (double)enemyGoal.getFrontRight().getY();
		obstructedArea = minMax(obstructedArea, minY, maxY);
		obstructedArea = mergeOverlappingValues(obstructedArea);

		TreeMap<Double, Double> availableArea = invertMap(obstructedArea);

		if(obstructedArea.firstKey() != minY)
			availableArea.put(minY, obstructedArea.firstKey());
		if(obstructedArea.lastEntry().getValue() != maxY)
			availableArea.put(obstructedArea.lastEntry().getValue(), maxY);

		double x = (double) enemyGoal.getFrontLeft().getX();
		
		if(availableArea.size() <= 0)
			return null;
		
		//in this case size DOES matter
		Double biggestKey = availableArea.firstKey();
		for(Entry<Double, Double> entry : availableArea.entrySet())
			if(entry.getValue() - entry.getKey() > availableArea.get(biggestKey) - biggestKey)
				biggestKey = entry.getKey();

		//return point that lies in the center of the biggest point
		Point hitmarker = new Point((float)x, (float)(biggestKey/2 + availableArea.get(biggestKey)/2));
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

		FieldZone[] zones = {FieldZone.EAST_RIGHT_FRONT, FieldZone.EAST_CENTER, FieldZone.EAST_LEFT_FRONT, FieldZone.EAST_MIDDLE, FieldZone.EAST_LEFT_SECOND_POST, FieldZone.EAST_RIGHT_SECOND_POST, 
							FieldZone.WEST_RIGHT_FRONT, FieldZone.WEST_CENTER, FieldZone.WEST_LEFT_FRONT, FieldZone.WEST_MIDDLE, FieldZone.WEST_LEFT_SECOND_POST, FieldZone.WEST_RIGHT_SECOND_POST};
		
		//check if the ball is in a zone from which we can actually make the angle
		if(!Arrays.asList(zones).contains(World.getInstance().locateFieldObject(ball))){
			System.out.println("Ball is in wrong zone (" + World.getInstance().locateFieldObject(ball) +".");
			return ;
		}

		//get the enemy goal (checking which side is ours, and get the opposite 
		Goal enemyGoal = (World.getInstance().getReferee().getDoesTeamPlaysWest(World.getInstance().getReferee().getOwnTeamColor())) ?  World.getInstance().getField().getEastGoal() : World.getInstance().getField().getWestGoal();

		//scoreArea.add(new Line2D.Double(enemyGoal.getFrontLeft().toPoint2D(), enemyGoal.getFrontRight().toPoint2D()));
		//drawPolygon(g2, new Point[]{enemyGoal.getFrontLeft(), enemyGoal.getFrontRight(), ball.getPosition()});
		drawLine(g2, new Line2D.Double(enemyGoal.getFrontLeft().toPoint2D(), ball.getPosition().toPoint2D()));
		drawLine(g2, new Line2D.Double(enemyGoal.getFrontRight().toPoint2D(), ball.getPosition().toPoint2D()));
		ArrayList<Robot> obstacles = World.getInstance().getAllRobotsInArea(new Point[]{enemyGoal.getFrontLeft(), enemyGoal.getFrontRight(), ball.getPosition()});

		//No obstacles?! shoot directly in the center of the goal;
		if(obstacles.size() == 0){
			System.out.println("No obstacles");
			drawOval(g2, (int) (enemyGoal.getFrontLeft().getX()), 0, 43);
			return;
		}
		

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
			
			double dx = enemyGoal.getFrontLeft().getX() - ball.getPosition().getX();
			double dyL = Math.tan(obstacleLeftAngle) * dx;
			double dyR = Math.tan(obstacleRightAngle) * dx;

			Point L = new Point((float) (ball.getPosition().getX() + dx),
								(float) (ball.getPosition().getY() + dyL));
			Point R = new Point((float) (ball.getPosition().getX() + dx),
					(float) (ball.getPosition().getY() + dyR));

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
		double minY = (double)enemyGoal.getFrontLeft().getY();
		double maxY = (double)enemyGoal.getFrontRight().getY();
		obstructedArea = minMax(obstructedArea, minY, maxY);
		obstructedArea = mergeOverlappingValues(obstructedArea);

		TreeMap<Double, Double> availableArea = invertMap(obstructedArea);

		if(obstructedArea.firstKey() != minY)
			availableArea.put(minY, obstructedArea.firstKey());
		if(obstructedArea.lastEntry().getValue() != maxY)
			availableArea.put(obstructedArea.lastEntry().getValue(), maxY);

		double x = (double) enemyGoal.getFrontLeft().getX();
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
			if(entry.getValue() - entry.getKey() > availableArea.get(biggestKey) - biggestKey)
				biggestKey = entry.getKey();

		//return point that lies in the center of the biggest point
		g2.setColor(Color.CYAN);
		Point hitmarker = new Point((float)x, (float)(biggestKey/2 + availableArea.get(biggestKey)/2));
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

    
	@SuppressWarnings("unused")
	private void drawPolygon(Graphics2D g2, Point[] points){
		int[] xPoints = new int[points.length];
		int[] yPoints = new int[points.length];
		int i = 0;
		for(Point point : points){
			xPoints[i] = (int) (point.getX()*ratio + getWidth()/2);
			yPoints[i] = (int) (-1*point.getY()*ratio + getHeight()/2);
			i++;
		}
		g2.drawPolygon(xPoints, yPoints, i);
	}
	
	private void drawLine(Graphics2D g2, Line2D.Double line){
		int x1 = (int) (line.getX1()*ratio + getWidth()/2);
		int y1 = (int) (-1*line.getY1()*ratio + getHeight()/2); 
		int x2 = (int) (line.getX2()*ratio + getWidth()/2); 
		int y2 = (int) (-1*line.getY2()*ratio + getHeight()/2);
		g2.drawLine(x1, y1, x2, y2);
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
    	String sizeDesc = String.format("%.1fcm", (double) FIELDWIDTH / (FIELDWIDTH / (FIELDHEIGHT / 10)) / 10);
    	g2.drawString(sizeDesc, getHeight() / 10 + 5 - sizeDesc.length() * 7 / 2, 30);
    }
    
    // must  be redone, zones now exist within the model.
    private void drawZone(Graphics g, Zone zone) {
	    Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));
		//zone.setOffset(getSize(), ratio);
		//zone.calculateAbsolutePoints();

		int[] scaledX = new int[zone.getNPoints()];
		int[] scaledY = new int[zone.getNPoints()];
		for (int index = 0; index < zone.getNPoints(); index++) {
			int unscaledX = zone.getAbsoluteXPoints()[index];
			int unscaledY = zone.getAbsoluteYPoints()[index];
			scaledX[index] = (int) (unscaledX*ratio);
			scaledY[index] = (int) (unscaledY*ratio);
		}
		g2.setColor(Color.BLUE);
		g2.drawPolygon(scaledX, scaledY, zone.getNPoints());
		g2.drawString(zone.getName(), (int)(((zone.getAbsoluteXPoints()[0] + zone.getAbsoluteXPoints()[2]) / 2 - zone.getName().length() / 2 * 4)*ratio), (int)(((zone.getAbsoluteYPoints()[0] + zone.getAbsoluteYPoints()[2]) / 2)*ratio));
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
		drawLine(g2, new Line2D.Double(World.getInstance().getField().getEastGoal().getFrontLeft().toPoint2D(),
				World.getInstance().getField().getEastGoal().getFrontRight().toPoint2D()));
		drawLine(g2, new Line2D.Double(World.getInstance().getField().getWestGoal().getFrontLeft().toPoint2D(),
				World.getInstance().getField().getWestGoal().getFrontRight().toPoint2D()));
		
	}
}