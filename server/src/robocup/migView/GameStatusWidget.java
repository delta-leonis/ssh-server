package robocup.migView;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import robocup.model.World;

/**
 * TODO check whether it works at all,
 * way of updating has been copied from the previous GUI but well, 
 * that wasn't exactly perfect either
 */
public class GameStatusWidget extends WidgetBox {
	private static final long serialVersionUID = 1L;

	private JTextField fieldHalfField
					  ,timePlayedField
					  ,gameStatusField
					  ,refereeStatusField
					  ,goalsField
				 	  ,keeperIdField;

	/**
	 * Creates widget 
	 */
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
	
	/**
	 * Updates the fields
	 */
	@Override
	public void update(){
		if (World.getInstance().getReferee().getStagetimeLeft() == 0) {
			timePlayedField.setText("0:00");
		} else {
			int timePlayed = 600000000 - World.getInstance().getReferee().getStagetimeLeft();
			timePlayedField.setText("" + java.util.concurrent.TimeUnit.MICROSECONDS.toMinutes(timePlayed) % 60
					+ ":" + java.util.concurrent.TimeUnit.MICROSECONDS.toSeconds(timePlayed) % 60);
		}

		if(World.getInstance().getReferee().getDoesTeamPlaysLeft(World.getInstance().getReferee().getAlly().getColor()))
			fieldHalfField.setText("Left");
		else
			fieldHalfField.setText("Right");
		
		//TODO ROB CHECK DEZE
		gameStatusField.setText("NOT YET IMPLEMENTED");
		
		refereeStatusField.setText(World.getInstance().getReferee().getStage().toString());
		keeperIdField.setText(World.getInstance().getReferee().getAlly().getGoalie() + "");
		goalsField.setText(World.getInstance().getReferee().getAlly().getScore() + "");
		
	}


}
