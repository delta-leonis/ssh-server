package org.ssh.field3d.gameobjects.geometry;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.ssh.field3d.FieldGame;
import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.shapes.FlatArc3D;
import org.ssh.field3d.core.shapes.FlatLine3D;
import org.ssh.field3d.gameobjects.GeometryGameObject;
import org.ssh.field3d.gameobjects.overlay.ContextOverlayGO;
import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.models.Goal;
import org.ssh.models.Model;
import org.ssh.util.Logger;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import protobuf.Geometry.FieldCicularArc;
import protobuf.Geometry.FieldLineSegment;
import protobuf.Geometry.Vector2f;

/**
 * FieldGO class. This class creates the field, the goals, lines and arcs.
 *
 * @see GeometryGameObject
 * @author marklef2
 */
public class FieldGO extends GeometryGameObject {
    
    /** The height of the field. */
    public static final double          FIELD_HEIGHT            = 1.0;
                                                                
    /** The penalty spot distance form goal */
    public static final double          FIELD_PENALTY_SPOT      = 1000.0;
                                                                
    /** The penalty spot size. */
    public static final double          FIELD_PENALTY_SPOT_SIZE = 10.0;
                                                                
    /** The width of a tile. */
    public static final double          FIELD_TILE_WIDTH        = 500.0;
                                                                
    /** The height of a tile. */
    public static final double          FIELD_TILE_DEPTH        = 500.0;
                                                                
    /** The logger. */
    private static final Logger         LOG                     = Logger.getLogger();
                                                                
    /** The file path for the grass texture. */
    private static final String         GRASS_TEXTURE_FILE      = "/org/ssh/view/textures/field/grass.png";
                                                                
    /** The line offset. */
    private static final double         LINE_Y_OFFSET           = 10.0;
                                                                
    /** The number of divisions in the mid circle. */
    private static final int            ARC_NUM_DIVISIONS       = 1000;
                                                                
    /** The tiles of the field. */
    private final Queue<Box>            fieldTiles;
    /** The lines of the field. */
    private final Queue<FlatLine3D>     fieldLines;
                                        
    /** The arcs of the field. */
    private final Queue<FlatArc3D>      fieldArcs;
                                        
    /** The goal game objects. */
    private final Queue<GoalGameObject> goalGameObjects;
                                        
    /** The grass material. */
    private final PhongMaterial         grassMaterial;
                                        
    /** The width of the tile. */
    private final double                tileWidth;
                                        
    /** the depth of the tile. */
    private final double                tileDepth;
                                        
    /** The field vision model. */
    private Field                       fieldVisionModel;
                                        
    /** The context menu overlay game object */
    private final ContextOverlayGO      contextOverlayGO;
                                        
    /** The east penalty spot. */
    private PenaltySpotGO               penaltySpotEast;
                                        
    /** The west penalty spot. */
    private PenaltySpotGO               penaltySpotWest;

    /** The group for the field. */
    private final Group                 fieldGroup;
    /** The group for the tiles of the field. */
    private final Group                 fieldTileGroup;

