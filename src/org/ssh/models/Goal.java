package org.ssh.models;

import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import org.ssh.managers.manager.Models;
import org.ssh.models.enums.Allegiance;

/**
 * Describes a goal on the {@link Field}
 *
 * @author Jeroen de Jong
 */
public class Goal extends FieldObject {

    /**
     * Defending team for this goal
     */
    private transient Allegiance allegiance;

    /**
     * Dimensions of the goal
     */
    private transient Game game;
    private Integer goalDepth, goalWidth;
    public static final transient Integer HEIGHT = 160;
    public static final transient Integer BORDER_THICKNESS = 20;

    /**
     * Creates a goal on a specified field half
     *
     * @param allegiance Defending team for this goal
     */
    public Goal(final Allegiance allegiance) {
        super("goal", allegiance.name());

        this.allegiance = allegiance;

        Models.<Game>get("game").ifPresent(game -> this.game = game);
    }

    @Override
    public void initialize() {
        super.initialize();
        goalDepth = 0;
        goalWidth = 0;
    }

    /**
     * @return depth of the goal in mm
     */
    public int getGoalDepth() {
        return this.goalDepth;
    }

    /**
     * @return width of the goal in mm
     */
    public int getGoalWidth() {
        return this.goalWidth;
    }

    /**
     * @return Defending team for this goal
     */
    public Allegiance getAllegiance() {
        return this.allegiance;
    }

    public PhongMaterial loadTexture(){
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(this.game.getTeamColor(getAllegiance()).toColor());
        return material;
    }

    @Override
    public Group createMeshView(){
        if(game == null)
            return null;

        Group goalGroup = new Group();
        PhongMaterial material = loadTexture();
        // Creating boxes
        Box leftBox = new Box();
        Box backBox = new Box();
        Box rightBox = new Box();

        // Setting left border box dimensions
        leftBox.setWidth(Goal.BORDER_THICKNESS);
        leftBox.setHeight(Goal.HEIGHT);
        leftBox.setDepth(getGoalDepth());

        // Setting back box dimensions
        backBox.setWidth(getGoalWidth());
        backBox.setHeight(Goal.HEIGHT);
        backBox.setDepth(Goal.BORDER_THICKNESS);

        // Setting right border box dimensions
        rightBox.setWidth(Goal.BORDER_THICKNESS);
        rightBox.setHeight(Goal.HEIGHT);
        rightBox.setDepth(getGoalDepth());

        // Translate the left border box into position
        leftBox.setTranslateX(-(getGoalWidth() / 2.0) + (Goal.BORDER_THICKNESS / 2.0));
        leftBox.setTranslateY(Goal.HEIGHT / 2.0);

        // Translate the right border box into position
        rightBox.setTranslateX((getGoalWidth() / 2.0) - (Goal.BORDER_THICKNESS / 2.0));
        rightBox.setTranslateY(Goal.HEIGHT / 2.0);

        // Translate the back border box into position
        backBox.setTranslateY(Goal.HEIGHT / 2.0);
        backBox.setTranslateZ(-getGoalDepth() / 2.0);

        // Setting material of the boxes
        leftBox.setMaterial(material);
        rightBox.setMaterial(material);
        backBox.setMaterial(material);

        // Adding boxes to the group of the goal
        goalGroup.getChildren().addAll(leftBox, backBox, rightBox);

        goalGroup.setRotationAxis(Rotate.Y_AXIS);
        goalGroup.setRotate(game.getSide(allegiance).getDegreeShift());
        return goalGroup;
    }
}