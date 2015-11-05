/**************************************************************************************************
 * 
 *	ContextMenuGO
 * 		This class is for our 3d context menus.
 * 
 **************************************************************************************************
 * 
 * 	TODO: change size according to zoom of camera
 * 	TODO: javadoc
 * 	TODO: comment
 * 	TODO: cleanup
 * 
 **************************************************************************************************
 * @see GameObject
 * 
 * @author marklef2
 * @date 15-10-2015
 */
package org.ssh.field3d.gameobjects.contextmenus;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.math.Xform;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class ContextMenuGO extends GameObject {
	
	private Rectangle _rectangle;
	private Xform _xform;
	private Xform _xform2;
	private Xform _xform3;
	private Group _contextMenuGroup, _controlsGroup;	
	private double _width, _height;
	private Vector3f _loc;
	

	public ContextMenuGO(Game game, double width, double height) {
		
		super(game);
		
		_xform = new Xform();
		_xform2 = new Xform();
		_xform3 = new Xform();
		_contextMenuGroup = new Group();
		_controlsGroup = new Group();
		
		_loc = new Vector3f();
		
		_width = width;
		_height = height;		
		
		_rectangle = new Rectangle(width, height);
		_rectangle.setFill(new Color(0.0, 0.0, 1.0, 0.75));
		
		_xform.getChildren().add(_xform2);
		_xform2.getChildren().add(_xform3);
		_xform3.getChildren().add(_contextMenuGroup);
		_contextMenuGroup.getChildren().add(_controlsGroup);
		_controlsGroup.getChildren().add(_rectangle);
		_rectangle.setTranslateZ(10);
		
		_xform3.rx.setAngle(180);
		_xform3.ry.setAngle(180);
		
		_contextMenuGroup.setVisible(false);
		_contextMenuGroup.setTranslateX(-width / 2.0);
		_contextMenuGroup.setTranslateY(-height/ 2.0);
	}

	@Override
	public void Initialize() {
	
		// Add context menu to world
		GetGame().GetWorldGroup().getChildren().add(_xform);
	}

	@Override
	public void Update(long timeDivNano) {
		
		// Calculate scale of the context menu
		double scale = 1 - (GetGame().GetMouseInputHandler().GetScrollWheelYValue() + 1000.0) / 2000.0;
		scale += 0.14; // Add minimal scale
	
		// Rotate towards camera
		this.Rotate(GetGame().GetThirdPersonCamera().GetRotateX(), GetGame().GetThirdPersonCamera().GetRotateY(), 0.0);
		// Translate to location
		_xform.setTranslate(_loc.x, _loc.y + (scale * 5 * (_height / 2.0)), _loc.z);
		// Scale
		_xform3.setScale(scale * 5, scale * 5, scale * 5);
	}

	@Override
	public void Destroy() { }
	
	
	
	public void Translate(double x, double y, double z) {
		
		_xform.setTranslate(x, y, z);
		_loc = new Vector3f((float)x, (float)y, (float)z);
	}
	public void Rotate(double x, double y, double z) {
		
		_xform2.setRotate(x, y, z);
	}
	
	
	public void Show() { _contextMenuGroup.setVisible(true); }
	public void Hide() { _contextMenuGroup.setVisible(false); }
	
	
	public double GetWidth() { return _width; }
	public double GetHeight() { return _height; }
		
	
	protected Group GetControlsGroup() { return _controlsGroup; }
	protected Group GetGroup() { return _contextMenuGroup; }
}
