package org.ssh.field3d.gameobjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ssh.field3d.FieldGame;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.shapes.Arc3D;
import org.ssh.field3d.core.shapes.FlatLine3D;
import org.ssh.field3d.gameobjects.contextmenus.GoalContextMenu;
import org.ssh.util.Logger;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

/**
 * FieldGO class. This class creates the field, the goals, lines and arcs.
 * 
 * @see GameObject.
 *      
 * @author marklef2
 */
// TODO: Remove magic numbers
// TODO: Change lines from box to FlatLine3D
// TODO: Read public statics from model
public class FieldGO extends GameObject {
    
    public static final double     FIELD_PENALTY_SPOT        = 1000.0;
    public static final double     FIELD_PENALTY_SPOT_SIZE   = 10.0;
                                                             
    public static final double     WEST_GOAL_ARC_LEFT_START  = 180.0;
    public static final double     WEST_GOAL_ARC_LEFT_END    = 270.0;
    public static final double     WEST_GOAL_ARC_RIGHT_START = 90.0;
    public static final double     WEST_GOAL_ARC_RIGHT_END   = 180.0;
                                                             
    public static final double     EAST_GOAL_ARC_RIGHT_START = 270.0;
    public static final double     EAST_GOAL_ARC_RIGHT_END   = 360.0;
    public static final double     EAST_GOAL_ARC_LEFT_START  = 0.0;
    public static final double     EAST_GOAL_ARC_LEFT_END    = 90.0;
                                                             
    public static final double     GOAL_ARC_RADIUS           = 1000.0;
    public static final double     GOAL_ARC_THICKNESS        = 10.0;
    public static final double     MID_CIRCLE_RADIUS         = 1000.0;
    public static final double     MID_CIRCLE_THICKNESS      = 10.0;
                                                             
    private static final Logger    LOG                       = Logger.getLogger("FieldGO");
    private static final String    GRASS_TEXTURE_FILE        = "./assets/textures/grass2.png";
                                                             
    private static final int       LINE_Y_OFFSET             = 10;
    private static final int       MID_CIRCLE_NUM_DIVISIONS  = 1000;
    private static final int       GOAL_ARC_NUM_DIVISIONS    = 100;
                                                             
    private final List<Box>        fieldBoxes;
    private final List<FlatLine3D> fieldLines;
                                   
    private final PhongMaterial    grassMaterial;
    private final GoalContextMenu  goalContextMenu;
    private final PenaltySpotGO    penaltySpotEast;
    private final PenaltySpotGO    penaltySpotWest;
                                   
    private final double           width, height;
    private final double           tileWidth, tileHeight;
                                   
    /**
     * Constructor
     * 
     * @param game
     *            The {@link GameObject}'s {@link Game}.
     * @param width
     *            The width as double.
     * @param height
     *            The height as double.
     */
    public FieldGO(final Game game, final double width, final double height) {
        
        // Initialize super class
        super(game);
        
        FileInputStream fileInput = null;
        
        // Creating lists for the tiles and lines
        this.fieldBoxes = new ArrayList<Box>();
        this.fieldLines = new ArrayList<FlatLine3D>();
        
        // Creating grass material with lawn green diffuse color
        this.grassMaterial = new PhongMaterial(Color.LAWNGREEN);
        
        // Creating new goal context menu
        this.goalContextMenu = new GoalContextMenu(this.GetGame(), 1000, 500);
        // Creating penalty spots
        this.penaltySpotEast = new PenaltySpotGO(this.GetGame(),
                new Vector3f((float) ((width / 2.0) - FieldGO.FIELD_PENALTY_SPOT), 20, 0),
                FieldGO.FIELD_PENALTY_SPOT_SIZE);
        this.penaltySpotWest = new PenaltySpotGO(this.GetGame(),
                new Vector3f((float) (-(width / 2.0) + FieldGO.FIELD_PENALTY_SPOT), 20, 0),
                FieldGO.FIELD_PENALTY_SPOT_SIZE);
                
        // Setting dimensions
        this.width = width;
        this.height = height;
        
        // Setting tile dimensions
        this.tileHeight = FieldGame.FIELD_TILE_DEPTH;
        this.tileWidth = FieldGame.FIELD_TILE_WIDTH;
        
        // Trying to load texture
        try {
            // Opening file
            fileInput = new FileInputStream(GRASS_TEXTURE_FILE);
            // Setting grass material
            this.grassMaterial.setDiffuseMap(new Image(fileInput));
            // Closing file
            fileInput.close();
        }
        catch (final FileNotFoundException fileNotFoundException) {
            
            // Log error
            LOG.info("Could not load " + GRASS_TEXTURE_FILE);
            LOG.exception(fileNotFoundException);
        }
        catch (IOException ioException) {
            
            // Log error
            LOG.finest(ioException.getStackTrace().toString());
            LOG.exception(ioException);
        }
    }
    
