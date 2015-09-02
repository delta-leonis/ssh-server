package robocup.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import robocup.model.Ally;
import robocup.model.Enemy;
import robocup.model.FieldObject;
import robocup.model.FieldPoint;
import robocup.model.Obstruction;
import robocup.model.Robot;
import robocup.model.World;

/**
 * {@link FieldPanel} is a {@link JPanel} that shows the field.
 */
@SuppressWarnings("serial")
public class FieldPanel extends JPanel {
	private World world;
	private double scaleRatio = 0.20;
	private int frameCount =0;
	private int FPS = 0;
	private long lastFPSpaint;
	private GUIModel model;
	
	public FieldPanel(){
		this.world = World.getInstance();
		this.model = world.getGuiModel();

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if(model.getMouseObject() == null)
					return;

				model.getMouseObject().setPosition(transformPoint(me.getX(), me.getY()));

				model.getMouseObject().setOverrideOnsight(true);
				if(model.getMouseObject() instanceof Robot)
					(model.getMouseObject() instanceof Ally ? world.getValidAllyIDs() : world.getValidEnemyIDs()).add(((Robot)model.getMouseObject()).getRobotId());
				repaint();
			}

			private FieldPoint transformPoint(int x, int y) {
				int width = (model.getQuadrantRotation() %2 == 0 ? getWidth() : getHeight());
				int length = (model.getQuadrantRotation() %2 == 0 ? getHeight() : getWidth());
				width = getWidth();
				length = getHeight();
				int newX = (int) ((x - width/2) / getScaleRatio());
				int newY = (int) ((y - length/2) / getScaleRatio());

				if(model.getQuadrantRotation() %2 == 0 )
					return new FieldPoint(newX *(model.getQuadrantRotation() == 2 ? -1 : 1) , newY*(model.getQuadrantRotation() == 0 ? -1 : 1));
				else
					return new FieldPoint(newY*(model.getQuadrantRotation() == 3 ? -1 : 1), newX*(model.getQuadrantRotation() == 3 ? -1 : 1));
			}
		});

	}
	
	public void paint(Graphics g){
		scaleRatio = getScaleRatio();

		Graphics2D g2 = (Graphics2D)g;
		RenderingHints rh = new RenderingHints(
	             RenderingHints.KEY_TEXT_ANTIALIASING,
	             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g2.setRenderingHints(rh);
		//create a green canvas
	    paintCanvas(g2);

	    paintFPSCounter(g2);

	    //setup Graphics2D for easy drawing
		g2.translate(getWidth()/2.0, getHeight()/2.0);							//origin at center of panel

		g2.scale(scaleRatio, scaleRatio);										//posY upward, negY downwards
		
		g2.transform(AffineTransform.getQuadrantRotateInstance(model.getQuadrantRotation()));
	    paintRaster(g2);
	    
	    //draw Field
		world.getField().paint(g2);

		//draw goals
		world.getField().getEastGoal().paint(g2);
		world.getField().getWestGoal().paint(g2);

		
		world.getAllRobots().get(2).setOverrideOnsight(true);
		world.getAllRobots().get(2).setPosition(new FieldPoint(400,200));

		//draw all robots
		if(model.showRobots())
			for(Robot robot: world.getAllRobots())
				robot.paint(g2);
		
		for(Obstruction obstruction : world.getObstructions())
			obstruction.paint(g2);

		g2.drawString("test", 0, 0);
		
		//draw ball
		if(model.showBall())
			world.getBall().paint(g2);

	}
	
	private void paintRaster(Graphics2D g2) {
		if(!model.showRaster())
			return;

	    double rasterSize = (double)world.getField().getLength()/12;
	    g2.setColor(Color.GREEN.darker().darker().darker());

	    for(int x = -world.getField().getLength()/2; x < world.getField().getLength()/2; x += rasterSize)
	    	g2.drawLine(x, -world.getField().getWidth()/2, x, world.getField().getWidth()/2);
	    for(int y = -world.getField().getWidth()/2; y< world.getField().getWidth()/2; y += rasterSize)
	    	g2.drawLine(-world.getField().getLength()/2, y, world.getField().getLength()/2, y);

	}

	private void paintFPSCounter(Graphics2D g2){
		g2.setFont(new Font(g2.getFont().getFontName(), Font.BOLD, (int) 22));
		g2.setColor(Color.YELLOW);
		g2.drawString("FPS: " + FPS, getWidth() - 100, 25);

	private void paintCanvas(Graphics2D g2) {
		g2.setColor(Color.GREEN.darker().darker());
	    g2.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * @return smallest ratio between either height or width of the field
	 */
	private double getScaleRatio(){
		double length = ( model.getQuadrantRotation() % 2 == 0) ? (double)world.getField().getLength() : (double)world.getField().getWidth();
		double width = ( model.getQuadrantRotation() % 2 == 0) ? (double)world.getField().getWidth() : (double)world.getField().getLength();
		
		double widthRatio = ((double) getWidth() - 100) / length;
		double heightRatio = ((double) getHeight() - 100) / width;
		return Math.min(widthRatio, heightRatio);
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
}
