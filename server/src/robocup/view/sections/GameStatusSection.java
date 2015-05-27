package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.view.SectionBox;

/**
 * Displays general information about the game and {@link Field}
 */
@SuppressWarnings("serial")
public class GameStatusSection extends SectionBox implements ActionListener{

	private JTextField fieldHalfField, timePlayedField, refereeStatusField, refereeCommandField, gameStatusField, goalsField, keeperIdField;
	private JButton startButton;
	private JButton stopButton;

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
		refereeCommandField = new JTextField();
		refereeCommandField.setEnabled(false);
		gameStatusField = new JTextField();
		gameStatusField.setEnabled(false);
		goalsField = new JTextField();
		goalsField.setEnabled(false);
		
		startButton = new JButton("Start");
		stopButton = new JButton("Stop");
		
		startButton.addActionListener(this);
		stopButton.addActionListener(this);

		add(new JLabel("Field half"));
		add(fieldHalfField, "growx");
		add(new JLabel("Time played"));
		add(timePlayedField, "growx");
		add(new JLabel("Game state"));
		add(refereeStatusField, "growx");
		add(new JLabel("Referee command"));
		add(refereeCommandField, "growx");
		add(new JLabel("Game Status"));
		add(gameStatusField, "growx");
		add(new JLabel("Goals"));
		add(goalsField, "growx");
		add(new JLabel("Keeper id"));
		add(keeperIdField, "growx");
		add(startButton, "growx");
		add(stopButton, "growx");
		
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

		refereeCommandField.setText(World.getInstance().getReferee().getCommand().toString());
		refereeStatusField.setText(World.getInstance().getReferee().getStage().toString());
		gameStatusField.setText(World.getInstance().getGameState().toString());
		keeperIdField.setText(World.getInstance().getReferee().getAlly().getGoalie() + "");
		goalsField.setText(World.getInstance().getReferee().getAlly().getScore() + "");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == startButton){
			World.getInstance().start();
		}
		if(e.getSource() == stopButton){
			World.getInstance().stop();
			for(int i = 0; i < 11; ++i){
				ComInterface.getInstance().send(1, i,0, 0, 0, 0, false);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

}
