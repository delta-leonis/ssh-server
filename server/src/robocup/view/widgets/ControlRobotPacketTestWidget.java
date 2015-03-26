package robocup.view.widgets;

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
import robocup.output.RobotCom;
import robocup.view.GUI;
import robocup.view.WidgetBox;

@SuppressWarnings("serial")
public class ControlRobotPacketTestWidget extends WidgetBox{

	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JLabel selectedRobotLabel;
	private int selectedRobotId;
	private boolean dribbling = false;
	private JTextField directionField;
	private JTextField directionSpeedField;
	private JTextField rotationAngleField;
	private JTextField rotationSpeedField;
	
	/**
	 * Create ControLRobotWidget
	 */
	public ControlRobotPacketTestWidget() {
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
		rotationAngleField = new JTextField("0");
		rotationSpeedField = new JTextField("0");


		add(new JLabel("Direction"), "growx");
		add(directionField, "growx");
		add(new JLabel("Direction Speed"), "growx");
		add(directionSpeedField, "growx");
		add(new JLabel(), "wrap");
		
		add(new JLabel("Rotation Angle"), "growx");
		add(rotationAngleField, "growx");
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
				send(directionField.getText(), directionSpeedField.getText(), rotationAngleField.getText(), rotationSpeedField.getText());
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
	public void send(String direction, String directionSpeed, String rotationAngle, String rotationSpeed){
		try{
			int directionInt = Integer.parseInt(direction);
			int directionSpeedInt = Integer.parseInt(directionSpeed);
			int rotationAngleInt = Integer.parseInt(rotationAngle);
			int rotationSpeedInt = Integer.parseInt(rotationSpeed);
			
			ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, directionInt, directionSpeedInt, 0, rotationAngleInt, rotationSpeedInt, 100, dribbling);

		}catch(NumberFormatException e){
			LOGGER.severe("Number Format Exception");
		}
	}	


	/**
	 * Update the selected robot panel
	 */
	@Override
	public void update() {
		requestFocusInWindow();
		selectedRobotId = ((GUI) SwingUtilities.getWindowAncestor((this))).getSelectedRobotId();
		selectedRobotLabel.setText(String.format("Robot #%d selected", selectedRobotId));
	}

}
