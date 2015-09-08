package robocup.model.enums;

import java.awt.Color;
/**
 * Describes the color of a team, either blue or yellow 
 */
public enum TeamColor {
	YELLOW, BLUE;

	/**
	 * @return Convert {@link TeamColor} a {@link Color}
	 */
	public Color toColor() {
		if (this.equals(TeamColor.BLUE))
			return Color.blue;
		return Color.yellow;
	}
}
