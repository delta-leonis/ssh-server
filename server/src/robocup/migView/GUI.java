package robocup.migView;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.model.Robot;
import robocup.model.World;

public class GUI extends JFrame  {
	private static final long serialVersionUID = 1L;

	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private JPanel robotContainer;
	private JPanel widgetContainer;
	private ConsoleWidget console;
	private int selectedRobotId;
	private ArrayList<RobotBox> allRobotBoxes = new ArrayList<RobotBox>();

	public GUI(){
		LOGGER.info("GUI started");

		setLookAndFeel();

		getContentPane().setBackground(UIManager.getColor("Panel.background"));
		MigLayout layout = new MigLayout("wrap 2", "[500][grow]", "[][grow]");
		this.setLayout(layout);
		this.setSize(800, 830);
		
		initRobotContainer();
		initWidgetContainer();
		initConsoleContainer();
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}
	
	private void setLookAndFeel(){
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    System.err.println("Aww :( no nimbus");
		}
	}

	public int getSelectedRobotId() {
		return selectedRobotId;
	}

	private void initConsoleContainer(){
		console = new ConsoleWidget();
		add(console, "span, growy, growx");
	}
	
	private void initWidgetContainer(){
		widgetContainer = new JPanel();
		widgetContainer.setBorder(BorderFactory.createTitledBorder("Widgets"));
		widgetContainer.setLayout(new MigLayout("wrap 1", "[grow]"));

		widgetContainer.add(new GameStatusWidget(), "growx");
		widgetContainer.add(new VisibleRobots(), "growx");
		widgetContainer.add(new ControlRobotWidget(), "growx");
		widgetContainer.add(new SettingsWidget(), "growx");
		widgetContainer.add(new PenguinWidget(), "growx, growy");
		
		this.add(widgetContainer, "growy, growx");
	}
	
	private void initRobotContainer(){
		robotContainer = new JPanel();
		robotContainer.setLayout(new MigLayout("wrap 2", "[250]related[250]"));
		robotContainer.setBorder(BorderFactory.createTitledBorder("Robots"));

		for(Robot robot : World.getInstance().getReferee().getAlly().getRobots()){
			RobotBox box = new RobotBox(robot);
			box.addMouseListener(new PanelClickListener());
			if(robot.isVisible())
				robotContainer.add(box);
			allRobotBoxes.add(box);
		}
		robotContainer.add(new JPanel(), "growy, span 2");
		this.add(robotContainer, "growy");
	}	


	private class PanelClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {

			for(Component item : robotContainer.getComponents()){
				if(item instanceof RobotBox){
					if( ((RobotBox)item).equals(((RobotBox)arg0.getSource())))
						((RobotBox)item).setBackground(Color.LIGHT_GRAY);
					else
						((RobotBox)item).setBackground(UIManager.getColor("Panel.background"));
				}
			}

			selectedRobotId = ((RobotBox)arg0.getSource()).getRobot().getRobotId();
			
			
			for(Component item : widgetContainer.getComponents())
				if(item instanceof ControlRobotWidget)
					((ControlRobotWidget)item).update();

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void update(String desc) {
		switch(desc)
		{
			case "robotContainer":
				//update all robot items
				for(RobotBox box : allRobotBoxes)
					box.update();
				break;

			case "robotBoxes":
				robotContainer.removeAll();
				for(RobotBox box : allRobotBoxes){
					box.repaint();
					if(box.getRobot().isVisible())
						robotContainer.add(box);
				}
				revalidate();
				
				robotContainer.repaint();
				repaint();
				break;
			
			case "widgetContainer":
			{
				for(Component item : widgetContainer.getComponents()){
					if(item instanceof WidgetBox)
						((WidgetBox)item).update();
				}
				break;
			}
			
			case "test":
				JOptionPane.showMessageDialog(this, "A basic JOptionPane message dialog");
				break;
		}
	}

}
