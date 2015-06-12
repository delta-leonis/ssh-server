package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import robocup.controller.handlers.protohandlers.DetectionHandler;
import robocup.model.Ally;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;
import robocup.view.SectionBox;

/**
 * {@link SectionBox} for selecting wich  {@link Robot robots} can be updated by the {@link DetectionHandler}.<br>
 */
@SuppressWarnings("serial")
public class ValidRobotSection extends SectionBox {

	private ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
	private Timer autoselectTimer;
	private World world;

	/**
	 * Creates a {@link JCheckBox} for every {@link Ally} and 
	 * implements a {@code anonymous buttonhandler} for selecting {@link Ally}s that are currently recognized/visible
	 */
	public ValidRobotSection() {
		super("Valid robots");
		world = World.getInstance();
		setLayout(new MigLayout("wrap 6", "[][][][][][]"));

		int numberOfRobots = world.getReferee().getAlly().getRobots().size();

		add(new JLabel("Ally IDs"), "span 4, growx");
		

		add(new JButton(new AbstractAction("Autoselect") {
			/**
			 * Loops all existing {@link Ally}s, when {@link Robot} is not onsight it is 
			 * removed from view
			 */
			public void actionPerformed(ActionEvent e) {
				for(JCheckBox box : checkboxes){
					int id = Integer.valueOf(box.getText().substring(1));
					(box.getName().equals("ally") ? world.getValidAllyIDs() : world.getValidEnemyIDs()).add(id);
					box.setEnabled(false);
				}

				autoselectTimer = new Timer();
				autoselectTimer.schedule(new SelectTimer(), 500);
				
			}
		}), "span 2, growx, wrap");

		for (int i = 0; i < numberOfRobots; i++) {
			JCheckBox checkbox = new JCheckBox("#" + i);
			checkbox.setName("ally");
			checkbox.addActionListener(new checkHandler());
			//checkbox.setSelected(world.getValidAllyIDs().contains(i));
			checkboxes.add(checkbox);
			add(checkbox, (numberOfRobots - 1 == i) ? "wrap" : "");
		}
		add(new JLabel("Enemy IDs"), "span 6, growx, wrap");
		
		numberOfRobots = world.getReferee().getEnemy().getRobots().size();

		for (int i = 0; i < numberOfRobots; i++) {
			JCheckBox checkbox = new JCheckBox("#" + i);
			checkbox.setName("enemy");
			checkbox.addActionListener(new checkHandler());
			checkboxes.add(checkbox);
			add(checkbox, (numberOfRobots - 1 == i) ? "wrap" : "");
		}
	}

	class SelectTimer extends TimerTask {
		public void run() {
			for(JCheckBox box : checkboxes){
				//id uitzoeken
				int id = Integer.valueOf(box.getText().substring(1));
				//team uitzoeken
				Team team = (box.getName().equals("ally") ? world.getReferee().getAlly() : world.getReferee().getEnemy());
				//onsight setten op checkbox
				box.setSelected(team.getRobotByID(id).isOnSight());
				if(!box.isSelected()){
					//eventueel uit de valid reeks weghalen
					(box.getName().equals("ally") ? world.getValidAllyIDs() : world.getValidEnemyIDs()).remove((Integer)id);
					team.getRobotByID(id).setPosition(null);
				}

				//enabulun
				box.setEnabled(true);
			}
		}
	}

	/**
	 * Handler for the check boxes
	 */
	private class checkHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JCheckBox checkbox = ((JCheckBox) e.getSource());
			ArrayList<Integer> list = (checkbox.getName().equals("ally") ? world.getValidAllyIDs() : world.getValidEnemyIDs());
			Team team = (checkbox.getName().equals("ally") ? world.getReferee().getAlly() : world.getReferee().getEnemy());
			int id = Integer.valueOf(checkbox.getText().substring(1));
			
			if(checkbox.isSelected())
				list.add((Integer)id);
			else {
				list.remove((Integer)id);
				team.getRobotByID(id).setPosition(null);
				team.getRobotByID(id).setOnSight(false);
			}
				
		}
	}

	@Override
	public void update() {
		for(JCheckBox checkbox : checkboxes)
			checkbox.setSelected(world.isValidRobotId((checkbox.getName().equals("ally") ? world.getReferee().getAlly() : world.getReferee().getEnemy()).getRobotByID(Integer.valueOf(checkbox.getText().substring(1)))));
	}

}
