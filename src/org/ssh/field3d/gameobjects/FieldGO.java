package org.ssh.field3d.gameobjects;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.ssh.field3d.FieldGame;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.shapes.FlatArc3D;
import org.ssh.field3d.core.shapes.FlatLine3D;
import org.ssh.field3d.gameobjects.overlay.contextmenus.ContextOverlayGO;
import org.ssh.util.Logger;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import protobuf.Geometry.Vector2f;

// TODO: Auto-generated Javadoc
/**
 * FieldGO class. This class creates the field, the goals, lines and arcs.
 *
 * @author marklef2
 * @see GameObject.
 *      
 */
// TODO: Remove magic numbers
// TODO: Change lines from box to FlatLine3D
// TODO: Read public statics from model
public class FieldGO extends GameObject {
    
    /** The penalty spot distance form goal. */
    public static final double     FIELD_PENALTY_SPOT        = 1000.0;
                                                             
    /** The penalty spot size. */
    public static final double     FIELD_PENALTY_SPOT_SIZE   = 10.0;
                                                             
    /** The west goal left arc starting angle. */
    public static final double     WEST_GOAL_ARC_LEFT_START  = 180.0;
                                                             
    /** The west goal left arc ending angle. */
    public static final double     WEST_GOAL_ARC_LEFT_END    = 270.0;
                                                             
    /** The west goal right arc starting angle. */
    public static final double     WEST_GOAL_ARC_RIGHT_START = 90.0;
                                                             
    /** The west goal right arc ending angle. */
    public static final double     WEST_GOAL_ARC_RIGHT_END   = 180.0;
                                                             
    /** The east goal right arc starting angle. */
    public static final double     EAST_GOAL_ARC_RIGHT_START = 270.0;
                                                             
    /** The east goal right arc ending angle. */
    public static final double     EAST_GOAL_ARC_RIGHT_END   = 360.0;
                                                             
    /** The east goal left arc starting angle. */
    public static final double     EAST_GOAL_ARC_LEFT_START  = 0.0;
                                                             
    /** The east goal left arc ending angle. */
    public static final double     EAST_GOAL_ARC_LEFT_END    = 90.0;
                                                             
    /** The goal arc diameter. */
    public static final double     GOAL_ARC_DIAMETER         = 1000.0;
                                                             
    /** The goal arc thickness. */
    public static final double     GOAL_ARC_THICKNESS        = 10.0;
                                                             
    /** The mid circle radius. */
    public static final double     MID_CIRCLE_RADIUS         = 1000.0;
                                                             
    /** The mid circle thickness. */
    public static final double     MID_CIRCLE_THICKNESS      = 10.0;
                                                             
    /** The logger. */
    private static final Logger    LOG                       = Logger.getLogger("FieldGO");
                                                             
    /** The file path for the grass texture. */
    private static final String    GRASS_TEXTURE_FILE        = "/org/ssh/view/textures/field/grass.png";
                                                             
    /** The line offset. */
    private static final double    LINE_Y_OFFSET             = 10.0;
                                                             
    /** The number of divisions in the mid circle. */
    private static final int       MID_CIRCLE_NUM_DIVISIONS  = 1000;
                                                             
    /** The number of divisions in the goal arcs. */
    private static final int       GOAL_ARC_NUM_DIVISIONS    = 100;
                                                             
    /** The tiles of the field. */
    private final List<Box>        fieldBoxes;
                                   
    /** The lines of the field. */
    private final List<FlatLine3D> fieldLines;
                                   
    /** The grass material. */
    private final PhongMaterial    grassMaterial;
                                   
    /** The east penalty spot. */
    private final PenaltySpotGO    penaltySpotEast;
                                   
    /** The west penalty spot. */
    private final PenaltySpotGO    penaltySpotWest;
                                   
    /** The width of the field. */
    private final double           width;
                                   
    /** The depth of the field. */
    private final double           depth;
                                   
    /** The width of the tile. */
    private final double           tileWidth;
                                   
    /** the depth of the tile. */
    private final double           tileDepth;
                                   
    /** The context overlay go. */
    private ContextOverlayGO       contextOverlayGO;
                                   
