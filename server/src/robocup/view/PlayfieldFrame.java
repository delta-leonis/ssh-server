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

	private static double RATIO = 0.10;

	private static int spaceBufferX = 15;
	private static int spaceBufferY = 35;

	private World world = World.getInstance();

	public static void main(String [] args) {
		JFrame frame = new JFrame();	
		frame.setSize((int) (World.getInstance().getField().getLength()*RATIO + spaceBufferX), (int) (World.getInstance().getField().getWidth()*RATIO + spaceBufferY));
		frame.setContentPane(new PlayfieldFrame());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Constructor of {@link PlayFieldFrame}. The frame size is set, the mouseListener is added for 
	 * the ball position and a situation is initialized via {@link #initFieldObjects()}.
	 */
	public PlayfieldFrame() {
		setSize((int) (World.getInstance().getField().getLength()*RATIO), (int) (World.getInstance().getField().getWidth()*RATIO));
		initFieldObjects();
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				double x = me.getX() - getWidth() / 2;
				double y = -1 * me.getY() + getHeight() / 2;
				world.getBall().setPosition(new FieldPoint(x, y));
				repaint();
			}
		});

	}

	/**
	 * Initializes a situation on the pitch with 2 {@link Robot}s and a {@link Ball}.
	 */
	private void initFieldObjects() {
		ArrayList<Robot> robots = world.getAllRobots();
		robots.get(0).setPosition(new FieldPoint(-2700, 400));
		robots.get(1).setPosition(new FieldPoint(-2500, -120));
		world.getBall().setPosition(new FieldPoint(100, -800));
	}

	public void paint(Graphics g) {
		super.paintComponents(g);

		//Main playing field
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

	/**
	 * Draws a shot line for the ball if there is a free shot.
	 * @param g2D {@link Graphics2D} to draw with.
	 */
	public void drawFreeShot(Graphics2D g2D) {
		g2D.setStroke(new BasicStroke(1));

		FieldPoint hitmarker = world.hasFreeShot();
		Ball ball = world.getBall();
		if (hitmarker != null)
			drawLine(g2D, new Line2D.Double(hitmarker.toPoint2D(), ball.getPosition().toPoint2D()));
		else
			System.out.println("No solution");
	}

	/**
	 * Checks whether a ally has a free shot, will only be checked if the robot
	 * is in one of the 6 center zones (due to accuracy)
	 * 
	 * @param executer
	 * @return
	 */
	public void drawFreeShot(Graphics g) {
		Ball ball = world.getBall();
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.RED);
		// only proceed when we are the ball owner
		if (ball.getOwner() instanceof Enemy)
			return;

		FieldZone[] shootingAllowedZones = { FieldZone.EAST_NORTH_FRONT, FieldZone.EAST_CENTER,
				FieldZone.EAST_SOUTH_FRONT, FieldZone.EAST_MIDDLE, FieldZone.EAST_SOUTH_SECONDPOST,
				FieldZone.EAST_NORTH_SECONDPOST, FieldZone.WEST_NORTH_FRONT, FieldZone.WEST_CENTER,
				FieldZone.WEST_SOUTH_FRONT, FieldZone.WEST_MIDDLE, FieldZone.WEST_SOUTH_SECONDPOST,
				FieldZone.WEST_NORTH_SECONDPOST };

		// check if the ball is in a zone from which we can actually make the angle
		if (!Arrays.asList(shootingAllowedZones).contains(world.locateFieldObject(ball))) {
			System.out.println("Ball is in wrong zone (" + world.locateFieldObject(ball) + ".");
			return;
		}

		// get the enemy goal (checking which side is ours, and get the opposite
		Goal enemyGoal = (world.getReferee().isWestTeamColor(world.getReferee().getAllyTeamColor())) ? world.getField()
				.getEastGoal() : world.getField().getWestGoal();

		drawLine(g2, new Line2D.Double(enemyGoal.getFrontSouth().toPoint2D(), ball.getPosition().toPoint2D()));
		drawLine(g2, new Line2D.Double(enemyGoal.getFrontNorth().toPoint2D(), ball.getPosition().toPoint2D()));
		
		System.out.println("Now calling getallrobotsinarea");
		//*************************************************************************************************************************************
		ArrayList<Robot> obstacles = world.getAllRobotsInArea(enemyGoal.getFrontSouth(), enemyGoal.getFrontNorth(),
				ball.getPosition());

		// No obstacles?! shoot directly in the center of the goal;
		if (obstacles.size() == 0) {
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

	/**
	 * 
	 * @param map
	 * @return
	 */
	private TreeMap<Double, Double> mergeOverlappingValues(TreeMap<Double, Double> map) {
		TreeMap<Double, Double> mergedMap = new TreeMap<Double, Double>();
		Double prevY1 = null, prevY2 = null;
		for (Entry<Double, Double> entry : map.entrySet()) {
			if (prevY1 == null) {
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
				continue;
			}
			Double Y1 = entry.getKey();
			Double Y2 = entry.getValue();
			if (prevY2 >= Y1) {
				// merge
				prevY1 = (prevY1 > Y1 ? Y1 : prevY1);
				prevY2 = (prevY2 > Y2 ? prevY2 : Y2);
			} else {
				mergedMap.put((prevY1 < prevY2 ? prevY1 : prevY2), (prevY1 > prevY2 ? prevY1 : prevY2));
				// no merge
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
			}
		}
		mergedMap.put((prevY1 < prevY2 ? prevY1 : prevY2), (prevY1 > prevY2 ? prevY1 : prevY2));
		return mergedMap;
	}

	private void drawLine(Graphics2D g2, Line2D.Double line) {
		Point2D.Double p1 = new FieldPoint(line.getP1().getX(), line.getP1().getY()).toGUIPoint(RATIO);
		Point2D.Double p2 = new FieldPoint(line.getP2().getX(), line.getP2().getY()).toGUIPoint(RATIO);
		g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
	}

	private void drawOval(Graphics2D g2, int _x, int _y, int _width) {
		int width = (int) (_width * RATIO);
		int x = (int) (_x * RATIO + getWidth() / 2 - width / 2);
		int y = (int) (-1 * _y * RATIO + getHeight() / 2 - width / 2);
		g2.drawOval(x, y, width, width);
	}

	private void drawFieldObjects(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.cyan);
		for (Robot robot : world.getAllRobots()) {
			if (robot.getPosition() != null){
				drawOval(g2, (int) robot.getPosition().toPoint2D().getX(), (int) robot.getPosition().toPoint2D().getY(),
					Robot.DIAMETER);
			}
		}

		Ball ball = world.getBall();
		g2.setColor(Color.orange);
		drawOval(g2, (int) ball.getPosition().toPoint2D().getX(), (int) ball.getPosition().toPoint2D().getY(), 42);
	}

	private void drawRaster(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));

		g2.setColor(Color.LIGHT_GRAY);
		for (double y = -getHeight() / 20; y < getWidth(); y += getHeight() / 10)
			g2.drawLine((int) y, 0, (int) y, getHeight());

		for (double x = 0; x < getHeight(); x += getHeight() / 10)
			g2.drawLine(0, (int) x, getWidth(), (int) x);

		g2.setColor(Color.WHITE);
		g2.drawLine(getHeight() / 20 + 5, 15, getHeight() / 20 + getHeight() / 10 - 5, 15);
		g2.drawLine(getHeight() / 20 + 5, 10, getHeight() / 20 + 5, 20);
		g2.drawLine(getHeight() / 20 + getHeight() / 10 - 5, 10, getHeight() / 20 + getHeight() / 10 - 5, 20);
		String sizeDesc = String.format("%.1fcm", (double) World.getInstance().getField().getLength()*RATIO / RATIO
				/ (double) (World.getInstance().getField().getLength()*RATIO / RATIO / (World.getInstance().getField().getWidth()*RATIO / RATIO / 10)) / 10);
		g2.drawString(sizeDesc, getHeight() / 10 + 5 - sizeDesc.length() * 7 / 2, 30);
	}

	// must be redone, zones now exist within the model.
	private void drawZone(Graphics g, FieldZone fieldZone) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));

		int[] scaledX = new int[fieldZone.getNumberOfVertices()];
		int[] scaledY = new int[fieldZone.getNumberOfVertices()];
		int index = 0;
		for (FieldPoint point : fieldZone.getVertices()) {
			scaledX[index] = (int) (point.toGUIPoint(RATIO).getX());
			scaledY[index] = (int) (point.toGUIPoint(RATIO).getY());
			index++;
		}

		g2.setColor(Color.BLUE);
		g2.drawPolygon(scaledX, scaledY, fieldZone.getNumberOfVertices());
		g2.drawString(fieldZone.name(), (int) (fieldZone.getCenterPoint().toGUIPoint(RATIO).getX() - fieldZone.name()
				.length() * 4), (int) (fieldZone.getCenterPoint().toGUIPoint(RATIO).getY()));
	}

	private void drawPlayfield(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));

		// center line
		g.setColor(Color.WHITE);
		g2.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

		// center circle
		g2.drawArc(getWidth() / 2 - getWidth() / 18, getHeight() / 2 - getWidth() / 18, getWidth() / 9, getWidth() / 9, 0, 360);

		// draw left penalty area
		g2.drawArc(-getWidth() / 9, getHeight() / 2 - getWidth() / 9 - getWidth() / 18, (int) (getWidth() / 9.0 * 2),
				(int) (getWidth() / 9.0 * 2), 0, 90);
		g2.drawArc(-getWidth() / 9, getHeight() / 2 - getWidth() / 9 + getWidth() / 18, (int) (getWidth() / 9.0 * 2),
				(int) (getWidth() / 9.0 * 2), 0, -90);
		g2.drawLine(getWidth() / 9 + 1, getHeight() / 2 - getWidth() / 18, getWidth() / 9 + 1, getHeight() / 2
				+ getWidth() / 18);

		// draw right penalty area
		g2.drawArc((int) (getWidth() / 9.0 * 8), getHeight() / 2 - getWidth() / 9 - getWidth() / 18,
				(int) (getWidth() / 9.0 * 2), getWidth() / 9 * 2, 180, -90);
		g2.drawArc((int) (getWidth() / 9.0 * 8), getHeight() / 2 - getWidth() / 9 + getWidth() / 18,
				(int) (getWidth() / 9.0 * 2), getWidth() / 9 * 2, 180, 90);
		g2.drawLine((int) (getWidth() / 9.0 * 8), getHeight() / 2 - getWidth() / 18, (int) (getWidth() / 9.0 * 8),
				getHeight() / 2 + getWidth() / 18);

		g2.setStroke(new BasicStroke(5));
		drawLine(g2, new Line2D.Double(world.getField().getEastGoal().getFrontSouth().toPoint2D(), world.getField()
				.getEastGoal().getFrontNorth().toPoint2D()));
		drawLine(g2, new Line2D.Double(world.getField().getWestGoal().getFrontSouth().toPoint2D(), world.getField()
				.getWestGoal().getFrontNorth().toPoint2D()));

	}
}