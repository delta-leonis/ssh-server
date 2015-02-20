package robocup.migView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import robocup.model.Robot;
import robocup.model.World;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

public class VisibleRobots extends WidgetBox {
	private static final long serialVersionUID = 1L;
	private ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
	
	public VisibleRobots() {
		super("Visible robots");
		setLayout(new MigLayout("wrap 6", "[][][][][][]"));

		int numberOfRobots =World.getInstance().getReferee().getAlly().getRobots().size();
		for(int i=0; i < numberOfRobots; i++){
			JCheckBox checkbox = new JCheckBox("#" + i);
			checkbox.addActionListener(new checkHandler());
			checkbox.setSelected(World.getInstance().getReferee().getAlly().getRobotByID(i).isVisible());
			checkboxes.add(checkbox);
			add(checkbox, (numberOfRobots-1 == i) ? "wrap" : "");
		}

		add(new JButton(new AbstractAction("Autoselect") {
		    public void actionPerformed(ActionEvent e) {
		    	int i =0;
		    	for(Robot robot : World.getInstance().getReferee().getAlly().getRobots()){
		    		robot.setVisible(robot.isOnSight());
					checkboxes.get(i).setSelected(robot.isVisible());
		    		i++;
		    	}
	    		World.getInstance().getGUI().update("robotBoxes");
		    }
		}), "span");
	}

    private class checkHandler implements ActionListener {
        public void actionPerformed(ActionEvent e){
        	JCheckBox checkbox = ((JCheckBox)e.getSource());
        	World.getInstance().getReferee().getAlly().getRobotByID(Integer.valueOf(checkbox.getText().substring(1))).setVisible(checkbox.isSelected());

    		World.getInstance().gui.update("robotBoxes");
        }
    }
	@Override
	public void update() {
	}

}
