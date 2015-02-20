package robocup.migView;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import robocup.Main;

public class PenguinWidget extends WidgetBox {
	private Logger LOGGER = Logger.getLogger(Main.class.getName());	
	public PenguinWidget() {
		super("Penguin Widget");
		add(new JLabel(new ImageIcon(PenguinWidget.class.getResource("/robocup/migView/penguin.png"))));
		
		
		add(new JButton(new AbstractAction("New logger entry") {
		    public void actionPerformed(ActionEvent e) {
		    	double wowlevel = Math.random();

		    	LOGGER.setLevel(java.util.logging.Level.FINEST);
		    	if(wowlevel <= 0.33)
		    		LOGGER.fine("little wow :o");
		    	else if(wowlevel <= 0.66)
		    		LOGGER.info("Wow:o");
		    	else
		    		LOGGER.severe("extra wow:o");
		    }
		}));
	}

	@Override
	public void update() {
	}

}
