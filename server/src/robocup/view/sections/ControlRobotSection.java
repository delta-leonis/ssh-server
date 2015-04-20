package robocup.view.sections;

import java.awt.Color;
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
public class ControlRobotSection extends SectionBox{

	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JLabel selectedRobotLabel;
	private int selectedRobotId;
	private boolean dribbling = false;
	private JTextField forwardBox,
						kickStrength,
						chipStrength;
	
	
	/**
	 * Create ControlRobotSection
	 */
	public ControlRobotSection() {
		super("Control robot");

		setLayout(new MigLayout("wrap 3", "[grow][grow][grow]"));
		selectedRobotLabel = new JLabel("Robot #0 selected");
		add(selectedRobotLabel, "span");

		JButton chipButton = new JButton("Chippen");
		JButton kickButton = new JButton("Kicken");
		JButton dribbleToggleButton = new JButton("Dribble toggle");
		JButton moveForwardButton = new JButton("↑");
		JButton moveBackButton = new JButton("↓");
		JButton moveLeftButton = new JButton("←");
		JButton moveRightButton = new JButton("→");
		JButton strafeLeftButton = new JButton("←←");
		JButton strafeRightButton = new JButton("→→");
		JButton forwardTestButton = new JButton("Forward");
		forwardTestButton.setBackground(Color.GREEN);
		forwardBox = new JTextField("500");
		kickStrength = new JTextField("100");
		chipStrength = new JTextField("100");
		
		ButtonListener buttonListener = new ButtonListener();
		chipButton.addActionListener(buttonListener);
		kickButton.addActionListener(buttonListener);
		dribbleToggleButton.addActionListener(buttonListener);
		moveForwardButton.addActionListener(buttonListener);
		moveBackButton.addActionListener(buttonListener);
		moveLeftButton.addActionListener(buttonListener);
		moveRightButton.addActionListener(buttonListener);
		forwardTestButton.addActionListener(buttonListener);
		strafeLeftButton.addActionListener(buttonListener);
		strafeRightButton.addActionListener(buttonListener);


		add(chipStrength, "growx");
		add(chipButton, "growx, wrap");
		add(kickStrength, "growx");
		add(kickButton, "growx, wrap");
		add(dribbleToggleButton, "growx");
		add(strafeLeftButton, "growx");
		add(moveForwardButton, "growx");
		add(strafeRightButton, "growx");
		add(moveLeftButton, "growx");
		add(moveBackButton, "growx");
		add(moveRightButton, "growx");
		add(forwardBox, "growx");
		add(forwardTestButton, "growx");
	}

	/**
	 * Handler for all actions for the {@link Robot}
	 */
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String buttonText = ((JButton) e.getSource()).getText();
			LOGGER.info(String.format("%s commando send to robot #%d", buttonText.split("\\s+" )[0], selectedRobotId));
			switch (buttonText) {
			case "Chippen":
				ComInterface.getInstance().send(1, selectedRobotId,0, 0, 0, Integer.valueOf(chipStrength.getText()), dribbling);
				break;
			case "Kicken":
				ComInterface.getInstance().send(1, selectedRobotId, 0, 0, 0, -1*Integer.valueOf(kickStrength.getText()), dribbling);
				break;
			case "Dribble toggle":
				dribbling = !dribbling;
				ComInterface.getInstance().send(1, selectedRobotId, 0, 0, 0, 0, dribbling);
				break;
			case "↑":
				ComInterface.getInstance().send(1, selectedRobotId,0, 500, 0, 0, dribbling);
				break;
			case "↓":
				ComInterface.getInstance().send(1, selectedRobotId, 0, -500, 0, 0, dribbling);
				break;
			case "←":
				ComInterface.getInstance().send(1, selectedRobotId, 0, 0, -200, 0, dribbling);
				break;
			case "→":
				ComInterface.getInstance().send(1, selectedRobotId, 0, 0, 200, 0, dribbling);
				break;
			case "←←":
				ComInterface.getInstance().send(1, selectedRobotId, -90, 500, 0, 0, dribbling);
				break;
			case "→→":
				ComInterface.getInstance().send(1, selectedRobotId, 90, 500, 0, 0, dribbling);
				break;
			case "Forward":
				try{
					ComInterface.getInstance().send(1, selectedRobotId, 0, Integer.parseInt(forwardBox.getText()), 0, 0, dribbling);
				}
				catch(Exception exc){
					LOGGER.severe("USE AN INTEGER.");
				}
				break;
			}
		}
	}

	/**
	 * Update the corresponding {@link RobotBox}
	 */
	@Override
	public void update() {
		selectedRobotId = ((GUI) SwingUtilities.getWindowAncestor((this))).getSelectedRobotId();
		selectedRobotLabel.setText(String.format("Robot #%d selected", selectedRobotId));
	}

}