    /**
     * Constructor. This instantiates a new FieldGO object.
     *
     * @param game
     *          The {@link Game} of the game object.
     */
    public FieldGO(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Creating lists for the tiles and lines
        this.fieldTiles = new ConcurrentLinkedQueue<>();
        this.fieldLines = new ConcurrentLinkedQueue<>();
        this.fieldArcs = new ConcurrentLinkedQueue<>();
        this.goalGameObjects = new ConcurrentLinkedQueue<>();

        // Creating new context overlay game object
        this.contextOverlayGO = new ContextOverlayGO(game);

        // Creating groups
        this.fieldGroup = new Group();
        this.fieldTileGroup = new Group();
        
        // Creating grass material with lawn green diffuse color
        this.grassMaterial = new PhongMaterial(Color.LAWNGREEN);
        
        // Setting tile dimensions
        this.tileDepth = FIELD_TILE_DEPTH;
        this.tileWidth = FIELD_TILE_WIDTH;

        // Add tile group to the field group
        this.fieldGroup.getChildren().add(this.fieldTileGroup);
        
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
        
        // Add context menu to the game objects of the game
        this.getGame().addGameObject(this.contextOverlayGO);

        // Execute on UI thread
        Platform.runLater(() -> {

            // Check if the world group does not contain the field group
            if (!this.getGame().getWorldGroup().getChildren().contains(this.fieldGroup)) {

                // Add the field group to the world
                this.getGame().getWorldGroup().getChildren().add(this.fieldGroup);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {

        // Execute on UI thread
        Platform.runLater(() -> {

            // Check if the world group contains the field group
            if (this.getGame().getWorldGroup().getChildren().contains(this.fieldGroup)) {

                // Remove the field group from the world group
                this.getGame().getWorldGroup().getChildren().remove(this.fieldGroup);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateGeometry() {
        
        // Trying to get Field model
        Optional<Model> optionalModel = Models.get("field");
        
        // If there is a model present
        if (optionalModel.isPresent()) {
            
            // Setting vision model
            this.fieldVisionModel = (Field) optionalModel.get();
            
            // Clear arcs
            this.clearArcs();
            // Clear lines
            this.clearLines();
            // Clear boxes
            this.clearBoxes();
            // Clear goals
            this.clearGoals();
            
            // Remove penalty spots
            ((FieldGame)this.getGame()).removeGeometryGameObject(penaltySpotEast);
            ((FieldGame)this.getGame()).removeGeometryGameObject(penaltySpotWest);
            
            // Generate tiles
            this.generateTiles();
            // Generate arcs
            this.generateArcs();
            // Generate goals
            this.generateGoals();
            // Generate lines
            this.generateLines();
            
            // Creating penalty spots
            this.penaltySpotEast = new PenaltySpotGO(this.getGame(),
                    new Point3D(((fieldVisionModel.getFieldLength() / 2.0) - FieldGO.FIELD_PENALTY_SPOT),
                            FieldGO.LINE_Y_OFFSET,
                            0.0),
                    FieldGO.FIELD_PENALTY_SPOT_SIZE,
                    this.fieldGroup);
            
            this.penaltySpotWest = new PenaltySpotGO(this.getGame(),
                    new Point3D((-(fieldVisionModel.getFieldLength() / 2.0) + FieldGO.FIELD_PENALTY_SPOT),
                            FieldGO.LINE_Y_OFFSET,
                            0.0),
                    FieldGO.FIELD_PENALTY_SPOT_SIZE,
                    this.fieldGroup);
                    
            // Adding new penalty spots
            ((FieldGame)this.getGame()).addGeometryGameObject(penaltySpotEast);
            ((FieldGame)this.getGame()).addGeometryGameObject(penaltySpotWest);

            // Rotate the entire field 180 degrees around the y-axis
            this.fieldGroup.setRotationAxis(Rotate.Y_AXIS);
            this.fieldGroup.setRotate(180);
            // Rotate the tiles of the field 180 degrees around the x-axis
            this.fieldTileGroup.setRotationAxis(Rotate.X_AXIS);
            this.fieldTileGroup.setRotate(180);
        }
    }

    /**
     * Add line method. This method adds an {@link FlatLine3D} to the field.
     *
     * @param start
     *              The start {@link Point2D location} of the {@link FlatLine3D line}.
     * @param end
     *              The end {@link Point2D location} of the {@link FlatLine3D line}.
     * @param thickness
     *              The thickness of the line.
     * @return The {@link FlatLine3D} added to the field.
     */
    private FlatLine3D addLine(final Point2D start, final Point2D end, final double thickness) {
        
        // Creating new flat line
        final FlatLine3D line = new FlatLine3D(start, end, thickness);
        
        // Add to lines
        this.fieldLines.add(line);
        
        // Translate a bit upwards
        line.getMeshView().setTranslateY(FieldGO.LINE_Y_OFFSET);
        
        // Execute on UI thread; add line mesh to the field group
        Platform.runLater(() -> this.fieldGroup.getChildren().add(line.getMeshView()));
        
        // Return the line
        return line;
    }
    
    /**
     * Remove line method. This method removes a {@link FlatLine3D line} from the field.
     * 
     * @param line
     *            The {@link FlatLine3D line} to remove from the field.
     */
    private void removeLine(FlatLine3D line) {

        // Execute UI thread
        Platform.runLater(() -> {

            // Check if we need to remove a line from the field
            if (this.fieldLines != null && this.fieldLines.contains(line) && this.fieldGroup.getChildren().contains(line.getMeshView())) {
                
                // Remove line from the field group
                this.fieldGroup.getChildren().remove(line.getMeshView());

                // Remove line from line list
                this.fieldLines.remove(line);
            }
        });
    }
    
    /**
     * Clear lines method. This method clears the lines of the field.
     */
    private void clearLines() {
        
        // Loop through lines, and remove them from the world
        this.fieldLines.forEach(this::removeLine);
    }
    
    /**
     * Add arc method. This method adds an {@link FlatArc3D arc} to the field.
     * 
     * @param startAngle
     *            The starting angle of the arc.
     * @param endAngle
     *            The ending angle of the arc.
     * @param diameter
     *            The diameter of the arc.
     * @param center
     *            The center location of the arc.
     * @param thickness
     *            The thickness of the arc.
     * @return The arc, null if something fails.
     */
    private FlatArc3D addArc(final float startAngle,
            final float endAngle,
            final float diameter,
            final Vector2f center,
            final float thickness) {

        // Check if the center point is not null
        if (center == null)
            return null;
            
        // Creating new arc
        final FlatArc3D tmpArc = new FlatArc3D(startAngle, endAngle, diameter, thickness, ARC_NUM_DIVISIONS);

        // Translate to position
        tmpArc.getMeshView().setTranslateX(center.getX());
        tmpArc.getMeshView().setTranslateY(LINE_Y_OFFSET);
        tmpArc.getMeshView().setTranslateZ(center.getY());
        
        // Execute on UI thread
        Platform.runLater(() -> {

            // Check if the field group does not contain the arc we are trying to add
            if (!this.fieldGroup.getChildren().contains(tmpArc.getMeshView())) {

                // Add the arc mesh to the field group
                this.fieldGroup.getChildren().add(tmpArc.getMeshView());
            }
        });
        
        // Add to the arc list
        this.fieldArcs.add(tmpArc);
        
        // Return the arc
        return tmpArc;
    }
    
    /**
     * Remove arc method. This method removes a {@link FlatArc3D arc} from the field.
     * 
     * @param arc
     *            The {@link FlatArc3D} to remove from the field.
     */
    private void removeArc(FlatArc3D arc) {
        
        // Check if we can remove the arc from the field
        Platform.runLater(() -> {
            
            if (this.fieldArcs != null && this.fieldArcs.contains(arc)) {
                
                // Remove arc from the field group
                this.fieldGroup.getChildren().remove(arc.getMeshView());
                // Remove the arc from the list of arcs
                this.fieldArcs.remove(arc);
            }
        });
    }
    
    /**
     * Clear arcs method. This method clears the arcs of the field.
     */
    private void clearArcs() {
        
        // Loop through arcs, remove the arc
        this.fieldArcs.forEach(this::removeArc);
    }
    
    /**
     * Add tile method. This method adds an {@link Box tile} to the field.
     * 
     * @param tile
     *            The {@link Box tile} to add to the field.
     */
    private void addTile(Box tile) {
        
        // Add box to field boxes
        this.fieldTiles.add(tile);
        
        // Add box to the world group
        Platform.runLater(() -> this.fieldTileGroup.getChildren().add(tile));
        
        // Hook on mouse clicked event
        tile.setOnMouseClicked(event -> {

            // Check if the right mouse button was clicked
            if (event.getButton() == MouseButton.SECONDARY) {

                // Getting the intersected point
                Point3D intersectedPoint = event.getPickResult().getIntersectedPoint();

                // Transform the click location on the tile to world space
                intersectedPoint = tile.localToParent(intersectedPoint);

                // Setting goals
                contextOverlayGO.setGoals(goalGameObjects);

                // Setting field location
                contextOverlayGO.setFieldLoc(new Point2D(intersectedPoint.getX(), intersectedPoint.getZ()));

                // Showing context menu
                contextOverlayGO.show();
            }
        });
    }
    
    /**
     * Remove tile method. This method removes a {@link Box tile} from the field.
     * 
     * @param tile
     *            The {@link Box tile} to remove from the field.
     */
    private void removeTile(Box tile) {

        // Execute on UI thread
        Platform.runLater(() -> {
            
            // Check if we need to remove a box from the field
            if (this.fieldTiles != null && this.fieldTiles.contains(tile)) {
                
                // Remove from the field group
                this.fieldGroup.getChildren().remove(tile);
                
                // Remove from box list
                this.fieldTiles.remove(tile);
            }
        });
    }
    
    /**
     * Clear tiles method. This method clears the boxes of the field.
     */
    private void clearBoxes() {
        
        // Loop through boxes and remove them
        this.fieldTiles.forEach(this::removeTile);
    }

    /**
     * Generate arcs method. This method generates the arcs of the field.
     */
    private void generateArcs() {
        
        // Getting arcs on the field
        List<FieldCicularArc> fieldCicularArcs = this.fieldVisionModel.getFieldArcs();
        
        // Check if arcs list is not null
        if (fieldCicularArcs != null) {
            
            // Loop through arcs
            for (FieldCicularArc circularArc : fieldCicularArcs) {
                
                // Getting values from circular arc object
                final float startAngle = circularArc.getA1();
                final float endAngle = circularArc.getA2();
                final float diameter = circularArc.getRadius() * 2;
                final float thickness = circularArc.getThickness();
                final Vector2f center = circularArc.getCenter();

                // Add arc to the world
                this.addArc(startAngle, endAngle, diameter, center, thickness);
            }
        }
    }
    
    /**
     * Generate goals method. This method generates the goals on the field.
     */
    private void generateGoals() {
        
        // Getting list of goals
        List<Goal> goals = this.fieldVisionModel.getFieldGoals();
        
        // Check if goal
        if (goals != null) {
            
            // Loop through goals
            for (Goal goal : goals) {
                
                // Create a game object for the goal
                GoalGameObject goal3d = new GoalGameObject(this.getGame(), goal, this.fieldGroup);
                
                // Add goal to goal game objects
                this.goalGameObjects.add(goal3d);
                
                // Add goal game object to the list of game objects
                ((FieldGame)this.getGame()).addGeometryGameObject(goal3d);
            }
        }
    }
    
    /**
     * Clear goals method. This method clears the goals of the field.
     */
    private void clearGoals() {

        // Loop through goal game objects
        this.goalGameObjects.forEach((goalGameObject) -> {

            // Remove goal from the geometry game objects
            ((FieldGame) this.getGame()).removeGeometryGameObject(goalGameObject);

            // Remove from the goal game object list
            this.goalGameObjects.remove(goalGameObject);
        });
    }
    
    /**
     * Generate lines method. This method generates the lines on the field.
     */
    private void generateLines() {
        
        // Getting line segments
        List<FieldLineSegment> fieldLineSegments = this.fieldVisionModel.getFieldLines();
        
        // Check if the list is not null
        if (fieldLineSegments != null) {

            // Loop through line segments
            fieldLineSegments.forEach((lineSegment) -> {

                // Getting start & end point of the line
                Point2D lineStart = new Point2D(lineSegment.getP1().getX(), lineSegment.getP1().getY());
                Point2D lineEnd = new Point2D(lineSegment.getP2().getX(), lineSegment.getP2().getY());

                // Add line to the field
                this.addLine(lineStart, lineEnd, lineSegment.getThickness());
            });
        }
    }
    
    /**
     * Generate tiles method. This method generates the field tiles.
     */
    private void generateTiles() {
        
        // Loop through x axis (add 1 tile at the front 'int i = -1', add 1 tile at the end
        // '(this.fieldVisionModel.getFieldLength() / this.tileWidth) + 1)'.
        for (int i = -1; i < (this.fieldVisionModel.getFieldLength() / this.tileWidth) + 1; i++) {
            
            // Loop through z axis (add 1 tile at the front 'int i = -1', add 1 tile at the end
            // '(this.fieldVisionModel.getFieldWidth() / this.tileDepth) + 1)'.
            for (int j = -1; j < (this.fieldVisionModel.getFieldWidth() / this.tileDepth) + 1; j++) {
                
                // Create new box
                final Box tmpBox = new Box(this.tileWidth, FIELD_HEIGHT, this.tileDepth);
                
                // Translate tile into position
                tmpBox.setTranslateX(-(this.fieldVisionModel.getFieldLength() / 2.0)
                        + ((i * this.tileWidth) + (this.tileWidth / 2.0)));
                tmpBox.setTranslateZ(-(this.fieldVisionModel.getFieldWidth() / 2.0)
                        + ((j * this.tileDepth) + (this.tileDepth / 2.0)));
                        
                // Set box material
                tmpBox.setMaterial(this.grassMaterial);
                
                // Add box to field
                this.addTile(tmpBox);
            }
        }
    }
}