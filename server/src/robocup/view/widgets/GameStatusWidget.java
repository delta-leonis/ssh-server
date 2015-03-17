package robocup.view.widgets;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import robocup.model.World;
import robocup.view.WidgetBox;

/**
 * TODO check whether it works at all,
 * way of updating has been copied from the previous GUI but well, 
 * that wasn't exactly perfect either
 */
@SuppressWarnings("serial")
public class GameStatusWidget extends WidgetBox {

	private JTextField fieldHalfField, timePlayedField, gameStatusField, refereeStatusField, goalsField, keeperIdField;

	/**
	 * Creates widget
	 */
	public GameStatusWidget() {
		super("Game Status");
		setLayout(new MigLayout("wrap 2", "[]related[grow]"));

		keeperIdField = new JTextField();
		keeperIdField.setEnabled(false);
		fieldHalfField = new JTextField();
		fieldHalfField.setEnabled(false);
		timePlayedField = new JTextField();
		timePlayedField.setEnabled(false);
		gameStatusField = new JTextField();
		gameStatusField.setEnabled(false);
		refereeStatusField = new JTextField();
		refereeStatusField.setEnabled(false);
		goalsField = new JTextField();
		goalsField.setEnabled(false);

		add(new JLabel("Field half"));
		add(fieldHalfField, "growx");
		add(new JLabel("Time played"));
		add(timePlayedField, "growx");
		add(new JLabel("Game status"));
		add(gameStatusField, "growx");
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

		if (World.getInstance().getReferee()
				.getDoesTeamPlaysEast(World.getInstance().getReferee().getAlly().getColor()))
			fieldHalfField.setText("Left");
		else
			fieldHalfField.setText("Right");

		// TODO Rob moet hier even aan verder werken, Jeroen wist t niet meer
		gameStatusField.setText("NOT YET IMPLEMENTED");

		refereeStatusField.setText(World.getInstance().getReferee().getStage().toString());
		keeperIdField.setText(World.getInstance().getReferee().getAlly().getGoalie() + "");
		goalsField.setText(World.getInstance().getReferee().getAlly().getScore() + "");

	}

}
