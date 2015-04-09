package robocup.view;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Abstract class for sections 
 */
@SuppressWarnings("serial")
public abstract class SectionBox extends JPanel {

	/**
	 * Create a sectionBox
	 * @param title 	the title of the sections 
	 */
	public SectionBox(String title) {
		this.setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * Will be called when SSL_Referee or SSL_DetectionRobot is handled
	 */
	public abstract void update();
}
