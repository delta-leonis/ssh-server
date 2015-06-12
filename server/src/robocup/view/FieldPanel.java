package robocup.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.controller.ai.movement.DijkstraPathPlanner;
import robocup.controller.ai.movement.DijkstraPathPlanner.Vertex;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.Enemy;
import robocup.model.FieldObject;
import robocup.model.FieldPoint;
import robocup.model.Goal;
import robocup.model.Obstruction;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.TeamColor;

/**
 * {@link FieldPanel} is a {@link JPanel} that shows the field.
 */
@SuppressWarnings("serial")
public class FieldPanel extends JPanel {

	private static double RATIO = 0.10;

	private static int spaceBufferX = 140;
	private static int spaceBufferY = 30;

	private World world = World.getInstance();

	private boolean showFreeShot;
	private boolean showRaster;
	private boolean showRobots;
	private boolean showZones;
	private boolean showBall;
	private boolean showCoords;
	private boolean mirror;
	private boolean showPathPlanner;
	private boolean drawNeighbours;
	private boolean drawVertices;
	private boolean showVectors;

	private FieldObject mouseObject;
	private int frameCount;
	private int FPS;
	private long lastFPSpaint;

	private boolean showObstructions;

	/**
	 * Constructor of {@link FieldPanel}. The panel size is set and the mouseListener is added for 
	 * the ball position.
	 */
	public FieldPanel() {
		setSize((int) (World.getInstance().getField().getLength()*RATIO), (int) (World.getInstance().getField().getWidth()*RATIO));
		showFreeShot = false;
		showRaster = false;
		mirror = false;
		showVectors = false;
		mouseObject = world.getBall();

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				double ratio = (double)((SwingUtilities.getWindowAncestor(me.getComponent())).getWidth() - spaceBufferX * 2)
						/ (double) world.getField().getLength();
				double x = - world.getField().getLength() / 2 + (me.getX() - spaceBufferX) / ratio;
				double y = -1 * (- (world.getField().getWidth()) / 2 + (me.getY() - spaceBufferY) / ratio);
				mouseObject.setPosition(new FieldPoint(x, y));
				mouseObject.setOverrideOnsight(true);
				if(mouseObject instanceof Ally || mouseObject instanceof Enemy)
					(mouseObject instanceof Ally ? world.getValidAllyIDs() : world.getValidEnemyIDs()).add(((Robot)mouseObject).getRobotId());
				repaint();
			}
		});

	}

	/**
	 * Function to get the size of the {@link JFrame} the {@link FieldPanel} should be in.
	 * @return width of the {@link JFrame} the {@link FieldPanel} should be in.
	 */
	public int getFrameSizeX() {
		return (int) (World.getInstance().getField().getLength()*RATIO + spaceBufferX*2);
	}

	/**
	 * Function to get the ../server/dist/size of the {@link JFrame} the {@link FieldPanel} should be in.
	 * @return height of the {@link JFrame} the {@link FieldPanel} should be in.
	 */
	public int getFrameSizeY() {
		return (int) (World.getInstance().getField().getWidth()*RATIO + spaceBufferY*2);
	}

	/**
	 * Sets the {@link FieldObject} for the mouse listener to replace.
	 * @param mouseObject {@link FieldObject} that has to be placed on the position of the mouse click.
	 */
	public void setMouseObject(FieldObject mouseObject) {
		this.mouseObject = mouseObject;
	}

	/**
	 * Paints the whole screen/field green. Draws the pitch by calling {@link FieldPanel#drawPlayfield(Graphics, double)}.
	 * Draws all {@link FieldZone}s by calling {@link FieldPanel#drawZone(Graphics, FieldZone, double)} for each {@link FieldZone}.
	 */
	public void paint(Graphics g) {
		super.paintComponents(g);

		g.setColor(Color.GREEN.darker());
		g.fillRect(0, 0, getWidth(), getHeight());

		double ratio = (double)((SwingUtilities.getWindowAncestor(this)).getWidth() - spaceBufferX * 2)
				/ (double) world.getField().getLength();

		drawRaster(g, ratio);
		if (showZones)
			for (FieldZone fieldZone : FieldZone.values())
				drawZone(g, fieldZone, ratio);
		drawPlayfield(g, ratio);
		drawFreeShot(g, ratio);
		drawPathPlanner(g, ratio);
		drawRobots(g, ratio);
		drawBall(g, ratio);
		drawCoords(g, ratio);
		drawFPS(g, ratio);
		drawVectors(g, ratio);
		drawObstructions(g, ratio);
	}
	
	private Shape getRectAngle(int x, int y, int width, int height, int angle){
		double theta = Math.toRadians(angle);
		
		// create rect centred on the point we want to rotate it about
		Rectangle2D rect = new Rectangle2D.Double(-width/2., -height/2., width, height);
	
		AffineTransform transform = new AffineTransform();
		transform.translate(x, y);
		transform.rotate(theta);

		return transform.createTransformedShape(rect);
	}

	/**
	 * Draws the obstructions that are placed on the field
	 * @param g		graphics object
	 * @param ratio	ratio for drawing
	 */
	private void drawObstructions(Graphics g, double ratio){
		if(!showObstructions)
			return;

		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(4));
		for(Obstruction obstruction : World.getInstance().getObstructions()){
			if(obstruction.getPosition() != null){
				g.setColor(Color.GRAY);
				Shape rect = getRectAngle((int)obstruction.getPosition().toGUIPoint(ratio, mirror).getX() + spaceBufferX, (int)obstruction.getPosition().toGUIPoint(ratio, mirror).getY()+ spaceBufferY, (int)(obstruction.getWidth()*ratio), (int)(obstruction.getLength()*ratio), (int)obstruction.getOrientation());
				g2.fill(rect);
				g.setColor(g.getColor().darker());
				g2.draw(rect);
			}
		}
	}
	
	/**
	 * Draws vectors voor {@link Ball} and {@link Ally Allies}
	 * @param g	graphics object
	 * @param ratio	ratio for drawing
	 */
	private void drawVectors(Graphics g, double ratio){
		if(!showVectors)
			return;

		if(showRobots)
			for(RobotExecuter executer : world.getRobotExecuters())
				if(executer.getLowLevelBehavior() != null && executer.getLowLevelBehavior().getGotoPosition() != null)
					drawRobotVector(g, ratio, executer);

		if(showBall){
			g.setColor(Color.ORANGE);
			Ball ball = world.getBall();
			FieldPoint startPoint = ball.getPosition();
			int length = (int) (1000*ratio);
			double dX = Math.cos(Math.toRadians(ball.getDirection()))*length;
			double dY = Math.sin(Math.toRadians(ball.getDirection()))*length;
			FieldPoint endPoint = new FieldPoint(startPoint.getX() + dX, startPoint.getY() + dY);
			g.drawLine((int) startPoint.toGUIPoint(ratio, mirror).getX() + spaceBufferX, (int) startPoint.toGUIPoint(ratio, mirror).getY() + spaceBufferY, (int) endPoint.toGUIPoint(ratio, mirror).getX() + spaceBufferX, (int) endPoint.toGUIPoint(ratio, mirror).getY() + spaceBufferY );
		}
	}

	/**
	 * Draws vectors voor  {@link Ally Ally}
	 * @param g	graphics object
	 * @param ratio	ratio for drawing
	 * @param executer executer containing destination to draw to
	 */
	private void drawRobotVector(Graphics g, double ratio, RobotExecuter executer) {
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(RobotBox.getRoleColor(((Ally)executer.getRobot()).getRole()));
		double gotoSpeed = executer.getLowLevelBehavior().getGotoPosition().getCurrentSpeed();
		if(gotoSpeed < 500)
			return;

		double percentage = ((double)GotoPosition.MAX_VELOCITY)/gotoSpeed;
		FieldPoint destination = executer.getLowLevelBehavior().getGotoPosition().getDestination();
		FieldPoint startPoint = executer.getRobot().getPosition();
		double dX = destination.getX() - startPoint.getX();
		double dY = destination.getY() - startPoint.getY();

		FieldPoint endPoint = new FieldPoint(startPoint.getX() + dX*percentage, startPoint.getY() + dY*percentage);
		FieldPoint stringPoint = new FieldPoint(startPoint.getX() + dX/2*percentage, startPoint.getY() + dY/2*percentage);
		g.drawLine((int) startPoint.toGUIPoint(ratio, mirror).getX() + spaceBufferX, (int)startPoint.toGUIPoint(ratio, mirror).getY() + spaceBufferY, (int)endPoint.toGUIPoint(ratio, mirror).getX() + spaceBufferX, (int)endPoint.toGUIPoint(ratio, mirror).getY() + spaceBufferY);
		
		g2.setColor(Color.BLACK);
		g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, (int) 10));
		String speed = "" + gotoSpeed;
		g2.drawString(speed, (int)stringPoint.toGUIPoint(ratio, mirror).getX() - speed.length()*5/2 + spaceBufferX, (int)stringPoint.toGUIPoint(ratio, mirror).getY() + spaceBufferY);
		
	}

	private void drawFPS(Graphics g, double ratio){
		g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, (int) 22));
		g.setColor(Color.YELLOW);
		g.drawString("FPS: " + FPS, getWidth() - 100, 25);
	}

	/**
	 * Function for updating the {@link FieldPanel}.
	 */
	public void update() {
		frameCount++;
		if(lastFPSpaint + 1000 < System.currentTimeMillis()){
			lastFPSpaint = System.currentTimeMillis();
			FPS = frameCount;
			frameCount = 0;
		}
		repaint();
	}

	/**
	 * Toggles the boolean {@link FieldPanel#showZones} and repaints.
	 */
	public void toggleShowZones() {
		showZones = !showZones;
		repaint();
	}

	/**
	 * Draws a given {@link FieldZone} on the pitch.
	 * @param g {@link Graphics} to draw to
	 * @param fieldZone {@link FieldZone} to draw
	 * @param ratio A {@link double} indicating the ratio for the sizing. (Screen width / real width) 
	 */
	private void drawZone(Graphics g, FieldZone fieldZone, double ratio) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));

		int[] scaledX = new int[fieldZone.getNumberOfVertices()];
		int[] scaledY = new int[fieldZone.getNumberOfVertices()];
		int index = 0;
		for (FieldPoint point : fieldZone.getVertices()) {
			scaledX[index] = (int) (point.toGUIPoint(ratio, mirror).getX()) + spaceBufferX;
			scaledY[index] = (int) (point.toGUIPoint(ratio, mirror).getY()) + spaceBufferY;
			index++;
		}

		g2.setColor(Color.BLUE);
		g2.drawPolygon(scaledX, scaledY, fieldZone.getNumberOfVertices());
		g2.drawString(fieldZone.name(), (int) (fieldZone.getCenterPoint().toGUIPoint(ratio, mirror).getX() - fieldZone.name()
				.length() * 4) + spaceBufferX, (int) (fieldZone.getCenterPoint().toGUIPoint(ratio, mirror).getY())
				+ spaceBufferY);
	}

	/**
	 * Draws all the field borders, the goals, the penalty areas, the penalty
	 * spots, the center circle and the center line. All sizes are based on the
	 * real field sizes stored in {@link Field}.
	 * @param g {@link Graphics} to draw to
	 * @param ratio A {@link double} indicating the ratio for the sizing. (Screen width / real width)
	 */
	private void drawPlayfield(Graphics g, double ratio) {
		Graphics2D g2 = (Graphics2D) g;
		int lineWidth = Math.max(1, (int) (world.getField().getLineWidth() * ratio));
		g2.setStroke(new BasicStroke(lineWidth));

		int length = (int) (world.getField().getLength() * ratio);
		int width = (int) (world.getField().getWidth() * ratio);
		int penalRadius = (int) (world.getField().getDefenceRadius() * ratio);
		int penalStretch = (int) (world.getField().getDefenceStretch() * ratio);
		int centerRadius = (int) (world.getField().getCenterCircleRadius() * ratio);

		// center line
		g.setColor(Color.WHITE);
		g2.drawLine(length / 2 + spaceBufferX, 0 + spaceBufferY, length / 2 + spaceBufferX, width + spaceBufferY);

		// borders
		g2.drawLine(spaceBufferX, spaceBufferY, length + spaceBufferX, spaceBufferY);
		g2.drawLine(spaceBufferX, width + spaceBufferY, length + spaceBufferX, width + spaceBufferY);
		g2.drawLine(spaceBufferX, spaceBufferY, spaceBufferX, width + spaceBufferY);
		g2.drawLine(length + spaceBufferX, spaceBufferY, length + spaceBufferX, width + spaceBufferY);

		// center circle
		g2.drawArc(length / 2 - centerRadius + spaceBufferX, width / 2 - centerRadius + spaceBufferY, centerRadius * 2,
				centerRadius * 2, 0, 360);

		// draw west penalty area
		g2.drawArc(spaceBufferX - penalRadius, width / 2 - penalRadius - penalStretch / 2 + spaceBufferY,
				(int) (penalRadius * 2), (int) (penalRadius * 2), 0, 90);
		g2.drawArc(spaceBufferX - penalRadius, width / 2 - penalRadius + penalStretch / 2 + spaceBufferY,
				(int) (penalRadius * 2), (int) (penalRadius * 2), 0, -90);
		g2.drawLine(spaceBufferX + penalRadius, width / 2 - penalStretch / 2 + spaceBufferY, spaceBufferX
				+ penalRadius, width / 2 + penalStretch / 2 + spaceBufferY);

		// draw east penalty area
		g2.drawArc(length + spaceBufferX - penalRadius, width / 2 - penalRadius - penalStretch / 2 + spaceBufferY,
				(int) (penalRadius * 2), (int) (penalRadius * 2), 180, -90);
		g2.drawArc(length + spaceBufferX - penalRadius, width / 2 - penalRadius + penalStretch / 2 + spaceBufferY,
				(int) (penalRadius * 2), (int) (penalRadius * 2), 180, 90);
		g2.drawLine(length + spaceBufferX - penalRadius, width / 2 - penalStretch / 2 + spaceBufferY, length
				+ spaceBufferX - penalRadius, width / 2 + penalStretch / 2 + spaceBufferY);

		// west penalty spot
		g2.drawArc((int) (world.getField().getPenaltyLineFromSpotDistance() * ratio) + spaceBufferX - 1,
				(int) ((new FieldPoint(0, 0)).toGUIPoint(ratio, mirror).getY()) + spaceBufferY - 1, 3, 3, 0, 360);
		// east penalty spot
		g2.drawArc(length - ((int) (world.getField().getPenaltyLineFromSpotDistance() * ratio)) + spaceBufferX - 1,
				(int) ((new FieldPoint(0, 0)).toGUIPoint(ratio, mirror).getY()) + spaceBufferY - 1, 3, 3, 0, 360);

		// center spot
		g2.drawArc(length / 2 + spaceBufferX - 1, width / 2 + spaceBufferY - 1, 3, 3, 0, 360);

		// goals
		//g2.setStroke(new BasicStroke(10));
		if (World.getInstance().getReferee().getEastTeam().isColor(TeamColor.YELLOW))
			g.setColor(Color.YELLOW);
		else
			g.setColor(Color.BLUE);

		drawGoal(g2, world.getField().getEastGoal(), ratio);
		g.setColor((g.getColor().equals(Color.BLUE)) ? Color.YELLOW : Color.BLUE);
		drawGoal(g2, world.getField().getWestGoal(), ratio);
		
	}
	
	private void drawGoal(Graphics2D g2, Goal goal, double ratio){
		Point2D.Double backNorth = goal.getBackNorth().toGUIPoint(ratio, mirror);
		Point2D.Double frontNorth = goal.getFrontNorth().toGUIPoint(ratio, mirror);
		Point2D.Double backSouth = goal.getBackSouth().toGUIPoint(ratio, mirror);
		Point2D.Double frontSouth = goal.getFrontSouth().toGUIPoint(ratio, mirror);
		g2.drawLine((int) backNorth.getX() + spaceBufferX,(int)  backNorth.getY() + spaceBufferY, (int) frontNorth.getX() + spaceBufferX, (int) frontNorth.getY() + spaceBufferY);
		g2.drawLine((int) backNorth.getX() + spaceBufferX,(int)  backNorth.getY() + spaceBufferY, (int) backSouth.getX() + spaceBufferX, (int) backSouth.getY() + spaceBufferY);
		g2.drawLine((int) backSouth.getX() + spaceBufferX,(int)  backSouth.getY() + spaceBufferY, (int) frontSouth.getX() + spaceBufferX, (int) frontSouth.getY() + spaceBufferY);
	}

	/**
	 * Toggle to show coordinates of both {@link Robot robots} and the field.
	 */
	public void toggleCoords(){
		showCoords = !showCoords;
		repaint();
	}
	/**
	 * Toggle to show {@link Obstruction obstructions} placed on the field.
	 */
	public void toggleObstructions(){
		showObstructions = !showObstructions;
		repaint();
	}
	
	/**
	 * Toggle to mirror the field horizontally
	 */
	public void toggleMirror(){
		mirror = !mirror;
		repaint();
	}
	
	/**
	 * Draws the co-ordinates of the edges of the field.
	 * @param g The graphics to draw on
	 * @param ratio The current ratio.
	 */
	public void drawCoords(Graphics g, double ratio){
		if(showCoords){
			g.setColor(Color.WHITE);
			FieldPoint northWest = new FieldPoint(-1* world.getField().getLength() / 2, world.getField().getWidth() / 2);
			FieldPoint northEast = new FieldPoint(world.getField().getLength() / 2, world.getField().getWidth() / 2);
			FieldPoint southWest= new FieldPoint(world.getField().getLength() / 2, -1*world.getField().getWidth() / 2);
			FieldPoint southEast = new FieldPoint(-1*world.getField().getLength() / 2, -1*world.getField().getWidth() / 2);
			drawCoord(g, northWest, ratio);
			drawCoord(g, northEast, ratio);
			drawCoord(g, southWest, ratio);
			drawCoord(g, southEast, ratio);
		}
	}
	
	/**
	 * Draw a coordinate as a string
	 * @param g		Graphics to draw on
	 * @param point	{@link FieldPoint} to be drawn
	 * @param ratio The current ratio
	 * @param yoffset An optional offset so the text isn't written over the actual point
	 */
	private void drawCoord(Graphics g, FieldPoint point, double ratio, int yoffset){
		Point2D.Double guiPoint = point.toGUIPoint(ratio, mirror);
		g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, (int) 12));
		String coordinate = String.format("[%.0f, %.0f]", point.getX(), point.getY());
		g.drawString(coordinate, (int)guiPoint.getX() - (coordinate.length()/2)*(g.getFont().getSize()/2)  + spaceBufferX, (int)guiPoint.getY() + yoffset + (yoffset > 0 ? 5 : 0) + spaceBufferY);
	}
	
	/**
	 * Draw a coordinate as a string
	 * @param g		Graphics to draw on
	 * @param point	{@link FieldPoint} to be drawn
	 * @param ratio The current ratio
	 */
	private void drawCoord(Graphics g, FieldPoint point, double ratio){
		drawCoord(g, point, ratio, 0);
	}

	/**
	 * Toggles the boolean {@link FieldPanel#showFreeShot} and repaints.
	 */
	public void toggleShowFreeShot() {
		showFreeShot = !showFreeShot;
		repaint();
	}

	/**
	 * Draws the possible free shot in the {@link FieldPanel}. The {@link FieldPoint} for the free shot is retrieved
	 * by calling {@link World#hasFreeShot()}.
	 * @param g {@link Graphics} to draw to
	 * @param ratio A {@link double} indicating the ratio for the sizing. (Screen width / real width)
	 */
	private void drawFreeShot(Graphics g, double ratio) {
		if(!showFreeShot)
			return;
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.orange);
		FieldPoint target = world.hasFreeShot();
		
		if(target == null)
			return;
		g2.drawLine((int) target.toGUIPoint(ratio, mirror).getX() + spaceBufferX, (int) target
				.toGUIPoint(ratio, mirror).getY()
				+ spaceBufferY, (int) world.getBall().getPosition().toGUIPoint(ratio, mirror).getX() + spaceBufferX,
				(int) world.getBall().getPosition().toGUIPoint(ratio, mirror).getY() + spaceBufferY);
	}

	/**
	 * Toggles the boolean {@link FieldPanel#showFreeShot} and repaints.
	 */
	public void toggleShowBall() {
		showBall = !showBall;
		repaint();
	}

	/**
	 * Draws the {@link Ball} in the {@link FieldPanel}.
	 * @param g {@link Graphics} to draw to
	 * @param ratio A {@link double} indicating the ratio for the sizing. (Screen width / real width)
	 */
	private void drawBall(Graphics g, double ratio) {
		if(!showBall)
			return;
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.orange);
		Ball ball = world.getBall();

		g2.fillOval((int) (ball.getPosition().toGUIPoint(ratio, mirror).getX() + spaceBufferX - (double) ball.SIZE / 2.0
				* ratio), (int) (ball.getPosition().toGUIPoint(ratio, mirror).getY() + spaceBufferY - (double) ball.SIZE / 2.0
				* ratio), (int) (ball.SIZE * ratio), (int) (ball.SIZE * ratio));
		if(showCoords)
			drawCoord(g2, ball.getPosition(), ratio, (int) (ball.SIZE*ratio));
	}

	/**
	 * Toggles the boolean {@link FieldPanel#showFreeShot} and repaints.
	 */
	public void toggleShowRaster() {
		showRaster = !showRaster;
		repaint();
	}

	/**
	 * Draws a raster all over the {@link FieldPanel}. The raster size in cm is displayed in the left top of the {@link FieldPanel}.
	 * @param g {@link Graphics} to draw to
	 * @param ratio A {@link double} indicating the ratio for the sizing. (Screen width / real width)
	 */
	private void drawRaster(Graphics g, double ratio) {
		if (!showRaster)
			return;
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));
		g2.setColor(new Color(0, 140, 0));

		double length = (double) world.getField().getLength() * ratio;
		double width = (double) world.getField().getWidth() * ratio;
		double rasterSize = length / 12.0;

		for (double x = spaceBufferX - 5 * rasterSize; x < getWidth() + spaceBufferX; x += rasterSize)
			g2.drawLine((int) x, 0, (int) x, getHeight());

		for (double y = width / 2 + spaceBufferY; y > 0; y -= rasterSize)
			g2.drawLine(0, (int) y, getWidth(), (int) y);

		for (double y = width / 2 + spaceBufferY; y < getHeight(); y += rasterSize)
			g2.drawLine(0, (int) y, getWidth(), (int) y);

		g2.setColor(Color.WHITE);
		String sizeDesc = String.format("rastersize: %.1fcm", (double) world.getField().getLength() / 12.0 / 10.0);
		g2.drawString(sizeDesc, getHeight() / 10 + 5 - sizeDesc.length() * 7 / 2, 20);
	}

	/**
	 * Toggles the boolean {@link FieldPanel#showRobots} and repaints.
	 */
	public void toggleShowRobots() {
		showRobots = !showRobots;
		repaint();
	}

	/**
	 * Draws all the {@link Robot}s in the {@link FieldPanel} in their team color. {@link Robot}s point are oriented as their
	 * {@link Robot#getOrientation()} describes.
	 * @param g {@link Graphics} to draw to
	 * @param ratio A {@link double} indicating the ratio for the sizing. (Screen width / real width)
	 */
	private void drawRobots(Graphics g, double ratio) {
		if (!showRobots)
			return;
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(4));
		Color allyColor = world.getReferee().getAllyTeamColor().toColor();
		Color enemyColor = world.getReferee().getEnemy().getColor().toColor();
		ArrayList<Robot> robots = world.getAllRobots();
		for(Robot robot : robots){
			if(!world.isValidRobotId(robot))
				continue;

			if (robot.getPosition() == null)
				continue;
		
			FieldPoint robotPosition = robot.getPosition();
			double robotOrientation = robot.getOrientation();
			
			// draw flat front part of robot
			FieldPoint left = new FieldPoint(robotPosition.getX()
					+ Math.cos(Math.toRadians(robotOrientation + 45.0)) * Robot.DIAMETER / 2.0, robot
					.getPosition().getY()
					+ Math.sin(Math.toRadians(robotOrientation + 45.0))
					* Robot.DIAMETER / 2.0);
			FieldPoint right = new FieldPoint(robotPosition.getX()
					+ Math.cos(Math.toRadians(robotOrientation -45.0)) * Robot.DIAMETER / 2.0, robot
					.getPosition().getY()
					+ Math.sin(Math.toRadians(robotOrientation -45.0))
					* Robot.DIAMETER / 2.0);

			g2.setColor((robot instanceof Ally) ? allyColor : enemyColor);
			if(!robot.isOnSight())
				g2.setColor(toGrayScale(g2.getColor()));
			if(isWindows()){
				//SOLID COLOR
				g2.fillArc(
						(int) (robotPosition.toGUIPoint(ratio, mirror).getX() - (double) (Robot.DIAMETER / 2) * ratio + spaceBufferX),
						(int) (robotPosition.toGUIPoint(ratio, mirror).getY() - (double) (Robot.DIAMETER / 2) * ratio + spaceBufferY),
						(int) (Robot.DIAMETER * ratio), (int) (Robot.DIAMETER * ratio),
						(int) robot.getOrientation() + (mirror ?  215: 35), 295);
				g2.fillPolygon(new int[] {(int) right.toGUIPoint(ratio, mirror).getX() + spaceBufferX, (int) left.toGUIPoint(ratio, mirror).getX() + spaceBufferX, (int) robot.getPosition().toGUIPoint(ratio, mirror).getX() + spaceBufferX},
						new int[] {(int) right.toGUIPoint(ratio, mirror).getY() + spaceBufferY, (int) left.toGUIPoint(ratio, mirror).getY() + spaceBufferY, (int) robot.getPosition().toGUIPoint(ratio, mirror).getY() + spaceBufferY}, 3);
			}
			//BORDERS
			g2.setColor(g2.getColor().darker());
			g2.drawLine((int) (left.toGUIPoint(ratio, mirror).getX() + spaceBufferX),
					(int) (left.toGUIPoint(ratio, mirror).getY() + spaceBufferY),
					(int) (right.toGUIPoint(ratio, mirror).getX() + spaceBufferX),
					(int) (right.toGUIPoint(ratio, mirror).getY() + spaceBufferY));

			// draw round part of robot
			g2.drawArc(
					(int) (robotPosition.toGUIPoint(ratio, mirror).getX() - (double) (Robot.DIAMETER / 2) * ratio + spaceBufferX),
					(int) (robotPosition.toGUIPoint(ratio, mirror).getY() - (double) (Robot.DIAMETER / 2) * ratio + spaceBufferY),
					(int) (Robot.DIAMETER * ratio), (int) (Robot.DIAMETER * ratio),
					(int) robotOrientation + (mirror ?  225 : 45), 270);

			g2.setColor(Color.BLACK);
			g2.setFont(new Font(g2.getFont().getFontName(), Font.BOLD, (int) (Robot.DIAMETER*ratio)/2));
			g2.drawString("" + robot.getRobotId(), (int) robot
					.getPosition().toGUIPoint(ratio, mirror).getX() -(robot.getRobotId()/10 + 1)*(g2.getFont().getSize()/3)+ spaceBufferX,
					(int) robotPosition.toGUIPoint(ratio, mirror).getY() +(g2.getFont().getSize()/3)+ spaceBufferY);
			if(robot instanceof Ally){
				g2.setColor(RobotBox.getRoleColor(((Ally)robot).getRole()));
				g2.fillRoundRect((int)(robot.getPosition().toGUIPoint(ratio, mirror).getX() + spaceBufferX + 10), 
						(int)(robot.getPosition().toGUIPoint(ratio, mirror).getY() + spaceBufferY + 10), 10, 10, 2, 2);
			}

			if(showCoords)
				drawCoord(g2, robotPosition, ratio, (int) (Robot.DIAMETER*ratio));
		}
	}
	
	private Color toGrayScale(Color color) {
        int grayColor = (int)(color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
        return new Color(grayColor, grayColor, grayColor);
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	/**
	 * Toggles the boolean {@link FieldPanel#showPathPlanner} and repaints.
	 */
	public void toggleShowPathPlanner() {
		showPathPlanner = !showPathPlanner;
		repaint();
	}
	
	/**
	 * Toggles whether the neighbours of the {@link Vertex vertices} of the 
	 * {@link DijkstraPathPlanner pathplanner} are shown.
	 */
	public void toggleDrawNeighbours(){
		drawNeighbours = !drawNeighbours;
		repaint();
	}
	
	public void toggleDrawVertices(){
		drawVertices = !drawVertices;
		repaint();
	}

	/**
	 * Draws all the paths that the current {@link Robot robots} have planned.
	 * @param g {@link Graphics} to draw to
	 * @param ratio A {@link double} indicating the ratio for the sizing. (Screen width / real width)
	 */
	private void drawPathPlanner(Graphics g, double ratio) {
		if (!showPathPlanner)
			return;
		
		for(RobotExecuter executer : world.getRobotExecuters()){
			if(executer.getLowLevelBehavior() != null && executer.getLowLevelBehavior().getGotoPosition() != null)
				drawIndividualPath(g, ratio, executer.getLowLevelBehavior().getGotoPosition().getPathPlanner(), executer.getRobot().getRobotId());
		}
		
	}
	
	private void drawIndividualPath(Graphics g, double ratio, DijkstraPathPlanner pathPlanner, int robotId){
		if(pathPlanner != null){
			LinkedList<FieldPoint> path = pathPlanner.getCurrentRoute();
			if(drawVertices){
				ArrayList<Vertex> vertices = pathPlanner.getAllVertices();
				if(vertices != null){
					for (Vertex vertex : vertices) {
						g.setColor(Color.MAGENTA);
					
						int x = (int)vertex.getPosition().toGUIPoint(ratio, mirror).getX() + spaceBufferX;
						int y = (int)vertex.getPosition().toGUIPoint(ratio, mirror).getY() + spaceBufferY;
							
						if(!vertex.isRemovable()){
							g.drawString("nRmvbl", x, y);
						}
						g.drawOval(x - 5, y - 5, 10, 10);
						g.setColor(Color.WHITE);
						if (drawNeighbours) {
							for (Vertex neighbour : vertex.getNeighbours()) {
								int x2 = (int) neighbour.getPosition().toGUIPoint(ratio, mirror).getX() + spaceBufferX;
								int y2 = (int) neighbour.getPosition().toGUIPoint(ratio, mirror).getY() + spaceBufferY;
								g.drawLine(x, y, x2, y2);
							}
						}
					}
				}
			}
			
			if(pathPlanner.getCopyOfObjects() != null){
				g.setColor(new Color(222, 168, 176));
				((Graphics2D)g).setStroke(new BasicStroke(4));

				for(Shape shape : pathPlanner.getCopyOfObjects()){
					if(shape instanceof Polygon){
						g.setColor(new Color(222, 168, 176));
						Polygon polygon = (Polygon)shape;
						Polygon result = new Polygon();
						for(int i = 0; i < polygon.npoints; ++i){
							Point2D point = new FieldPoint(polygon.xpoints[i], polygon.ypoints[i]).toGUIPoint(ratio, mirror);
							result.addPoint((int)point.getX() + spaceBufferX, (int)point.getY() + spaceBufferY);
						}
						g.drawPolygon(result);
					}
					else if(shape instanceof Rectangle2D){
						g.setColor(Color.red);
						Rectangle2D rect = (Rectangle2D)shape;
						Point2D start = new FieldPoint(rect.getX(), rect.getY()).toGUIPoint(ratio,mirror);
						g.drawRect((int)start.getX() + spaceBufferX, (int)start.getY() + spaceBufferY, (int)(rect.getWidth() * ratio), (int)(rect.getHeight() * ratio));	
					}
				}
				((Graphics2D)g).setStroke(new BasicStroke(1));

			}
			if(pathPlanner.getDestination() != null){
				int x = (int)pathPlanner.getDestination().toGUIPoint(ratio, mirror).getX() + spaceBufferX;
				int y = (int)pathPlanner.getDestination().toGUIPoint(ratio,mirror).getY() + spaceBufferY;
				if(path != null){
					if(!path.isEmpty()){
						drawPath(g, path, pathPlanner.getSource(), pathPlanner.getDestination(), ratio);
						g.setColor(Color.GREEN);
						((Graphics2D)g).setStroke(new BasicStroke(5));
						g.drawOval(x - 10, y - 10, 20, 20);
						((Graphics2D)g).setStroke(new BasicStroke(1));
						g.drawString(""+robotId, x+20, y);
		
					}
					else{
						if(pathPlanner.getDestination() != null){
							g.setColor(Color.RED);
							((Graphics2D)g).setStroke(new BasicStroke(5));
							g.drawOval(x - 10, y - 10, 20, 20);
							((Graphics2D)g).setStroke(new BasicStroke(1));
							g.drawString(""+robotId, x+20, y);
						}
					}
				}
			}

		}
	}
	
	public void drawPath(Graphics g, LinkedList<FieldPoint> path, FieldPoint start, FieldPoint destination, double ratio) {
		Graphics2D g2 = (Graphics2D)g;
		
		g.setColor(Color.YELLOW);
		g2.setStroke(new BasicStroke(20));
		Point2D.Double previous = start.toGUIPoint(ratio, mirror);
		for (FieldPoint p : path) {
			int x = (int) p.toGUIPoint(ratio, mirror).getX() + spaceBufferX;
			int y = (int) p.toGUIPoint(ratio, mirror).getY() + spaceBufferY;
			int x2 = (int) previous.getX() + spaceBufferX;
			int y2 = (int) previous.getY() + spaceBufferY;
			g.drawLine(x, y, x2, y2);
			g2.draw(new Line2D.Double(x, y, x2, y2));
			previous = p.toGUIPoint(ratio, mirror);
		}
		// Draw point to destination
		int x = (int) previous.getX() + spaceBufferX;
		int y = (int) previous.getY() + spaceBufferY;
		int x2 = (int) destination.toGUIPoint(ratio, mirror).getX() + spaceBufferX;
		int y2 = (int) destination.toGUIPoint(ratio, mirror).getY() + spaceBufferY;
		g.drawLine(x, y, x2, y2);
		g2.draw(new Line2D.Float(x, y, x2, y2));
		g2.setStroke(new BasicStroke(1));

	}

	public void toggleShowVectors() {
		showVectors = !showVectors;
	}
}