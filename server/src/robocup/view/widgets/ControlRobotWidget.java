package robocup.view.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class ControlRobotWidget extends WidgetBox{

	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JLabel selectedRobotLabel;
	private int selectedRobotId;
	private boolean dribbling = false
				  , keyPressed = false;
	
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
		JButton moveForwardButton = new JButton("↑");
		JButton moveBackButton = new JButton("↓");
		JButton moveLeftButton = new JButton("←");
		JButton moveRightButton = new JButton("→");

		chipButton.addActionListener(new ButtonListener());
		kickButton.addActionListener(new ButtonListener());
		dribbleToggleButton.addActionListener(new ButtonListener());
		moveForwardButton.addActionListener(new ButtonListener());
		moveBackButton.addActionListener(new ButtonListener());
		moveLeftButton.addActionListener(new ButtonListener());
		moveRightButton.addActionListener(new ButtonListener());

		add(chipButton, "growx");
		add(kickButton, "growx");
		add(dribbleToggleButton, "growx");
		add(new JLabel(), "growx");
		add(moveForwardButton, "growx");
		add(new JLabel(), "growx");
		add(moveLeftButton, "growx");
		add(moveBackButton, "growx");
		add(moveRightButton, "growx");

		addKeyListener(new MoveKeyListener());
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
			case "↑":
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 200, 0, 0, 0, 0, dribbling);
				break;
			}
			
			
		}
	}
	
	private class MoveKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e){
			int code = e.getKeyCode();
			if(code != KeyEvent.VK_ESCAPE && keyPressed)
				return;
			
			switch(code){
				case KeyEvent.VK_UP:
					System.out.println("Test! " + code);
					keyPressed = true;
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 200, 0, 0, 0, 0, dribbling);
					break;
				case KeyEvent.VK_DOWN:
					System.out.println("Test! " + code);
					keyPressed = true;
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, -200, 0, 0, 0, 0, dribbling);
					break;
				case KeyEvent.VK_LEFT:
					System.out.println("Test! " + code);
					keyPressed = true;
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 20, 0, dribbling);
					break;
				case KeyEvent.VK_RIGHT:
					System.out.println("Test! " + code);
					keyPressed = true;
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, -20, 0, dribbling);
					break;
				case KeyEvent.VK_ESCAPE:
					System.out.println("Test! " + code);
					keyPressed = false;
					for(int i = 0; i < 12; ++i){
						ComInterface.getInstance(RobotCom.class).send(1, i, 0, 0, 0, 0, 0,0, false);
					}
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			System.out.println("Test! ");
			keyPressed = false;
			ComInterface.getInstance(RobotCom.class).send(1,selectedRobotId, 0, 0, 0, 0, 0,0, false);
		}

		@Override
		public void keyTyped(KeyEvent e) {			
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
