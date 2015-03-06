package robocup.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import robocup.model.World;
import robocup.model.Zone;

@SuppressWarnings("serial")
public class PlayfieldFrame extends JPanel {
	private ArrayList<Zone> zoneList;
	private static int FIELDWIDTH = 6000;
	private static int FIELDHEIGHT = 4000;
	private static double ratio = 0.20;
	
	public static void main(String [] args){
		JFrame hai = new JFrame();	
		hai.setSize((int)(FIELDWIDTH*ratio) + 4, (int)(FIELDHEIGHT*ratio) + 28);
		hai.setContentPane(new PlayfieldFrame());
		hai.setVisible(true);
		//hai.setResizable(false);
		hai.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public PlayfieldFrame(){
		zoneList = new ArrayList<Zone>();
		setSize((int)(FIELDWIDTH*ratio), (int)(FIELDHEIGHT*ratio));
//		voorbeeld
//		zoneList.add(new Zone(this.getSize(), "example", 0, 0, ratio, 
//				new int[]{0,	500,	500,	0}, 
//				new int[]{0,	0,		500,	500}));
		Color allyColor = World.getInstance().getReferee().getAlly().getColor().toColor();
		Color enemyColor = World.getInstance().getReferee().getEnemy().getColor().toColor();

		/*
		 * Left side
		 */
		//first row
		zoneList.add(new Zone(this.getSize(), allyColor, "rightCorner", -3000, -2000, ratio, 
				new int[]{0,	1400,	400,	0}, 
				new int[]{0,	0,		1000,	1000}));
		zoneList.add(new Zone(this.getSize(), allyColor, "rightSecondPost", -2600, -2000, ratio, 
				new int[]{0,	1000,	1000,	300}, 
				new int[]{1000,	0,		1500,	1500}));
		zoneList.add(new Zone(this.getSize(), allyColor, "center", -2300, -500, ratio, 
				new int[]{0,	700,	700,	0},
				new int[]{0,	0,		1000,	1000}));
		zoneList.add(new Zone(this.getSize(), allyColor, "leftSecondPost", -2600, 500, ratio, 
				new int[]{300,	1000,	1000,	0}, 
				new int[]{0,	0,		1500,	500}));
		zoneList.add(new Zone(this.getSize(), allyColor, "leftCorner", -3000, 1000, ratio, 
				new int[]{0,	400,	1400,	0}, 
				new int[]{0,	0,	1000,	1000}));

		//second row
		zoneList.add(new Zone(this.getSize(), allyColor, "rightFront", -1600, -2000, ratio, 
				new int[]{0,	1600,	1600,	0},
				new int[]{0,	0,		1500,	1500}));
		zoneList.add(new Zone(this.getSize(), allyColor, "middle", -1600, -500, ratio, 
				new int[]{0,	1600,	1600,	0},
				new int[]{0,	0,		1000,	1000}));
		zoneList.add(new Zone(this.getSize(), allyColor, "leftFront", -1600, 500, ratio, 
				new int[]{0,	1600,	1600,	0},
				new int[]{0,	0,		1500,	1500}));
		

		/*
		 * Right side
		 */
		//outter row
		zoneList.add(new Zone(this.getSize(), enemyColor, "leftCorner", 1600, -2000, ratio, 
				new int[]{0,	1400,	1400,	1000}, 
				new int[]{0,	0,		1000,	1000}));
		zoneList.add(new Zone(this.getSize(), enemyColor, "leftSecondPost", 1600, -2000, ratio,
				new int[]{0,	1000,	700,	0}, 
				new int[]{0,	1000,	1500,	1500}));
		zoneList.add(new Zone(this.getSize(), enemyColor, "center", 1600, -500, ratio, 
				new int[]{0,	700,	700,	0},
				new int[]{0,	0,		1000,	1000}));
		zoneList.add(new Zone(this.getSize(), enemyColor, "rightSecondPost", 1600, 500, ratio, 
				new int[]{0,	700,	1000,	0},
				new int[]{0,	0,		500,	1500}));
		zoneList.add(new Zone(this.getSize(), enemyColor, "rightCorner", 1600, 1000, ratio, 
				new int[]{1000,	1400,	1400,	0}, 
				new int[]{0,	0,		1000,	1000}));

		//inner row
		zoneList.add(new Zone(this.getSize(), enemyColor, "leftFront", 0, -2000, ratio, 
				new int[]{0,	1600,	1600,	0},
				new int[]{0,	0,		1500,	1500}));
		zoneList.add(new Zone(this.getSize(), enemyColor, "middle", 0, -500, ratio, 
				new int[]{0,	1600,	1600,	0},
				new int[]{0,	0,		1000,	1000}));
		zoneList.add(new Zone(this.getSize(), enemyColor, "rightFront", 0, 500, ratio, 
				new int[]{0,	1600,	1600,	0},
				new int[]{0,	0,		1500,	1500}));
		
	}

    public void paint(Graphics g){
		super.paintComponents(g);
		setRatio();
		
		//main playing field
		g.setColor(Color.GREEN.darker());
		g.fillRect(2, 2, getWidth()-4, getHeight()-4);

    	drawRaster(g);

    	for(Zone zone:zoneList)
    		drawZone(g, zone);
    	

    	drawPlayfield(g);
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setColor(Color.white);
		 //g2.drawString(String.format("Ratio: 1pixel : %dmm", (int)(1/ratio)), 5, 15);
    }
    
    private void setRatio(){
    	boolean correctAspectRatio = (((double)getWidth()/(double)getHeight() > ((double)FIELDHEIGHT/(double)FIELDWIDTH) + 0.05) && (double)getWidth()/(double)getHeight() < ((double)FIELDHEIGHT/(double)FIELDWIDTH)+0.05);

    	if (!correctAspectRatio) {
    		
    	  final JFrame _frame = (JFrame) SwingUtilities.getWindowAncestor(this);
    	  final int _height = (int) (getWidth()/1.5); // calculated height
    	  ratio=(double)getHeight()/FIELDHEIGHT;
    	  SwingUtilities.invokeLater(new Runnable(){
    	    public void run() {
    	      _frame.setSize(getWidth() +4, _height +28);
    	    }
    	  });
    	  
    	}
    }
    
    private void drawRaster(Graphics g){
	    Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));

