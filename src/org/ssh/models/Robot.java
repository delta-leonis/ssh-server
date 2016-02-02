package org.ssh.models;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import org.ssh.managers.manager.Models;
import org.ssh.models.enums.Allegiance;
import org.ssh.models.enums.Malfunction;
import org.ssh.models.enums.Malfunction.MalfunctionType;
import org.ssh.ui.components.centersection.gamescene.shapes.ArcLine3D;
import org.ssh.ui.lua.console.AvailableInLua;
import protobuf.Detection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Describes a Robot on the {@link Field} as a {@link FieldObject}
 *
 * @author Jeroen de Jong
 */
public class Robot extends FieldObject {

    /**
     * The file for the robot model.
     */
    private static final String ROBOT_MODEL_FILE = "/org/ssh/view/3dmodels/robot_model.obj";

    /**
     * The directory for the robot texture.
     */
    private static final String ROBOT_TEXTURE_DIR = "/org/ssh/view/textures/robots/";

    /**
     * The robot height.
     */
    public static final transient float ROBOT_HEIGHT = 150.0f;

    /**
     * The robot radius
     */
    public static final transient float DIAMETER = 180.0f;
    /**
     * The conversion from radians to degrees
     */
    private static final double RAD_TO_DEG = 180.0 / Math.PI;
    private static final float SELECTION_CIRCLE_THICKNESS = 50f;
    private static final float SELECTION_CIRCLE_OFFSET = SELECTION_CIRCLE_THICKNESS * 1.8f;

    /**
     * allegiance of this robot
     */
    private final transient Allegiance allegiance;

    /**
     * Unique robot id [0-15]
     */
    @Alias("robot_id")
    private final transient Integer robotId;

    /**
     * timestamp of last update for this model
     */
    private transient Long lastUpdated;

    /**
     * current dribble speed (-1 is max backward, 1 is max forward)
     */
    private transient Float dribbleSpeed;

    /**
     * The boolean for the selected state
     */
    private BooleanProperty isSelected;

    /**
     * Orientation which the robot is facing, not driving
     */
    private FloatProperty orientation;

    /**
     * height of this robot as provided by ssl-vision
     */
    private Float height;

    /**
     * If the robot is connected to the sofware
     */
    private transient BooleanProperty isConnected;
    /**
     * If the robot is on sight
     */
    private transient BooleanProperty isOnSight;

    private transient ReadOnlyBooleanProperty hasController;
    /**
     * The boolean for the selected state
     */
    private transient ListProperty<Malfunction> malfunctions;

    private BooleanProperty visible;

    /** Shows to which cluster groups this robots belongs to like so: {0.55, 0.23, 0.22} */
    private transient double[] clusterGroup;

    /**
     * Instantiates a new robot with specified properties
     *
     * @param robotId    robot id
     * @param allegiance allegiance of this robot ({@link Allegiance#ALLY} of {@link Allegiance#OPPONENT})
     */
    public Robot(final Integer robotId, final Allegiance allegiance) {
        super("robot", allegiance.identifier() + robotId);
        this.allegiance = allegiance;
        this.robotId = robotId;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.dribbleSpeed = 0.0f;
        this.isSelected = new SimpleBooleanProperty(false);
        this.isConnected = new SimpleBooleanProperty(false);
        this.isOnSight = new SimpleBooleanProperty(false);
        this.hasController = new SimpleBooleanProperty(false);
        this.visible = new SimpleBooleanProperty(true);
        this.orientation = new SimpleFloatProperty(0.0f);
        this.malfunctions = new SimpleListProperty<>(
                javafx.collections.FXCollections.observableList(new ArrayList<>()));
        this.lastUpdated = System.currentTimeMillis();
        clusterGroup = new double[]{};
    }

    /**
     * @return current dribblespeed
     */
    public float getDribbleSpeed() {
        return this.dribbleSpeed;
    }

    /**
     * @return presumed height of this robot as provided by ssl-vision
     */
    public Float getHeight() {
        return height;
    }

    /**
     * @return the Orientation which the robot is facing, not driving
     */
    public Float getOrientation() {
        return this.orientation.get();
    }

