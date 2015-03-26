package robocup.view.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;
import robocup.view.WidgetBox;

@SuppressWarnings("serial")
public class SettingsWidget extends WidgetBox {

	private JComboBox<String> fieldHalfBox, frequencyBox;
	private JComboBox<Level> levelBox;
	// Base station frequencies
	private static final int[] frequencies = { 2436, 2450, 2490, 2500, 2525 };
	private Logger LOGGER = Logger.getLogger(Main.class.getName());

	/**
	 * Creates the settingsWidget
	 */
	public SettingsWidget() {
		super("Settings");
		setLayout(new MigLayout("wrap 2", "[][grow]"));

		fieldHalfBox = new JComboBox<String>();
		fieldHalfBox.setEditable(false);
		fieldHalfBox.addItem("left");
		fieldHalfBox.addItem("right");
		
		levelBox = new JComboBox<Level>();
		levelBox.addItem(Level.ALL);
		levelBox.addItem(Level.FINEST);
		levelBox.addItem(Level.FINER);
		levelBox.addItem(Level.FINE);
		levelBox.addItem(Level.INFO);
		levelBox.addItem(Level.CONFIG);
		levelBox.addItem(Level.WARNING);
		levelBox.addItem(Level.SEVERE);
		levelBox.addItem(Level.OFF);
		levelBox.setSelectedItem(LOGGER.getLevel());

		JButton setHalfButton = new JButton("Set fieldhalf");
		setHalfButton.addActionListener(new ButtonListener());

		frequencyBox = new JComboBox<String>();
		frequencyBox.setEditable(false);
		for (int x = 0; x < frequencies.length; x++)
			frequencyBox.addItem(x + 1 + ". " + frequencies[x]);

		JButton setFrequencyButton = new JButton("Set frequency");
		setFrequencyButton.addActionListener(new ButtonListener());

		JButton setLevelButton = new JButton("Set logger level");
		setLevelButton.addActionListener(new ButtonListener());
		add(new JLabel("Field half"));
		add(fieldHalfBox);
		add(setHalfButton, "span 2");
		add(new JLabel("Field frequency"));
		add(frequencyBox);
		add(setFrequencyButton, "span 2");
		add(levelBox);
		add(setLevelButton, "span 2");
	}

	/**
	 * ActionListener to set a different settings
	 *
	 */
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (((JButton) e.getSource()).getText()) {
			case "Set frequency":
				int frequency = Integer.valueOf(((String) frequencyBox.getSelectedItem()).substring(3));
				LOGGER.info("Basestation frequency set to " + frequency);
				ComInterface.getInstance(RobotCom.class).send(127, frequency);
				break;

			case "Terminate":
			{
				LOGGER.info("Terminate command send to all robots");
				ComInterface.getInstance(RobotCom.class).send(1, 0, 0, 0, 0, 0, 0,0, false);
				break;
			}
			
			case "Set logger level":
			{
				LOGGER.setLevel((Level)levelBox.getSelectedItem());
				LOGGER.config("Logger level set to " +(Level)levelBox.getSelectedItem());
				break;
			}
			
			case "Set fieldhalf":
				if (fieldHalfBox.getSelectedItem().equals("right"))
					World.getInstance().getReferee()
							.setRightTeamByColor(World.getInstance().getReferee().getAlly().getColor());
				else
					World.getInstance().getReferee()
							.setRightTeamByColor(World.getInstance().getReferee().getEnemy().getColor());
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
		if (World.getInstance().getReferee()
				.getDoesTeamPlaysEast(World.getInstance().getReferee().getAlly().getColor()))
			fieldHalfBox.setSelectedItem("left");
		else
			fieldHalfBox.setSelectedItem("right");
	}
}
