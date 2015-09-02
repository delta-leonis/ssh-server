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
import robocup.model.Drawable;
import robocup.model.FieldObject;
import robocup.model.FieldPoint;
import robocup.model.Obstruction;
import robocup.model.Robot;
import robocup.model.World;

	@SuppressWarnings("serial")
	public class ItemPanel extends JPanel{
		private FieldObject item;
		private ObjectGraphic objectgraphic;
		private JButton removeOverride;

		public ItemPanel(FieldObject object){
			item = object;
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
			 * determine object, and shape to be drawn
			 */
			@Override
			public void paint(Graphics g){
				super.paint(g);

				Graphics2D g2 = (Graphics2D)g;
				double scaleRatio = getScaleRatio();
				g2.translate(getWidth()/2.0, getHeight()/2.0);							//origin at center of panel
				g2.scale(scaleRatio, scaleRatio);										//posY upward, negY downwards
				
				if(item instanceof Drawable)
					((Drawable)item).paint(g2, new FieldPoint(0,0));
				else
					drawQuestionmark(g);
			}

			private double getScaleRatio() {
				double widthRatio = ((double) getWidth()-10) / (double)Robot.DIAMETER;
				double heightRatio = ((double) getHeight() -10) / (double)Robot.DIAMETER;
				return Math.min(widthRatio, heightRatio);
			}
			
		}
		public void update(){
			boolean selected = World.getInstance().getGuiModel().getMouseObject().equals(item);
			setBackground(selected ? Color.LIGHT_GRAY : UIManager.getColor("Panel.background"));
			objectgraphic.setBackground(selected ? Color.LIGHT_GRAY : UIManager.getColor("Panel.background"));
		}
	}