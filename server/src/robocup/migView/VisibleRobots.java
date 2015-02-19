package robocup.migView;

import robocup.model.Robot;
import robocup.model.World;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

public class VisibleRobots extends WidgetBox {
	private static final long serialVersionUID = 1L;

	public VisibleRobots() {
		super("Visible robots");
		setLayout(new MigLayout("wrap 6", "[][][][][][]"));

		update();
	}
	
	@Override
	public void update() {
		removeAll();

		for(Robot robot : World.getInstance().getAlly().getRobots()){
			JCheckBox checkbox = new JCheckBox("#" + robot.getRobotId());
			checkbox.setSelected(robot.isVisible());
			add(checkbox);
		}

		revalidate();
		repaint();
	}

}
