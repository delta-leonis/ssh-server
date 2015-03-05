package robocup.controller.ai.movement.test;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import robocup.model.Point;


public class TestPathPlannerVisualTestPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JButton randomButton;
	private JButton lockedInSourceButton;
	private JButton lockedInDestinationButton;
	private JButton robotTooCloseButton;
	private TestPathPlannerPanel plannerPanel;
	private TestPathPlanner planner;
	
	public TestPathPlannerVisualTestPanel(TestPathPlanner planner){
		setLayout(new FlowLayout());
		this.planner = planner;
		plannerPanel = planner.getRandomRobotsTestPanel();
		add(plannerPanel);
		
		randomButton = new JButton("Random Robots");
		lockedInSourceButton = new JButton("Lock In Source");
		lockedInDestinationButton = new JButton("Lock In Destination");
		robotTooCloseButton = new JButton("Robot Too Close");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(randomButton);
		buttonPanel.add(lockedInSourceButton);
		buttonPanel.add(lockedInDestinationButton);
		buttonPanel.add(robotTooCloseButton);
		randomButton.addActionListener(this);
		lockedInSourceButton.addActionListener(this);
		lockedInDestinationButton.addActionListener(this);
		robotTooCloseButton.addActionListener(this);
		
		add(buttonPanel);		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == randomButton){
			Point destination = new Point((int)(Math.random() * 6000 - 3000), (int)(Math.random() * 4000 - 2000));
			planner.setupRandomRobots(true);
			plannerPanel.refresh(destination);
			repaint();
		}
		else if(e.getSource() == lockedInSourceButton){
			Point destination = new Point(1500,1500);
			planner.setupLockedInSource(destination, true);
			plannerPanel.refresh(destination);
			repaint();
		}
		else if(e.getSource() == lockedInDestinationButton){
			Point destination = new Point(1500,1500);
			planner.setupLockedInDestination(destination, true);
			plannerPanel.refresh(destination);
			repaint();
		}
		else if(e.getSource() == robotTooCloseButton){
			Point destination = new Point(1500,1500);
			planner.setupRobotsTooClose(destination, true);
			plannerPanel.refresh(destination);
			repaint();
		}
	}
}
