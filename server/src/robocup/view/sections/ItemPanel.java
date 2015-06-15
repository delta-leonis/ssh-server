package robocup.view.sections;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.FieldObject;
import robocup.model.Obstruction;
import robocup.model.Robot;
import robocup.model.World;

	@SuppressWarnings("serial")
	public class ItemPanel extends JPanel{
		private FieldObject item;
		private boolean selected;
		private ObjectGraphic objectgraphic;
		private JButton removeOverride;

		public ItemPanel(FieldObject object){
			item = object;
			selected = false;
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			setLayout(new MigLayout("wrap 1", "[]"));
			objectgraphic = new ObjectGraphic();
			removeOverride = new JButton("x");
			if(!(item instanceof Obstruction))
				removeOverride.setEnabled(false);
			removeOverride.addActionListener(new ButtonListener());
			add(removeOverride, "growx");
			
			add(objectgraphic, "");
		}

		private class ButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				String buttonText = ((JButton) e.getSource()).getText();
				if(buttonText.equals("x")){
					item.setOverrideOnsight(false);
					if(item instanceof Obstruction){
						World.getInstance().getObstructions().remove(item);
						JPanel section = (JPanel) ((JButton)e.getSource()).getParent().getParent();
						section.remove(((JButton)e.getSource()).getParent());
						section.getParent().repaint();
					}
				}
			}
		}

		public void setSelected(boolean _selected){
			selected = _selected;
			setBackground(selected ? Color.LIGHT_GRAY : UIManager.getColor("Panel.background"));
			objectgraphic.setBackground(selected ? Color.LIGHT_GRAY : UIManager.getColor("Panel.background"));
		}
		
		public boolean isSelected(){
			return selected;
		}
		
		/**
		 * @return item corresponding to this panel
		 */
		public FieldObject getItem() {
			return item;
		}
		
		@Override
		public void paint(Graphics g){
			super.paint(g);
			if(objectgraphic != null)
				objectgraphic.repaint();
			if(removeOverride != null && !(item instanceof Obstruction))
				removeOverride.setEnabled(item.overrideOnsight());
		}

		/**
		 * Class that will draw a graphic corresponding to the representing item
		 * Currently capable of 3 drawings
		 *  - a ball
		 *  - a robot (blue and yellow)
		 *  - a obstruction
		 *
		 */
		private class ObjectGraphic extends JPanel{
			/**
			 * Create new graphic (standard size: 51x51)
			 */
			public ObjectGraphic(){
				setPreferredSize(new Dimension(51, 51));
			}
			
			/**
			 * method to draw a ball (20x20) 
			 * @param g graphic object to draw on
			 */
			private void drawBall(Graphics g){
				g.setColor(Color.orange);
				g.fillOval(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20);
			}

			/**
			 * @param color
			 * @return given color in grayscale
			 */
			@SuppressWarnings("unused")
			private Color toGrayScale(Color color) {
		        int grayColor = (int)(color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
		        return new Color(grayColor, grayColor, grayColor);
			}

			/**
			 * Lazy bugfix for unix.
			 * @return true when used operating system is windows
			 */
			private boolean isWindows() {
				return System.getProperty("os.name").toLowerCase().contains("windows");
			}
			
			/**
			 * Draws a robot, color of drawing is determined by the teamcolor
			 * @param g graphic object to draw to
			 */
			private void drawRobot(Graphics g){
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(4));
				Color allyColor = World.getInstance().getReferee().getAllyTeamColor().toColor();
				Color enemyColor = (allyColor.equals(Color.BLUE) ? Color.YELLOW : Color.BLUE);
				Robot robot = (Robot) item;
				double ratio = ((double)getWidth() -10) / (double)Robot.DIAMETER;
				double robotOrientation = robot.getOrientation();
				double leftX = getWidth()/2 + Math.cos(Math.toRadians(-robotOrientation + 45.0)) * Robot.DIAMETER / 2.0 * ratio;
				double leftY = getHeight()/2 + Math.sin(Math.toRadians(-robotOrientation + 45.0)) * Robot.DIAMETER / 2.0 * ratio;
				double rightX = getWidth()/2 + Math.cos(Math.toRadians(-robotOrientation - 45.0)) * Robot.DIAMETER / 2.0 * ratio;
				double rightY = getHeight()/2 + Math.sin(Math.toRadians(-robotOrientation - 45.0)) * Robot.DIAMETER / 2.0 *ratio;
				g2.setColor((robot instanceof Ally) ? allyColor : enemyColor);

				if(isWindows()){
					//SOLID COLOR
					g2.fillArc(
							(int) (getWidth()/2 - (double) (Robot.DIAMETER / 2) * ratio),
							(int) (getHeight()/2 - (double) (Robot.DIAMETER / 2) * ratio),
							(int) (Robot.DIAMETER * ratio), (int) (Robot.DIAMETER * ratio),
							(int) robot.getOrientation() + 35, 295);
					g2.fillPolygon(new int[] {(int) rightX, (int) leftX, getWidth()/2},
							new int[] {(int) rightY, (int) leftY , (int)getHeight()/2}, 3);
				}
				//BORDERS
				g2.setColor(g2.getColor().darker());
				g2.drawLine((int) (leftX),
						(int) (leftY),
						(int) (rightX),
						(int) (rightY));

				// draw round part of robot
				g2.drawArc(
						(int) (getWidth()/2 - (double) (Robot.DIAMETER / 2) * ratio),
						(int) (getHeight()/2- (double) (Robot.DIAMETER / 2) * ratio),
						(int) (Robot.DIAMETER * ratio), (int) (Robot.DIAMETER * ratio),
						(int) robotOrientation + 45, 270);

				g2.setColor(Color.BLACK);
				g2.setFont(new Font(g2.getFont().getFontName(), Font.BOLD, (int) (Robot.DIAMETER*ratio)/2));
				g2.drawString("" + robot.getRobotId(), (int) getWidth()/2 -(robot.getRobotId()/10 + 1)*(g2.getFont().getSize()/3),
						(int) getHeight()/2 +(g2.getFont().getSize()/3));
			}

			/**
			 * 
			 * @param x		center X for rectangle
			 * @param y		center Y for rectangle
			 * @param width	width of rectangle
			 * @param length length of rectangle
			 * @param angle	angle of the rectangle
			 * @return		shape to be drawn or filled by {@link Graphics}
			 */
			private Shape getRectAngle(int x, int y, int width, int length, int angle){
				double theta = Math.toRadians(angle);
				
				// create rect centred on the point we want to rotate it about
				Rectangle2D rect = new Rectangle2D.Double(-width/2., -length/2., width, length);
			
				AffineTransform transform = new AffineTransform();
				transform.translate(x, y);
				transform.rotate(theta);

				return transform.createTransformedShape(rect);
			}

			/**
			 * draws an obstruction graphic
			 * @param g Graphics object to draw to
			 */
			private void drawObstruction(Graphics g){
				Obstruction obstruction = (Obstruction)item;
				g.setColor(Color.GRAY);
				Graphics2D g2 = (Graphics2D)g;
				g2.setStroke(new BasicStroke(4));
				double ratio = ((double)getWidth() -10) / (double)Math.max(obstruction.getWidth(), obstruction.getLength());
				Shape rect = getRectAngle((int) (getWidth()/2), 
						   (int)(getHeight()/2),
										   (int)(obstruction.getWidth()*ratio), 
										   (int)(obstruction.getLength()*ratio),
										   (int) obstruction.getOrientation());
				g2.fill(rect);
				g.setColor(g.getColor().darker());
				g2.draw(rect);
			}

			/**
			 * draw an questionmark in the middle of the panel
			 * @param g	graphics to draw to
			 */
			private void drawQuestionmark(Graphics g) {
				g.setFont(g.getFont().deriveFont(22f));
				g.drawString("?", getWidth()/2-5, getHeight()/2-3);
			}

			/**
			 * {@inheritDoc}
			 * 
			 * determinse object, and shape to be drawn
			 */
			@Override
			public void paint(Graphics g){
				super.paint(g);

				if(item instanceof Ball)
					drawBall(g);
				else if(item instanceof Robot)
					drawRobot(g);
				else if(item instanceof Obstruction)
					drawObstruction(g);
				else
					drawQuestionmark(g);
			}
			
		}
		

	}