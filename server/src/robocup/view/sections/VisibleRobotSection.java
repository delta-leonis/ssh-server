package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import net.miginfocom.swing.MigLayout;
import robocup.model.Robot;
import robocup.model.World;
import robocup.view.SectionBox;

/**
 * {@link SectionBox} that makes it possible to remove certain {@link RobotBox}es from the {@link GUI}.<br>
 */
@SuppressWarnings("serial")
public class VisibleRobotSection extends SectionBox {

	private ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();

	/**
	 * Creates a {@link JCheckBox} for every {@link Ally} and 
	 * implements a {@code anonymous buttonhandler} for selecting {@link Ally}s that are currently recognized/visible
	 */
	public VisibleRobotSection() {
		super("Visible robots");
		setLayout(new MigLayout("wrap 6", "[][][][][][]"));

		int numberOfRobots = World.getInstance().getReferee().getAlly().getRobots().size();

		for (int i = 0; i < numberOfRobots; i++) {
			JCheckBox checkbox = new JCheckBox("#" + i);
			checkbox.addActionListener(new checkHandler());
			checkbox.setSelected(World.getInstance().getReferee().getAlly().getRobotByID(i).isVisible());
			checkboxes.add(checkbox);
			add(checkbox, (numberOfRobots - 1 == i) ? "wrap" : "");
		}

		add(new JButton(new AbstractAction("Autoselect") {
			/**
			 * Loops all existing {@link Ally}s, when {@link Robot} is not onsight it is 
			 * removed from view
			 */
			public void actionPerformed(ActionEvent e) {
				int i = 0;
				for (Robot robot : World.getInstance().getReferee().getAlly().getRobots()) {
					robot.setVisible(robot.isOnSight());
					checkboxes.get(i).setSelected(robot.isVisible());
					i++;
				}
				World.getInstance().getGUI().update("robotBoxes");
			}
		}), "span");
	}

	/**
	 * Handler for the check boxes
	 */
	private class checkHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JCheckBox checkbox = ((JCheckBox) e.getSource());
			World.getInstance().getReferee().getAlly().getRobotByID(Integer.valueOf(checkbox.getText().substring(1)))
					.setVisible(checkbox.isSelected());
			World.getInstance().getGUI().update("robotBoxes");
		}
	}

	/**
	 * currently unused
	 */
	@Override
	public void update() {
	}

}
