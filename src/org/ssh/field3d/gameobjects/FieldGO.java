package org.ssh.field3d.gameobjects;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.core.math.Vector3f;
import org.ssh.field3d.core.shapes.FlatArc3D;
import org.ssh.field3d.core.shapes.FlatLine3D;
import org.ssh.field3d.gameobjects.overlay.ContextOverlayGO;
import org.ssh.managers.manager.Models;
import org.ssh.models.Field;
import org.ssh.models.Goal;
import org.ssh.models.Model;
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
import protobuf.Geometry.FieldCicularArc;
import protobuf.Geometry.FieldLineSegment;
import protobuf.Geometry.Vector2f;

/**
 * FieldGO class. This class creates the field, the goals, lines and arcs.
 *
 * @author marklef2
 * @see GameObject
 */
// TODO: Read location of penalty spot + penalty spot size from model
public class FieldGO extends GameObject {
    
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
    private final Queue<Box>            fieldBoxes;
                                        
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
                                        
    /**
     * 
     * @param game
     */
    public FieldGO(final Game game) {
        
        // Initialize super class
        super(game);
        
        // Creating lists for the tiles and lines
        this.fieldBoxes = new ConcurrentLinkedQueue<Box>();
        this.fieldLines = new ConcurrentLinkedQueue<FlatLine3D>();
        this.fieldArcs = new ConcurrentLinkedQueue<FlatArc3D>();
        this.goalGameObjects = new ConcurrentLinkedQueue<GoalGameObject>();
        
        this.contextOverlayGO = new ContextOverlayGO(game);
        
        // Creating grass material with lawn green diffuse color
        this.grassMaterial = new PhongMaterial(Color.LAWNGREEN);
        
        // Setting tile dimensions
        this.tileDepth = FIELD_TILE_DEPTH;
        this.tileWidth = FIELD_TILE_WIDTH;
        
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
     * {@inheritDoc}
     */
    @Override
    public void onUpdateGeometry() {
        
        // Trying to get Field model
        Optional<Field> optionalModel = Models.<Field>get("field");
        
        // If there is a model present
        if (optionalModel.isPresent()) {
            
            // Setting vision model
            this.fieldVisionModel = optionalModel.get();
            
            // Clear arcs
            this.clearArcs();
            // Clear lines
            this.clearLines();
            // Clear boxes
            this.clearBoxes();
            // Clear goals
            this.clearGoals();
            
            // Remove penalty spots
            this.getGame().removeGameObject(penaltySpotEast);
            this.getGame().removeGameObject(penaltySpotWest);
            
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
                    new Vector3f((float) ((fieldVisionModel.getFieldLength() / 2.0) - FieldGO.FIELD_PENALTY_SPOT),
                            (float) FieldGO.LINE_Y_OFFSET,
                            0),
                    FieldGO.FIELD_PENALTY_SPOT_SIZE);
            this.penaltySpotWest = new PenaltySpotGO(this.getGame(),
                    new Vector3f((float) (-(fieldVisionModel.getFieldLength() / 2.0) + FieldGO.FIELD_PENALTY_SPOT),
                            (float) FieldGO.LINE_Y_OFFSET,
                            0),
                    FieldGO.FIELD_PENALTY_SPOT_SIZE);
                    
            // Adding new penalty spots
            this.getGame().addGameObject(penaltySpotEast);
            this.getGame().addGameObject(penaltySpotWest);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateDetection() {
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
    private FlatLine3D addLine(final Point2D start, final Point2D end, final double thickness) {
        
        // Creating new flat line
        final FlatLine3D line = new FlatLine3D(start, end, thickness);
        
        // Add to lines
        this.fieldLines.add(line);
        
        // Translate a bit upwards
        line.getMeshView().setTranslateY(FieldGO.LINE_Y_OFFSET);
        
        // Add to world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().add(line.getMeshView()));
        
        // Return the line
        return line;
    }
    
    /**
     * Remove line method. This method removes a {@line FlatLine3D line} from the field.
     * 
     * @param line
     *            The line to remove from the field.
     */
    private void removeLine(FlatLine3D line) {
        
        Platform.runLater(() -> {
            // Check if we need to remove a line from the field
            if (this.fieldLines != null && this.fieldLines.contains(line)) {
                
                // Remove line from world
                this.getGame().getWorldGroup().getChildren().remove(line.getMeshView());
                
                // Remove line from line list
                this.fieldLines.remove(line);
            }
        });
    }
    
    /**
     * Clear lines method. This method clears the lines of the field.
     */
    private void clearLines() {
        
        // Loop through lines
        for (FlatLine3D line : this.fieldLines) {
            
            // Remove the line
            this.removeLine(line);
        }
    }
    
    /**
     * addArc method. This method adds an arc to the field.
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
     * @return The arc.
     */
    private FlatArc3D addArc(final float startAngle,
            final float endAngle,
            final float diameter,
            final Vector2f center,
            final float thickness) {
            
        // Creating new arc
        final FlatArc3D tmpArc = new FlatArc3D(startAngle, endAngle, diameter, thickness, ARC_NUM_DIVISIONS);
        
        // Translate to position
        tmpArc.getMeshView().setTranslateX(center.getX());
        tmpArc.getMeshView().setTranslateY(LINE_Y_OFFSET);
        tmpArc.getMeshView().setTranslateZ(center.getY());
        
        // Add arc to the world
        Platform.runLater(() -> {
            if (!this.getGame().getWorldGroup().getChildren().contains(tmpArc.getMeshView())) {
                this.getGame().getWorldGroup().getChildren().add(tmpArc.getMeshView());
            }
        });
        
        // Add to the arc list
        this.fieldArcs.add(tmpArc);
        
        // Return the arc
        return tmpArc;
    }
    
    /**
     * Remove arc method. This method removes a {@FlatArc3D arc} from the field.
     * 
     * @param arc
     *            The {@link FlatArc3D} to remove from the field.
     */
    private void removeArc(FlatArc3D arc) {
        
        // Check if we can remove the arc from the field
        Platform.runLater(() -> {
            
            if (this.fieldArcs != null && this.fieldArcs.contains(arc)) {
                
                // Remove arc from the world
                this.getGame().getWorldGroup().getChildren().remove(arc.getMeshView());
                // Remove the arc from the list of arcs
                this.fieldArcs.remove(arc);
            }
        });
    }
    
    /**
     * Clear arcs method. This method clears the arcs of the field.
     */
    private void clearArcs() {
        
        // Loop through arcs
        for (FlatArc3D arc : this.fieldArcs) {
            
            // Remove the arc
            this.removeArc(arc);
        }
    }
    
    /**
     * addBox method. This method adds an box to the world.
     * 
     * @param box
     *            The {@link Box} to be added.
     */
    private void addTile(Box box) {
        
        // Add box to field boxes
        this.fieldBoxes.add(box);
        
        // Add box to the world group
        Platform.runLater(() -> this.getGame().getWorldGroup().getChildren().add(box));
        
        // Hook on mouse clicked event
        box.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent event) {
                
                // Check if the right mouse button was clicked
                if (event.getButton() == MouseButton.SECONDARY) {
                    
                    // Getting the intersected point
                    Point3D intersectedPoint = event.getPickResult().getIntersectedPoint();
                    
                    // Transform the click location on the tile to world space
                    intersectedPoint = box.localToParent(intersectedPoint);
                    
                    // Setting field location
                    contextOverlayGO.setFieldLoc(new Point2D(intersectedPoint.getX(), intersectedPoint.getZ()));
                    
                    // Showing context menu
                    contextOverlayGO.show();
                }
            }
            
        });
    }
    
