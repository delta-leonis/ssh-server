package org.ssh.models.enums;

/**
 * Enum for identifiing different malfunction types. It also contains a little information (
 * description, type )
 *
 * @author Ryan Meulenkamp
 */
public enum Malfunction {

    /**
     * When a malfunctioning dribbler is detected, this error message could be added to the list
     */
    DRIBBLER_BROKEN(MalfunctionType.WARNING,
            "The dribbler is not working!"),
    /**
     * when a wheel malfunctions, the robot should be switched immediately. It could be a danger to
     * other robots.
     */
    WHEEL_BROKEN(MalfunctionType.ERROR,
            "A wheel is malfunctioning!"),
    /**
     * a malfunctioning wheel encoder will not likely cause any damage to other robots, but will
     * influence the shooting precision of a robot. Therefore it should be switched as soon as
     * possible, or it should be given a defending role.
     */
    WHEEL_ENCODER_BROKEN(MalfunctionType.WARNING,
            "An encoder is malfunctioning! This will influence the precision of the robots shooting.");

    /**
     * a malfunction could be of the type ERROR or WARNING. warnings are small problems which do not
     * cause any damage to other robots, while errors could cause dangerous situations.
     */
    private final MalfunctionType malfunctionType;
    /**
     * This string will contain a little information about this malfunction.
     */
    private final String description;

    /**
     * the two different malfunction types.
     *
     * @author Ryan Meulenkamp
     */
    public enum MalfunctionType {
        ERROR,
        WARNING
    }

    /**
     * constructor of malfunction
     *
     * @param malfunctionType the type of malfunction
     * @param description     a little description of the malfunction
     */
    Malfunction(MalfunctionType malfunctionType, String description) {
        this.malfunctionType = malfunctionType;
        this.description = description;
    }

    /**
     * a function to get the malfunctiontype from this enum
     *
     * @return the malfunctiontype
     */
    public MalfunctionType getMalfunctionType() {
        return malfunctionType;
    }

    /**
     * a function to get a little description of this malfunction
     *
     * @return description of this malfunction.
     */
    public String getDescription() {
        return description;
    }

    /**
     * the toString function makes printing the information of a malfunction more easy.
     */
    @Override
    public String toString() {
        return description;
    }
}