package model;

import java.util.ArrayList;

import model.enums.Color;

public class Team {

	private String name;
	private Color color;
	private String side;
	private int score;
	private int timeoutsLeft;
	private ArrayList<Long> remainingCardTimes;
	private int yellowCards;
	private int redCards;
	private ArrayList<Robot> robots;

	public Team(String name, Color color, String side) {
		this.name = name;
		this.color = color;
		this.side = side;
		remainingCardTimes = new ArrayList<Long>();
		robots = new ArrayList<Robot>();
	}

	public Team(String name, Color color, int yellowCards, int redCards, int score, int timeoutsLeft) {
		this.name = name;
		this.color = color;
		this.yellowCards = yellowCards;
		this.redCards = redCards;
		this.score = score;
		this.timeoutsLeft = timeoutsLeft;
		remainingCardTimes = new ArrayList<Long>();
		robots = new ArrayList<Robot>();
	}

	public void update(String name, int score, int redCards, int yellowCards, int timeoutsLeft) {
		this.name = name;
		this.score = score;
		this.redCards = redCards;
		this.yellowCards = yellowCards;
		this.timeoutsLeft = timeoutsLeft;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the teamColor
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param teamColor
	 *            the teamColor to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the timeoutsLeft
	 */
	public int getTimeoutsLeft() {
		return timeoutsLeft;
	}

	/**
	 * @param timeoutsLeft
	 *            the timeoutsLeft to set
	 */
	public void setTimeoutsLeft(int timeoutsLeft) {
		this.timeoutsLeft = timeoutsLeft;
	}

	/**
	 * @return the remainingCardTimes
	 */
	public ArrayList<Long> getRemainingCardTimes() {
		return remainingCardTimes;
	}

	/**
	 * @param remainingCardTimes
	 *            the remainingCardTimes to set
	 */
	public void setRemainingCardTimes(ArrayList<Long> remainingCardTimes) {
		this.remainingCardTimes = remainingCardTimes;
	}

	/**
	 * @return the yellowcards
	 */
	public int getYelloCcards() {
		return yellowCards;
	}

	/**
	 * @param yellowcards
	 *            the yellowcards to set
	 */
	public void setYellowCards(int yellowCards) {
		this.yellowCards = yellowCards;
	}

	/**
	 * @return the redcards
	 */
	public int getRedCards() {
		return redCards;
	}

	/**
	 * @param redcards
	 *            the redcards to set
	 */
	public void setRedCards(int redCards) {
		this.redCards = redCards;
	}

	/**
	 * @return the robots
	 */
	public ArrayList<Robot> getRobots() {
		return robots;
	}

	public void addRobot(Robot robot) {
		robots.add(robot);
	}

	public String getSide() {
		return side;
	}
	
	public void setSide(String side) {
		this.side = side;
	}

	public Robot getRobotByID(int id) {
		Robot r = null;
		for (int i = 0; i < robots.size(); i++) {
			if (robots.get(i).getRobotID() == id) {
				r = robots.get(i);
				break;
			}
		}

		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Team [name=" + name + ", color=" + color + ", score=" + score + ", timeoutsLeft=" + timeoutsLeft
				+ ", remainingCardTimes=" + remainingCardTimes + ", yellowCards=" + yellowCards + ", redCards="
				+ redCards + ", robots=\r\n" + printRobots() + "]" + "\r\n";
	}

	public String printRobots() {
		String robotString = "";
		for (int i = 0; i < robots.size(); i++) {
			robotString += "\t" + robots.get(i).toString();
		}
		
		return robotString;
	}
}
