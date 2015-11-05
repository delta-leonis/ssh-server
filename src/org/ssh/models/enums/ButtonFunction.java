package org.ssh.models.enums;

import net.java.games.input.Component;

/**
 * Describes different assignable functions for a {@link ControllerLayout}, and whether that function should be assigned to a  {@link ButtonType.ANALOG} or  {@link ButtonType.DIGITAL} controller {@link Component}
 * 
 * @author Jeroen
 *
 */
public enum ButtonFunction {
	KICK			 (ButtonType.DIGITAL, false),
	KICK_STRENGTH	 (ButtonType.ANALOG , true),
	CHIP			 (ButtonType.DIGITAL, false),
	CHIP_STRENGTH	 (ButtonType.ANALOG, true),
	CHIPKICK_STRENGTH(ButtonType.ANALOG, true),
	DRIBBLE			 (ButtonType.DIGITAL, true),
	DRIBBLE_TOGGLE	 (ButtonType.DIGITAL, false),
	DRIBBLE_SPEED	 (ButtonType.ANALOG, true),
	SELECT_NEXT_ROBOT(ButtonType.DIGITAL, false),
	SELECT_PREV_ROBOT(ButtonType.DIGITAL, false),
	STOP_ALL_ROBOTS  (ButtonType.DIGITAL, false),
	ORIENTATION_X	 (ButtonType.ANALOG, true),
	ORIENTATION_Y	 (ButtonType.ANALOG, true),
	DIRECTION_X		 (ButtonType.ANALOG, true),
	DIRECTION_Y		 (ButtonType.ANALOG, true),
	DIRECTION_FORWARD(ButtonType.DIGITAL, true),
	DIRECTION_BACKWARD (ButtonType.DIGITAL, true),
	DIRECTION_LEFT	 (ButtonType.DIGITAL, true),
	DIRECTION_RIGHT	 (ButtonType.DIGITAL, true),
//	ORIENTATION_NORTH(ButtonType.DIGITAL),   use-case ?
//	ORIENTATION_SOUTH(ButtonType.DIGITAL),
	ORIENTATION_EAST (ButtonType.DIGITAL, true),
	ORIENTATION_WEST (ButtonType.DIGITAL, true), 
	DIRECTION_POV	 (ButtonType.DIGITAL, true)
	;
	
	/**
	 * Enum that describes a possible button type.<br>
	 * which is either digital, or analog
	 * 
	 * @author Jeroen
	 *
	 */
	public enum ButtonType {
		ANALOG, DIGITAL
	}

	/**
	 * Describes whether the Controller {@link Component} for this {@link ButtonFunction} should be {@link ButtonType.ANALOG} or {@link ButtonType.DIGITAL} 
	 */
	private final ButtonType buttonType;
	
	private final boolean persistant;

	ButtonFunction(ButtonType type, boolean persistant){
		buttonType = type;
		this.persistant = persistant;
	}
	
	/**
	 * @return Get the {@link ButtonType} for this ButtonFunction
	 */
	public ButtonType getButtonType() {
		return buttonType;
	}

	public boolean isAnalog(){
		return this.getButtonType().equals(ButtonType.ANALOG);
	}
	public boolean isDigital(){
		return !isAnalog();
	}
	
	public boolean isPersistant(){
		return persistant;
	}
}