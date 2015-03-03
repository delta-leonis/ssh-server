package robocup.migView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

@SuppressWarnings("serial")
public class SettingsWidget extends WidgetBox {
	private JComboBox<String> fieldHalfBox,
							  frequencyBox;

	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	/**
	 * Creates the settingsWidget
	 */
	public SettingsWidget(){
		super("Settings");
		setLayout(new MigLayout("wrap 2", "[][grow]"));
		
		fieldHalfBox = new JComboBox<String>();
		fieldHalfBox.setEditable(false);
		fieldHalfBox.addItem("left");
		fieldHalfBox.addItem("right");

		JButton setHalfButton = new JButton("Set fieldhalf");
		setHalfButton.addActionListener(new ButtonListener());

		frequencyBox = new JComboBox<String>();
		frequencyBox.setEditable(false);
		frequencyBox.addItem("1. 2436");
		frequencyBox.addItem("2. 2450");
		frequencyBox.addItem("3. 2490");
		frequencyBox.addItem("4. 2500");
		frequencyBox.addItem("5. 2525");
		
		JButton setFrequencyButton = new JButton("Set frequency");
		setFrequencyButton.addActionListener(new ButtonListener());

		add(new JLabel("Field half"));
		add(fieldHalfBox);
		add(setHalfButton, "span 2");
		add(new JLabel("Field frequency"));
		add(frequencyBox);
		add(setFrequencyButton, "span 2");
	}
	

	/**
	 * ActionListener to set a different settings
	 *
	 */
    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
        	switch(((JButton)e.getSource()).getText()){
        		case "Set frequency":
        			int frequency = Integer.valueOf(((String)frequencyBox.getSelectedItem()).substring(3));
        			LOGGER.info("Basestation frequency set to " + frequency);
    				ComInterface.getInstance(RobotCom.class).send(127, frequency);
        			break;

        		case "Set fieldhalf":
        			if(fieldHalfBox.getSelectedItem().equals("right"))
        				World.getInstance().getReferee().setRightTeamByColor(World.getInstance().getReferee().getAlly().getColor());
    				else
        				World.getInstance().getReferee().setRightTeamByColor(World.getInstance().getReferee().getEnemy().getColor());
        			World.getInstance().getGUI().update("widgetContainer");
        			break;
        	}
        }
    }
    
    /**
     * When a SSL_DetectionFrame message is handled, the field half may be changed
     */
	@Override
	public void update() {
		if(World.getInstance().getReferee().getDoesTeamPlaysLeft(World.getInstance().getReferee().getAlly().getColor()))
			fieldHalfBox.setSelectedItem("left");
		else
			fieldHalfBox.setSelectedItem("right");
		
	}

}
