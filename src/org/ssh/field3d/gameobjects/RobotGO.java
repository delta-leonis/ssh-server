/**************************************************************************************************
 * 
 *	RobotGO
 * 		This class is for our Robot GameObjects.
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
package org.ssh.field3d.gameobjects;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.ssh.field3d.FieldGame;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.shapes.Arc3D;
import org.ssh.field3d.gameobjects.contextmenus.RobotInfoContextMenu;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;


public class RobotGO extends GameObject {
	
	// TODO: MOVE TO CONFIG
	public static final double ROBOT_HEIGHT = 200.0;
	public static final double ROBOT_RADIUS = 250.0;
	public static final double ROBOT_SELECTION_CIRCLE_THICKNESS = 50.0;
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Private variables
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	private MeshView _model, _selectionArcMesh;
	private Vector3f _loc, _vel;
	private PhongMaterial _material;
	private PhongMaterial _selectionCircleMaterial;
	private RobotInfoContextMenu _contextMenu;
	private Arc3D _selectionArc;
	private Color _selectionCircleColor;
	
	private boolean _isSelected;
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Constructors
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public RobotGO(Game game) {
		
		super(game);
		
		ObjModelImporter modelImporter = new ObjModelImporter();
		
		_loc = new Vector3f(0.0f, 10.0f, 0.0f);
		_vel = new Vector3f();
		_selectionArc = new Arc3D(0.0, 360.0, ROBOT_RADIUS, ROBOT_SELECTION_CIRCLE_THICKNESS, 100);
		
		_material = new PhongMaterial(Color.WHITE);
		_selectionCircleMaterial = new PhongMaterial();
		_contextMenu = new RobotInfoContextMenu(game);
						
		_selectionArcMesh = _selectionArc.MeshView();
		_selectionArcMesh.setCullFace(CullFace.NONE);
		_selectionArcMesh.setRotationAxis(Rotate.X_AXIS);
		_selectionArcMesh.setRotate(90.0);
		
		// Setting selection circle color & material
		_selectionCircleColor = Color.BLUE;		
		_selectionCircleMaterial.setDiffuseColor(_selectionCircleColor);
		_selectionCircleMaterial.setSpecularColor(_selectionCircleColor);
		_selectionCircleMaterial.setSpecularPower(20.0);
		
		_selectionArcMesh.setMaterial(_selectionCircleMaterial);;
		
		// Read org.ssh.models
		modelImporter.read("./assets/models/robot_model.obj");
		
		// Check if we've loaded successfully
		if (modelImporter.getImport().length > 0) { 
		
			// Getting org.ssh.models from the org.ssh.models importer
			_model = modelImporter.getImport()[0];
			
			try {
				_material.setDiffuseMap(new Image(new FileInputStream("./assets/textures/robotTextureTest2.png")));
				_material.setSpecularColor(Color.WHITE);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Setting org.ssh.models material
			_model.setMaterial(_material);
			
		} else {
			
			// Show error
			System.out.println("Missing ./assets/models/robots/yellow/robot_yellow_8.obj");
		}
		
		
		// Setting not selected
		_isSelected = false;		
		SetSelected(_isSelected);
	}
	


	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Overridden methods from GameObject
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void Initialize() {
		
		// Create a org.ssh.models group
		Group modelGroup = new Group();		
		
		// Add our org.ssh.models to the org.ssh.models group
		modelGroup.getChildren().add(_model);
		modelGroup.getChildren().add(_selectionArcMesh);
		
		// Add org.ssh.models group to the world group
		GetGame().GetWorldGroup().getChildren().addAll(modelGroup);
		
		GetGame().AddGameObject(_contextMenu);
		
		_model.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				
				// TODO: show context menu
				SetSelected(!_isSelected);
			}
			
		});
	}


	@Override
	public void Update(long timeDivNano) {
		
		// Check if we are on the border
		if (_loc.z > FieldGame.FIELD_DEPTH / 2){
			
			// Limit location
			_loc.z = (float) (FieldGame.FIELD_DEPTH / 2.0f); 
			// Invert velocity
			_vel.z *= -1;
			
		} else if (_loc.z < -(FieldGame.FIELD_DEPTH / 2)) {
			
			// Limit location
			_loc.z = (float) -(FieldGame.FIELD_DEPTH / 2.0f);
			// Invert velocity
			_vel.z *= -1;
		}
		
		// Update location 
		_loc = _loc.add(_vel.scale(timeDivNano / 1000000000.0f));
		
		_contextMenu.Translate(_loc.x, _loc.y + _contextMenu.GetHeight(), _loc.z);
		
		_contextMenu.SetLabelLocationText("Location: " + _loc);
		_contextMenu.SetLabelSpeedText("Speed: " + _vel);
		
		// Translate to location
		_model.setTranslateX(_loc.x);
		_model.setTranslateY(_loc.y);
		_model.setTranslateZ(_loc.z);
		
		// Translate selection circle to location
		_selectionArcMesh.setTranslateX(_loc.x);
		_selectionArcMesh.setTranslateY(_loc.y - (ROBOT_HEIGHT / 2.2));
		_selectionArcMesh.setTranslateZ(_loc.z);
	}


	@Override
	public void Destroy() { }

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Getters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public Vector3f GetLocation() { return _loc; }
	public Vector3f GetVelocity() { return _vel; }
	public Color GetSelectionCircleColor() { return _selectionCircleColor; }
	
	public boolean GetSelected() { return _isSelected; }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	//	Setters
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void SetLocation(Vector3f location) { _loc = location; }
	public void SetSelectionCircleColor(Color color) { 
		
		// Set selection circle color
		_selectionCircleColor = color; 
		
		// Update material
		_selectionCircleMaterial.setDiffuseColor(_selectionCircleColor);
		_selectionCircleMaterial.setSpecularColor(_selectionCircleColor);
	}
	
	public void SetSelected(boolean isSelected) {
		
		_isSelected = isSelected;
		_selectionArcMesh.setVisible(_isSelected);
	}
}
