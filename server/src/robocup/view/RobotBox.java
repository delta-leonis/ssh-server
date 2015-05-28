package robocup.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import robocup.model.Ally;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.RobotMode;

/**
 * A {@link JPanel} that displays the info for a single {@link Robot}<br>
 *  when {@code update()} is called all information will be refreshed with the information in the corresponding {@link Robot} object 
 */
@SuppressWarnings("serial")
public class RobotBox extends JPanel {
	private Robot robot;
	private JLabel robotStatus, robotPosition, robotRole;

	/**
	 * Creates a {@link JPanel} with the information of a single {@link Robot}
	 * @param _robot a {@link Robot} object whose information will be displayed in this object
	 */
	public RobotBox(Robot _robot) {
		robot = _robot;
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.setLayout(new MigLayout("wrap 2", "[250][right, 250]"));

		robotStatus = new JLabel();
		setRobotStatus(robot.isOnSight());

		JLabel robotId = new JLabel("#" + robot.getRobotId());
		robotId.setFont(new Font(robotId.getFont().getFontName(), Font.PLAIN, 20));

		robotRole = new JLabel("UNKNOWN");
		robotPosition = new JLabel("UNKNOWN");

		this.add(robotStatus);
		this.add(robotId);

		add(new JLabel("Role:"));
		this.add(robotRole);
		add(new JLabel("Position:"));
		this.add(robotPosition);
	}

	/**
	 * @return {@link Robot} used in this panel
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Sets the label containing the robot status
	 * @param online whether the robot is online
	 */
	private void setRobotStatus(boolean online) {
		if (online) {
			robotStatus.setText("Online");
			robotStatus.setForeground(Color.green);
		} else {
			robotStatus.setText("Offline");
			robotStatus.setForeground(Color.red);
		}
	}

	private Color getRoleColor(RobotMode robotMode) {
		Color[] colors = {Color.BLUE.brighter().brighter(), Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.YELLOW};
		if(robotMode == null)
			return Color.LIGHT_GRAY;
		return colors[Math.min(robotMode.ordinal(), colors.length-1)];
	}
	
	/**
	 * Updates all {@link Robot} information, usually called upon {@code SSL_DetectionFrame}
	 */
	public void update() {
		setRobotStatus(robot.isOnSight());
		robotRole.setText(((Ally)robot).getRole() == null ? "Undefined" : ((Ally)robot).getRole().toString());
		robotPosition.setText(robot.getPosition() != null ? (int)robot.getPosition().getX() + ", " + (int)robot.getPosition().getY() : "Undefined");

		if(robot.getRobotId() == World.getInstance().getGUI().getSelectedRobot().getRobotId())
			setBackground(getRoleColor(((Ally)robot).getRole()).darker());
		else
			setBackground(getRoleColor(((Ally)robot).getRole()));
	}
}