    /**
     * Initialize method. Generates tiles, lines, goals and arcs.
     */
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
        
        // Adding game objects to the game
        this.GetGame().AddGameObject(this.goalContextMenu);
        this.GetGame().AddGameObject(this.penaltySpotEast);
        this.GetGame().AddGameObject(this.penaltySpotWest);
    }
    
    /**
     * Update method.
     * 
     * @param timeDivNano
     *            Time difference in nanoseconds.
     */
    @Override
    public void Update(final long timeDivNano) {
    }
    
    /**
     * Destroy method. Destroys tiles and lines
     */
    @Override
    public void Destroy() {
        
        // Check if we need to remove field boxes
        if ((this.fieldBoxes != null) && (this.fieldBoxes.size() > 0)) {
            
            // Loop through field tiles
            for (final Box tmpBox : this.fieldBoxes) {
                
                // Remove box from list
                this.fieldBoxes.remove(tmpBox);
            }
        }
        
        // Check if we need to remove field lines
        if ((this.fieldLines != null) && (this.fieldLines.size() > 0)) {
            
            // Loop through field lines
            for (final FlatLine3D line : this.fieldLines) {
                
                // Remove line from list
                this.fieldLines.remove(line);
            }
        }
    }
    
    /**
     * addLine method. This method creates and adds flat lines to the world.
     * 
     * @param startX
     *            The x-coordinate for the start of the line.
     * @param startZ
     *            The z-coordinate for the start of the line.
     * @param endX
     *            The x-coordinate for the end of the line.
     * @param endZ
     *            The z-coordinate for the end of the line.
     * @param thickness
     *            The thickness of the line
     * @return The line created.
     */
    private FlatLine3D addLine(final double startX,
            final double startZ,
            final double endX,
            final double endZ,
            final double thickness) {
            
        final FlatLine3D line = new FlatLine3D(startX, startZ, endX, endZ, thickness);
        final MeshView lineMesh = line.GetMeshView();
        
        // Add to lines
        this.fieldLines.add(line);
        
        // Translate line up
        lineMesh.setTranslateY(LINE_Y_OFFSET);
        
        // Add to world group
        Platform.runLater(() -> this.GetGame().GetWorldGroup().getChildren().add(line.GetMeshView()));
        
        // Return the line
        return line;
    }
    
    /**
     * addBox method. This method creates and adds an box to the world.
     * 
     * @param position
     *            The position of the box
     * @param dimension
     *            The dimension of the box
     * @return The box created.
     */
    private Box addBox(Vector3f position, Vector3f dimension) {
        
        // Creating new box
        Box rtn = new Box();
        
        // Setting position
        rtn.setTranslateX(position.x);
        rtn.setTranslateY(position.y);
        rtn.setTranslateZ(position.z);
        
        // Setting dimensions
        rtn.setWidth(dimension.x);
        rtn.setHeight(dimension.y);
        rtn.setDepth(dimension.z);
        
        addBox(rtn);
        
        // Return the box
        return rtn;
    }
    
    /**
     * addBox method. This method adds an box to the world.
     * 
     * @param box
     *            The {@link Box} to be added.
     */
    private void addBox(Box box) {
        
        // Add box to field boxes
        this.fieldBoxes.add(box);
        
        // Add box to the world group
        Platform.runLater(() -> this.GetGame().GetWorldGroup().getChildren().add(box));
    }
    
    /**
     * generateArcs method. This method generates the arcs of the field.
     */
    private void generateArcs() {
        
        // Create defense area arcs
        final MeshView goalWestArcLeftMesh = new Arc3D(WEST_GOAL_ARC_LEFT_START,
                WEST_GOAL_ARC_LEFT_END,
                GOAL_ARC_RADIUS,
                GOAL_ARC_THICKNESS,
                GOAL_ARC_NUM_DIVISIONS).MeshView();
        final MeshView goalWestArcRightMesh = new Arc3D(WEST_GOAL_ARC_RIGHT_START,
                WEST_GOAL_ARC_RIGHT_END,
                GOAL_ARC_RADIUS,
                GOAL_ARC_THICKNESS,
                GOAL_ARC_NUM_DIVISIONS).MeshView();
                
        final MeshView goalEastArcLeftMesh = new Arc3D(EAST_GOAL_ARC_LEFT_START,
                EAST_GOAL_ARC_LEFT_END,
                GOAL_ARC_RADIUS,
                GOAL_ARC_THICKNESS,
                GOAL_ARC_NUM_DIVISIONS).MeshView();
        final MeshView goalEastArcRightMesh = new Arc3D(EAST_GOAL_ARC_RIGHT_START,
                EAST_GOAL_ARC_RIGHT_END,
                GOAL_ARC_RADIUS,
                GOAL_ARC_THICKNESS,
                GOAL_ARC_NUM_DIVISIONS).MeshView();
                
        final MeshView midCircleMesh = new Arc3D(0.0,
                360.0,
                MID_CIRCLE_RADIUS,
                MID_CIRCLE_THICKNESS,
                MID_CIRCLE_NUM_DIVISIONS).MeshView();
                
        // Setting rotation axis
        midCircleMesh.setRotationAxis(Rotate.X_AXIS);
        goalEastArcLeftMesh.setRotationAxis(Rotate.X_AXIS);
        goalEastArcRightMesh.setRotationAxis(Rotate.X_AXIS);
        goalWestArcLeftMesh.setRotationAxis(Rotate.X_AXIS);
        goalWestArcRightMesh.setRotationAxis(Rotate.X_AXIS);
        
        // Rotate arcs
        midCircleMesh.setRotate(90.0);
        goalWestArcLeftMesh.setRotate(90.0);
        goalWestArcRightMesh.setRotate(90.0);
        goalEastArcLeftMesh.setRotate(90.0);
        goalEastArcRightMesh.setRotate(90.0);
        
        // Translate arcs into position
        goalWestArcRightMesh.setTranslateX((FieldGame.FIELD_WIDTH / 2.0));
        goalWestArcRightMesh.setTranslateZ(500);
        goalWestArcRightMesh.setTranslateY(-235);
        
        goalWestArcLeftMesh.setTranslateX(FieldGame.FIELD_WIDTH / 2.0);
        goalWestArcLeftMesh.setTranslateY(265);
        goalWestArcLeftMesh.setTranslateZ(-500);
        
        goalEastArcRightMesh.setTranslateX(-(FieldGame.FIELD_WIDTH / 2.0));
        goalEastArcRightMesh.setTranslateY(265);
        goalEastArcRightMesh.setTranslateZ(-500);
        
        goalEastArcLeftMesh.setTranslateX(-(FieldGame.FIELD_WIDTH / 2.0));
        goalEastArcLeftMesh.setTranslateY(-235);
        goalEastArcLeftMesh.setTranslateZ(500);
        
        // Translate mid circle up
        midCircleMesh.setTranslateY(LINE_Y_OFFSET);
        
        // Setting face culling
        goalWestArcLeftMesh.setCullFace(CullFace.NONE);
        goalWestArcRightMesh.setCullFace(CullFace.NONE);
        goalEastArcLeftMesh.setCullFace(CullFace.NONE);
        goalEastArcRightMesh.setCullFace(CullFace.NONE);
        midCircleMesh.setCullFace(CullFace.NONE);
        
        // Add arcs to the world
        Platform.runLater(() -> {
            
            this.GetGame().GetWorldGroup().getChildren().add(goalWestArcLeftMesh);
            this.GetGame().GetWorldGroup().getChildren().add(goalWestArcRightMesh);
            this.GetGame().GetWorldGroup().getChildren().add(goalEastArcLeftMesh);
            this.GetGame().GetWorldGroup().getChildren().add(goalEastArcRightMesh);
            this.GetGame().GetWorldGroup().getChildren().add(midCircleMesh);
        });
    }
    
    /**
     * generateGoals method. This method generates the goals on the field.
     */
    private void generateGoals() {
        
        // TODO: remove magic numbers
        
        // Calculating West goal left border
        final Vector3f goalWestLeftPos = new Vector3f(
                (float) ((FieldGame.FIELD_WIDTH / 2.0) + (FieldGame.FIELD_GOAL_DEPTH / 2.0)),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                (float) -(FieldGame.FIELD_GOAL_WIDTH / 2.0));
        final Vector3f goalWestLeftDim = new Vector3f((float) FieldGame.FIELD_GOAL_DEPTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_LINE_WIDTH);
                
        // Calculating West goal right border
        final Vector3f goalWestRightPos = new Vector3f(
                (float) ((FieldGame.FIELD_WIDTH / 2.0) + (FieldGame.FIELD_GOAL_DEPTH / 2.0)),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                (float) (FieldGame.FIELD_GOAL_WIDTH / 2.0));
        final Vector3f goalWestRightDim = new Vector3f((float) FieldGame.FIELD_GOAL_DEPTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_LINE_WIDTH);
                
        // Calculating West goal back border
        final Vector3f goalWestBackPos = new Vector3f(
                (float) ((FieldGame.FIELD_WIDTH / 2.0) + FieldGame.FIELD_GOAL_DEPTH),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                0.0f);
        final Vector3f goalWestBackDim = new Vector3f((float) FieldGame.FIELD_GOAL_LINE_WIDTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_WIDTH);
                
        // Add west left box
        this.addBox(goalWestLeftPos, goalWestLeftDim).setOnMouseClicked(event -> {
            
            // Translate context menu
            this.goalContextMenu.Translate(goalWestLeftPos.x - 500, goalWestLeftPos.y, goalWestLeftPos.z - 500);
            // Show context menu
            this.goalContextMenu.show();
        });
        // Add west right box
        this.addBox(goalWestRightPos, goalWestRightDim).setOnMouseClicked(event -> {
            
            // Translate context menu
            this.goalContextMenu.Translate(goalWestRightPos.x - 500, goalWestRightPos.y, goalWestRightPos.z + 500);
            // Show context menu
            this.goalContextMenu.show();
        });
        // Add west back box
        this.addBox(goalWestBackPos, goalWestBackDim).setOnMouseClicked(event -> {
            
            // Translate context menu
            this.goalContextMenu.Translate(goalWestBackPos.x,
                    goalWestBackPos.y + (goalWestBackDim.y / 2.0),
                    goalWestBackPos.z);
            // Show context menu
            this.goalContextMenu.show();
        });
        
        // Calculating East goal left border
        final Vector3f goalEastLeftPos = new Vector3f(
                (float) (-(FieldGame.FIELD_WIDTH / 2.0) - (FieldGame.FIELD_GOAL_DEPTH / 2.0)),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                (float) -(FieldGame.FIELD_GOAL_WIDTH / 2.0));
        final Vector3f goalEastLeftDim = new Vector3f((float) FieldGame.FIELD_GOAL_DEPTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_LINE_WIDTH);
                
        // Calculating East goal right border
        final Vector3f goalEastRightPos = new Vector3f(
                (float) (-(FieldGame.FIELD_WIDTH / 2.0) - (FieldGame.FIELD_GOAL_DEPTH / 2.0)),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                (float) (FieldGame.FIELD_GOAL_WIDTH / 2.0));
        final Vector3f goalEastRightDim = new Vector3f((float) FieldGame.FIELD_GOAL_DEPTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_LINE_WIDTH);
                
        // Calculating East goal back border
        final Vector3f goalEastBackPos = new Vector3f(
                (float) (-(FieldGame.FIELD_WIDTH / 2.0) - FieldGame.FIELD_GOAL_DEPTH),
                10.0f + (float) (FieldGame.FIELD_GOAL_HEIGHT / 2.0),
                0.0f);
        final Vector3f goalEastBackDim = new Vector3f((float) FieldGame.FIELD_GOAL_LINE_WIDTH,
                (float) FieldGame.FIELD_GOAL_HEIGHT,
                (float) FieldGame.FIELD_GOAL_WIDTH);
                
        // Add east goal, left border to the field and add a mouse click listener
        this.addBox(goalEastLeftPos, goalEastLeftDim).setOnMouseClicked(event -> {
            
            // Translate context menu
            this.goalContextMenu.Translate(goalEastLeftPos.x - 500, goalEastLeftPos.y, goalEastLeftPos.z - 500);
            // Show context menu
            this.goalContextMenu.show();
        });
        // Add east goal, right border to the field and add a mouse click listener
        this.addBox(goalEastRightPos, goalEastRightDim).setOnMouseClicked(event -> {
            
            // Translate context menu
            this.goalContextMenu.Translate(goalEastRightPos.x - 500, goalEastRightPos.y, goalEastRightPos.z + 500);
            // Show context menu
            this.goalContextMenu.show();
        });
        // Add east goal, back border to the field and add a mouse click listener
        this.addBox(goalEastBackPos, goalEastBackDim).setOnMouseClicked(event -> {
            
            // Translate context menu
            this.goalContextMenu.Translate(goalEastBackPos.x,
                    goalEastBackPos.y + (goalEastBackDim.y / 2.0),
                    goalEastBackPos.z);
            // Show context menu
            this.goalContextMenu.show();
        });
        
    }
    
    /**
     * generateLines method. This method generates the lines on the field.
     */
    private void generateLines() {
        
        // TODO: change lines to FlatLine3D
        // TODO: remove magic numbers
        
        // Calculate mid line
        final Vector3f midLinePos = new Vector3f(0.0f, 10.0f, 0.0f);
        final Vector3f midLineDim = new Vector3f(10.0f,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                (float) FieldGame.FIELD_DEPTH);
                
        // Calculate south line
        final Vector3f southSideLinePos = new Vector3f(0.0f, 10.0f, (float) (-FieldGame.FIELD_DEPTH / 2.0));
        final Vector3f southSisdeLineDim = new Vector3f((float) FieldGame.FIELD_WIDTH,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                10.0f);
        // Calculate north line
        final Vector3f northSideLinePos = new Vector3f(0.0f, 10.0f, (float) (FieldGame.FIELD_DEPTH / 2.0));
        final Vector3f northSideLineDim = new Vector3f((float) FieldGame.FIELD_WIDTH,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                10.0f);
        // Calculate west line
        final Vector3f westSideLinePos = new Vector3f((float) (FieldGame.FIELD_WIDTH / 2.0), 10.0f, 0.0f);
        final Vector3f westSideLineDim = new Vector3f(10.0f,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                (float) FieldGame.FIELD_DEPTH);
        final Vector3f eastSideLinePos = new Vector3f((float) -(FieldGame.FIELD_WIDTH / 2.0), 10.0f, 0.0f);
        final Vector3f eastSideLineDim = new Vector3f(10.0f,
                (float) FieldGame.FIELD_LINE_HEIGHT,
                (float) FieldGame.FIELD_DEPTH);
                
        // Calculate east defense line
        final Vector3f goalEastDefencePos = new Vector3f((float) -(FieldGame.FIELD_WIDTH / 2.0) + 500.0f, 10.0f, 0.0f);
        final Vector3f goalEastDefenceDim = new Vector3f((float) FieldGame.FIELD_LINE_WIDTH, 10.0f, 500.0f);
        // Calculate west defense line
        final Vector3f goalWestDefencePos = new Vector3f((float) ((FieldGame.FIELD_WIDTH / 2.0) - 500), 10.0f, 0.0f);
        final Vector3f goalWestDefenceDim = new Vector3f((float) FieldGame.FIELD_LINE_WIDTH, 10.0f, 500.0f);
        
        // Adding boxes to field
        this.addBox(midLinePos, midLineDim);
        this.addBox(southSideLinePos, southSisdeLineDim);
        this.addBox(northSideLinePos, northSideLineDim);
        this.addBox(westSideLinePos, westSideLineDim);
        this.addBox(eastSideLinePos, eastSideLineDim);
        this.addBox(goalEastDefencePos, goalEastDefenceDim);
        this.addBox(goalWestDefencePos, goalWestDefenceDim);
    }
    
    /**
     * generateTiles method. This method generates the field tiles.
     */
    private void generateTiles() {
        
        // Loop through x axis
        for (int i = 0; i < (this.width / this.tileWidth); i++) {
            // Loop through z axis
            for (int j = 0; j < (this.height / this.tileHeight); j++) {
                
                // Create new box
                final Box tmpBox = new Box(this.tileWidth, FieldGame.FIELD_HEIGHT, this.tileHeight);
                
                // Translate tile into position
                tmpBox.setTranslateX(-(this.width / 2.0) + ((i * this.tileWidth) + (this.tileWidth / 2.0)));
                tmpBox.setTranslateZ(-(this.height / 2.0) + ((j * this.tileHeight) + (this.tileHeight / 2.0)));
                
                // Set box material
                tmpBox.setMaterial(this.grassMaterial);
                
                // Add box to field
                this.addBox(tmpBox);
            }
        }
    }
}
