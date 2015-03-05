package robocup.model.enums;

public enum TeamColor {
	YELLOW, BLUE;
	
	public java.awt.Color toColor(){
		if(this == TeamColor.BLUE){
			return java.awt.Color.blue;
		}
		return java.awt.Color.yellow;
	}
}
