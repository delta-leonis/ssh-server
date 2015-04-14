package robocup.view;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Abstract class for different sections for the {@link GUI}
 */
@SuppressWarnings("serial")
public abstract class SectionBox extends JPanel {

	/**
	 * Create a sectionBox
	 * @param title 	the title of the section
	 */
	public SectionBox(String title) {
		this.setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * Will be called when {@code SSL_Referee} or {@code SSL_DetectionRobot} is handled
	 */
	public abstract void update();
}
