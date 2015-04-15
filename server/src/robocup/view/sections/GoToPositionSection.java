package robocup.view.sections;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import robocup.Main;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ally;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.output.ComInterface;
import robocup.view.SectionBox;

/**
 * Section made to test the {@link GotoPosition} class.
 * TODO: May have to be tweaked when the cameras are in place.
 */
public class GoToPositionSection extends SectionBox implements ActionListener{
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	private JTextField robotIdField;
	private JTextField xRobotField;
	private JTextField yRobotField;
	private JTextField xDestinationField;
	private JTextField yDestinationField;
	private JTextField xTargetField;
	private JTextField yTargetField;
	
	private JButton startButton;
	
	private GotoPosition go;

	public GoToPositionSection(){
		super("Go To Position");
		setLayout(new MigLayout("wrap 2", "[grow][grow]"));

		startButton = new JButton("Start Test");
		
		robotIdField = new JTextField("id");
		xRobotField = new JTextField();
		yRobotField = new JTextField();
		xDestinationField = new JTextField();
		yDestinationField = new JTextField();
		xTargetField = new JTextField();
		yTargetField = new JTextField();
		
		robotIdField.setPreferredSize(new Dimension(100, 20));
		xRobotField.setPreferredSize(new Dimension(100, 20));
		yRobotField.setPreferredSize(new Dimension(100, 20));
		xDestinationField.setPreferredSize(new Dimension(100, 20));
		yDestinationField.setPreferredSize(new Dimension(100, 20));
		xTargetField.setPreferredSize(new Dimension(100, 20));
		yTargetField.setPreferredSize(new Dimension(100, 20));

		
		add(startButton);					add(robotIdField);
		add(new JLabel("Robot x"));			add(new JLabel("Robot y"));
		add(xRobotField);					add(yRobotField);
		add(new JLabel("Destination x"));	add(new JLabel("Destination y"));
		add(xDestinationField);				add(yDestinationField);
		add(new JLabel("Target x"));		add(new JLabel("Target y"));
		add(xTargetField);					add(yTargetField);

	}

	@Override
	public void update() {	}

	@Override
	public void actionPerformed(ActionEvent a) {
		if(a.getSource() == startButton){
			Robot robot = new Ally(2,2);
			robot.setPosition(new FieldPoint(Integer.parseInt(xRobotField.getText()), Integer.parseInt(yRobotField.getText())));
			go = new GotoPosition(robot, ComInterface.getInstance(), new FieldPoint(Integer.parseInt(xDestinationField.getText()), Integer.parseInt(yDestinationField.getText())));
			for(int i = 0; i < 1000; ++i){
				go.calculate();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					LOGGER.warning("Error in GoToPositionSection: " + e.getMessage());
				}
			}
			
		}
	}
	
}
