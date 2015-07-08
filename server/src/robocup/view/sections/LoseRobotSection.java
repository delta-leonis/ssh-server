package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;

import robocup.Main;
import robocup.model.World;
import robocup.view.SectionBox;

/**
 * Example that shows how to implement a Section,
 * this specific example dumps information in the Logger when
 * a button is pressed. In most cases it isn't necessary to have a external class
 * for a buttonHandler, here is a example that implements a anonymous button listener
 *
 */
@SuppressWarnings("serial")
public class LoseRobotSection extends SectionBox {

	private Logger LOGGER = Logger.getLogger(Main.class.getName());

	/**
	 * Creates GUI with a button
	 */
	public LoseRobotSection() {
		super("Lose Robot Section");
		//add(new JLabel(new ImageIcon(LoseRobotSection.class.getResource("/robocup/view/penguin.png"))));

		final JCheckBox cb_LoseRobot = new JCheckBox("Set robots out of sight");
		
		cb_LoseRobot.setSelected(false);
		cb_LoseRobot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				World.getInstance().setAllRobotsOffsight(cb_LoseRobot.isSelected());
			}
        });
		
		add(cb_LoseRobot);
	}

	@Override
	public void update() {
	}

}
