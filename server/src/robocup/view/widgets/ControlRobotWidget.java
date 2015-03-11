package robocup.view.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.output.ComInterface;
import robocup.output.RobotCom;
import robocup.view.GUI;
import robocup.view.WidgetBox;

@SuppressWarnings("serial")
public class ControlRobotWidget extends WidgetBox {

	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JLabel selectedRobotLabel;
	private int selectedRobotId;
	private boolean dribbling = false;

	/**
	 * Create ControLRobotWidget
	 */
	public ControlRobotWidget() {
		super("Control robot");

		setLayout(new MigLayout("wrap 3", "[grow][grow][grow]"));
		selectedRobotLabel = new JLabel("Robot #0 selected");
		add(selectedRobotLabel, "span");

		JButton chipButton = new JButton("Chippen");
		JButton kickButton = new JButton("Kicken");
		JButton dribbleToggleButton = new JButton("Dribble toggle");
		chipButton.addActionListener(new ButtonListener());
		kickButton.addActionListener(new ButtonListener());
		dribbleToggleButton.addActionListener(new ButtonListener());

		add(chipButton, "growx");
		add(kickButton, "growx");
		add(dribbleToggleButton, "growx");
	}

	/**
	 * Handler for chip and kick button
	 */
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String buttonText = ((JButton) e.getSource()).getText();
			LOGGER.info(String.format("%s commando send to robot #%d", buttonText.split("\\s+" )[0], selectedRobotId));
			switch (buttonText) {
			case "Chippen":
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0, 100, dribbling);
				break;
			case "Kicken":
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0, -100, dribbling);
				break;
			case "Dribble toggle":
				dribbling = !dribbling;
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0, 0, dribbling);
				break;
			}
		}
	}

	/**
	 * Update the selected robot panel
	 */
	@Override
	public void update() {
		selectedRobotId = ((GUI) SwingUtilities.getWindowAncestor((this))).getSelectedRobotId();
		selectedRobotLabel.setText(String.format("Robot #%d selected", selectedRobotId));
	}

}
