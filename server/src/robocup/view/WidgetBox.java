package robocup.view;

import javax.swing.*;

/**
 * Abstract class for Widgets 
 */
@SuppressWarnings("serial")
public abstract class WidgetBox extends JPanel  {

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
