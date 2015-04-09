package robocup.test;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import robocup.model.FieldPoint;
import robocup.view.SectionBox;


public class TestPathPlannerVisualTestPanel extends SectionBox implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JButton randomButton;
	private JButton lockedInSourceButton;
	private JButton lockedInDestinationButton;
	private JButton robotTooCloseButton;
	private TestPathPlannerPanel plannerPanel;
	private TestPathPlanner planner;
	
	public TestPathPlannerVisualTestPanel(TestPathPlanner planner){
		super("Path Planner Test");
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
		setPreferredSize(new Dimension(800, 700));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == randomButton){
			FieldPoint destination = new FieldPoint((int)(Math.random() * 6000 - 3000), (int)(Math.random() * 4000 - 2000));
			planner.setupRandomRobots(true);
			plannerPanel.refresh(destination);
			repaint();
		}
		else if(e.getSource() == lockedInSourceButton){
			FieldPoint destination = new FieldPoint(1500,1500);
			planner.setupLockedInSource(destination, true);
			plannerPanel.refresh(destination);
			repaint();
		}
		else if(e.getSource() == lockedInDestinationButton){
			FieldPoint destination = new FieldPoint(1500,1500);
			planner.setupLockedInDestination(destination, true);
			plannerPanel.refresh(destination);
			repaint();
		}
		else if(e.getSource() == robotTooCloseButton){
			FieldPoint destination = new FieldPoint(1500,1500);
			planner.setupRobotsTooClose(destination, true);
			plannerPanel.refresh(destination);
			repaint();
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
