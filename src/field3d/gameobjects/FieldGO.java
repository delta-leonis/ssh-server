package field3d.gameobjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import field3d.FieldGame;
import field3d.core.game.Game;
import field3d.core.gameobjects.GameObject;
import field3d.core.math.Vector3f;
import field3d.core.shapes.Arc3D;
import field3d.gameobjects.contextmenus.GoalContextMenu;


import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;


// TODO: Javadoc, cleanup
public class FieldGO extends GameObject {
	
	
	private ArrayList<Box> _fieldTiles;
	private ArrayList<Box> _fieldLines;
	
	private PhongMaterial _grassMaterial;
	private Image _grassTexture;
	private GoalContextMenu _goalContextMenu;
	
	private double _width, _height; 
	private double _tileWidth, _tileHeight;
	
	
	public FieldGO(Game game, double width, double height) {
		
		// Initialize super class
		super(game);
		
		FileInputStream fileInput = null;
		
		_fieldTiles = new ArrayList<Box>();
		_fieldLines = new ArrayList<Box>();
		_grassMaterial = new PhongMaterial(Color.LAWNGREEN);
		_goalContextMenu = new GoalContextMenu(GetGame(), 1000, 500);
		
		
		_width = width;
		_height = height;
		
		_tileHeight = FieldGame.FIELD_TILE_DEPTH;
		_tileWidth = FieldGame.FIELD_TILE_WIDTH;
		
		
		try {
			 fileInput = new FileInputStream("./assets/textures/grass2.png");
			 _grassTexture = new Image(fileInput);
			 _grassMaterial.setDiffuseMap(_grassTexture);
			 
		} catch (FileNotFoundException e) {
			
			// TODO: Logger handling
			e.printStackTrace();
		}
	}
	
	

	@Override
	public void Initialize() {
		
		// Generate tiles
		generateTiles();
		
		// Generate lines
		generateLines();
		
		// Generate lines
		generateGoals();
		
		// Generate arcs
		generateArcs();
		
		GetGame().AddGameObject(_goalContextMenu);
	}

	@Override
	public void Destroy() {

		if (_fieldTiles != null && _fieldTiles.size() > 0) {
			
			for (Box tmpBox : _fieldTiles) {
				
				// Remove box from list
				_fieldTiles.remove(tmpBox);
			}
		}
		
		if (_fieldLines != null && _fieldLines.size() > 0) {
			
			for (Box tmpBox : _fieldLines) {
				
				// Remove box from list
				_fieldLines.remove(tmpBox);
			}
		}
		
	}
	
	@Override
	public void Update(long timeDivNano) { }
	
	
	
	private void generateTiles() {

		// Loop through x axis
		for (int i = 0; i < _width / _tileWidth; i++) {
			// Loop through z axis
			for (int j = 0; j < _height / _tileHeight; j++) {
				
				// Create new box
				Box tmpBox = new Box(_tileWidth, FieldGame.FIELD_HEIGHT, _tileHeight);
				
				// Translate tile into position
				tmpBox.setTranslateX(-(_width / 2.0) + ((i * _tileWidth) + (_tileWidth / 2.0)));
				tmpBox.setTranslateZ(-(_height / 2.0) + ((j * _tileHeight) + (_tileHeight / 2.0)));
				
				// Set box material
				tmpBox.setMaterial(_grassMaterial);
				
				GetGame().GetWorldGroup().getChildren().add(tmpBox);
				
				_fieldTiles.add(tmpBox);
			}
		}
	}
	
