package robocup.migView;

import javax.swing.*;

/**
 * Abstract class for Widgets 
 */
public abstract class WidgetBox extends JPanel  {
	private static final long serialVersionUID = 1L;

	/**
	 * Create a widgetBox
	 * @param title 	the title of the widget 
	 */
	public WidgetBox(String title){
		this.setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * Will be called when SSL_Referee or SSL_DetectionRobot is handled
	 */
	public abstract void update();
}
