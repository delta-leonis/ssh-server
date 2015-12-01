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
        if (this.equals(TeamColor.BLUE))
            return Color.BLUE;
        return Color.YELLOW;
    }

    public TeamColor swap() {
        return (this.equals(TeamColor.BLUE) ? TeamColor.YELLOW : TeamColor.BLUE);
    }
}