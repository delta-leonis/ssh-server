package org.ssh.models.enums;

import javafx.scene.paint.Color;

/**
 * Describes the color of a team, either blue or yellow
 * 
 * @author Jeroen de Jong
 */
public enum TeamColor {
    YELLOW,
    BLUE;
    
    /**
     * @return Convert {@link TeamColor} a {@link Color}
     */
    public Color toColor() {
        return this.equals(TeamColor.BLUE) ? Color.BLUE : Color.YELLOW;
    }

    public TeamColor getOpposite() {
        return this.equals(TeamColor.BLUE) ? TeamColor.YELLOW : TeamColor.BLUE;
    }

    public String identifier() {
        return name().substring(0, 1);
    }
}