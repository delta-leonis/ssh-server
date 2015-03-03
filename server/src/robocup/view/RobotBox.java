package robocup.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import robocup.model.Robot;

public class RobotBox extends JPanel {
	private static final long serialVersionUID = 1L;

	private Robot robot;
	private JLabel robotStatus, robotPosition, robotRole;

	/**
	 * Creates a JPanel with the information of a single robot
	 * @param _robot
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
	 * @return robot object used in this panel
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Sets the robotstatus
	 * @param desired status
	 */
	private void setRobotStatus(boolean online) {
		if (online) {
			robotStatus.setText("Online");
			robotStatus.setForeground(new Color(0, 0xFF, 0));
		} else {
			robotStatus.setText("Offline");
			robotStatus.setForeground(new Color(0xFF, 0, 0));
		}
	}

	/**
	 * Updates all robot information, called upon SSL_DetectionFrame
	 */
	public void update() {
		setRobotStatus(robot.isOnSight());
		robotRole.setText(robot.getRole().toString());
		robotPosition.setText(robot.getPosition().getX() + ", " + robot.getPosition().getY());
	}
}
