package org.ssh.field3d.gameobjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.ssh.field3d.FieldGame;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.shapes.Arc3D;
import org.ssh.field3d.gameobjects.contextmenus.GoalContextMenu;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

// TODO: Javadoc, cleanup
public class FieldGO extends GameObject {
    
    // TODO: read from org.ssh.models
    public static final double    FIELD_PENALTY_SPOT      = 1000.0;
    public static final double    FIELD_PENALTY_SPOT_SIZE = 10.0;
                                                          
    private final ArrayList<Box>  _fieldTiles;
    private final ArrayList<Box>  _fieldLines;
                                  
    private final PhongMaterial   _grassMaterial;
    private Image                 _grassTexture;
    private final GoalContextMenu _goalContextMenu;
    private final PenaltySpotGO   _penaltySpot1;
    private final PenaltySpotGO   _penaltySpot2;
                                  
    private final double          _width, _height;
    private final double          _tileWidth, _tileHeight;
                                  
    public FieldGO(final Game game, final double width, final double height) {
        
        // Initialize super class
        super(game);
        
        FileInputStream fileInput = null;
        
        this._fieldTiles = new ArrayList<Box>();
        this._fieldLines = new ArrayList<Box>();
        this._grassMaterial = new PhongMaterial(Color.LAWNGREEN);
        this._goalContextMenu = new GoalContextMenu(this.GetGame(), 1000, 500);
        this._penaltySpot1 = new PenaltySpotGO(this.GetGame(),
                new Vector3f((float) ((width / 2.0) - FieldGO.FIELD_PENALTY_SPOT), 20, 0),
                FieldGO.FIELD_PENALTY_SPOT_SIZE);
        this._penaltySpot2 = new PenaltySpotGO(this.GetGame(),
                new Vector3f((float) (-(width / 2.0) + FieldGO.FIELD_PENALTY_SPOT), 20, 0),
                FieldGO.FIELD_PENALTY_SPOT_SIZE);
                
        this._width = width;
        this._height = height;
        
        this._tileHeight = FieldGame.FIELD_TILE_DEPTH;
        this._tileWidth = FieldGame.FIELD_TILE_WIDTH;
        
        try {
            fileInput = new FileInputStream("./assets/textures/grass2.png");
            this._grassTexture = new Image(fileInput);
            this._grassMaterial.setDiffuseMap(this._grassTexture);
            
        }
        catch (final FileNotFoundException e) {
            
            // TODO: Logger handling
            e.printStackTrace();
        }
    }
    
    private Box addLine(final Vector3f loc, final Vector3f dimensions) {
        
        final Box box = new Box(dimensions.x, dimensions.y, dimensions.z);
        
        // Setting translations
        box.setTranslateX(loc.x);
        box.setTranslateY(loc.y);
        box.setTranslateZ(loc.z);
        
        // Add to lines
        this._fieldLines.add(box);
        
        // Add to world group
        this.GetGame().GetWorldGroup().getChildren().add(box);
        
        return box;
    }
    
    @Override
    public void Destroy() {
        
        if ((this._fieldTiles != null) && (this._fieldTiles.size() > 0)) {
            
            for (final Box tmpBox : this._fieldTiles) {
                
                // Remove box from list
                this._fieldTiles.remove(tmpBox);
            }
        }
        
        if ((this._fieldLines != null) && (this._fieldLines.size() > 0)) {
            
            for (final Box tmpBox : this._fieldLines) {
                
                // Remove box from list
                this._fieldLines.remove(tmpBox);
            }
        }
        
    }
    
    private void generateArcs() {
        
        // TODO: generate arcs for the mid circle & goal zones
        // TODO: remove magic numbers
        final Arc3D goalLeftArcLeft = new Arc3D(180.0, 270.0, 1000.0, 10.0, 100);
        final Arc3D goalLeftArcRight = new Arc3D(90.0, 180.0, 1000.0, 10.0, 100);
        final Arc3D goalRightArcLeft = new Arc3D(0.0, 90.0, 1000.0, 10.0, 100);
        final Arc3D goalRightArcRight = new Arc3D(270, 360, 1000.0, 10.0, 100);
        final Arc3D midCircle = new Arc3D(0.0, 360.0, 1000, 10.0, 1000);
        
        final MeshView goalLeftLeftArcMesh = goalLeftArcLeft.MeshView();
        final MeshView goalLeftRightArcMesh = goalLeftArcRight.MeshView();
        final MeshView goalRightArcLeftMesh = goalRightArcLeft.MeshView();
        final MeshView goalRightArcRightMesh = goalRightArcRight.MeshView();
        final MeshView midCircleMesh = midCircle.MeshView();
        
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
        
        // TODO: use org.ssh.models
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
        
        this.GetGame().GetWorldGroup().getChildren().add(goalLeftLeftArcMesh);
        this.GetGame().GetWorldGroup().getChildren().add(goalLeftRightArcMesh);
        this.GetGame().GetWorldGroup().getChildren().add(goalRightArcLeftMesh);
        this.GetGame().GetWorldGroup().getChildren().add(goalRightArcRightMesh);
        this.GetGame().GetWorldGroup().getChildren().add(midCircleMesh);
    }
    
