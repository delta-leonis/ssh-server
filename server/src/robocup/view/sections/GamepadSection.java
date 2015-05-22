package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import robocup.Main;
import robocup.gamepad.Gamepad;
import robocup.view.SectionBox;

@SuppressWarnings("serial")
public class GamepadSection extends SectionBox {

	private Logger LOGGER = Logger.getLogger(Main.class.getName());

	public GamepadSection() {
		super("Gamepad Section");

		add(new JButton(new AbstractAction("hoest gamepadspul uit") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				new Gamepad();
			}
		}));
	}

	@Override
	public void update() {
		
	}

}