    /**
     * @return the Orientation as a property
     */
    public ReadOnlyFloatProperty orientationProperty() {
        return orientation;
    }

    /**
     * @return robot ID [0-15]
     */
    public Integer getRobotId() {
        return this.robotId;
    }

    /**
     * @return team allegiance of this robot (Ally / Opponent)
     */
    public Allegiance getAllegiance() {
        return this.allegiance;
    }

    /**
     * @return timestamp of last update for this model
     */
    public Long lastUpdated() {
        return lastUpdated;
    }

    /**
     * @return True, if the robot is selected.
     */
    public boolean isSelected() {
        return this.isSelected.get();
    }

    /**
     * @return Property for binding isSelected to the GUI
     */
    public ReadOnlyBooleanProperty isSelectedProperty() {
        return this.isSelected;
    }

    /**
     * Set a new value for selection
     * @param value new value
     */
    public void setSelected(boolean value){
        this.isSelected.setValue(value);
    }

    /**
     * @return True, if the software had connection to this robot
     */
    public boolean isConnected() {
        return this.isConnected.get();
    }

    /**
     * @return Property for binding isConnected to the GUI
     */
    public ReadOnlyBooleanProperty isConnectedProperty() {
        return this.isConnected;
    }

    /**
     * @return True, if the vision software sees the robot on the field
     */
    public boolean isOnSight() {
        return this.isOnSight.get();
    }

    /**
     * @return Property for binding isOnSightProperty to the GUI
     */
    public ReadOnlyBooleanProperty isOnSightProperty() {
        return this.isOnSight;
    }


    /**
     * @return whether the robot is has a controller assigned or not
     */
    public boolean hasController(){
        return this.hasController.get();
    }

    /**
     * @return Property containing boolean which describes whether the robot is has a controller assigned or not
     */
    public ReadOnlyBooleanProperty hasControllerProperty() {
        return this.hasController;
    }
    /**
     * @return A list of all the malfunctions in this robot
     */
    public List<Malfunction> getMalfunctions() {
        return malfunctions.get();
    }

    /**
     * @param malfunctionType the type of malfunction to collect a list from
     * @return a list of malfunctions of a specified type
     */
    public List<Malfunction> getMalfunctions(MalfunctionType malfunctionType) {
        return malfunctions.stream().filter(malfunction -> malfunction.getMalfunctionType() == malfunctionType)
                .collect(Collectors.toList());
    }

    /**
     * @return the property for binding the list of malfunctions to another property, or to listen
     * to changes in this list.
     */
    public ReadOnlyListProperty<Malfunction> malfunctionsProperty() {
        return this.malfunctions;
    }

    /**
     * @param malfunctionType the type to look for in the list of malfunctions
     * @return true if the list of malfunctions contains a mach of this malfunctiontype
     */
    public boolean hasMalfunctionOfType(MalfunctionType malfunctionType) {
        return malfunctions.stream().anyMatch(funct -> funct.getMalfunctionType().equals(malfunctionType));
    }

    /**
     * @return a boolean indicating whether the list of malfunctions contains an error-type malfunction.
     */
    public boolean hasErrors() {
        return hasMalfunctionOfType(MalfunctionType.ERROR);
    }

    /**
     * @return a list of all the errors in this robot
     */
    public List<Malfunction> getErrors() {
        return getMalfunctions(MalfunctionType.ERROR);
    }

    /**
     * @return a boolean indicating whether the list of this robots malfunctions contains a warning.
     */
    public boolean hasWarnings() {
        return hasMalfunctionOfType(MalfunctionType.WARNING);
    }

    /**
     * @return a list containing all the warning-type malfunctions.
     */
    public List<Malfunction> getWarnings() {
        return getMalfunctions(MalfunctionType.WARNING);
    }