    private void generateGoals() {
        
        ////////////////////////////////////////////////
        // Top Goal
        ////////////////////////////////////////////////
        final Vector3f goalTopLeftPos = new Vector3f(
                (float) ((FieldGame.FIELD_WIDTH / 2.0) + (FieldGame.FIELD_GOAL_DEPTH / 2.0)),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                (float) -(FieldGame.FIELD_GOAL_WIDTH / 2.0));
        final Vector3f goalTopLeftDim = new Vector3f((float) FieldGame.FIELD_GOAL_DEPTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_LINE_WIDTH);
                
        final Vector3f goalTopRightPos = new Vector3f(
                (float) ((FieldGame.FIELD_WIDTH / 2.0) + (FieldGame.FIELD_GOAL_DEPTH / 2.0)),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                (float) (FieldGame.FIELD_GOAL_WIDTH / 2.0));
        final Vector3f goalTopRightDim = new Vector3f((float) FieldGame.FIELD_GOAL_DEPTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_LINE_WIDTH);
                
        final Vector3f goalTopTopPos = new Vector3f(
                (float) ((FieldGame.FIELD_WIDTH / 2.0) + FieldGame.FIELD_GOAL_DEPTH),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                0.0f);
        final Vector3f goalTopTopDim = new Vector3f((float) FieldGame.FIELD_GOAL_LINE_WIDTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_WIDTH);
                
        final Vector3f goalTopDefencePos = new Vector3f((float) ((FieldGame.FIELD_WIDTH / 2.0) - 500), 10.0f, 0.0f);
        final Vector3f goalTopDefenceDim = new Vector3f((float) FieldGame.FIELD_LINE_WIDTH, 10.0f, 500.0f);
        
        this.addLine(goalTopLeftPos, goalTopLeftDim).setOnMouseClicked(event -> {
            
            // TODO: Show context menu
            FieldGO.this._goalContextMenu.Translate(goalTopLeftPos.x - 500, goalTopLeftPos.y, goalTopLeftPos.z - 500);
            FieldGO.this._goalContextMenu.Rotate(0.0, 90.0, 0.0);
            FieldGO.this._goalContextMenu.Show();
            
        });
        this.addLine(goalTopRightPos, goalTopRightDim).setOnMouseClicked(event -> {
            
            // TODO: Show context menu
            FieldGO.this._goalContextMenu.Translate(goalTopRightPos.x - 500,
                    goalTopRightPos.y,
                    goalTopRightPos.z + 500);
            FieldGO.this._goalContextMenu.Rotate(0.0, 90.0, 0.0);
            FieldGO.this._goalContextMenu.Show();
        });
        ;
        this.addLine(goalTopTopPos, goalTopTopDim).setOnMouseClicked(event -> {
            
            // TODO: Show context menu
            FieldGO.this._goalContextMenu.Translate(goalTopTopPos.x,
                    goalTopTopPos.y + (goalTopTopDim.y / 2.0),
                    goalTopTopPos.z);
            FieldGO.this._goalContextMenu.Rotate(0.0, 90.0, 0.0);
            FieldGO.this._goalContextMenu.Show();
        });
        ;
        
        this.addLine(goalTopDefencePos, goalTopDefenceDim);
        
        ////////////////////////////////////////////////
        // Bottom Goal
        ////////////////////////////////////////////////
        final Vector3f goalBottomLeftPos = new Vector3f(
                (float) (-(FieldGame.FIELD_WIDTH / 2.0) - (FieldGame.FIELD_GOAL_DEPTH / 2.0)),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                (float) -(FieldGame.FIELD_GOAL_WIDTH / 2.0));
        final Vector3f goalBottomLeftDim = new Vector3f((float) FieldGame.FIELD_GOAL_DEPTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_LINE_WIDTH);
                
        final Vector3f goalBottomRightPos = new Vector3f(
                (float) (-(FieldGame.FIELD_WIDTH / 2.0) - (FieldGame.FIELD_GOAL_DEPTH / 2.0)),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                (float) (FieldGame.FIELD_GOAL_WIDTH / 2.0));
        final Vector3f goalBottomRightDim = new Vector3f((float) FieldGame.FIELD_GOAL_DEPTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_LINE_WIDTH);
                
        final Vector3f goalBottomTopPos = new Vector3f(
                (float) (-(FieldGame.FIELD_WIDTH / 2.0) - FieldGame.FIELD_GOAL_DEPTH),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                0.0f);
        final Vector3f goalBottomTopDim = new Vector3f((float) FieldGame.FIELD_GOAL_LINE_WIDTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_WIDTH);
                
        final Vector3f goalBottomDefencePos = new Vector3f((float) -(FieldGame.FIELD_WIDTH / 2.0) + 500.0f,
                10.0f,
                0.0f);
        final Vector3f goalBottomDefenceDim = new Vector3f((float) FieldGame.FIELD_LINE_WIDTH, 10.0f, 500.0f);
        
        final Box goalBottomLeft = this.addLine(goalBottomLeftPos, goalBottomLeftDim);
        goalBottomLeft.setOnMouseClicked(event -> {
            
            // TODO: Show context menu
            FieldGO.this._goalContextMenu.Translate(goalBottomLeftPos.x - 500,
                    goalBottomLeftPos.y,
                    goalBottomLeftPos.z - 500);
            FieldGO.this._goalContextMenu.Rotate(0.0, 90.0, 0.0);
            FieldGO.this._goalContextMenu.Show();
            
        });
        
        this.addLine(goalBottomRightPos, goalBottomRightDim).setOnMouseClicked(event -> {
            
            // TODO: Show context menu
            FieldGO.this._goalContextMenu.Translate(goalBottomRightPos.x - 500,
                    goalBottomRightPos.y,
                    goalBottomRightPos.z + 500);
            FieldGO.this._goalContextMenu.Rotate(0.0, 90.0, 0.0);
            FieldGO.this._goalContextMenu.Show();
        });
        
        this.addLine(goalBottomTopPos, goalBottomTopDim).setOnMouseClicked(event -> {
            
            // TODO: Show context menu
            FieldGO.this._goalContextMenu.Translate(goalBottomTopPos.x,
                    goalBottomTopPos.y + (goalBottomTopDim.y / 2.0),
                    goalBottomTopPos.z);
            FieldGO.this._goalContextMenu.Rotate(0.0, 90.0, 0.0);
            FieldGO.this._goalContextMenu.Show();
        });
        
        this.addLine(goalBottomDefencePos, goalBottomDefenceDim);
    }
    
