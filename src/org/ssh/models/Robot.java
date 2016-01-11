package org.ssh.models;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.ssh.models.enums.Allegiance;
import org.ssh.models.enums.Malfunction;
import org.ssh.models.enums.Malfunction.MalfunctionType;
import protobuf.Detection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Describes a Robot on the {@link Field} as a {@link FieldObject}
 *
 * @author Jeroen de Jong
 *
 */
public class Robot extends FieldObject {

    /** The robot height. */
    public static final transient float ROBOT_HEIGHT   = 150.0f;

    /** The robot radius */
    public static final transient float ROBOT_DIAMETER = 180.0f;

    /** allegiance of this robot */
    private final transient Allegiance allegiance;

    /**  Unique robot id [0-15] */
    @Alias ("robot_id")
    private final transient Integer     robotId;

    /** timestamp of last update for this model */
    private transient Long              lastUpdated;

    /** current dribble speed (-1 is max backward, 1 is max forward) */
    private Float                       dribbleSpeed;

    /** The boolean for the selected state */
    private BooleanProperty             isSelected;

    /** Orientation which the robot is facing, not driving */
    private FloatProperty               orientation;

    /** height of this robot as provided by ssl-vision */
    private Float                       height;

    /** If the robot is connected to the sofware */
    private transient BooleanProperty                 isConnected;
    /** If the robot is on sight */
    private transient BooleanProperty                 isOnSight;
    /** The boolean for the selected state */
    private transient ListProperty<Malfunction> malfunctions;

    /**
     * Instantiates a new robot with specified properties
     *
     * @param robotId
     *            robot id
     * @param allegiance
     *            allegiance of this robot ({@link Allegiance#ALLY} of {@link Allegiance#OPPONENT})
     */
    public Robot(final Integer robotId, final Allegiance allegiance) {
        super("robot", allegiance.identifier() + robotId);
        this.allegiance = allegiance;
        this.robotId = robotId;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.isSelected = new SimpleBooleanProperty(false);
        this.isConnected = new SimpleBooleanProperty(false);
        this.isOnSight = new SimpleBooleanProperty(false);
        this.orientation = new SimpleFloatProperty(0.0f);
        this.malfunctions = new SimpleListProperty<>(
                javafx.collections.FXCollections.observableList(new ArrayList<>()));
        this.lastUpdated = System.currentTimeMillis();
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
    public ReadOnlyFloatProperty orientationProperty() { return orientation; }

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
    public Long lastUpdated(){
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
     * @return A list of all the malfunctions in this robot
     */
    public List<Malfunction> getMalfunctions() {
        return malfunctions.get();
    }

    /**
     * @param malfunctionType the type of malfunction to collect a list from
     * @return a list of malfunctions of a specified type
     */
    public List<Malfunction> getMalfunctions(MalfunctionType malfunctionType){
        return malfunctions.stream().filter(malfunction -> malfunction.getMalfunctionType() == malfunctionType)
                .collect(Collectors.toList());
    }

    /**
     * @return the property for binding the list of malfunctions to another property, or to listen
     *         to changes in this list.
     */
    public ReadOnlyListProperty<Malfunction> malfunctionsProperty() {
        return this.malfunctions;
    }

    /**
     * @param malfunctionType the type to look for in the list of malfunctions
     * @return true if the list of malfunctions contains a mach of this malfunctiontype
     */
    public boolean hasMalfunctionOfType(MalfunctionType malfunctionType){
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
     * Update this Robot-model based on a {@link Detection.DetectionRobot} protobuf message
     *
     * @param protobufRobot protobuf message with new information
     * @return succesvalue of the update
     */
    public boolean update(final Detection.DetectionRobot protobufRobot){
        return update(protobufRobot.getAllFields().entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().getName(),
                Map.Entry::getValue
        )));
    }
}