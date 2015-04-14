package robocup.view.sections;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.output.ComInterface;
import robocup.view.GUI;
import robocup.view.SectionBox;
/**
 * TestSection to remotely send commands to a {@link Robot} whose {@link RobotBox} is selected in the {@link GUI}
 */
@SuppressWarnings("serial")
public class ControlRobotPacketTestSection extends SectionBox{

	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JLabel selectedRobotLabel;
	private int selectedRobotId;
	private boolean dribbling = false;
	private JTextField directionField;
	private JTextField directionSpeedField;
	private JTextField rotationSpeedField;
	
	/**
	 * Create ControlRobotSection
	 */
	public ControlRobotPacketTestSection() {
		super("Control robot");

		setLayout(new MigLayout());
		selectedRobotLabel = new JLabel("Robot #0 selected");
		add(selectedRobotLabel, "span");

		JButton sendPacketButton = new JButton("Send");

		ButtonListener buttonListener = new ButtonListener();
		sendPacketButton.addActionListener(buttonListener);
		
		directionField = new JTextField("0");
		directionField.setPreferredSize(new Dimension(50, 20));
		directionSpeedField = new JTextField("0");
		directionSpeedField.setPreferredSize(new Dimension(50, 20));
		rotationSpeedField = new JTextField("0");


		add(new JLabel("Direction"), "growx");
		add(directionField, "growx");
		add(new JLabel("Direction Speed"), "growx");
		add(directionSpeedField, "growx");
		add(new JLabel(), "wrap");
		
		add(new JLabel("Rotation Speed"), "growx");
		add(rotationSpeedField, "growx");
		add(sendPacketButton, "growx");

		setFocusable(true);
		requestFocusInWindow();
	}

	/**
	 * Handler for chip and kick button
	 */
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String buttonText = ((JButton) e.getSource()).getText();
			LOGGER.info(String.format("%s commando send to robot #%d", buttonText.split("\\s+" )[0], selectedRobotId));
			switch (buttonText) {
			case "Send":
				send(directionField.getText(), directionSpeedField.getText(), rotationSpeedField.getText());
				break;
			}
		}
	}
	/**
	 * Sends a packet using the given strings.
	 * Prints an error if the strings aren't ints.
	 * @param direction The direction the robot needs to strafe
	 * @param directionSpeed The speed the robot needs to strafe towards said direction
	 * @param rotationAngle The direction the robot needs to face, relative to itself. (-180 : 180)
	 * @param rotationSpeed The speed at which the robot turns.
	 */
	public void send(String direction, String directionSpeed, String rotationSpeed){
		try{
			int directionInt = Integer.parseInt(direction);
			int directionSpeedInt = Integer.parseInt(directionSpeed);
			int rotationSpeedInt = Integer.parseInt(rotationSpeed);
			
			ComInterface.getInstance().send(1, selectedRobotId, directionInt, directionSpeedInt, rotationSpeedInt, 100, dribbling);

		}catch(NumberFormatException e){
			LOGGER.severe("Number Format Exception");
		}
	}	


	/**
	 * Update the corresponding {@link RobotBox}
	 */
	@Override
	public void update() {
		requestFocusInWindow();
		selectedRobotId = ((GUI) SwingUtilities.getWindowAncestor((this))).getSelectedRobotId();
		selectedRobotLabel.setText(String.format("Robot #%d selected", selectedRobotId));
	}

}
