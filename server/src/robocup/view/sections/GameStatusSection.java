package robocup.view.sections;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import robocup.model.World;
import robocup.view.SectionBox;

/**
 * Displays general information about the game and {@link Field}
 */
@SuppressWarnings("serial")
public class GameStatusSection extends SectionBox {

	private JTextField fieldHalfField, timePlayedField, refereeStatusField, goalsField, keeperIdField;

	/**
	 * Creates section
	 */
	public GameStatusSection() {
		super("Game Status");
		setLayout(new MigLayout("wrap 2", "[]related[grow]"));

		keeperIdField = new JTextField();
		keeperIdField.setEnabled(false);
		fieldHalfField = new JTextField();
		fieldHalfField.setEnabled(false);
		timePlayedField = new JTextField();
		timePlayedField.setEnabled(false);
		refereeStatusField = new JTextField();
		refereeStatusField.setEnabled(false);
		goalsField = new JTextField();
		goalsField.setEnabled(false);

		add(new JLabel("Field half"));
		add(fieldHalfField, "growx");
		add(new JLabel("Time played"));
		add(timePlayedField, "growx");
		add(new JLabel("Referee status"));
		add(refereeStatusField, "growx");
		add(new JLabel("Goals"));
		add(goalsField, "growx");
		add(new JLabel("Keeper id"));
		add(keeperIdField, "growx");
		update();
	}

	/**
	 * Updates the fields
	 */
	@Override
	public void update() {
		if (World.getInstance().getReferee().getStagetimeLeft() == 0) {
			timePlayedField.setText("0:00");
		} else {
			int timePlayed = 600000000 - World.getInstance().getReferee().getStagetimeLeft();
			timePlayedField.setText("" + java.util.concurrent.TimeUnit.MICROSECONDS.toMinutes(timePlayed) % 60 + ":"
					+ java.util.concurrent.TimeUnit.MICROSECONDS.toSeconds(timePlayed) % 60);
		}

		if (World.getInstance().getReferee().isEastTeamColor(World.getInstance().getReferee().getAlly().getColor()))
			fieldHalfField.setText("east");
		else
			fieldHalfField.setText("west");

		refereeStatusField.setText(World.getInstance().getReferee().getStage().toString());
		keeperIdField.setText(World.getInstance().getReferee().getAlly().getGoalie() + "");
		goalsField.setText(World.getInstance().getReferee().getAlly().getScore() + "");

	}

}
