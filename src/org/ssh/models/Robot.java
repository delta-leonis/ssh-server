package org.ssh.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import org.ssh.models.enums.Malfunction;
import org.ssh.models.enums.Malfunction.MalfunctionType;
import org.ssh.models.enums.TeamColor;
import org.ssh.util.Alias;
import org.ssh.managers.manager.Models;
import org.ssh.models.enums.Allegiance;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
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
    private Allegiance allegiance;

    /**  Unique robot id [0-15] */
    @Alias ("robot_id")
    private final transient Integer     robotId;
                                        
    /** timestamp of last update for this model */
    private transient Double            lastUpdated;

    /** current dribble speed (-1 is max backward, 1 is max forward) */
    private Float                       dribbleSpeed;
                                        
    /** The boolean for the selected state */
    private Boolean                     isSelected;
                                        
    /** Orientation which the robot is facing, not driving */
    private Float                       orientation;
                                        
    /** height of this robot as provided by ssl-vision */
    private Float                       height;

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
        this.isSelected = new SimpleBooleanProperty(false);
        this.isConnected = new SimpleBooleanProperty(false);
        this.isOnSight = new SimpleBooleanProperty(false);
        this.malfunctions = new SimpleListProperty<Malfunction>();
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
        return this.orientation;
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
    public Double lastUpdated(){
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
    public BooleanProperty isSelectedProperty() {
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
    public BooleanProperty isConnectedProperty() {
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
    public BooleanProperty isOnSightProperty() {
        return this.isOnSight;
    }

    /**
     * @return A list of all the malfunctions in this robot
     */
    public ArrayList<Malfunction> getMalfunctions() {
        return new ArrayList<Malfunction>(malfunctions.get());
    }

    /**
     * @return the property for binding the list of malfunctions to another property, or to listen
     *         to changes in this list.
     */
    public ListProperty<Malfunction> malfunctionsProperty() {
        return this.malfunctions;
    }

    /**
     * @return a boolean indicating whether the list of malfunctions contains an error-type malfunction.
     */
    public boolean hasErrors() {
        // dunno which is better
        return malfunctions.stream().anyMatch(funct -> funct.getMalfunctionType().equals(MalfunctionType.ERROR));
        // return getErrors > 0;
    }

    /**
     * @return a list of all the errors in this robot
     */
    public List<Malfunction> getErrors() {
        return malfunctions.stream().filter(malfunction -> malfunction.getMalfunctionType() == MalfunctionType.ERROR)
                .collect(Collectors.toList());
    }

    /**
     * @return a boolean indicating whether the list of this robots malfunctions contains a warning.
     */
    public boolean hasWarnings() {
        // dunno which is better
        return malfunctions.stream().anyMatch(funct -> funct.getMalfunctionType().equals(MalfunctionType.WARNING));
        // return getWarnings > 0;
    }

    /**
     * @return a list containing all the warning-type malfunctions.
     */
    public List<Malfunction> getWarnings() {
        return malfunctions.stream().filter(malfunction -> malfunction.getMalfunctionType() == MalfunctionType.WARNING)
                .collect(Collectors.toList());
    }
}