	private void generateLines() { 
		
		/////////////////////////////////////////////////////
		// Outline
		/////////////////////////////////////////////////////	
		Vector3f midLinePos = new Vector3f(0.0f, 10.0f, 0.0f);
		Vector3f midLineDim = new Vector3f(10.0f, (float)FieldGame.FIELD_LINE_HEIGHT, (float)FieldGame.FIELD_DEPTH);
		
		Vector3f leftSideLinePos = new Vector3f(0.0f, 10.0f, (float)(-FieldGame.FIELD_DEPTH / 2.0));
		Vector3f leftSisdeLineDim = new Vector3f((float)FieldGame.FIELD_WIDTH, (float)FieldGame.FIELD_LINE_HEIGHT, 10.0f);
		Vector3f rightSideLinePos = new Vector3f(0.0f, 10.0f, (float)(FieldGame.FIELD_DEPTH / 2.0));
		Vector3f rightSideLineDim = new Vector3f((float)FieldGame.FIELD_WIDTH, (float)FieldGame.FIELD_LINE_HEIGHT, 10.0f);
		
		
		Vector3f topSideLinePos = new Vector3f((float)(FieldGame.FIELD_WIDTH / 2.0), 10.0f, 0.0f);
		Vector3f topSideLineDim = new Vector3f(10.0f, (float)FieldGame.FIELD_LINE_HEIGHT, (float)FieldGame.FIELD_DEPTH);
		Vector3f bottomSideLinePos = new Vector3f((float)-(FieldGame.FIELD_WIDTH / 2.0), 10.0f, 0.0f);
		Vector3f bottomSideLineDim = new Vector3f(10.0f, (float)FieldGame.FIELD_LINE_HEIGHT, (float)FieldGame.FIELD_DEPTH);
		
		addLine(midLinePos, midLineDim);
		addLine(leftSideLinePos, leftSisdeLineDim);
		addLine(rightSideLinePos, rightSideLineDim);
		addLine(topSideLinePos, topSideLineDim);
		addLine(bottomSideLinePos, bottomSideLineDim);
	}
	
	
	private void generateArcs() {
		
		// TODO: generate arcs for the mid circle & goal zones
		// TODO: remove magic numbers
		Arc3D goalLeftArcLeft = new Arc3D(180.0, 270.0, 1000.0, 10.0, 100);
		Arc3D goalLeftArcRight = new Arc3D(90.0, 180.0, 1000.0, 10.0, 100);
		Arc3D goalRightArcLeft = new Arc3D(0.0, 90.0, 1000.0, 10.0, 100);
		Arc3D goalRightArcRight = new Arc3D(270, 360, 1000.0, 10.0, 100);
		Arc3D midCircle = new Arc3D(0.0, 360.0, 1000, 10.0, 1000);
		
		MeshView goalLeftLeftArcMesh = goalLeftArcLeft.MeshView();
		MeshView goalLeftRightArcMesh = goalLeftArcRight.MeshView();
		MeshView goalRightArcLeftMesh = goalRightArcLeft.MeshView();
		MeshView goalRightArcRightMesh = goalRightArcRight.MeshView();
		MeshView midCircleMesh = midCircle.MeshView();
		
		
		midCircleMesh.setRotationAxis(Rotate.X_AXIS);
		
		goalLeftLeftArcMesh.setRotationAxis(Rotate.X_AXIS);
		goalLeftRightArcMesh.setRotationAxis(Rotate.X_AXIS);
		goalRightArcLeftMesh.setRotationAxis(Rotate.X_AXIS);
		goalRightArcRightMesh.setRotationAxis(Rotate.X_AXIS);
		
		midCircleMesh.setRotate(90.0);
		
		goalLeftLeftArcMesh.setRotate(90.0);
		goalLeftRightArcMesh.setRotate(90.0);
		goalRightArcLeftMesh.setRotate(90.0);
		goalRightArcRightMesh.setRotate(90.0);
		
		// TODO: use model
		goalLeftRightArcMesh.setTranslateX((FieldGame.FIELD_WIDTH / 2.0));
		goalLeftRightArcMesh.setTranslateZ(500);
		goalLeftRightArcMesh.setTranslateY(-235);
		
		goalLeftLeftArcMesh.setTranslateX(FieldGame.FIELD_WIDTH / 2.0);
		goalLeftLeftArcMesh.setTranslateY(265);
		goalLeftLeftArcMesh.setTranslateZ(-500);
		
		goalRightArcRightMesh.setTranslateX(-(FieldGame.FIELD_WIDTH / 2.0));
		goalRightArcRightMesh.setTranslateY(265);
		goalRightArcRightMesh.setTranslateZ(-500);
		
		goalRightArcLeftMesh.setTranslateX(-(FieldGame.FIELD_WIDTH / 2.0));
		goalRightArcLeftMesh.setTranslateY(-235);
		goalRightArcLeftMesh.setTranslateZ(500);
		
		midCircleMesh.setTranslateY(10.0f);
		
		goalLeftLeftArcMesh.setCullFace(CullFace.NONE);
		goalLeftRightArcMesh.setCullFace(CullFace.NONE);
		goalRightArcLeftMesh.setCullFace(CullFace.NONE);
		goalRightArcRightMesh.setCullFace(CullFace.NONE);
		midCircleMesh.setCullFace(CullFace.NONE);
		
		GetGame().GetWorldGroup().getChildren().add(goalLeftLeftArcMesh);
		GetGame().GetWorldGroup().getChildren().add(goalLeftRightArcMesh);
		GetGame().GetWorldGroup().getChildren().add(goalRightArcLeftMesh);
		GetGame().GetWorldGroup().getChildren().add(goalRightArcRightMesh);
		GetGame().GetWorldGroup().getChildren().add(midCircleMesh);
	}
	
