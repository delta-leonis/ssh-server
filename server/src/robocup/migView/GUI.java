package robocup.migView;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.*;

import robocup.Main;
import robocup.model.Robot;
import robocup.model.World;
import net.miginfocom.swing.MigLayout;

/**
 * TODO
 * 	Nog work in progress,
 *  documentatie ontbreekt. eerst wordt het huidge systeem gefixed zodat
 *  alle robots gewoon bestaan, en onsight worden verwerkt. 
 * 
 * @author Jeroen
 *
 */
public class GUI extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	private World world;
	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private ArrayList<WidgetBox> widgets = new ArrayList<WidgetBox>();
	private ArrayList<RobotBox> robotboxes = new ArrayList<RobotBox>();
	private JPanel robotContainer;

	public GUI(World world){
		LOGGER.info("GUI started");
		try {
			javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.world = world;
		world.addObserver(this);

		MigLayout layout = new MigLayout("wrap 3", "[200]related[200]related[grow]", "[grow]");
		this.setLayout(layout);
		
		initRobotContainer();
		initWidgetContainer();
	}
	
	private void initWidgetContainer(){
		JPanel widgetContainer = new JPanel();
		this.add(widgetContainer);
	}
	
	private void initRobotContainer(){
		robotContainer = new JPanel();
		this.add(robotContainer, "span 2");
		
		for(Robot robot : world.getAlly().getRobots()){
			RobotBox box = new RobotBox(robot);
			robotboxes.add(box);
			robotContainer.add(box);
		}
		this.add(robotContainer);
	}	

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0.equals(world)) {
			//update all robot items
			for(Component item : robotContainer.getComponents()){
				if(item instanceof RobotBox)
					((RobotBox)item).update();
			}
		}
		
	}
}
