package robocup.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import robocup.model.Ball;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.FieldZone;

/**
 * {@link FieldPanel} is a {@link JPanel} that shows the field.
 */
@SuppressWarnings("serial")
public class FieldPanel extends JPanel {

	private static double RATIO = 0.10;

	private static int FIELDWIDTH = World.getInstance().getField().getWidth();
	private static int FIELDHEIGHT = World.getInstance().getField().getHeight();

	private static int FIELDWIDTH_GUI = (int)(World.getInstance().getField().getWidth()*RATIO);
	private static int FIELDHEIGHT_GUI = (int)(World.getInstance().getField().getHeight()*RATIO);

	private static int spaceBufferX = 140;
	private static int spaceBufferY = 30;

	private World world = World.getInstance();

	private int updateCounter;

	private boolean showFreeShot;
	private boolean showRaster;
	private boolean showRobots;
	private boolean showZones;
	private boolean showBall;
	
	private boolean showPathPlanner;
	private boolean drawNeighbours;
	private boolean drawVertices;

	/**
	 * Constructor of {@link FieldPanel}. The panel size is set and the mouseListener is added for 
	 * the ball position.
	 */
	public FieldPanel() {
		setSize(FIELDWIDTH_GUI, FIELDHEIGHT_GUI);
		showFreeShot = false;
		showRaster = false;
		updateCounter = 0;

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				double ratio = (double)((SwingUtilities.getWindowAncestor(me.getComponent())).getWidth() - spaceBufferX * 2)
						/ (double)FIELDWIDTH;
				double x = - FIELDWIDTH / 2 + (me.getX() - spaceBufferX) / ratio;
				double y = -1 * (- (FIELDHEIGHT) / 2 + (me.getY() - spaceBufferY) / ratio);
				world.getBall().setPosition(new FieldPoint(x, y));
				repaint();
			}
		});

	}

	/**
	 * Function to get the size of the {@link JFrame} the {@link FieldPanel} should be in.
	 * @return width of the {@link JFrame} the {@link FieldPanel} should be in.
	 */
	public int getFrameSizeX() {
		return FIELDWIDTH_GUI + spaceBufferX*2;
	}

	/**
	 * Function to get the size of the {@link JFrame} the {@link FieldPanel} should be in.
	 * @return height of the {@link JFrame} the {@link FieldPanel} should be in.
	 */
	public int getFrameSizeY() {
		return FIELDHEIGHT_GUI + spaceBufferY*2;
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
				/ (double)FIELDWIDTH;

		drawRaster(g, ratio);
		if (showZones)
			for (FieldZone fieldZone : FieldZone.values())
				drawZone(g, fieldZone, ratio);
		drawPlayfield(g, ratio);
		drawFreeShot(g, ratio);
		drawRobots(g, ratio);
		drawBall(g, ratio);
		drawPathPlanner(g, ratio);
	}

	/**
	 * Function for updating the {@link FieldPanel}. Updates with a result of 2 times
	 * per second at a frame rate of 60 fps
	 */
	public void update() {
		updateCounter ++;
		if(updateCounter < 10)
			return;
		repaint();
		updateCounter = 0;
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
			scaledX[index] = (int) (point.toGUIPoint(ratio).getX()) + spaceBufferX;
			scaledY[index] = (int) (point.toGUIPoint(ratio).getY()) + spaceBufferY;
			index++;
		}

		g2.setColor(Color.BLUE);
		g2.drawPolygon(scaledX, scaledY, fieldZone.getNumberOfVertices());
		g2.drawString(fieldZone.name(), (int) (fieldZone.getCenterPoint().toGUIPoint(ratio).getX() - fieldZone.name()
				.length() * 4) + spaceBufferX, (int) (fieldZone.getCenterPoint().toGUIPoint(ratio).getY())
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

		int width = (int) (FIELDWIDTH * ratio);
		int height = (int) (FIELDHEIGHT * ratio);
		int penalRadius = (int) (world.getField().getDefenceRadius() * ratio);
		int penalStretch = (int) (world.getField().getDefenceStretch() * ratio);
		int centerRadius = (int) (world.getField().getCenterCircleRadius() * ratio);

		// center line
		g.setColor(Color.WHITE);
		g2.drawLine(width / 2 + spaceBufferX, 0 + spaceBufferY, width / 2 + spaceBufferX, height + spaceBufferY);

		// borders
		g2.drawLine(spaceBufferX, spaceBufferY, width + spaceBufferX, spaceBufferY);
		g2.drawLine(spaceBufferX, height + spaceBufferY, width + spaceBufferX, height + spaceBufferY);
		g2.drawLine(spaceBufferX, spaceBufferY, spaceBufferX, height + spaceBufferY);
		g2.drawLine(width + spaceBufferX, spaceBufferY, width + spaceBufferX, height + spaceBufferY);

		// center circle
		g2.drawArc(width / 2 - centerRadius + spaceBufferX, height / 2 - centerRadius + spaceBufferY, centerRadius * 2,
				centerRadius * 2, 0, 360);

		// draw west penalty area
		g2.drawArc(spaceBufferX - penalRadius, height / 2 - penalRadius - penalStretch / 2 + spaceBufferY,
				(int) (penalRadius * 2), (int) (penalRadius * 2), 0, 90);
		g2.drawArc(spaceBufferX - penalRadius, height / 2 - penalRadius + penalStretch / 2 + spaceBufferY,
				(int) (penalRadius * 2), (int) (penalRadius * 2), 0, -90);
		g2.drawLine(spaceBufferX + penalRadius, height / 2 - penalStretch / 2 + spaceBufferY, spaceBufferX
				+ penalRadius, height / 2 + penalStretch / 2 + spaceBufferY);

		// draw east penalty area
		g2.drawArc(width + spaceBufferX - penalRadius, height / 2 - penalRadius - penalStretch / 2 + spaceBufferY,
				(int) (penalRadius * 2), (int) (penalRadius * 2), 180, -90);
		g2.drawArc(width + spaceBufferX - penalRadius, height / 2 - penalRadius + penalStretch / 2 + spaceBufferY,
				(int) (penalRadius * 2), (int) (penalRadius * 2), 180, 90);
		g2.drawLine(width + spaceBufferX - penalRadius, height / 2 - penalStretch / 2 + spaceBufferY, width
				+ spaceBufferX - penalRadius, height / 2 + penalStretch / 2 + spaceBufferY);

		// west penalty spot
		g2.drawArc((int) (world.getField().getPenaltyLineFromSpotDistance() * ratio) + spaceBufferX - 1,
				(int) ((new FieldPoint(0, 0)).toGUIPoint(ratio).getY()) + spaceBufferY - 1, 3, 3, 0, 360);
		// east penalty spot
		g2.drawArc(width - ((int) (world.getField().getPenaltyLineFromSpotDistance() * ratio)) + spaceBufferX - 1,
				(int) ((new FieldPoint(0, 0)).toGUIPoint(ratio).getY()) + spaceBufferY - 1, 3, 3, 0, 360);

		// center spot
		g2.drawArc(width / 2 + spaceBufferX - 1, height / 2 + spaceBufferY - 1, 3, 3, 0, 360);

		// goals
		g2.setStroke(new BasicStroke(10));
		g2.drawLine((int) world.getField().getWestGoal().getFrontSouth().toGUIPoint(ratio).getX() + spaceBufferX - 5,
				(int) world.getField().getWestGoal().getFrontSouth().toGUIPoint(ratio).getY() + spaceBufferY,
				(int) world.getField().getWestGoal().getFrontNorth().toGUIPoint(ratio).getX() + spaceBufferX - 5,
				(int) world.getField().getWestGoal().getFrontNorth().toGUIPoint(ratio).getY() + spaceBufferY);
		g2.drawLine((int) world.getField().getEastGoal().getFrontSouth().toGUIPoint(ratio).getX() + spaceBufferX + 5,
				(int) world.getField().getEastGoal().getFrontSouth().toGUIPoint(ratio).getY() + spaceBufferY,
				(int) world.getField().getEastGoal().getFrontNorth().toGUIPoint(ratio).getX() + spaceBufferX + 5,
				(int) world.getField().getEastGoal().getFrontNorth().toGUIPoint(ratio).getY() + spaceBufferY);
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

		try {
			g2.drawLine((int) world.hasFreeShot().toGUIPoint(ratio).getX() + spaceBufferX, (int) world.hasFreeShot()
				.toGUIPoint(ratio).getY()
				+ spaceBufferY, (int) world.getBall().getPosition().toGUIPoint(ratio).getX() + spaceBufferX,
				(int) world.getBall().getPosition().toGUIPoint(ratio).getY() + spaceBufferY);
		} catch (NullPointerException e) {
			// no free shot
		}
		
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

		g2.fillOval((int) (ball.getPosition().toGUIPoint(ratio).getX() + spaceBufferX - (double) ball.SIZE / 2.0
				* ratio), (int) (ball.getPosition().toGUIPoint(ratio).getY() + spaceBufferY - (double) ball.SIZE / 2.0
				* ratio), (int) (ball.SIZE * ratio), (int) (ball.SIZE * ratio));
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

		double width = (double) FIELDWIDTH * ratio;
		double height = (double) FIELDHEIGHT * ratio;
		double rasterSize = width / 16.0;

		for (double x = spaceBufferX - 5 * rasterSize; x < getWidth() + spaceBufferX; x += rasterSize)
			g2.drawLine((int) x, 0, (int) x, getHeight());

		for (double y = height / 2 + spaceBufferY; y > 0; y -= rasterSize)
			g2.drawLine(0, (int) y, getWidth(), (int) y);

		for (double y = height / 2 + spaceBufferY; y < getHeight(); y += rasterSize)
			g2.drawLine(0, (int) y, getWidth(), (int) y);

		g2.setColor(Color.WHITE);
		String sizeDesc = String.format("rastersize: %.1fcm", (double) FIELDWIDTH / 16.0 / 10.0);
		g2.drawString(sizeDesc, getHeight() / 10 + 5 - sizeDesc.length() * 7 / 2, 30);
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

		Team allyTeam = world.getReferee().getAlly();
		g2.setColor(allyTeam.getColor().toColor());
		for (Robot robot : allyTeam.getRobots()) {
			if (robot.getPosition() != null) {
				// draw round part of robot
				g2.drawArc(
						(int) (robot.getPosition().toGUIPoint(ratio).getX() - (double) (Robot.DIAMETER / 2) * ratio + spaceBufferX),
						(int) (robot.getPosition().toGUIPoint(ratio).getY() - (double) (Robot.DIAMETER / 2) * ratio + spaceBufferY),
						(int) (Robot.DIAMETER * ratio), (int) (Robot.DIAMETER * ratio),
						(int) robot.getOrientation() + 45, 270);

				// draw flat front part of robot
				FieldPoint left = new FieldPoint(robot.getPosition().getX()
						+ Math.cos(Math.toRadians(robot.getOrientation() + 45.0)) * Robot.DIAMETER / 2.0, robot
						.getPosition().getY()
						+ Math.sin(Math.toRadians(robot.getOrientation() + 45.0))
						* Robot.DIAMETER / 2.0);
				FieldPoint right = new FieldPoint(robot.getPosition().getX()
						+ Math.cos(Math.toRadians(robot.getOrientation() - 45.0)) * Robot.DIAMETER / 2.0, robot
						.getPosition().getY()
						+ Math.sin(Math.toRadians(robot.getOrientation() - 45.0))
						* Robot.DIAMETER / 2.0);
				g2.drawLine((int) (left.toGUIPoint(ratio).getX() + spaceBufferX),
						(int) (left.toGUIPoint(ratio).getY() + spaceBufferY),
						(int) (right.toGUIPoint(ratio).getX() + spaceBufferX),
						(int) (right.toGUIPoint(ratio).getY() + spaceBufferY));
				g2.drawString("" + robot.getRobotId(), (int) robot
						.getPosition().toGUIPoint(ratio).getX() - 2 + spaceBufferX,
						(int) robot.getPosition().toGUIPoint(ratio).getY() - 2 + spaceBufferY);
			}
		}

		Team enemyTeam = world.getReferee().getEnemy();
		g2.setColor(enemyTeam.getColor().toColor());
		for (Robot robot : enemyTeam.getRobots()) {
			if (robot.getPosition() != null) {
				// draw round part of robot
				g2.drawArc(
						(int) (robot.getPosition().toGUIPoint(ratio).getX() - (double) (Robot.DIAMETER / 2) * ratio + spaceBufferX),
						(int) (robot.getPosition().toGUIPoint(ratio).getY() - (double) (Robot.DIAMETER / 2) * ratio + spaceBufferY),
						(int) (Robot.DIAMETER * ratio), (int) (Robot.DIAMETER * ratio),
						(int) robot.getOrientation() + 45, 270);

				// draw flat front part of robot
				FieldPoint left = new FieldPoint(robot.getPosition().getX()
						+ Math.cos(Math.toRadians(robot.getOrientation() + 45.0)) * Robot.DIAMETER / 2.0, robot
						.getPosition().getY()
						+ Math.sin(Math.toRadians(robot.getOrientation() + 45.0))
						* Robot.DIAMETER / 2.0);
				FieldPoint right = new FieldPoint(robot.getPosition().getX()
						+ Math.cos(Math.toRadians(robot.getOrientation() - 45.0)) * Robot.DIAMETER / 2.0, robot
						.getPosition().getY()
						+ Math.sin(Math.toRadians(robot.getOrientation() - 45.0))
						* Robot.DIAMETER / 2.0);
				g2.drawLine((int) (left.toGUIPoint(ratio).getX() + spaceBufferX),
						(int) (left.toGUIPoint(ratio).getY() + spaceBufferY),
						(int) (right.toGUIPoint(ratio).getX() + spaceBufferX),
						(int) (right.toGUIPoint(ratio).getY() + spaceBufferY));
			}
		}
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
		
		ArrayList<RobotExecuter> robotExecuters = World.getInstance().getRobotExecuters();
		
		for(RobotExecuter executer : robotExecuters){
			if(executer.getLowLevelBehavior() != null)
				drawIndividualPath(g, ratio, executer.getLowLevelBehavior().getGotoPosition().getPathPlanner());
		}
		
	}
	
	private void drawIndividualPath(Graphics g, double ratio, DijkstraPathPlanner pathPlanner){
		if(pathPlanner != null){
			LinkedList<FieldPoint> path = pathPlanner.getCurrentRoute();
			if(drawVertices){
				ArrayList<Vertex> vertices = pathPlanner.getTestVertices();
				for (Vertex vertex : vertices) {
					g.setColor(Color.MAGENTA);
				
					int x = (int)vertex.getPosition().toGUIPoint(ratio).getX() + spaceBufferX;
					int y = (int)vertex.getPosition().toGUIPoint(ratio).getY() + spaceBufferY;
						
					if(!vertex.isRemovable()){
						g.drawString("nRmvbl", x, y);
					}
					g.drawOval(x - 5, y - 5, 10, 10);
					if (drawNeighbours) {
						g.setColor(new Color((int) (Math.random() * 255),
								(int) (Math.random() * 255), (int) (Math
										.random() * 255)));
						for (Vertex neighbour : vertex.getNeighbours()) {
							int x2 = (int) neighbour.getPosition().toGUIPoint(ratio).getX() + spaceBufferX;
							int y2 = (int) neighbour.getPosition().toGUIPoint(ratio).getY() + spaceBufferY;
							g.drawLine(x, y, x2, y2);
						}
					}
				}
			}		

			if(path != null && !path.isEmpty())
				drawPath(g, path, pathPlanner.getSource(), pathPlanner.getDestination(), ratio);
		}
	}
	
	public void drawPath(Graphics g, LinkedList<FieldPoint> path, FieldPoint start, FieldPoint destination, double ratio) {
		Graphics2D g2 = (Graphics2D)g;
		
		g.setColor(Color.ORANGE);
		g2.setStroke(new BasicStroke(3));
		Point2D.Double previous = start.toGUIPoint(ratio);
		for (FieldPoint p : path) {
			int x = (int) p.toGUIPoint(ratio).getX() + spaceBufferX;
			int y = (int) p.toGUIPoint(ratio).getY() + spaceBufferY;
			int x2 = (int) previous.getX() + spaceBufferX;
			int y2 = (int) previous.getY() + spaceBufferY;
			g.drawLine(x, y, x2, y2);
			g2.draw(new Line2D.Double(x, y, x2, y2));
			previous = p.toGUIPoint(ratio);
		}
		// Draw point to destination
		int x = (int) previous.getX() + spaceBufferX;
		int y = (int) previous.getY() + spaceBufferY;
		int x2 = (int) destination.toGUIPoint(ratio).getX() + spaceBufferX;
		int y2 = (int) destination.toGUIPoint(ratio).getY() + spaceBufferY;
		g.drawLine(x, y, x2, y2);
		g2.draw(new Line2D.Float(x, y, x2, y2));
	}
}