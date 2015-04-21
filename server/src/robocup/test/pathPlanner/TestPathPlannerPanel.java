package robocup.test.pathPlanner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JPanel;

import robocup.controller.ai.movement.DijkstraPathPlanner;
import robocup.controller.ai.movement.DijkstraPathPlanner.Vertex;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;

/**
 * A JPanel that can print the current robots on the field
 * and the path given by the {@link DijkstraPathPlanner}
 *
 */
public class TestPathPlannerPanel extends JPanel{
	private static final long serialVersionUID = 1L;

	public static final int LINE_WIDTH_PATHPLANNER = 5;

	public static final int HEIGHT = 4000;
	public static final int WIDTH = 6000;

	public static final double RATIO = 0.10;

	public static final int X_OFFSET = (int) (WIDTH * RATIO) / 2;
	public static final int Y_OFFSET = (int) (HEIGHT * RATIO) / 2;
	
	private FieldPoint destination;
	private boolean drawNeighbours;
	private boolean drawPath;
	private TestPathPlanner planner;
	private LinkedList<FieldPoint> path;
	private ArrayList<Vertex> vertices;
	
	public TestPathPlannerPanel(FieldPoint destination,
			boolean drawNeighbours, boolean drawPath,
			TestPathPlanner planner){
		this.destination = destination;
		this.drawNeighbours = drawNeighbours;
		this.drawPath = drawPath;
		this.planner = planner;
		this.path = planner.getRoute(World.getInstance().getAllRobots().get(0).getPosition(), destination, 0, true);
		vertices = planner.getTestVertices();
		setPreferredSize(new Dimension((int)(WIDTH * RATIO), (int)(HEIGHT * RATIO)));
	}
	
	public void refresh(FieldPoint destination){
		this.destination = destination;
		this.path = planner.getRoute(World.getInstance().getAllRobots().get(0).getPosition(), destination, 0, true);
		vertices = planner.getTestVertices();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
			
		drawField(g);
	
		for (Vertex vertex : vertices) {
			g.setColor(Color.MAGENTA);
	
			int x = (int) (X_OFFSET + vertex.getPosition().getX()
					* RATIO);
			int y = (int) (Y_OFFSET - vertex.getPosition().getY()
					* RATIO);
			if(!vertex.isRemovable()){
				g.drawString("nRmvbl", x, y);
			}
			g.drawOval(x - 5, y - 5, 10, 10);
			if (drawNeighbours) {
				g.setColor(new Color((int) (Math.random() * 255),
						(int) (Math.random() * 255), (int) (Math
								.random() * 255)));
				for (Vertex neighbour : vertex.getNeighbours()) {
					int x2 = (int) (X_OFFSET + neighbour.getPosition()
							.getX() * RATIO);
					int y2 = (int) (Y_OFFSET - neighbour.getPosition()
							.getY() * RATIO);
					g.drawLine(x, y, x2, y2);
				}
			}
		}
			
		g.setColor(Color.BLACK);
		for (Rectangle2D rect : planner.getObjects()) {
			drawRobot(
					g,
					(int) rect.getCenterX(),
					(int) rect.getCenterY(),
					90,
					DijkstraPathPlanner.DISTANCE_TO_ROBOT,
					DijkstraPathPlanner.VERTEX_DISTANCE_TO_ROBOT,
					"[" + (int) rect.getCenterX() + ","
							+ (int) rect.getCenterY() + "]");
		}
		// Source
		g.setColor(Color.RED);
		Robot source = World.getInstance().getReferee().getAlly().getRobotByID(0);
		drawRobot(g, (int) source.getPosition().getX(), (int) source
				.getPosition().getY(), 90, DijkstraPathPlanner.DISTANCE_TO_ROBOT,
				DijkstraPathPlanner.VERTEX_DISTANCE_TO_ROBOT, "id=" + source.getRobotId());
		// Destination
		g.setColor(Color.BLUE);
		drawRobot(g, (int) destination.getX(),
				(int) destination.getY(), 90, DijkstraPathPlanner.DISTANCE_TO_ROBOT,
				DijkstraPathPlanner.VERTEX_DISTANCE_TO_ROBOT, "dest");
		
	
		// Draw path
		if (drawPath) {
			if(path != null)
				if(!path.isEmpty())
				drawPath(g, g2, path, source.getPosition(), destination);
		}
	}

	

	public final static void drawRobot(Graphics g, int x, int y, int radius,
			int dangerZone, int vertexDist, String name) {
		int realX = (int) (X_OFFSET + x * RATIO);
		int realY = (int) (Y_OFFSET - y * RATIO);
		// Robot
		g.fillOval(realX - (int) (radius * RATIO), realY
				- (int) (radius * RATIO), (int) (radius * 2 * RATIO),
				(int) (radius * 2 * RATIO));
		// DangerZone
		g.drawRect(realX - (int) (dangerZone * RATIO), realY
				- (int) (dangerZone * RATIO), (int) (dangerZone * 2 * RATIO),
				(int) (dangerZone * 2 * RATIO));
		// Vertice square.
		// g.drawRect(realX - (int)(vertexDist * RATIO), realY -
		// (int)(vertexDist * RATIO),
		// (int)(vertexDist * 2 * RATIO), (int)(vertexDist * 2 * RATIO));

		g.drawString(name, realX + 10, realY + 10);
	}

	public final static void drawPath(Graphics g, Graphics2D g2,
			LinkedList<FieldPoint> path, FieldPoint start, FieldPoint destination) {
		g.setColor(Color.ORANGE);
		g2.setStroke(new BasicStroke(LINE_WIDTH_PATHPLANNER));
		FieldPoint previous = start;
		for (FieldPoint p : path) {
			int x = (int) (X_OFFSET + p.getX() * RATIO);
			int y = (int) (Y_OFFSET - p.getY() * RATIO);
			int x2 = (int) (X_OFFSET + previous.getX() * RATIO);
			int y2 = (int) (Y_OFFSET - previous.getY() * RATIO);
			g.drawLine(x, y, x2, y2);
			g2.draw(new Line2D.Float(x, y, x2, y2));
			previous = p;
		}
		// Draw point to destination
		int x = (int) (X_OFFSET + previous.getX() * RATIO);
		int y = (int) (Y_OFFSET - previous.getY() * RATIO);
		int x2 = (int) (X_OFFSET + destination.getX() * RATIO);
		int y2 = (int) (Y_OFFSET - destination.getY() * RATIO);
		g.drawLine(x, y, x2, y2);
		g2.draw(new Line2D.Float(x, y, x2, y2));
	}

	public void drawField(Graphics g){
		// Draw background
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, (int)(WIDTH * RATIO), (int)(HEIGHT * RATIO));
		// Draw obstacles
		g.setColor(Color.BLACK);

		g.drawLine(0, Y_OFFSET, (int) (WIDTH * RATIO),
				Y_OFFSET);
		g.drawLine(X_OFFSET, 0, X_OFFSET,
				(int) (HEIGHT * RATIO));
		g.drawRect(0, 0, 600, 400);

		g.setColor(Color.WHITE);
		g.drawRect(0, Y_OFFSET - 25, 10, 50);
		g.drawRect((int) (WIDTH * RATIO) - 10,
				Y_OFFSET - 25, 10, 50);
	}
}
