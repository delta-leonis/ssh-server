package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import robocup.model.ProtoLog;
import robocup.view.SectionBox;

@SuppressWarnings("serial")
public class RecordSection extends SectionBox {
	private boolean recording;
	private boolean playing;
	private ProtoLog logFile = new ProtoLog();
	private JSlider cursorSlider;
	private JFileChooser chooser = new JFileChooser();
	private Timer playbackTimer;

	private JButton homeButton, prevButton, recButton, stopButton, nextButton, endButton, playButton, pauseButton;
	private ImageIcon homeIcon = new ImageIcon(getClass().getResource("../buttonImages/home.png")),
						endIcon = new ImageIcon(getClass().getResource("../buttonImages/end.png")),
						prevIcon = new ImageIcon(getClass().getResource("../buttonImages/previous.png")),
						nextIcon = new ImageIcon(getClass().getResource("../buttonImages/next.png")),
						playIcon = new ImageIcon(getClass().getResource("../buttonImages/play.png")),
						pauseIcon = new ImageIcon(getClass().getResource("../buttonImages/pause.png")),
						stopIcon = new ImageIcon(getClass().getResource("../buttonImages/stop.png")),
						recIcon = new ImageIcon(getClass().getResource("../buttonImages/record-red.png"));
	public RecordSection() {
		super("Record protobuf");
		recording = false;
		playing = false;
		logFile.genRandomData(430);
		setLayout(new MigLayout("wrap 6", "[grow][grow][grow][grow][grow][grow]"));
		ButtonListener listener = new ButtonListener();

		add(new JLabel());
		add(new JLabel());
		stopButton = new JButton(stopIcon);
		stopButton.addActionListener(listener);
		stopButton.setName("stop");
		add(stopButton);
		
		recButton = new JButton(recIcon);
		recButton.addActionListener(listener);
		recButton.setName("record");
		add(recButton, "wrap");
		
		homeButton = new JButton(homeIcon);
		homeButton.addActionListener(listener);
		homeButton.setName("home");
		add(homeButton);

		prevButton = new JButton(prevIcon);
		prevButton.addActionListener(listener);
		prevButton.setName("previous");
		add(prevButton);

		playButton = new JButton(playIcon);
		playButton.addActionListener(listener);
		playButton.setName("play");
		add(playButton);

		pauseButton = new JButton(pauseIcon);
		pauseButton.addActionListener(listener);
		pauseButton.setName("pause");
		add(pauseButton);

		nextButton = new JButton(nextIcon);
		nextButton.addActionListener(listener);
		nextButton.setName("next");
		add(nextButton);

		endButton = new JButton(endIcon);
		endButton.addActionListener(listener);
		endButton.setName("end");
		add(endButton);

		cursorSlider = new JSlider(JSlider.HORIZONTAL,
                0,logFile.getSize()-1, logFile.getCursor());
		cursorSlider.addChangeListener(new SliderListener());

		//Turn on labels at major tick marks.
		cursorSlider.setMajorTickSpacing(10);
		cursorSlider.setMinorTickSpacing(1);
		cursorSlider.setPaintTicks(true);
		cursorSlider.setPaintLabels(true);
		
		add(cursorSlider, "span 6, growx");
		
		playbackTimer = new Timer();
	}

	/**
	 * ChangeListener for slider in GUI
	 */
	private class SliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			logFile.setCursor(((JSlider)e.getSource()).getValue());
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String buttonText = ((JButton) e.getSource()).getName();
			switch (buttonText) {
				case "record":
					logFile = new ProtoLog();
					recording = true;
					playing = false;
					break;

				case "stop":
					playing = false;
					recording = false;
					if(logFile.getSize() > 0)
						if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
							logFile.saveToFile(chooser.getSelectedFile() + ".txt");
					break;

				case "play":
					recording = false;
					playing = true;
					playbackTimer.schedule(new PlaybackTimerTask(), 0);
					break;

				case "pause":
					playing = false;
					break;

				case "end":
					logFile.setCursor(logFile.getSize()-1);
					break;

				case "home":
					logFile.setCursor(0);
					break;

				case "next":
					logFile.setCursor(logFile.getCursor()+1);
					break;

				case "previous":
					logFile.setCursor(logFile.getCursor()-1);
					break;
			}
		}
	}


	/**
	 * TimerTask specifically for updating all GUI elements at a set frequency
	 */
	class PlaybackTimerTask extends TimerTask {
		public void run() {
			if(playing){
				logFile.setCursor(logFile.getCursor() +1);
				playbackTimer.schedule(new PlaybackTimerTask(), logFile.getTimeDelta());
				if(logFile.getCursor() >= logFile.getSize()-1)
					playing = false;
			}
		}
	}

	@Override
	public void update() {
		homeButton.setEnabled(!recording && !playing && (logFile.getCursor() > 0));
		prevButton.setEnabled(!recording && !playing && (logFile.getCursor() > 0));
		endButton.setEnabled (!recording && !playing && (logFile.getSize()-1 > logFile.getCursor()));
		nextButton.setEnabled(!recording && !playing && (logFile.getSize()-1 > logFile.getCursor()));
		recButton.setEnabled(!recording && !playing);
		stopButton.setEnabled(recording && !playing);
		playButton.setEnabled(!recording && !playing);
		pauseButton.setEnabled(!recording && playing);
		cursorSlider.setMaximum(logFile.getSize()-1);
		cursorSlider.setValue(logFile.getCursor());
	}

}
