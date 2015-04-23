package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;

import net.miginfocom.swing.MigLayout;
import robocup.controller.handlers.protohandlers.DetectionHandler;
import robocup.model.Ally;
import robocup.model.Robot;
import robocup.model.World;
import robocup.view.SectionBox;

/**
 * {@link SectionBox} for selecting wich  {@link Robot robots} can be updated by the {@link DetectionHandler}.<br>
 */
@SuppressWarnings("serial")
public class ValidRobotSection extends SectionBox {

	private ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();

	/**
	 * Creates a {@link JCheckBox} for every {@link Ally} and 
	 * implements a {@code anonymous buttonhandler} for selecting {@link Ally}s that are currently recognized/visible
	 */
	public ValidRobotSection() {
		super("Valid robots");
		setLayout(new MigLayout("wrap 6", "[][][][][][]"));

		int numberOfRobots = World.getInstance().getReferee().getAlly().getRobots().size();

		for (int i = 0; i < numberOfRobots; i++) {
			JCheckBox checkbox = new JCheckBox("#" + i);
			checkbox.addActionListener(new checkHandler());
			checkbox.setSelected(World.getInstance().getValidRobotIDs().contains(i));
			checkboxes.add(checkbox);
			add(checkbox, (numberOfRobots - 1 == i) ? "wrap" : "");
		}
	}

	/**
	 * Handler for the check boxes
	 */
	private class checkHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JCheckBox checkbox = ((JCheckBox) e.getSource());
			if(checkbox.isSelected())
				World.getInstance().getValidRobotIDs().add(Integer.valueOf(checkbox.getText().substring(1)));
			else
				World.getInstance().getValidRobotIDs().remove(Integer.valueOf(checkbox.getText().substring(1)));

			World.getInstance().gui.update("robotBoxes");
		}
	}

	/**
	 * currently unused
	 */
	@Override
	public void update() {
	}

}
