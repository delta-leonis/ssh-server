package robocup.view;

import robocup.model.FieldObject;
import robocup.model.Robot;
import robocup.model.World;

public class GUIModel {
	private boolean showCoordinates,
					showRaster,
					showRobots ,
					showBall,
					mirrored;
	private Robot selectedRobot;
	private FieldObject mouseObject;
	private int quadrantRotation;
	
	public GUIModel(){
		showCoordinates = true;
		showRaster = true;
		showRobots = true;
		showBall = true;
		mirrored = false;
	}

	public boolean showCoordinates() {
		return showCoordinates;
	}

	public void setShowCoordinates(boolean showCoordinates) {
		this.showCoordinates = showCoordinates;
	}

	public boolean showRaster() {
		return showRaster;
	}

	public void setShowRaster(boolean showRaster) {
		this.showRaster = showRaster;
	}

	public boolean showRobots() {
		return showRobots;
	}

	public void setShowRobots(boolean showRobots) {
		this.showRobots = showRobots;
	}

	public boolean showBall() {
		return showBall;
	}

	public void setShowBall(boolean showBall) {
		this.showBall = showBall;
	}

	public boolean isMirrored() {
		return mirrored;
	}

	public void setMirrored(boolean mirrored) {
		this.mirrored = mirrored;
	}

	public Robot getSelectedRobot() {
		return selectedRobot;
	}

	public void setSelectedRobot(Robot selectedRobot) {
		this.selectedRobot = selectedRobot;
	}

	public FieldObject getMouseObject() {
		return mouseObject;
	}

	public void setMouseObject(FieldObject mouseObject) {
		this.mouseObject = mouseObject;
	}

	public void nextQuadrantRotation() {
		if(++quadrantRotation >= 4)
			quadrantRotation = 0; 
	}
	
	public int getQuadrantRotation(){
		return quadrantRotation;
	}
}