	private void generateGoals() {
		
		////////////////////////////////////////////////
		//	Top Goal
		////////////////////////////////////////////////
		Vector3f goalTopLeftPos = new Vector3f((float)((FieldGame.FIELD_WIDTH / 2.0) + (FieldGame.FIELD_GOAL_DEPTH / 2.0)), 10.0f + (float)(FieldGame.FIELD_GOAL_HEIGHT / 2.0), (float)-(FieldGame.FIELD_GOAL_WIDTH / 2.0));
		Vector3f goalTopLeftDim = new Vector3f((float)FieldGame.FIELD_GOAL_DEPTH, (float)FieldGame.FIELD_GOAL_HEIGHT, (float)FieldGame.FIELD_GOAL_LINE_WIDTH);
		
		Vector3f goalTopRightPos = new Vector3f((float)((FieldGame.FIELD_WIDTH / 2.0) + (FieldGame.FIELD_GOAL_DEPTH / 2.0)), 10.0f + (float)(FieldGame.FIELD_GOAL_HEIGHT / 2.0), (float)(FieldGame.FIELD_GOAL_WIDTH / 2.0));
		Vector3f goalTopRightDim = new Vector3f((float)FieldGame.FIELD_GOAL_DEPTH, (float)FieldGame.FIELD_GOAL_HEIGHT, (float)FieldGame.FIELD_GOAL_LINE_WIDTH);
		
		Vector3f goalTopTopPos = new Vector3f((float)((FieldGame.FIELD_WIDTH / 2.0) + FieldGame.FIELD_GOAL_DEPTH), 10.0f + (float)(FieldGame.FIELD_GOAL_HEIGHT / 2.0), 0.0f);
		Vector3f goalTopTopDim = new Vector3f((float)FieldGame.FIELD_GOAL_LINE_WIDTH, (float)FieldGame.FIELD_GOAL_HEIGHT, (float)FieldGame.FIELD_GOAL_WIDTH);
		
		Vector3f goalTopDefencePos = new Vector3f((float)(FieldGame.FIELD_WIDTH / 2.0 - 500), 10.0f, 0.0f);
		Vector3f goalTopDefenceDim = new Vector3f((float)FieldGame.FIELD_LINE_WIDTH, 10.0f, 500.0f);
		
		addLine(goalTopLeftPos, goalTopLeftDim).setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				// TODO: Show context menu
				_goalContextMenu.Translate(goalTopLeftPos.x - 500, goalTopLeftPos.y, goalTopLeftPos.z - 500);
				_goalContextMenu.Rotate(0.0, 90.0, 0.0);
				_goalContextMenu.Show();
				
		
			} } );
		addLine(goalTopRightPos, goalTopRightDim).setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				// TODO: Show context menu
				_goalContextMenu.Translate(goalTopRightPos.x -500, goalTopRightPos.y, goalTopRightPos.z + 500);
				_goalContextMenu.Rotate(0.0, 90.0, 0.0);
				_goalContextMenu.Show();
			}
			
		});;
		addLine(goalTopTopPos, goalTopTopDim).setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				// TODO: Show context menu
				_goalContextMenu.Translate(goalTopTopPos.x, goalTopTopPos.y + (goalTopTopDim.y / 2.0), goalTopTopPos.z);
				_goalContextMenu.Rotate(0.0, 90.0, 0.0);
				_goalContextMenu.Show();
			}
		});;
		
		addLine(goalTopDefencePos, goalTopDefenceDim);
		
		
		////////////////////////////////////////////////
		//	Bottom Goal
		////////////////////////////////////////////////
		Vector3f goalBottomLeftPos = new Vector3f((float)( -(FieldGame.FIELD_WIDTH / 2.0) - (FieldGame.FIELD_GOAL_DEPTH / 2.0) ), 10.0f + (float)(FieldGame.FIELD_GOAL_HEIGHT / 2.0), (float)-(FieldGame.FIELD_GOAL_WIDTH / 2.0));
		Vector3f goalBottomLeftDim = new Vector3f((float)FieldGame.FIELD_GOAL_DEPTH, (float)FieldGame.FIELD_GOAL_HEIGHT, (float)FieldGame.FIELD_GOAL_LINE_WIDTH);
	
		Vector3f goalBottomRightPos = new Vector3f((float)( -(FieldGame.FIELD_WIDTH / 2.0) - (FieldGame.FIELD_GOAL_DEPTH / 2.0) ), 10.0f + (float)(FieldGame.FIELD_GOAL_HEIGHT / 2.0), (float)(FieldGame.FIELD_GOAL_WIDTH / 2.0));
		Vector3f goalBottomRightDim = new Vector3f((float)FieldGame.FIELD_GOAL_DEPTH, (float)FieldGame.FIELD_GOAL_HEIGHT, (float)FieldGame.FIELD_GOAL_LINE_WIDTH);
		
		Vector3f goalBottomTopPos = new Vector3f((float)(-(FieldGame.FIELD_WIDTH / 2.0) - FieldGame.FIELD_GOAL_DEPTH), 10.0f + (float)(FieldGame.FIELD_GOAL_HEIGHT / 2.0), 0.0f);
		Vector3f goalBottomTopDim = new Vector3f((float)FieldGame.FIELD_GOAL_LINE_WIDTH, (float)FieldGame.FIELD_GOAL_HEIGHT, (float)FieldGame.FIELD_GOAL_WIDTH);
		
		Vector3f goalBottomDefencePos = new Vector3f((float)-(FieldGame.FIELD_WIDTH / 2.0) + 500.0f, 10.0f, 0.0f);
		Vector3f goalBottomDefenceDim = new Vector3f((float)FieldGame.FIELD_LINE_WIDTH, 10.0f, 500.0f);
		
		Box goalBottomLeft = addLine(goalBottomLeftPos, goalBottomLeftDim);
		goalBottomLeft.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				// TODO: Show context menu
				_goalContextMenu.Translate(goalBottomLeftPos.x - 500, goalBottomLeftPos.y, goalBottomLeftPos.z - 500);
				_goalContextMenu.Rotate(0.0, 90.0, 0.0);
				_goalContextMenu.Show();
				
		
			}
			
		});
		
		addLine(goalBottomRightPos, goalBottomRightDim).setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				// TODO: Show context menu
				_goalContextMenu.Translate(goalBottomRightPos.x -500, goalBottomRightPos.y, goalBottomRightPos.z + 500);
				_goalContextMenu.Rotate(0.0, 90.0, 0.0);
				_goalContextMenu.Show();
			}
			
		});
		
		addLine(goalBottomTopPos, goalBottomTopDim).setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				// TODO: Show context menu
				_goalContextMenu.Translate(goalBottomTopPos.x, goalBottomTopPos.y + (goalBottomTopDim.y / 2.0), goalBottomTopPos.z);
				_goalContextMenu.Rotate(0.0, 90.0, 0.0);
				_goalContextMenu.Show();
			}
		});
		
		addLine(goalBottomDefencePos, goalBottomDefenceDim);
	}

	
	private Box addLine(Vector3f loc, Vector3f dimensions) {
		
		Box box = new Box(dimensions.x, dimensions.y, dimensions.z);
		
		// Setting translations
		box.setTranslateX(loc.x);
		box.setTranslateY(loc.y);
		box.setTranslateZ(loc.z);
		
		// Add to lines
		_fieldLines.add(box);
		
		// Add to world group
		GetGame().GetWorldGroup().getChildren().add(box);
		
		return box;
	}	
}