    /**
     * Update this Robot-model based on a {@link Detection.DetectionRobot} protobuf message.
     * <em>Note: </em> the visibility of this robot will also be set to true
     *
     * @param protobufRobot protobuf message with new information
     * @return succesvalue of the update
     */
    public boolean update(final Detection.DetectionRobot protobufRobot) {
        return update(protobufRobot.getAllFields().entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().getName(),
                Map.Entry::getValue
        )));
    }

    /**
     * Load texture method. This method loads the correct texture according to the vision model.
     */
    private PhongMaterial loadTexture() {

        PhongMaterial material = new PhongMaterial(Color.GRAY);

        Optional<Game> oGame = Models.<Game>get("game");
        if(!oGame.isPresent())
            return material;

        String textureFilename = ROBOT_TEXTURE_DIR
                + getIdentifier()
                .replaceFirst("(A|O)", oGame.get().getTeamColor(getAllegiance()).identifier())
                .replace(" ", "")
                + ".png";

        // Getting texture as input stream
        InputStream textureInputStream = this.getClass().getResourceAsStream(textureFilename);


        // If the texture input stream is not null
        if (textureInputStream != null) {
            // Loading texture & setting diffuse map of the model material
            material.setDiffuseMap(new Image(textureInputStream));
        } else {
            // Log error
            LOG.warning("Could not load texture: " + textureFilename);
        }

        return material;
    }

    private ArcLine3D createSelectionCircle(Color color){
        // between the robot and the circle should be about 80% of the width of the circle as offset
        ArcLine3D selectionCircle = new ArcLine3D( 0f, (float) (2*Math.PI),
                Robot.DIAMETER + Robot.SELECTION_CIRCLE_THICKNESS + Robot.SELECTION_CIRCLE_OFFSET,
                0, 0,
                Robot.SELECTION_CIRCLE_THICKNESS);
        selectionCircle.setMaterial( new PhongMaterial(color) );
        return selectionCircle;
    }

    @Override
    protected Group createNode() {
        Group robotGroup = new Group();

        // Creating model importer
        final ObjModelImporter modelImporter = new ObjModelImporter();

        // Read model into model importer
        modelImporter.read(this.getClass().getResource(ROBOT_MODEL_FILE));

        // Check if we have loaded something
        if (modelImporter.getImport().length > 0) {

            // Getting model from the model importer
            MeshView model = modelImporter.getImport()[0];

            // move the robot up
            model.setTranslateY(Robot.ROBOT_HEIGHT/2d);
            // make sure we rotate the right axis
            model.setRotationAxis(Rotate.Y_AXIS);
            // bind
            model.rotateProperty().bind(orientationProperty().multiply(Robot.RAD_TO_DEG));

            model.setOnMousePressed(se -> this.setSelected(!isSelected()));

            model.setMaterial(loadTexture());

            ArcLine3D selectionCircle = createSelectionCircle(Color.BLUE);
            selectionCircle.visibleProperty().bind(isSelectedProperty());
            selectionCircle.setTranslateY(10);
            robotGroup.getChildren().add(selectionCircle);

            //robotGroup.visibleProperty().bind(visibleProperty());
            robotGroup.getChildren().add(model);
        }

        return robotGroup;
    }

    /**
     * Set whether the robot should be visible in the GUI
     * @param visible new visibility
     */
    public void setVisible(boolean visible) {
        this.visible.setValue(visible);
    }

    /**
     * @return Property describing whether the robot should be visible in the GUI
     */
    public ReadOnlyBooleanProperty visibleProperty(){
        return visible;
    }

    /**
     * @return true if the robot should be visible in the GUI
     */
    public boolean isVisible(){
        return visible.getValue();
    }

    /**
     * @return The cluster group this {@link Robot} belongs to
     */
    public double[] getClusterGroup(){
        return this.clusterGroup;
    }

    /**
     * Sets the cluster group this {@link Robot} is assigned to
     * @param cluster the cluster to set
     */
    public void setClusterGroup(double[] cluster){
        this.clusterGroup = cluster;
    }

    /**
     * @return A {@link Color} representation of the cluster group.
     */
    public Color getClusterColor(){
        return Color.rgb((int)(clusterGroup[0]*255), (int)(clusterGroup[1]*255), (int)(clusterGroup[2]*255));
    }
}