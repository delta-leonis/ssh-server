package robocup.migView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import robocup.model.Robot;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class RobotBox extends JPanel {
	private static final long serialVersionUID = 1L;

	private Robot robot;
	private JLabel robotStatus
				  ,robotPosition
				  ,robotRole;
	private JButton chipButton
				   ,kickButton;

	public RobotBox(Robot _robot){
		robot = _robot;
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		this.setLayout(new MigLayout("wrap 2", "[250][right, 250]"));
		initComponents();
		
	}
	
	private void initComponents(){
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
	
	public Robot getRobot(){
		return robot;
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
