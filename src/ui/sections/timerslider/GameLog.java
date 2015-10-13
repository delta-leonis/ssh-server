package ui.sections.timerslider;

import java.util.ArrayList;

/**
 * Dummy class for the game logs
 */
public class GameLog {
	private String filename;
	private int length;
	private ArrayList<Integer> goalTimes;
	private ArrayList<Integer> timeouts;
	
	/**
	 * Constructor of the GameLog
	 * @param filename Name of the file
	 * @param length Length in seconds
	 */
	public GameLog(String filename, int length){
		this.filename = filename;
		this.length = length;
		goalTimes = new ArrayList<Integer>();
		goalTimes.add(2);
		goalTimes.add(10);
		goalTimes.add(4);
		goalTimes.add(3);
		goalTimes.add(18);
		timeouts = new ArrayList<Integer>();
		timeouts.add(130);
		timeouts.add(50);

	}
	
	public String getFilename(){
		return filename;
	}
	
	public ArrayList<Integer> getGoalTimes(){
		return goalTimes;
	}
	
	public ArrayList<Integer> getTimeouts(){
		return timeouts;
	}
	
	public int getTimeSeconds(){
		return length;
	}
	
	public String getLength(){
		int hours = length / 3600;
		int minutes = (length - hours * 3600) / 60;
		int seconds = length % 60;
		return hours + ":" + minutes + ":" + seconds;
	}
}