    	g2.setColor(Color.LIGHT_GRAY);
    	for(double y=-getHeight()/20; y < getWidth(); y+= getHeight()/10)
    		g2.drawLine((int) y, 0, (int) y, getHeight());

    	for(double x=0; x < getHeight(); x+= getHeight()/10)
    		g2.drawLine(0, (int)x, getWidth(), (int)x);
    	
    	g2.setColor(Color.WHITE);
    	g2.drawLine(getHeight()/20 + 5, 15, getHeight()/20 + getHeight()/10 - 5, 15);
    	g2.drawLine(getHeight()/20 + 5, 10, getHeight()/20 + 5, 20);
    	g2.drawLine(getHeight()/20 + getHeight()/10 - 5, 10, getHeight()/20 + getHeight()/10 - 5, 20);
    	String sizeDesc = String.format("%.1fcm", (double) FIELDWIDTH/(FIELDWIDTH/(FIELDHEIGHT/10))/10);
    	g2.drawString(sizeDesc, getHeight()/10 + 5 - sizeDesc.length()*7/2,30);
    }
    
    private void drawZone(Graphics g, Zone zone){
	    Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		zone.setOffset(getSize(), ratio);
		zone.calculateAbsolutePoints();
		g2.setColor(zone.getColor());
		g2.drawPolygon(zone.getAbsoluteXPoints(), zone.getAbsoluteYPoints(), zone.getNPoints());
		g2.drawString(zone.getName(), (zone.getAbsoluteXPoints()[0] + zone.getAbsoluteXPoints()[2]) / 2 - zone.getName().length()/2*4, (zone.getAbsoluteYPoints()[0] + zone.getAbsoluteYPoints()[2]) / 2)
		;
    }

	private void drawPlayfield(Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));

		//middle line
		g.setColor(Color.WHITE);
		g2.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
		
		//middle circle
		g2.drawArc(getWidth()/2 - getWidth()/18, getHeight()/2 - getWidth()/18, getWidth()/9, getWidth()/9, 0, 360);
		
		//draw left goaly space
		g2.drawArc(-getWidth()/9, getHeight()/2 - getWidth()/9 - getWidth()/18, (int) (getWidth()/9.0*2), (int) (getWidth()/9.0*2), 0, 90);
		g2.drawArc(-getWidth()/9, getHeight()/2 - getWidth()/9 + getWidth()/18, (int) (getWidth()/9.0*2), (int) (getWidth()/9.0*2), 0, -90);
		g2.drawLine(getWidth()/9 +1, getHeight()/2 - getWidth()/18, 
					getWidth()/9 +1, getHeight()/2 + getWidth()/18);

		//draw right goaly area thingy <insert footbalterm>
		g2.drawArc((int) (getWidth()/9.0*8), getHeight()/2 - getWidth()/9 - getWidth()/18, (int) (getWidth()/9.0*2), getWidth()/9*2, 180, -90);
		g2.drawArc((int) (getWidth()/9.0*8), getHeight()/2 - getWidth()/9 + getWidth()/18, (int) (getWidth()/9.0*2), getWidth()/9*2, 180, 90);
		g2.drawLine((int) (getWidth()/9.0*8), getHeight()/2 - getWidth()/18, 
					(int) (getWidth()/9.0*8), getHeight()/2 + getWidth()/18);
	}

	
}
