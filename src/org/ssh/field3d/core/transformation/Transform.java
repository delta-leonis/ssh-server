/**
 * 
 * Transformation class, handles 3d transformation
 * 
 * @author marklef2
 * @date 29-9-2015
 */
package org.ssh.field3d.core.transformation;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;


public class Transform extends Group {
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private Translate _position;
	private Translate _pivot;
	private Translate _invertedPivot;
	private Scale _scale;
	private Rotate _rotateX, _rotateY, _rotateZ;
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * Constructor
	 * 
	 */
	public Transform() {
		
		// Creating new objects
		_position = new Translate();
		_pivot = new Translate();
		_scale = new Scale();
		
		_rotateX = new Rotate();
		_rotateY = new Rotate();
		_rotateZ = new Rotate();
		
		
		// Setting rotation axis
		_rotateX.setAxis(Rotate.X_AXIS);
		_rotateY.setAxis(Rotate.Y_AXIS);
		_rotateZ.setAxis(Rotate.Z_AXIS);
		
		// Setting inverted position
		_invertedPivot = _pivot.createInverse();
		
		// Clear transforms
		getTransforms().clear();
		// Setting transforms
		getTransforms().addAll(_position, _rotateZ, _rotateY, _rotateX);
	}	

/*	
	public void update() {
		
		// Clear transforms
		getTransforms().clear();
		// Setting transforms
		getTransforms().addAll(_position, _pivot, _rotateZ, _rotateY, _rotateX, _scale, _invertedPivot);
	}*/
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public Translate getPosition() { return _position; }
	public Translate getPivot() { return _pivot; }
	public Translate getInvertedPivot() { return _invertedPivot; }
	public Scale getScale() { return _scale; }
	public Rotate getRotateX() { return _rotateX; }
	public Rotate getRotateY() { return _rotateY; }
	public Rotate getRotateZ() { return _rotateZ; }
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void setPosition(Translate position) { 
		
		// Setting position
		_position = position; 
	}
	public void setPosition(Point3D position) {
		
		// Setting position
		_position.setX(position.getX());
		_position.setY(position.getY());
		_position.setZ(position.getZ());
	}
	
	
	/////////////////////////////////////////////
	//
	// Pivot
	//
	/////////////////////////////////////////////
	public void setPivot(Translate pivot) {
		
		// Setting pivot
		_pivot = pivot;
		
		// Setting inverted pivot
		_invertedPivot = _pivot.createInverse();
	}
	public void setPivot(Point3D pivot) {
		
		// Setting pivot
		_pivot.setX(pivot.getX());
		_pivot.setY(pivot.getY());
		_pivot.setZ(pivot.getZ());
		
		// Setting inverted pivot
		_invertedPivot.setX(-pivot.getX());
		_invertedPivot.setY(-pivot.getY());
		_invertedPivot.setZ(-pivot.getZ());
	}
	
	
	/////////////////////////////////////////////
	//
	// Inverted pivot
	//
	/////////////////////////////////////////////
	public void setInvertedPivot(Translate invertedPivot) {
	
		// Setting inverted pivot
		_invertedPivot = invertedPivot;
		
		// Setting pivot
		_pivot = _invertedPivot.createInverse();
	}
	public void setInvertedPivot(Point3D invertedPivot) {
		
		// Setting inverted pivot
		_invertedPivot.setX(invertedPivot.getX());
		_invertedPivot.setY(invertedPivot.getY());
		_invertedPivot.setZ(invertedPivot.getZ());
		
		// Setting pivot
		_pivot.setX(invertedPivot.getX());
		_pivot.setY(invertedPivot.getY());
		_pivot.setZ(invertedPivot.getZ());
	}
	
	
	/////////////////////////////////////////////
	//
	// Scale
	//
	/////////////////////////////////////////////
	public void setScale(Scale scale) { _scale = scale; }
	public void setScale(Point3D scale) {
		
		_scale.setX(scale.getX());
		_scale.setY(scale.getY());
		_scale.setZ(scale.getZ());
	}
	public void setScale(float scale) {
	
		_scale.setX(scale);
		_scale.setY(scale);
		_scale.setZ(scale);
	}
	
	
	/////////////////////////////////////////////
	//
	// X-Rotation
	//
	/////////////////////////////////////////////
	public void setRotateX(Rotate rotateX) 	{ _rotateX = rotateX; }
	public void setRotateX(float angleX) 	{ _rotateX.setAngle(angleX); }
	
	
	/////////////////////////////////////////////
	//
	// Y-Rotation
	//
	/////////////////////////////////////////////
	public void setRotateY(Rotate rotateY) 	{ _rotateY = rotateY; }
	public void setRotateY(float angleY) 	{ _rotateY.setAngle(angleY); }
	
	
	/////////////////////////////////////////////
	//
	// Z-Rotation
	//
	/////////////////////////////////////////////
	public void setRotateZ(Rotate rotateZ) 	{ _rotateZ = rotateZ; }
	public void setRotateZ(float angleZ) 	{ _rotateZ.setAngle(angleZ); }

	
	@Override
	public String toString() {
		
		// TODO: complete method
		String rtn = "";
		
		rtn += this.getClass().getName() + ": Position; ";
	
		
		return rtn;
	}
}
