package robocup.migView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import net.miginfocom.swing.MigLayout;

public class SettingsWidget extends WidgetBox {
	private JComboBox fieldHalfBox;

	public SettingsWidget(){
		super("Settings");
		setLayout(new MigLayout("wrap 2", "[][grow]"));
		
		fieldHalfBox = new JComboBox();
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
        			throw new NotImplementedException();
        	}
        }
    }
	@Override
	public void update() {
		fieldHalfBox.setSelectedItem("left"); //TODO get info from model
	}

}
