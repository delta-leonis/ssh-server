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

@SuppressWarnings("serial")
public class VisibleRobotSection extends SectionBox {

	private ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();

	/**
	 * Creates a check box for all robots and 
	 * implements a anonymous button handler for selecting on sight robots
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

		// Removes the visibility of all panels of robots that aren't onSight
		add(new JButton(new AbstractAction("Autoselect") {
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
			World.getInstance().gui.update("robotBoxes");
		}
	}

	@Override
	public void update() {
	}

}
