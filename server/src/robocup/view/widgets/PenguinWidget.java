package robocup.view.widgets;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import robocup.Main;
import robocup.model.World;
import robocup.view.WidgetBox;

/**
 * Example that shows how to implement a Widget,
 * this specific example dumps information in the Logger when
 * a button is pressed. In most cases it isn't necessary to have a external class
 * for a buttonHandler, here is a example that implements a anonymous button listener
 *
 */
@SuppressWarnings("serial")
public class PenguinWidget extends WidgetBox {

	private Logger LOGGER = Logger.getLogger(Main.class.getName());

	/**
	 * Creates GUI with a button
	 */
	public PenguinWidget() {
		super("Penguin Widget");
		add(new JLabel(new ImageIcon(PenguinWidget.class.getResource("/robocup/view/penguin.png"))));

		add(new JButton(new AbstractAction("New logger entry") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				double wowlevel = Math.random();

				LOGGER.setLevel(java.util.logging.Level.FINEST);
				if (wowlevel <= 0.33)
					LOGGER.fine("Selected robot: " + World.getInstance().getGUI().getSelectedRobotId());
				else if (wowlevel <= 0.66)
					LOGGER.info("Log info");
				else
					LOGGER.severe("Log severe problem");
			}
		}));
	}

	@Override
	public void update() {
	}

}
