package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import robocup.Main;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.view.SectionBox;

/**
 * {@link SectionBox} used to spin the robot at a adjustable speed,
 * every second it will revert the direction and spin the other way around
 * 
 * NOTE: mainly used for testing durability of the underlayment
 */
@SuppressWarnings("serial")
public class RotateRobotSection extends SectionBox{

	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JTextField textField;
	private boolean run = false;
	private Timer revertTimer;
	private World world;
	
	/**
	 * Create components for the GUI elements
	 */
	public RotateRobotSection(){
		super("Rotate robot");
		world = World.getInstance();
		JButton spinButton = new JButton("spin");
		spinButton.addActionListener(new ButtonListener());
		add(spinButton);
		JButton termButton = new JButton("Terminate");
		termButton.addActionListener(new ButtonListener());
		add(termButton);
		textField = new JTextField("500");
		add(new JLabel("speed"));
		add(textField);
	}

	/**
	 * task that gets the current speed and inverts it, effectively inverting the direction of the spinning
	 */
	  class ReverseDirectionTask extends TimerTask {
	    public void run() {
	    	if(shouldBeRunning()){
	    		textField.setText("" + (Integer.valueOf(textField.getText())*-1));
	    		LOGGER.info("inverted rotation speed");
				ComInterface.getInstance().send(1,  world.getGUI().getSelectedRobot().getRobotId(), 0, 0,  Integer.valueOf(textField.getText()),0, false);
				revertTimer = new Timer();
				revertTimer.schedule(new ReverseDirectionTask(),  1000);
	    	}else{
	    		System.out.println("terminated");
	    		ComInterface.getInstance().send(1,  world.getGUI().getSelectedRobot().getRobotId(), 0, 0, 0,0, false);
	    	}

	    }
	  }
	
	/**
	 * @return true if the timer needs to be going
	 */
	private boolean shouldBeRunning(){
		return run;
	}
	
	/**
	 * Handler for the Terminate and spin button,<br>
	 * the spin button wil start a new {@link Timer} with the {@code ReverseDirectionTask()}
	 */
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (((JButton) e.getSource()).getText()) {
				case "Terminate":
				{
					run = false;
					LOGGER.info("Terminate command send to robot #"+  world.getGUI().getSelectedRobot().getRobotId());
					ComInterface.getInstance().send(1,  world.getGUI().getSelectedRobot().getRobotId(), 0, 0, 0,0, false);
					break;
				}
				
				case "spin":
				{
					run = true;
					LOGGER.info("Spin robot #" +  world.getGUI().getSelectedRobot().getRobotId() + ", speed " + Integer.valueOf(textField.getText()));
					ComInterface.getInstance().send(1,  world.getGUI().getSelectedRobot().getRobotId(), 0, 0, Integer.valueOf(textField.getText()),0, false);
					revertTimer = new Timer();
					revertTimer.schedule(new ReverseDirectionTask(),  1000);
				}
			}
		}
	}


	/**
	 * unused
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}