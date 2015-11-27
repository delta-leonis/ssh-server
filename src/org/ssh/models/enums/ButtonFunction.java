package org.ssh.models.enums;

import org.ssh.controllers.ControllerLayout;

import net.java.games.input.Component;

/**
 * Describes different assignable functions for a {@link ControllerLayout}, and whether that
 * function should be assigned to a {@link ButtonType.ANALOG} or {@link ButtonType.DIGITAL}
 * controller {@link Component}.
 *
 * @author Jeroen de jong
 *         
 */
public enum ButtonFunction {
    KICK(ButtonType.DIGITAL,
            false),
    KICK_STRENGTH(ButtonType.ANALOG,
            true),
    CHIP(ButtonType.DIGITAL,
            false),
    CHIP_STRENGTH(ButtonType.ANALOG,
            true),
    CHIPKICK_STRENGTH(ButtonType.ANALOG,
            true),
    DRIBBLE_PERSISTENT(ButtonType.DIGITAL,
            true),
    DRIBBLE_TOGGLE(ButtonType.DIGITAL,
            false),
    DRIBBLE_SPEED(ButtonType.ANALOG,
            true),
    SELECT_NEXT_ROBOT(ButtonType.DIGITAL,
            false),
    SELECT_PREV_ROBOT(ButtonType.DIGITAL,
            false),
    STOP_ALL_ROBOTS(ButtonType.DIGITAL,
            false),
    ORIENTATION_X(ButtonType.ANALOG,
            true),
    ORIENTATION_Y(ButtonType.ANALOG,
            true),
    DIRECTION_X(ButtonType.ANALOG,
            true),
    DIRECTION_Y(ButtonType.ANALOG,
            true),
    DIRECTION_FORWARD(ButtonType.DIGITAL,
            true),
    DIRECTION_BACKWARD(ButtonType.DIGITAL,
            true),
    DIRECTION_LEFT(ButtonType.DIGITAL,
            true),
    DIRECTION_RIGHT(ButtonType.DIGITAL,
            true),
    ORIENTATION_EAST(ButtonType.DIGITAL,
            true),
    ORIENTATION_WEST(ButtonType.DIGITAL,
            true),
    DIRECTION_POV(ButtonType.POV,
            true);
            
    /**
     * Enum that describes a possible button type.<br>
     * which is either digital, or analog
     * 
     * @author Jeroen de Jong
     *         
     */
    public enum ButtonType {
        ANALOG,
        DIGITAL,
        POV // the 'hat'-switch (arrow keys) on a gamepad
    }
    
    /**
     * Describes whether the Controller {@link Component} for this {@link ButtonFunction} should be
     * {@link ButtonType.ANALOG} or {@link ButtonType.DIGITAL}.
     */
    private final ButtonType buttonType;
    /**
     * Whether this function should be read and updated every time the controller gets polled.
     */
    private final boolean    persistent;
                             
    /**
     * Constructs a buttonfunction
     * 
     * @param type
     *            type of button (analog, digital, whatever).
     * @param persistent
     *            whether this buttonfunction should be {@link #persistent}.
     */
    ButtonFunction(final ButtonType type, final boolean persistent) {
        this.buttonType = type;
        this.persistent = persistent;
    }
    
    /**
     * @return Get the {@link ButtonType} for this ButtonFunction.
     */
    public ButtonType getButtonType() {
        return this.buttonType;
    }
    
    /**
     * @return true if this button should be updated regardless of it's previous state, see
     *         {@link #persistent}.
     */
    public boolean isPersistant() {
        return this.persistent;
    }
}