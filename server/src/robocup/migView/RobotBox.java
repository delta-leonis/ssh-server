package robocup.migView;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import robocup.model.Robot;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class RobotBox extends JPanel {
	private static final long serialVersionUID = 1L;

	private Robot robot;
	private JLabel robotStatus;
	private JTextArea robotPosition
					 ,robotRole;
	private JButton chipButton
				   ,kickButton;

	public RobotBox(Robot _robot){
		robot = _robot;

		this.setLayout(new MigLayout("wrap 2"));
		initComponents();
	}
	
	private void initComponents(){
		robotStatus = new JLabel();
		setRobotStatus(robot.isOnSight());

		JLabel robotID = new JLabel(robot.getRobotId() + "");
		robotID.setFont(new Font(robotID.getFont().getFontName(), Font.PLAIN, 20));

		robotRole = new JTextArea();
		robotPosition = new JTextArea();
		robotRole.setEditable(false);
		robotPosition.setEditable(false);

		chipButton = new JButton("Chippen");
		kickButton = new JButton("Kicken");
		chipButton.addActionListener(new ButtonListener());
		kickButton.addActionListener(new ButtonListener());
		
		this.add(robotStatus);
		this.add(robotID);
		this.add(robotRole, "span 2");
		this.add(robotPosition, "span 2");

	}
	
    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
        	switch(((JButton)e.getSource()).getText()){
    			case "Chippen":
    				ComInterface.getInstance(RobotCom.class).send(1, robot.getRobotId(), 0, 0, 0, 0, 0, 100, false);
    				break;

    			case "Kicken":
    				ComInterface.getInstance(RobotCom.class).send(1, robot.getRobotId(), 0, 0, 0, 0, 0, -100, false);
    				break;
        	}
        }
    }
    
    private void setRobotStatus(boolean online) {
    	if(online) {
    		robotStatus.setText("Online");
    		robotStatus.setForeground(new Color(0, 0xFF, 0));
    	}
    	else{
    		robotStatus.setText("Offline");
    		robotStatus.setForeground(new Color(0xFF, 0, 0));
    	}
    }
    
    public void update(){
    	setRobotStatus(robot.isOnSight());
    	robotRole.setText(robot.getRole().toString());
    	robotPosition.setText(robot.getPosition().getX() + ", " + robot.getPosition().getY());
    }
}
