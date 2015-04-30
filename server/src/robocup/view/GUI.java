package robocup.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.model.Robot;
import robocup.model.World;
import robocup.view.sections.ConsoleSection;
import robocup.view.sections.ControlRobotSection;
import robocup.view.sections.FieldControlSection;
import robocup.view.sections.GameStatusSection;
import robocup.view.sections.RotateRobotSection;
import robocup.view.sections.SettingsSection;
import robocup.view.sections.ValidRobotSection;
import robocup.view.sections.VisibleRobotSection;

/**
 * Main GUI for controlling and monitoring the robots<br>
 * The Layout that is used everywhere is a {@link MigLayout} for easier resizing,
 * and an easier way to implement modular sections.
 *
 */
@SuppressWarnings("serial")
public class GUI extends JFrame {

	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JPanel robotContainer;
	private JPanel sectionContainer;
	private ConsoleSection console;
	private int selectedRobotId = -1;
	private ArrayList<RobotBox> allRobotBoxes = new ArrayList<RobotBox>();
	private Timer updateTimer;
	private int updateFrequency = 60; //update frequency in Hertz


	JPanel container = new JPanel();
	JScrollPane scrollPane = new JScrollPane(container);
	/**
	 * Build the GUI elements
	 */
	public GUI() {

		setLookAndFeel();
		container.setBackground(UIManager.getColor("Panel.background"));
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		add(scrollPane);

		container.setLayout(new MigLayout("wrap 2", "[500][grow]", "[][grow]"));

		initRobotContainer();
		initSectionContainer();
		initConsoleContainer();

		setTitle("User interface RoboCup SSH");
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		LOGGER.info("GUI is started and initialized");
		updateTimer = new Timer();
		updateTimer.schedule(new updateGUITask(), 1000/updateFrequency);
	}

	/**
	 * TimerTask specifically for updating all GUI elements at a set frecuency
	 */
	class updateGUITask extends TimerTask {
		public void run() {
			update("robotContainer");
			update("sectionContainer");
			updateTimer.schedule(new updateGUITask(), 1000/updateFrequency);
		}
	}
	
	/**
	 * Set a new updateFrequency for the GUI elements
	 * @param frequency a new update frequency in Hz
	 */
	public void setUpdateFrequency(int frequency){
		updateFrequency = frequency;
	}
	
	/**
	 * Tries to set the "Nimbus" {@link LookAndFeel} to the {@link UIManager}
	 */
	private void setLookAndFeel() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Nimbus look and feel could not be loaded");
		}
	}

	/**
	 * Returns the id of the {@link Robot} who's {@link RobotBox} is selected 
	 * @return robotId
	 */
	public int getSelectedRobotId() {
		return selectedRobotId;
	}

	/**
	 * Adds a {@link ConsoleSection} at the bottom of the GUI
	 */
	private void initConsoleContainer() {
		console = new ConsoleSection();
		container.add(console, "span, growy, growx");
	}

	/**
	 * Adds all {@link SectionBox}es to a {@link JPanel} on the right of the GUI
	 */
	private void initSectionContainer() {
		sectionContainer = new JPanel();
		//sectionContainer.setBorder(BorderFactory.createTitledBorder("Sections"));
		sectionContainer.setLayout(new MigLayout("wrap 1", "[grow]"));

		sectionContainer.add(new GameStatusSection(), "growx");
		sectionContainer.add(new ValidRobotSection(), "growx");
		sectionContainer.add(new VisibleRobotSection(), "growx");
		sectionContainer.add(new FieldControlSection(), "growx");
//		sectionContainer.add(new PathPlannerTestSection(), "growx");	// Comment "World.getInstance().getGUI().update("robotContainer");" in Main.initTeams() for this section to work.
//		sectionContainer.add(new ControlRobotPacketTestSection(), "growx");
		sectionContainer.add(new ControlRobotSection(), "growx");
		sectionContainer.add(new SettingsSection(), "growx");
	//	sectionContainer.add(new PenguinSection(), "growx, growy");
		sectionContainer.add(new RotateRobotSection(), "growx, growy");

		container.add(sectionContainer, "growy, growx");
	}

	/**
	 * Adds all {@link RobotBox} to a {@link JPanel} on the left
	 */
	private void initRobotContainer() {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new MigLayout("wrap 1", "[grow]", "[grow]"));
		
		robotContainer = new JPanel();
		robotContainer.setLayout(new MigLayout("wrap 2", "[250]related[250]"));
		robotContainer.setBorder(BorderFactory.createTitledBorder("Robots"));

		for (Robot robot : World.getInstance().getReferee().getAlly().getRobots()) {
			RobotBox box = new RobotBox(robot);
			box.addMouseListener(new PanelClickListener());
			if (robot.getRobotId() == 0)
				box.setBackground(Color.LIGHT_GRAY);
			if (robot.isVisible())
				robotContainer.add(box);
			allRobotBoxes.add(box);
		}

		robotContainer.add(new JPanel(), "growy, span 2");
		wrapper.add(robotContainer, "growy");
		container.add(wrapper, "growy");
	}

	/**
	 * Handler for selecting individual {@link Robot}s by clicking the corresponding {@link RobotBox}
	 */
	private class PanelClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// loop all RobotBoxes, and change the background color
			for (Component item : robotContainer.getComponents()) {
				if (item instanceof RobotBox) {
					if (((RobotBox) item).equals(((RobotBox) arg0.getSource())))
						((RobotBox) item).setBackground(Color.LIGHT_GRAY);
					else
						((RobotBox) item).setBackground(UIManager.getColor("Panel.background"));
				}
			}

			selectedRobotId = ((RobotBox) arg0.getSource()).getRobot().getRobotId();
			//update("sectionContainer");
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	
	
	/**
	 * Update gui elements such as specific {@link SectionBox}es or {@link RobotBox}es
	 * @param desc of the containers that needs to be updated
	 * 		<br><b>Possibilities:</b>
	 * 		<br><b>robotContainer</b>	updates all individual {@link RobotBox}es
	 * 		<br><b>robotBoxes</b>	updates all individual {@link RobotBox}es and removes them from the view if necessary
	 * 		<br><b>sectionContainer</b>	updates all individual {@link SectionBox}es
	 */
	public void update(String desc) {
		LOGGER.fine(String.format("Repainted GUI (%s)", desc));
		switch (desc) {
		case "robotContainer":
			// update all robot items
			for (RobotBox box : allRobotBoxes)
				box.update();
			break;
			
		case "robotBoxes":
			robotContainer.removeAll();
			for (RobotBox box : allRobotBoxes)
					if(box.getRobot().isVisible())
						robotContainer.add(box);
			revalidate();
			repaint();
			break;

		case "sectionContainer":
			for (Component item : sectionContainer.getComponents()) {
				if (item instanceof SectionBox){
					LOGGER.fine("Updated: " + ((TitledBorder)(((SectionBox) item).getBorder())).getTitle());
					((SectionBox) item).update();
				}
			}

			break;

		default:
			LOGGER.severe(String.format("Could not update %s", desc));
			break;
		}
	}
}
