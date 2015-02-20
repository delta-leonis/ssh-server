package robocup.migView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import robocup.model.World;

@SuppressWarnings("serial")
public class SettingsWidget extends WidgetBox {
	private JComboBox<String> fieldHalfBox;

	public SettingsWidget(){
		super("Settings");
		setLayout(new MigLayout("wrap 2", "[][grow]"));
		
		fieldHalfBox = new JComboBox<String>();
		fieldHalfBox.setEditable(false);
		fieldHalfBox.addItem("left");
		fieldHalfBox.addItem("right");

		JButton setButton = new JButton("Set");
		setButton.addActionListener(new ButtonListener());

		add(new JLabel("Field half"));
		add(fieldHalfBox);
		add(setButton, "span 2");
	}
	

	
    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
        	switch(((JButton)e.getSource()).getText()){
        		case "Set":
        			if(fieldHalfBox.getSelectedItem().equals("right"))
        				World.getInstance().getReferee().setRightTeamByColor(World.getInstance().getReferee().getAlly().getColor());
    				else
        				World.getInstance().getReferee().setRightTeamByColor(World.getInstance().getReferee().getEnemy().getColor());
        	}
        }
    }
	@Override
	public void update() {
		fieldHalfBox.setSelectedItem("right");
		if(World.getInstance().getReferee().getDoesTeamPlaysLeft(World.getInstance().getReferee().getAlly().getColor()))
			fieldHalfBox.setSelectedItem("left");
		
	}

}
