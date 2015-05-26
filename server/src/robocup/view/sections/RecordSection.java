package robocup.view.sections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;






import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import robocup.input.ProtoParser;
import robocup.model.ProtoLog;
import robocup.model.World;
import robocup.model.enums.LogState;
import robocup.view.SectionBox;

@SuppressWarnings("serial")
public class RecordSection extends SectionBox {
	private ProtoLog logFile = World.getInstance().getProtoLog();
	private JSlider cursorSlider;
	private JFileChooser chooser = new JFileChooser();
	FileNameExtensionFilter filter = new FileNameExtensionFilter("log files", "protobuf", "protolog");
	private Timer playbackTimer;

	private JButton homeButton, prevButton, recButton, stopRecButton, nextButton, endButton, playPauseButton, stopButton, openFileButton;
	private ImageIcon homeIcon = new ImageIcon(getClass().getResource("../buttonImages/home.png")),
						endIcon = new ImageIcon(getClass().getResource("../buttonImages/end.png")),
						prevIcon = new ImageIcon(getClass().getResource("../buttonImages/previous.png")),
						nextIcon = new ImageIcon(getClass().getResource("../buttonImages/next.png")),
						playIcon = new ImageIcon(getClass().getResource("../buttonImages/play.png")),
						pauseIcon = new ImageIcon(getClass().getResource("../buttonImages/pause.png")),
						stopIcon = new ImageIcon(getClass().getResource("../buttonImages/stop.png")),
						recIcon = new ImageIcon(getClass().getResource("../buttonImages/record-red.png")),
						openIcon = new ImageIcon(getClass().getResource("../buttonImages/open.png"));
	public RecordSection() {
		super("Record protobuf");
		logFile.setState(LogState.READY);
		chooser.setFileFilter(filter);
		setLayout(new MigLayout("wrap 6", "[grow][grow][grow][grow][grow][grow]"));
		ButtonListener listener = new ButtonListener();

		add(new JLabel());
		add(new JLabel());
		
		stopRecButton = new JButton(stopIcon);
		stopRecButton.addActionListener(listener);
		stopRecButton.setName("stopRec");
		add(stopRecButton);
		
		recButton = new JButton(recIcon);
		recButton.addActionListener(listener);
		recButton.setName("record");
		add(recButton);

		openFileButton = new JButton(openIcon);
		openFileButton.addActionListener(listener);
		openFileButton.setName("openFile");
		add(openFileButton, "wrap");
		
		homeButton = new JButton(homeIcon);
		homeButton.addActionListener(listener);
		homeButton.setName("home");
		add(homeButton);

		prevButton = new JButton(prevIcon);
		prevButton.addActionListener(listener);
		prevButton.setName("previous");
		add(prevButton);

		playPauseButton = new JButton(playIcon);
		playPauseButton.addActionListener(listener);
		playPauseButton.setName("playPause");
		add(playPauseButton);

		stopButton = new JButton(stopIcon);
		stopButton.addActionListener(listener);
		stopButton.setName("stop");
		add(stopButton);

		nextButton = new JButton(nextIcon);
		nextButton.addActionListener(listener);
		nextButton.setName("next");
		add(nextButton);

		endButton = new JButton(endIcon);
		endButton.addActionListener(listener);
		endButton.setName("end");
		add(endButton);

		cursorSlider = new JSlider(JSlider.HORIZONTAL,
                0, 0, 0);
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
			if(logFile.getState() == LogState.PAUSE)
				ProtoParser.getInstance().parseVision(new ByteArrayInputStream(logFile.getData(logFile.getCursor())));
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String buttonText = ((JButton) e.getSource()).getName();
			switch (buttonText) {
				case "record":
					logFile.clear();
					logFile.setState(LogState.RECORDING);
					break;

				case "openFile":
					if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
						logFile.loadMessages(chooser.getSelectedFile());
					logFile.setState(LogState.READY);
					break;
					
				case "stopRec":
					if(logFile.getSize() > 0)
						if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
							logFile.saveToFile(chooser.getSelectedFile().toString().replace(".protolog", "") + ".protolog");
					logFile.setState(LogState.READY);
					break;

				case "stop":
					logFile.setState(LogState.READY);
					logFile.setCursor(0);
					break;
					
				case "playPause":
					//when it should pause playback
					if(logFile.getState() == LogState.PLAY){
						logFile.setState(LogState.PAUSE);
						return;
					}

					//when it should start playback
					if(logFile.getCursor() < logFile.getSize()-1){
						logFile.setState(LogState.PLAY);
						playbackTimer.schedule(new PlaybackTimerTask(), 0);
					}else
						logFile.setState(LogState.READY);

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
			if(logFile.getState() == LogState.PLAY){
				logFile.setCursor(logFile.getCursor() +1);
				if(logFile.getCursor() >= logFile.getSize()-1){
					logFile.setState(LogState.READY);
					logFile.setCursor(0);
				}
				playbackTimer.schedule(new PlaybackTimerTask(), Math.abs(logFile.getTimeDelta()));
			}
		}
	}

	@Override
	public void update() {
		LogState state = logFile.getState();
		
		logFile.setCursor(Math.min(logFile.getCursor(), logFile.getSize()-1));
		logFile.setCursor(Math.max(logFile.getCursor(), 0));
		homeButton.setEnabled(state != LogState.RECORDING && state != LogState.PLAY && (logFile.getCursor() > 0));
		prevButton.setEnabled(state != LogState.RECORDING && state != LogState.PLAY && (logFile.getCursor() > 0));
		endButton.setEnabled (state != LogState.RECORDING && state != LogState.PLAY && (logFile.getSize()-1 > logFile.getCursor()));
		nextButton.setEnabled(state != LogState.RECORDING && state != LogState.PLAY && (logFile.getSize()-1 > logFile.getCursor()));
		recButton.setEnabled(state != LogState.RECORDING && state != LogState.PLAY && state != LogState.PAUSE);
		openFileButton.setEnabled(state != LogState.RECORDING);
		stopRecButton.setEnabled(state == LogState.RECORDING && state != LogState.PLAY);
		playPauseButton.setEnabled(state != LogState.RECORDING);
		playPauseButton.setIcon(state == LogState.PLAY ? pauseIcon : playIcon);
		stopButton.setEnabled(state != LogState.RECORDING && (state == LogState.PLAY || state == LogState.PAUSE));
		cursorSlider.setMaximum(logFile.getSize()-1);
		cursorSlider.setValue(logFile.getCursor());
		if(state != LogState.RECORDING){
			cursorSlider.show(true);
			Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			int part = (logFile.getSize()-1)/10;
			for(int i=0; i <= 10; i++)
				labelTable.put( part * i , new JLabel("" + part* i ));
			cursorSlider.setLabelTable( labelTable );
			cursorSlider.setMajorTickSpacing((logFile.getSize()-1)/10);
			cursorSlider.setMinorTickSpacing((logFile.getSize()-1)/100);
		}else
			cursorSlider.show(false);
		cursorSlider.setEnabled(state != LogState.RECORDING);
	}

}
