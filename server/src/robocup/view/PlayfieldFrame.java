package robocup.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
//import javax.swing.SwingUtilities;




import robocup.model.World;
//import robocup.model.World;
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
	}
	


    public void paint(Graphics g) {
		super.paintComponents(g);
		//setRatio();
		
		//main playing field
		g.setColor(Color.GREEN.darker());
		g.fillRect(2, 2, getWidth() - 4, getHeight() - 4);

    	drawRaster(g);

    	Map<FieldZone, Zone> allZones = World.getInstance().getField().getZones();
    	for (final Map.Entry<FieldZone, Zone> zone : allZones.entrySet()) {
    		drawZone(g, zone.getValue());
    	}
    	
    	drawPlayfield(g);
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setColor(Color.white);
		 //g2.drawString(String.format("Ratio: 1pixel : %dmm", (int)(1/ratio)), 5, 15);
    }
    
    /*private void setRatio() {
    	boolean correctAspectRatio = (((double)getWidth() / (double)getHeight() > ((double)FIELDHEIGHT / (double)FIELDWIDTH) + 0.05) && (double)getWidth() / (double)getHeight() < ((double)FIELDHEIGHT/(double)FIELDWIDTH) + 0.05);

    	if (!correctAspectRatio) {
    		
    	  final JFrame _frame = (JFrame) SwingUtilities.getWindowAncestor(this);
    	  final int _height = (int) (getWidth() / 1.5); // calculated height
    	  ratio = (double)getHeight() / FIELDHEIGHT;
    	  SwingUtilities.invokeLater(new Runnable(){
    	    public void run() {
    	      _frame.setSize(getWidth() + 4, _height + 28);
    	    }
    	  });
    	}
    }*/
    
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
    
    // must  be redone, zones now exist within the modell.
    private void drawZone(Graphics g, Zone zone) {
	    Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
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