    private void generateLines() {
        
        /////////////////////////////////////////////////////
        // Outline
        /////////////////////////////////////////////////////
        final Vector3f midLinePos = new Vector3f(0.0f, 10.0f, 0.0f);
        final Vector3f midLineDim = new Vector3f(10.0f,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                (float) FieldGame.FIELD_DEPTH);
                
        final Vector3f leftSideLinePos = new Vector3f(0.0f, 10.0f, (float) (-FieldGame.FIELD_DEPTH / 2.0));
        final Vector3f leftSisdeLineDim = new Vector3f((float) FieldGame.FIELD_WIDTH,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                10.0f);
        final Vector3f rightSideLinePos = new Vector3f(0.0f, 10.0f, (float) (FieldGame.FIELD_DEPTH / 2.0));
        final Vector3f rightSideLineDim = new Vector3f((float) FieldGame.FIELD_WIDTH,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                10.0f);
                
        final Vector3f topSideLinePos = new Vector3f((float) (FieldGame.FIELD_WIDTH / 2.0), 10.0f, 0.0f);
        final Vector3f topSideLineDim = new Vector3f(10.0f,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                (float) FieldGame.FIELD_DEPTH);
        final Vector3f bottomSideLinePos = new Vector3f((float) -(FieldGame.FIELD_WIDTH / 2.0), 10.0f, 0.0f);
        final Vector3f bottomSideLineDim = new Vector3f(10.0f,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                (float) FieldGame.FIELD_DEPTH);
                
        this.addLine(midLinePos, midLineDim);
        this.addLine(leftSideLinePos, leftSisdeLineDim);
        this.addLine(rightSideLinePos, rightSideLineDim);
        this.addLine(topSideLinePos, topSideLineDim);
        this.addLine(bottomSideLinePos, bottomSideLineDim);
    }
    
    private void generateTiles() {
        
        // Loop through x axis
        for (int i = 0; i < (this._width / this._tileWidth); i++) {
            // Loop through z axis
            for (int j = 0; j < (this._height / this._tileHeight); j++) {
                
                // Create new box
                final Box tmpBox = new Box(this._tileWidth, FieldGame.FIELD_HEIGHT, this._tileHeight);
                
                // Translate tile into position
                tmpBox.setTranslateX(-(this._width / 2.0) + ((i * this._tileWidth) + (this._tileWidth / 2.0)));
                tmpBox.setTranslateZ(-(this._height / 2.0) + ((j * this._tileHeight) + (this._tileHeight / 2.0)));
                
                // Set box material
                tmpBox.setMaterial(this._grassMaterial);
                
                this.GetGame().GetWorldGroup().getChildren().add(tmpBox);
                
                this._fieldTiles.add(tmpBox);
            }
        }
    }
    
    @Override
    public void Initialize() {
        
        // Generate tiles
        this.generateTiles();
        
        // Generate lines
        this.generateLines();
        
        // Generate lines
        this.generateGoals();
        
        // Generate arcs
        this.generateArcs();
        
        this.GetGame().AddGameObject(this._goalContextMenu);
        this.GetGame().AddGameObject(this._penaltySpot1);
        this.GetGame().AddGameObject(this._penaltySpot2);
    }
    
    @Override
    public void Update(final long timeDivNano) {
    }
}
