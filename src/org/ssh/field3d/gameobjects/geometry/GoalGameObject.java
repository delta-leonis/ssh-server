package org.ssh.field3d.gameobjects.geometry;

import org.ssh.field3d.core.game.Game;
import org.ssh.field3d.core.gameobjects.GameObject;
import org.ssh.field3d.gameobjects.GeometryGameObject;
import org.ssh.managers.manager.Models;
import org.ssh.models.Goal;
import org.ssh.models.Team;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

/**
 * Goal game object class. This class represents a 3D goal on the 3D field.
 * 
 * @author marklef2
 * @see GameObject
 */
public class GoalGameObject extends GeometryGameObject {
    
    /** The thickness of the wood of the goal. */
    public static final float GOAL_BORDER_THICKNESS = 20.0f;
                                                    
    /** The vision model for the goal. */
    private Goal              goalVisionModel;
                              
    /** The {@link Group} for the goal. */
    private Group             goalGroup;
                              
    /** The field group. */
    private final Group       fieldGroup;
                              
    /** The {@link PhongMaterial} for the goal. */
    private PhongMaterial     goalMaterial;
                              
    /** The list of teams. */
    private List<Team>        teams;
    private org.ssh.models.Game game;

    /**
     * Constructor of the GoalGameObject. This creates a new instance of GoalGameObject.
     * 
     * @param game
     *            The game of the {@link GameObject}.
     * @param goalVisionModel
     *            The vision {@ink Goal model} of the goal.
     */
    public GoalGameObject(Game game, Goal goalVisionModel, final Group fieldGroup) {
        
        // Initialize super class
        super(game);
        
        // Creating group for the goal elements
        this.goalGroup = new Group();

        // retreive helper-class 'Game' (for team-colors and playside)
        Models.<org.ssh.models.Game> get("game").ifPresent(gameModel -> this.game = gameModel);

        //require game to be set
        if(this.game == null)
            return;

        // require goalVisionModel to be set
        if (goalVisionModel == null)
            return;
        
        // Creating a PhongMaterial for the goal
        this.goalMaterial = new PhongMaterial();
        
        // Setting goal vision model
        this.goalVisionModel = goalVisionModel;
        
        this.fieldGroup = fieldGroup;
        
        // Setting rotation axis
        this.goalGroup.setRotationAxis(Rotate.Y_AXIS);
        
        // Create goal
        createGoal(this.goalVisionModel.getGoalWidth(),
                Goal.GOAL_HEIGHT,
                this.goalVisionModel.getGoalDepth(),
                this.goalMaterial);
                
        // Check which side the goal belongs to
        switch (this.game.getSide(goalVisionModel.getAllegiance())) {
            
            // Goal is on the west side
            case WEST: {
                
                // Rotate three-quarter
                this.goalGroup.setRotate(270.0);
                break;
            }
                
                // The goal is on the south side
            case SOUTH: {
                
                // Rotate a half
                this.goalGroup.setRotate(180.0);
                break;
            }
                
                // The goal is on the east side
            case EAST: {
                
                // Rotate a quarter
                this.goalGroup.setRotate(90.0);
                break;
            }
                
                // The goal is on the north side NORTH == default
            default: {
                
                // Rotate to 0 degrees
                this.goalGroup.setRotate(0.0);
                break;
            }
            
        }
        
        // Translate into position
        this.goalGroup.setTranslateX(this.goalVisionModel.getPosition().getX());
        this.goalGroup.setTranslateY(0);
        this.goalGroup.setTranslateZ(this.goalVisionModel.getPosition().getY());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        
        Platform.runLater(() -> {
            // Check if we need to remove the goal group
            if (this.fieldGroup.getChildren().contains(goalGroup)) {
                
                // Remove the goal group from the world
                this.fieldGroup.getChildren().remove(goalGroup);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialize() {
        
        // Update team color
        updateTeamColor();
        
        // Add goal group to the world group
        Platform.runLater(() -> this.fieldGroup.getChildren().add(this.goalGroup));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateGeometry() {
        
        // Update team color
        updateTeamColor();

// Translate goal
        this.goalGroup.setTranslateX(this.goalVisionModel.getPosition().getX());
        this.goalGroup.setTranslateZ(this.goalVisionModel.getPosition().getY());
    }
    
    /**
     * Gets the {@link Group} of the goal.
     * 
     * @return The {@link Group} of the goal.
     */
    public Group getGoalGroup() {
        return this.goalGroup;
    }
    
    /**
     * Creates a goal with a given width, height, depth & material.
     * 
     * @param width
     *            The width of the goal.
     * @param height
     *            The height of the goal.
     * @param depth
     *            The depth of the goal.
     * @param material
     *            The material of the goal.
     */
    private void createGoal(float width, float height, float depth, PhongMaterial material) {
        
        // Creating boxes
        Box leftBox = new Box();
        Box backBox = new Box();
        Box rightBox = new Box();
        
        // Clear group
        this.goalGroup.getChildren().clear();
        
        // Setting left border box dimensions
        leftBox.setWidth(GOAL_BORDER_THICKNESS);
        leftBox.setHeight(height);
        leftBox.setDepth(depth);
        
        // Setting back box dimensions
        backBox.setWidth(width);
        backBox.setHeight(height);
        backBox.setDepth(GOAL_BORDER_THICKNESS);
        
        // Setting right border box dimensions
        rightBox.setWidth(GOAL_BORDER_THICKNESS);
        rightBox.setHeight(height);
        rightBox.setDepth(depth);
        
        // Translate the left border box into position
        leftBox.setTranslateX(-(width / 2.0) + (GOAL_BORDER_THICKNESS / 2.0));
        leftBox.setTranslateY(height / 2.0);
        
        // Translate the right border box into position
        rightBox.setTranslateX((width / 2.0) - (GOAL_BORDER_THICKNESS / 2.0));
        rightBox.setTranslateY(height / 2.0);
        
        // Translate the back border box into position
        backBox.setTranslateY(height / 2.0);
        backBox.setTranslateZ(depth / 2.0);
        
        // Setting material of the boxes
        leftBox.setMaterial(material);
        rightBox.setMaterial(material);
        backBox.setMaterial(material);
        
        // Adding boxes to the group of the goal
        this.goalGroup.getChildren().add(leftBox);
        this.goalGroup.getChildren().add(backBox);
        this.goalGroup.getChildren().add(rightBox);
    }
    
    /**
     * Update team color method. This method updates the team color of the goal (according to the
     * vision model).
     */
    private void updateTeamColor() {
        if(game != null)
            goalMaterial.setDiffuseColor(this.game.getTeamColor(goalVisionModel.getAllegiance()).toColor());

    }
}
