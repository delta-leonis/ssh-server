package robocup.migView;

import javax.swing.*;

public class WidgetBox extends JPanel {
	private static final long serialVersionUID = 1L;

	public WidgetBox(String title){
		this.setBorder(BorderFactory.createTitledBorder(title));
	}
}
