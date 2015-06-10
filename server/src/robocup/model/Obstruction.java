package robocup.model;

public class Obstruction extends FieldObject {
	private int width,
				length;
	private double orientation;

	public Obstruction(int _width, int _length){
		width = _width;
		length = _length;
	}

	public int getWidth(){
		return width;
	}

	public int getLength(){
		return length;
	}
	
	public double getOrientation(){
		return orientation;
	}

	public void setOrientation(int newOrientation) {
		orientation = newOrientation;
	}
}
