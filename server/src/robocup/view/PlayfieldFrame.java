package robocup.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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
	}

	private void initFieldObjects(){
		ArrayList<Robot> robots = World.getInstance().getAllRobots();
		robots.get(0).setPosition(new Point(-500,400));
		robots.get(1).setPosition(new Point(-2500, -250));
		robots.get(2).setPosition(new Point(-1500, -120));

		World.getInstance().getBall().setPosition(new Point(100,100));
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
    	drawFreeShot(g);
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setColor(Color.white);
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
		if(ball.getOwner() instanceof Enemy){
			System.out.println("Enemy has the ball");
			return;
		}
		
		FieldZone[] zones = {FieldZone.WEST_RIGHT_CORNER, FieldZone.EAST_CENTER, FieldZone.EAST_LEFT_FRONT, FieldZone.EAST_MIDDLE, FieldZone.EAST_LEFT_SECOND_POST, 
							FieldZone.WEST_CENTER, FieldZone.WEST_LEFT_FRONT, FieldZone.WEST_MIDDLE, FieldZone.WEST_LEFT_SECOND_POST};
		
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
			System.out.println("divertAngle: "+ Math.toDegrees(obstacleLeftAngle));
			
			double dx = enemyGoal.getFrontLeft().getX() - ball.getPosition().getX();
			double dyL = Math.tan(obstacleLeftAngle) * dx;
			double dyR = Math.tan(obstacleRightAngle) * dx;

			Point L = new Point((float) (ball.getPosition().getX() + dx),
								(float) (ball.getPosition().getY() + dyL));
			Point R = new Point((float) (ball.getPosition().getX() + dx),
					(float) (ball.getPosition().getY() + dyR));

			g2.setColor(Color.blue);
			drawOval(g2, (int) L.getX(), (int) L.getY(), 10);
			drawLine(g2, new Line2D.Double(L.toPoint2D(), ball.getPosition().toPoint2D()));
			g2.setColor(Color.yellow);
			drawOval(g2, (int) R.getX(), (int) R.getY(), 10);
			drawLine(g2, new Line2D.Double(R.toPoint2D(), ball.getPosition().toPoint2D()));
			
			//is there a point with the same start X coordinate
			//if so, check whether it is bigger, and replace it if necessary
			if(!obstructedArea.containsKey(L.toPoint2D().getY()) ||
					(obstructedArea.get(L.toPoint2D().getY())) > R.toPoint2D().getY())
				obstructedArea.put(L.toPoint2D().getY(), R.toPoint2D().getY());
		}

		
		//list with area that is not blocked
		ArrayList<Line2D.Double> availableArea = new ArrayList<Line2D.Double>();

		Double minMaxValue = 10000.0;
		Double maxKey = null;
		for(Entry<Double, Double> entry : obstructedArea.entrySet()) {
			if(entry.getValue() > (double) enemyGoal.getFrontRight().getY() && entry.getValue() < minMaxValue){
				minMaxValue = entry.getValue();
				maxKey = entry.getKey();
			}
		}

		double maxY;
		if(maxKey == null)
			maxY = enemyGoal.getFrontRight().getY();
		else{
			maxY = maxKey;
			obstructedArea.remove(maxKey);
		}

		Double minKey = obstructedArea.floorKey((double) enemyGoal.getFrontLeft().getY());
		Double minY;
		if(minKey == null)
			minY = (double) enemyGoal.getFrontLeft().getY();
		else{
			minY = obstructedArea.get(minKey);
			obstructedArea.remove(minKey);
		}

		//merge lines that overlap
		Line2D.Double currentLine = new Line2D.Double();
		
		double x = (double) enemyGoal.getFrontLeft().getX();

		currentLine = new Line2D.Double(x, minY, x, maxY);
		g2.setColor(Color.PINK);
		drawLine(g2, currentLine);
		currentLine = new Line2D.Double(x, obstructedArea.firstEntry().getKey(), x, obstructedArea.firstEntry().getValue());
		g2.setColor(Color.MAGENTA);
		drawLine(g2, currentLine);
		
		obstructedArea.remove(obstructedArea.firstKey());
		for(Entry<Double, Double> entry : obstructedArea.entrySet()) {
			Double Y1 = entry.getKey();
			Double Y2 = entry.getValue();
			if(Y1 < currentLine.getY1()){
				availableArea.add(new Line2D.Double(x, Y1, x, Y2));
			}
		}

		if(availableArea.size() <= 0){
			System.out.println("No aviable area");
			return;
		}

		//in this case size DOES matter
		Line2D.Double biggest = availableArea.get(0);
		for(Line2D.Double line : availableArea)
			if((line.getX1() + line.getX2()) > (biggest.getX1() + biggest.getX2()))
				biggest = line;
		
		//return point that lies in the center of the biggest point
		g2.setColor(Color.CYAN);
		drawLine(g2, biggest);
		return;
		//return new Point((float)(biggest.getX2()/2), (float)y);
	}
    
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
	}
}