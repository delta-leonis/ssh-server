package robocup.migView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class ControlRobotWidget extends WidgetBox {

	private static final long serialVersionUID = 1L;
	private JLabel selectedRobotLabel;
	private int selectedRobotId;
	
	public ControlRobotWidget(){
		super("Control robot");
		
		setLayout(new MigLayout("wrap 2", "[grow][grow]"));
		selectedRobotLabel = new JLabel("Selecteer een robot");
		add(selectedRobotLabel, "span");

		JButton chipButton = new JButton("Chippen");
		JButton kickButton = new JButton("Kicken");
		chipButton.addActionListener(new ButtonListener());
		kickButton.addActionListener(new ButtonListener());

		add(chipButton, "growx");
		add(kickButton, "growx");
		
	}
	
    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
        	switch(((JButton)e.getSource()).getText()){
    			case "Chippen":
    				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0, 100, false);
    				break;

    			case "Kicken":
    				ComInterface.getInstance(RobotCom.class).send(1, selectedRobotId, 0, 0, 0, 0, 0, -100, false);
    				break;
        	}
        }
    }
	
	@Override
	public void update() {
		selectedRobotId = ((GUI) SwingUtilities.getWindowAncestor((this))).getSelectedRobotId();
		selectedRobotLabel.setText(String.format("Robot #%d geselecteerd", selectedRobotId));
	}

}
