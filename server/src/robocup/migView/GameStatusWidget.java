package robocup.migView;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import robocup.model.World;
import net.miginfocom.swing.MigLayout;

public class GameStatusWidget extends WidgetBox {
	private static final long serialVersionUID = 1L;

	private JTextField fieldHalfField
					  ,timePlayedField
					  ,gameStatusField
					  ,refereeStatusField
					  ,goalsField
				 	  ,keeperIdField;

	public GameStatusWidget() {
		super("Game Status");
		
		setLayout(new MigLayout("wrap 2", "[]related[grow]"));

		keeperIdField = new JTextField();
		fieldHalfField = new JTextField();
		timePlayedField = new JTextField();
		gameStatusField = new JTextField();
		refereeStatusField = new JTextField();
		goalsField = new JTextField();
		

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
	
	@Override
	public void update(){
		if (World.getInstance().getReferee().getStagetimeLeft() == 0) {
			timePlayedField.setText("0:00");
		} else {
			int timePlayed = 600000000 - World.getInstance().getReferee().getStagetimeLeft();
			timePlayedField.setText("" + java.util.concurrent.TimeUnit.MICROSECONDS.toMinutes(timePlayed) % 60
					+ ":" + java.util.concurrent.TimeUnit.MICROSECONDS.toSeconds(timePlayed) % 60);
		}
		goalsField.setText("" + World.getInstance().getAlly().getScore());


		fieldHalfField.setText("NOT YET IMPLEMENTED");
		gameStatusField.setText("NOT YET IMPLEMENTED");
		refereeStatusField.setText("NOT YET IMPLEMENTED");
		keeperIdField.setText("NOT YET IMPLEMENTED");
		
	}


}
