package robocup.view.sections;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

import robocup.view.FieldPanel;
import robocup.view.SectionBox;

@SuppressWarnings("serial")
public class FieldControlSection extends SectionBox {

	private JFrame frame;
	private FieldPanel fieldPanel;

	public FieldControlSection() {
		super("Field Conrol Section");

		frame = new JFrame();
		fieldPanel = new FieldPanel();
		
		add(new JButton(new AbstractAction("Show field") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				frame.setTitle("Field");
				frame.setSize(fieldPanel.getFrameSizeX(), fieldPanel.getFrameSizeY());
				frame.setContentPane(fieldPanel);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		}));

		add(new JButton(new AbstractAction("Raster") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				fieldPanel.toggleShowRaster();
			}
		}));

		add(new JButton(new AbstractAction("Free shot") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				fieldPanel.toggleShowFreeShot();
			}
		}));
	}

	@Override
	public void update() {
	}

}
