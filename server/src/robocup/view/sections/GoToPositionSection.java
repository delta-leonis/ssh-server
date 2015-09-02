package robocup.view.sections;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.view.GUI;
import robocup.view.RobotBox;
import robocup.view.SectionBox;

/**
 * Section made to test the {@link GotoPosition} class.
 * TODO: May have to be tweaked when the cameras are in place.
 */
public class GoToPositionSection extends SectionBox implements ActionListener{
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	private int selectedRobotId;

	private JLabel robotIdLabel;
	private JTextField xRobotField;
	private JTextField yRobotField;
	private JTextField xDestinationField;
	private JTextField yDestinationField;
	private JTextField xTargetField;
	private JTextField yTargetField;
	
	private JButton startButton;
	
	private GotoPosition go;
	private World world;

	public GoToPositionSection(){
		super("Go To Position");
		world = World.getInstance();
		setLayout(new MigLayout("wrap 2", "[grow][grow]"));

		startButton = new JButton("Start Test");
		
		robotIdLabel = new JLabel("Robot #0 selected");
		xRobotField = new JTextField("0");
		yRobotField = new JTextField("0");
		xDestinationField = new JTextField("1000");
		yDestinationField = new JTextField("1000");
		xTargetField = new JTextField("0");
		yTargetField = new JTextField("0");
		
		robotIdLabel.setPreferredSize(new Dimension(100, 20));
		xRobotField.setPreferredSize(new Dimension(100, 20));
		yRobotField.setPreferredSize(new Dimension(100, 20));
		xDestinationField.setPreferredSize(new Dimension(100, 20));
		yDestinationField.setPreferredSize(new Dimension(100, 20));
		xTargetField.setPreferredSize(new Dimension(100, 20));
		yTargetField.setPreferredSize(new Dimension(100, 20));
		
		startButton.addActionListener(this);

		
		add(startButton);					add(robotIdLabel);
		add(new JLabel("Robot x"));			add(new JLabel("Robot y"));
		add(xRobotField);					add(yRobotField);
		add(new JLabel("Destination x"));	add(new JLabel("Destination y"));
		add(xDestinationField);				add(yDestinationField);
		add(new JLabel("Target x"));		add(new JLabel("Target y"));
		add(xTargetField);					add(yTargetField);

	}

	/**
	 * Update the corresponding {@link RobotBox}
	 */
	@Override
	public void update() {
		selectedRobotId = world.getGuiModel().getSelectedRobot().getRobotId();
		robotIdLabel.setText(String.format("Robot #%d selected", selectedRobotId));
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		if(a.getSource() == startButton){
//			Team allyTeam = world.getReferee().getAlly();
//			Robot robot = allyTeam.getRobotByID(selectedRobotId);
//			robot.setPosition(new FieldPoint(Integer.parseInt(xRobotField.getText()), Integer.parseInt(yRobotField.getText())));
//			robot.setOnSight(true);
			
//			go = new GotoPosition(world.getReferee().getAlly().getRobotByID(selectedRobotId),
//									new FieldPoint(Integer.parseInt(xDestinationField.getText()), Integer.parseInt(yDestinationField.getText())),
//									new FieldPoint(Integer.parseInt(xTargetField.getText()), Integer.parseInt(yTargetField.getText())));
//			

//			System.out.println("Id: " + selectedRobotId + " Destination [" + xDestinationField + "," + yDestinationField +"]");
//			System.out.println("RobotPosition: " + world.getReferee().getAlly().getRobotByID(selectedRobotId).getPosition());
			for(int i = 0; i < 5000; ++i){
				go = new GotoPosition(world.getReferee().getAlly().getRobotByID(selectedRobotId), 
						world.getReferee().getAlly().getRobotByID(selectedRobotId).getPosition(),
						world.getBall().getPosition());
				go.calculate(0,true);
//				System.out.println("Ball pos: " +  world.getBall().getPosition());
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					LOGGER.warning("Error in GoToPositionSection: " + e.getMessage());
				}
			}
			System.out.println("Stop");
			ComInterface.getInstance().send(1, selectedRobotId, 0, 0, 0, 0, false);
			ComInterface.getInstance().send(1, selectedRobotId, 0, 0, 0, 0, false);
			
		}
	}
	
}
