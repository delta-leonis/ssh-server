package robocup.view.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JTextField;

import robocup.Main;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;
import robocup.view.WidgetBox;


@SuppressWarnings("serial")
public class rotateRobotWidget extends WidgetBox{

	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JTextField textField;
	private boolean run = false;
	private Timer wisselTimer;
	
	public rotateRobotWidget(){
		super("Rotate robot");
		JButton spinButton = new JButton("spin");
		spinButton.addActionListener(new ButtonListener());
		add(spinButton);
		JButton termButton = new JButton("Terminate");
		termButton.addActionListener(new ButtonListener());
		add(termButton);
		textField = new JTextField("500");
		add(textField);
	}

	  class RemindTask extends TimerTask {
	    public void run() {
	    	if(shouldBeRunning()){
	    		textField.setText("" + (Integer.valueOf(textField.getText())*-1));
	    		LOGGER.info("inverted rotation speed");
				ComInterface.getInstance(RobotCom.class).send(1,  World.getInstance().getGUI().getSelectedRobotId(), 0, 0,  Integer.valueOf(textField.getText()),0, false);
				wisselTimer = new Timer();
				wisselTimer.schedule(new RemindTask(),  1000);
	    	}else{
	    		System.out.println("terminated");
	    		ComInterface.getInstance(RobotCom.class).send(1,  World.getInstance().getGUI().getSelectedRobotId(), 0, 0, 0,0, false);
	    	}

	    }
	  }
	private boolean shouldBeRunning(){
		return run;
	}
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (((JButton) e.getSource()).getText()) {
				case "Terminate":
				{
					run = false;
					LOGGER.info("Terminate command send to robot #"+  World.getInstance().getGUI().getSelectedRobotId());
					ComInterface.getInstance(RobotCom.class).send(1,  World.getInstance().getGUI().getSelectedRobotId(), 0, 0, 0,0, false);
					break;
				}
				
				case "spin":
				{
					run = true;
					LOGGER.info("Spin robot #" +  World.getInstance().getGUI().getSelectedRobotId() + ", speed " + Integer.valueOf(textField.getText()));
					ComInterface.getInstance(RobotCom.class).send(1,  World.getInstance().getGUI().getSelectedRobotId(), 0, 0, Integer.valueOf(textField.getText()),0, false);
					wisselTimer = new Timer();
					wisselTimer.schedule(new RemindTask(),  1000);
				}
			}
		}
	}


	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}