    /**
     * Constructor.
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
        
        // Creating lists for the tiles and lines
        this.fieldBoxes = new ArrayList<Box>();
        this.fieldLines = new ArrayList<FlatLine3D>();
        
        // Creating grass material with lawn green diffuse color
        this.grassMaterial = new PhongMaterial(Color.LAWNGREEN);
        
        // Creating penalty spots
        this.penaltySpotEast = new PenaltySpotGO(this.getGame(),
                new Vector3f((float) ((width / 2.0) - FieldGO.FIELD_PENALTY_SPOT), 20, 0),
                FieldGO.FIELD_PENALTY_SPOT_SIZE);
        this.penaltySpotWest = new PenaltySpotGO(this.getGame(),
                new Vector3f((float) (-(width / 2.0) + FieldGO.FIELD_PENALTY_SPOT), 20, 0),
                FieldGO.FIELD_PENALTY_SPOT_SIZE);
                
        this.contextOverlayGO = new ContextOverlayGO(getGame());
        
        // Setting dimensions
        this.width = width;
        this.depth = height;
        
        // Setting tile dimensions
        this.tileDepth = FieldGame.FIELD_TILE_DEPTH;
        this.tileWidth = FieldGame.FIELD_TILE_WIDTH;
        
        // Getting resource
        InputStream textureInputStream = this.getClass().getResourceAsStream(GRASS_TEXTURE_FILE);
        
        // Check if the texture file exists
        if (textureInputStream != null) {
            
            // Setting diffuse map
            this.grassMaterial.setDiffuseMap(new Image(textureInputStream));
        }
        else {
            
            // Log error
            LOG.info("Could not load " + GRASS_TEXTURE_FILE);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Generate tiles
        this.generateTiles();
        
        // Generate lines
        this.generateLines();
        
        // Generate lines
        this.generateGoals();
        
        // Generate arcs
        this.generateArcs();
        
        // Adding game objects to the game
        this.getGame().addGameObject(this.penaltySpotEast);
        this.getGame().addGameObject(this.penaltySpotWest);
        
        this.getGame().addGameObject(this.contextOverlayGO);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(final long timeDivNano) {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
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
     * @param start
     *            The {@link Vector2f starting point} of the line.
     * @param end
     *            The {@link Vector2f ending point} of the line.
     * @param thickness
     *            The thickness of the line.
     * @return The {@link FlatLine3D}.
     */
    private FlatLine3D addLine(final Vector2f start, final Vector2f end, final double thickness) {
        
        final FlatLine3D line = new FlatLine3D(start, end, thickness);
        final MeshView lineMesh = line.getMeshView();
        
        // Add to lines
        this.fieldLines.add(line);
        
        // Translate line up
        lineMesh.setTranslateY(LINE_Y_OFFSET);
        
        // Add to world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().add(line.getMeshView()));
        
        // Return the line
        return line;
    }
    
