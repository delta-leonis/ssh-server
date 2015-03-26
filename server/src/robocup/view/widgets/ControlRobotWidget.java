package robocup.view.widgets;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ally;
import robocup.model.Point;
import robocup.output.ComInterface;
import robocup.output.RobotCom;
import robocup.view.GUI;
import robocup.view.WidgetBox;

@SuppressWarnings("serial")
public class ControlRobotWidget extends WidgetBox{

	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JLabel selectedRobotLabel;
	private int selectedRobotId;
	private boolean dribbling = false;
	private JTextField forwardBox;
	
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
		JButton strafeLeftButton = new JButton("←←");
		JButton strafeRightButton = new JButton("→→");
		JButton slowDownTestButton = new JButton("Slow Down Test");
		slowDownTestButton.setBackground(Color.MAGENTA);
		JButton forwardTestButton = new JButton("Forward");
		forwardTestButton.setBackground(Color.GREEN);
		forwardBox = new JTextField("500");
		
		ButtonListener buttonListener = new ButtonListener();
		chipButton.addActionListener(buttonListener);
		kickButton.addActionListener(buttonListener);
		dribbleToggleButton.addActionListener(buttonListener);
		moveForwardButton.addActionListener(buttonListener);
		moveBackButton.addActionListener(buttonListener);
		moveLeftButton.addActionListener(buttonListener);
		moveRightButton.addActionListener(buttonListener);
		slowDownTestButton.addActionListener(buttonListener);
		forwardTestButton.addActionListener(buttonListener);
		strafeLeftButton.addActionListener(buttonListener);
		strafeRightButton.addActionListener(buttonListener);


		add(chipButton, "growx");
		add(kickButton, "growx");
		add(dribbleToggleButton, "growx");
		add(strafeLeftButton, "growx");
		add(moveForwardButton, "growx");
		add(strafeRightButton, "growx");
		add(moveLeftButton, "growx");
		add(moveBackButton, "growx");
		add(moveRightButton, "growx");
		add(forwardBox, "growx");
		add(forwardTestButton, "growx");
		add(slowDownTestButton, "growx");

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
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 500, 0, 0, 0, 0, dribbling);
				break;
			case "↓":
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, -500, 0, 0, 0, 0, dribbling);
				break;
			case "←":
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, -200, 0, dribbling);
				break;
			case "→":
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 200, 0, dribbling);
				break;
			case "←←":
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, -90, 500, 0, 0, 0, 0, dribbling);
				break;
			case "→→":
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 90, 500, 0, 0, 0, 0, dribbling);
				break;
			case "Slow Down Test":
				slowDownTest();
				break;
			case "Forward":
				try{
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, Integer.parseInt(forwardBox.getText()), 0, 0, 0, 0, dribbling);
				}
				catch(Exception exc){
					LOGGER.severe("USE AN INTEGER.");
				}
				break;
			}
		}
	}
	
	private class MoveKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e){
			int code = e.getKeyCode();
			
			switch(code){
				case KeyEvent.VK_W:
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 750, 0, 0, 0, 0, dribbling);
					break;
				case KeyEvent.VK_S:
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, -750, 0, 0, 0, 0, dribbling);
					break;
				case KeyEvent.VK_A:
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, -100, 0, dribbling);
					break;
				case KeyEvent.VK_D:
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 100, 0, dribbling);
					break;
				case KeyEvent.VK_E:
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 90, 500, 0, 0, 0, 0, dribbling);
					break;
					
				case KeyEvent.VK_Q:
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, -90, 500, 0, 0, 0, 0, dribbling);
					break;
					
				case KeyEvent.VK_1:
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0, -100, dribbling);
					break;
					
				case KeyEvent.VK_2:
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0, 100, dribbling);
					break;	
					
				case KeyEvent.VK_R:
					dribbling = !dribbling;
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0, 0, dribbling);
					break;

				case KeyEvent.VK_ESCAPE:
				case KeyEvent.VK_SPACE:
					LOGGER.info("Send terminate command");
					ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0,0, false);
					for(int i = 0; i < 12; ++i){
						ComInterface.getInstance(RobotCom.class).send(1, i, 0, 0, 0, 0, 0,0, false);
						System.out.println();
					}
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {		
		}
	}
	
	private void slowDownTest(){
		try{
			GotoPosition go = new GotoPosition(new Ally(0, false, 0), ComInterface.getInstance(RobotCom.class), new Point(0,0));
			ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, go.getSpeed(500, 100), 0, 0, 0, 0, dribbling);
			System.out.println("Test! " + go.getSpeed(500, 100));
			Thread.sleep(500);
			
			for(int i = 0; i < 10; ++i){
				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, go.getSpeed(100 - (i*10), 100), 0, 0, 0, 0, dribbling);
				System.out.println("Test! " + go.getSpeed(100 - (i*10), 100));
				Thread.sleep(50);
			}
			ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0, 0, dribbling);

			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Update the selected robot panel
	 */
	@Override
	public void update() {
		//requestFocusInWindow();
		selectedRobotId = ((GUI) SwingUtilities.getWindowAncestor((this))).getSelectedRobotId();
		selectedRobotLabel.setText(String.format("Robot #%d selected", selectedRobotId));
	}

}
