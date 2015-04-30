package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import robocup.Main;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.view.SectionBox;

/**
 * Section that controls general settings concerning the current game,<br>
 * such as {@link Field} settings and general {@link Robot} settings
 */
@SuppressWarnings("serial")
public class SettingsSection extends SectionBox {

	private JComboBox<String> fieldHalfBox, frequencyBox;
	private JComboBox<Level> levelBox;
	// Base station frequencies
	private static final int[] frequencies = { 2436, 2450, 2490, 2500, 2525 };
	private Logger LOGGER = Logger.getLogger(Main.class.getName());

	/**
	 * Creates the components for the GUI
	 */
	public SettingsSection() {
		super("Settings");
		setLayout(new MigLayout("wrap 2", "[][grow]"));

		fieldHalfBox = new JComboBox<String>();
		fieldHalfBox.setEditable(false);
		fieldHalfBox.addItem("east");
		fieldHalfBox.addItem("west");

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
			frequencyBox.addItem(x + ". " + frequencies[x]);

		JButton setFrequencyButton = new JButton("Set frequency");
		setFrequencyButton.addActionListener(new ButtonListener());

		JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,
                0, 60, 30);
		framesPerSecond.addChangeListener(new SliderListener());

		//Turn on labels at major tick marks.
		framesPerSecond.setMajorTickSpacing(10);
		framesPerSecond.setMinorTickSpacing(1);
		framesPerSecond.setPaintTicks(true);
		framesPerSecond.setPaintLabels(true);
		
		JButton setLevelButton = new JButton("Set logger level");
		setLevelButton.addActionListener(new ButtonListener());
		add(new JLabel("Field half"));
		add(fieldHalfBox);
		add(setHalfButton, "span 2, growx");
		add(new JLabel("Field frequency"));
		add(frequencyBox);
		add(setFrequencyButton, "span 2, growx");
		add(new JLabel("Minimum logger level"));
		add(levelBox);
		add(setLevelButton, "span 2");
		add(new JLabel("GUI Updates per second"), "span 2, growx");
		add(framesPerSecond, "span 2, growx");

		if (World.getInstance().getReferee()
					.isEastTeamColor(World.getInstance().getReferee().getAlly().getColor()))
			fieldHalfBox.setSelectedItem("east");
		else
			fieldHalfBox.setSelectedItem("west");
	}


	/**
	 * ChangeListener for update-frequency slider in GUI
	 */
	private class SliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			int newFrequency = Math.max(((JSlider)e.getSource()).getValue(), 1); //minimum of 1 update a second
			World.getInstance().getGUI().setUpdateFrequency(newFrequency);
		}
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
				ComInterface.getInstance().send(127, frequency);
				break;

			case "Terminate":
			{
				LOGGER.info("Terminate command send to all robots");
				ComInterface.getInstance().send(1, 0, 0, 0, 0, 0, false);
				break;
			}
			
			case "Set logger level":
			{
				LOGGER.setLevel((Level)levelBox.getSelectedItem());
				LOGGER.config("Logger level set to " +(Level)levelBox.getSelectedItem());
				break;
			}
			
			case "Set fieldhalf":
				if (fieldHalfBox.getSelectedItem().equals("west"))
					World.getInstance().getReferee()
							.setWestTeam(World.getInstance().getReferee().getAlly());
				else
					World.getInstance().getReferee()
							.setWestTeam(World.getInstance().getReferee().getEnemy());
				break;
			}
		}
	}

	/**
	 * When a {@code SSL_DetectionFrame} message is handled the field half may be changed
	 */
	@Override
	public void update() {
	}
}