    /**
     * Remove box method. This method removes a {@link Box} from the field.
     * 
     * @param box
     *            The {@link Box} to remove.
     */
    private void removeBox(Box box) {
        
        Platform.runLater(() -> {
            
            // Check if we need to remove a box from the field
            if (this.fieldBoxes != null && this.fieldBoxes.contains(box)) {
                
                // Remove from the world group
                this.getGame().getWorldGroup().getChildren().remove(box);
                
                // Remove from box list
                this.fieldBoxes.remove(box);
            }
        });
    }
    
    /**
     * Clear boxes method. This method clears the boxes of the field.
     */
    private void clearBoxes() {
        
        // Loop through 'tiles'
        for (Box box : this.fieldBoxes) {
            
            // Remove tile
            this.removeBox(box);
        }
    }
    
    /**
     * generateArcs method. This method generates the arcs of the field.
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
     * generateGoals method. This method generates the goals on the field.
     */
    private void generateGoals() {
        
        // Getting list of goals
        List<Goal> goals = Models.<Goal>getAll("goal");

        // Loop through goals
        for (Goal goal : goals) {

            // Create a game object for the goal
            GoalGameObject goal3d = new GoalGameObject(this.getGame(), goal);

            // Add goal to goal game objects
            this.goalGameObjects.add(goal3d);

            // Add goal game object to the list of game objects
            this.getGame().addGameObject(goal3d);
        }
    }
    
    /**
     * Clear goals method. This method clears the goals of the field.
     */
    private void clearGoals() {
        
        // Loop through goal game objects
        for (GoalGameObject goalGameObject : this.goalGameObjects) {
            
            this.getGame().removeGameObject(goalGameObject);
        }
    }
    
    /**
     * generateLines method. This method generates the lines on the field.
     */
    private void generateLines() {
        
        // Getting line segments
        List<FieldLineSegment> fieldLineSegments = this.fieldVisionModel.getFieldLines();
        
        // Check if the list is not null
        if (fieldLineSegments != null) {
            
            // Loop through line segments
            for (FieldLineSegment lineSegment : fieldLineSegments) {
                
                // Getting start & end point of the line
                Point2D lineStart = new Point2D(lineSegment.getP1().getX(), lineSegment.getP1().getY());
                Point2D lineEnd = new Point2D(lineSegment.getP2().getX(), lineSegment.getP2().getY());
                
                // Add line to the field
                this.addLine(lineStart, lineEnd, lineSegment.getThickness());
            }
        }
    }
    
    /**
     * generateTiles method. This method generates the field tiles.
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