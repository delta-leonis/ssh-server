package robocup.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import robocup.model.FieldPoint;
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
	
	private boolean showFreeShot;
	private boolean showRaster;
	private boolean showRobots;

	/**
	 * Constructor of {@link FieldPanel}. The panel size is set and the mouseListener is added for 
	 * the ball position.
	 */
	public FieldPanel() {
		setSize(FIELDWIDTH_GUI, FIELDHEIGHT_GUI);
		showFreeShot = false;
		showRaster = false;

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
		for (FieldZone fieldZone : FieldZone.values())
			drawZone(g, fieldZone, ratio);
		drawPlayfield(g, ratio);
		drawFreeShot(g, ratio);
		drawRobots(g, ratio);
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

	private void drawFreeShot(Graphics g, double ratio2) {
		if(!showFreeShot)
			return;
		// TODO draw free shot
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

	public void toggleShowRobots() {
		showRobots = !showRobots;
		repaint();
	}

	private void drawRobots(Graphics g, double ratio2) {
		// TODO draw robots
	}
}