    /**
     * addBox method. This method creates and adds an box to the world.
     * 
     * @param position
     *            The {@link Vector3f position} of the box.
     * @param dimension
     *            The {@link Vector3f dimension} of the box.
     * @return The {@link Box box} created.
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
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().add(box));
    }
    
    /**
     * generateArcs method. This method generates the arcs of the field.
     */
    private void generateArcs() {
        
        // Create defense area arcs
        final MeshView goalWestArcLeftMesh = new FlatArc3D(WEST_GOAL_ARC_LEFT_START,
                WEST_GOAL_ARC_LEFT_END,
                GOAL_ARC_DIAMETER,
                GOAL_ARC_THICKNESS,
                GOAL_ARC_NUM_DIVISIONS).MeshView();
        final MeshView goalWestArcRightMesh = new FlatArc3D(WEST_GOAL_ARC_RIGHT_START,
                WEST_GOAL_ARC_RIGHT_END,
                GOAL_ARC_DIAMETER,
                GOAL_ARC_THICKNESS,
                GOAL_ARC_NUM_DIVISIONS).MeshView();
                
        final MeshView goalEastArcLeftMesh = new FlatArc3D(EAST_GOAL_ARC_LEFT_START,
                EAST_GOAL_ARC_LEFT_END,
                GOAL_ARC_DIAMETER,
                GOAL_ARC_THICKNESS,
                GOAL_ARC_NUM_DIVISIONS).MeshView();
        final MeshView goalEastArcRightMesh = new FlatArc3D(EAST_GOAL_ARC_RIGHT_START,
                EAST_GOAL_ARC_RIGHT_END,
                GOAL_ARC_DIAMETER,
                GOAL_ARC_THICKNESS,
                GOAL_ARC_NUM_DIVISIONS).MeshView();
                
        final MeshView midCircleMesh = new FlatArc3D(0.0,
                360.0,
                MID_CIRCLE_RADIUS,
                MID_CIRCLE_THICKNESS,
                MID_CIRCLE_NUM_DIVISIONS).MeshView();
                
        // Translate arcs into position
        goalWestArcRightMesh.setTranslateX((FieldGame.FIELD_WIDTH / 2.0));
        goalWestArcRightMesh.setTranslateY(LINE_Y_OFFSET);
        goalWestArcRightMesh.setTranslateZ(GOAL_ARC_DIAMETER / 4.0);
        
        goalWestArcLeftMesh.setTranslateX(FieldGame.FIELD_WIDTH / 2.0);
        goalWestArcLeftMesh.setTranslateY(LINE_Y_OFFSET);
        goalWestArcLeftMesh.setTranslateZ(-GOAL_ARC_DIAMETER / 4.0);
        
        goalEastArcRightMesh.setTranslateX(-(FieldGame.FIELD_WIDTH / 2.0));
        goalEastArcRightMesh.setTranslateY(LINE_Y_OFFSET);
        goalEastArcRightMesh.setTranslateZ(-GOAL_ARC_DIAMETER / 4.0);
        
        goalEastArcLeftMesh.setTranslateX(-(FieldGame.FIELD_WIDTH / 2.0));
        goalEastArcLeftMesh.setTranslateY(LINE_Y_OFFSET);
        goalEastArcLeftMesh.setTranslateZ(GOAL_ARC_DIAMETER / 4.0);
        
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
            
            this.getGame().getWorldGroup().getChildren().add(goalWestArcLeftMesh);
            this.getGame().getWorldGroup().getChildren().add(goalWestArcRightMesh);
            this.getGame().getWorldGroup().getChildren().add(goalEastArcLeftMesh);
            this.getGame().getWorldGroup().getChildren().add(goalEastArcRightMesh);
            this.getGame().getWorldGroup().getChildren().add(midCircleMesh);
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
        this.addBox(goalWestLeftPos, goalWestLeftDim);
        // Add west right box
        this.addBox(goalWestRightPos, goalWestRightDim);
        // Add west back box
        this.addBox(goalWestBackPos, goalWestBackDim);
        
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
        this.addBox(goalEastLeftPos, goalEastLeftDim);
        // Add east goal, right border to the field and add a mouse click listener
        this.addBox(goalEastRightPos, goalEastRightDim);
        // Add east goal, back border to the field and add a mouse click listener
        this.addBox(goalEastBackPos, goalEastBackDim);
        
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
            for (int j = 0; j < (this.depth / this.tileDepth); j++) {
                
                // Create new box
                final Box tmpBox = new Box(this.tileWidth, FieldGame.FIELD_HEIGHT, this.tileDepth);
                
                // Translate tile into position
                tmpBox.setTranslateX(-(this.width / 2.0) + ((i * this.tileWidth) + (this.tileWidth / 2.0)));
                tmpBox.setTranslateZ(-(this.depth / 2.0) + ((j * this.tileDepth) + (this.tileDepth / 2.0)));
                
                // Set box material
                tmpBox.setMaterial(this.grassMaterial);
                
                // Hook on mouse clicked event
                tmpBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        
                        // If the mouse event was with the right mouse button
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            
                            // Getting location on field
                            System.out.println();
                            Point3D locOnField = mouseEvent.getPickResult().getIntersectedNode().localToParent(mouseEvent.getPickResult().getIntersectedPoint());
                            
                            // Set the location on the field of the context overlay
                            contextOverlayGO.setFieldLoc(new Point2D(locOnField.getX(), locOnField.getZ()));
                            
                            // Show context menu
                            contextOverlayGO.show();
                        }
                    }
                });
                
                // Add box to field
                this.addBox(tmpBox);
            }
        }
    }
}
