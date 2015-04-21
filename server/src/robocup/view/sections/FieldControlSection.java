package robocup.view.sections;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

import robocup.view.FieldPanel;
import robocup.view.SectionBox;

/**
 * {@link FieldControlSection} is a {@link SectionBox} for controlling a graphical interface for the field.
 * It has {@link JButton}s for showing the {@link FieldPanel} in a {@link JFrame} and for toggling what to show
 * in the {@link FieldPanel}. Inter alia, there are {@link JButton}s to show a raster and to display a free shot.
 */
@SuppressWarnings("serial")
public class FieldControlSection extends SectionBox {

	private JFrame frame;
	private FieldPanel fieldPanel;

	public FieldControlSection() {
		super("Field Conrol Section");

		frame = new JFrame();
		fieldPanel = new FieldPanel();

		this.setLayout(new MigLayout("wrap 4", "[grow]", "[grow][grow][grow]"));

		add(new JButton(new AbstractAction("Show field") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				frame.setTitle("Field");
				frame.setSize(fieldPanel.getFrameSizeX(), fieldPanel.getFrameSizeY());
				frame.setContentPane(fieldPanel);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		}), "growx");

		add(new JButton(new AbstractAction("Zones") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				fieldPanel.toggleShowZones();
			}
		}), "growx");

		add(new JButton(new AbstractAction("Raster") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				fieldPanel.toggleShowRaster();
			}
		}), "growx");

		add(new JButton(new AbstractAction("Robots") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				fieldPanel.toggleShowRobots();
			}
		}), "growx");

		add(new JButton(new AbstractAction("Ball") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				fieldPanel.toggleShowBall();
			}
		}), "growx");

		add(new JButton(new AbstractAction("Free shot") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				fieldPanel.toggleShowFreeShot();
			}
		}), "growx");
	}

	@Override
	public void update() {
		fieldPanel.update();
	}

}
