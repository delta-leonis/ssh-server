package org.ssh.controllers;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Services;
import org.ssh.models.Model;
import org.ssh.models.Robot;
import org.ssh.models.enums.ButtonFunction;
import org.ssh.models.enums.ProducerType;
import org.ssh.services.service.Producer;
import org.ssh.util.Logger;

import net.java.games.input.Controller;
import protobuf.Radio.RadioProtocolCommand;

/**
 * This class is used to process a {@link ControllerLayout Controller} {@link Model} and generate a
 * protobuf message accordingly
 *
 * @TODO stop_all_robots
 *       
 * @author Jeroen de Jong
 * @author Thomas Hakkers
 *         
 */
@SuppressWarnings ("rawtypes")
public class ControllerHandler extends Producer {
                                                     
    /**
     * Map with previous buttonstates and respective values
     */
    private Map<ButtonFunction, Float> previousButtonState = new EnumMap<ButtonFunction, Float>(ButtonFunction.class);
    /**
     * Controllerlayout that should be used for inputprocessing
     */
    private final ControllerLayout           layout;
    // respective logger
    private static final Logger              LOG                 = Logger.getLogger();
    
    private static ControllerSettings settings;
                                                                     
    /**
     * Instantiates a handler assigned to a specific layout
     * 
     * @param layout
     */
    public ControllerHandler(final ControllerLayout layout) {
        super("ControllerHandler " + layout.getController().getName(), ProducerType.SCHEDULED);
        
        this.layout = layout;
        
        Optional<Model> model = Models.get("controllersettings");
        if(model.isPresent())
            settings = (ControllerSettings)model.get();

        // fill previousButtonState array
        resetPreviousState();
        
    }
    
    /**
     * @return Layout for this handler
     */
    public ControllerLayout getLayout() {
        return this.layout;
    }
    
    /**
     * Sets all possible binding values to zero.
     */
    private void resetPreviousState() {
        layout.getBindings().entries().forEach(entry -> this.previousButtonState.put(entry.getValue(), 0f));
    }
    
    /**
     * Checks of a button is being pressed
     * 
     * @param buttonValue
     *            value of the button to check
     * @return succes value
     */
    private static boolean isPressed(final Float buttonValue) {
        return Float.compare(buttonValue, 0f) != 0;
    }
    
    /**
     * Fill a {@link RadioProtocolCommand.Builder RadioProtocolCommand} with data as read from
     * assigned {@link Controller}
     * 
     * @param robotID
     *            robot that should be controlled
     * @return Filled in {@link RadioProtocolCommand.Builder RadioProtocolCommand}
     */
    public RadioProtocolCommand.Builder process(final int robotID) {
        // Initialize protobuf packet with robotID
        final RadioProtocolCommand.Builder packet = RadioProtocolCommand.newBuilder();
        packet.setRobotId(robotID);
        
        // update controller values
        this.layout.getController().poll();
        final Set<ButtonFunction> duplicates = new HashSet<ButtonFunction>();
        final Map<ButtonFunction, Float> currentButtonState = new EnumMap<ButtonFunction, Float>(ButtonFunction.class);
        // Make a list with all buttons that changed values
        this.layout.getBindings()
                .entries().stream()
                // filter all buttons that are persistent
                .filter(entry -> entry.getValue().isPersistent()
                        || (entry.getKey().getPollData() != this.previousButtonState.get(entry.getValue())))
                .forEach(entry -> {
                    // Try to add it to duplicates. If it returns false, it already exists
                    if(duplicates.add(entry.getValue())){
                        // if it doesn't exist yet, just put it into the list
                        currentButtonState.put(entry.getValue(), entry.getKey().getPollData());
                    }
                    else{
                     // if it already exists, add the value on top of the current value
                        currentButtonState.put(entry.getValue(), currentButtonState.get(entry.getValue()) + entry.getKey().getPollData());
                    }
                }
                );
                
        processInput(currentButtonState, packet);
        
        // Save the previous button state to avoid duplication
        // For each entry
        for (final Entry<ButtonFunction, Float> entry : currentButtonState.entrySet())
            // On each key in previousButtonState
            this.previousButtonState.put(entry.getKey(),
                    // Check whether the value has changed
                    this.previousButtonState.get(entry.getKey()).compareTo(entry.getValue()) == 0
                        // If it has changed, add it, else, don't change
                        ? this.previousButtonState.get(entry.getKey()) 
                        : entry.getValue());
        
        return packet;
    }
    
    private void processInput(final Map<ButtonFunction, Float> currentButtonState, final RadioProtocolCommand.Builder packet){
        // create a stream with current buttons
        if (!currentButtonState.entrySet().stream()
                // process each button
                .map(entry -> this.processButtonEntry(entry, packet, currentButtonState))
                // check for false succes values
                .reduce(true, (accumulator, succes) -> accumulator && succes))
            ControllerHandler.LOG.warning("Not every button-press was processed succesfully (controller: %s).\n",
                    this.layout.getFullName());
    }
    
    /**
     * Mutate the given {@link RadioProtocolCommand.Builder packet} with modifier given as
     * 
     * @param button
     * @param packet
     *            {@link RadioProtocolCommand.Builder packet} to mutate
     * @return
     */
    private boolean processButtonEntry(final Entry<ButtonFunction, Float> button, final RadioProtocolCommand.Builder packet, final Map<ButtonFunction, Float> currentButtonState) {
        final ButtonFunction function = button.getKey();
        final Float buttonValue = button.getValue();
        // switch to the right function handling
        switch (function) {
            case KICK:
                return this.kick(packet, buttonValue, currentButtonState);
                
            case CHIP:
                return this.chip(packet, buttonValue, currentButtonState);
                
            case DRIBBLE_PERSISTENT:
                return ControllerHandler.dribblePersistent(packet, buttonValue);
                
            case DRIBBLE_TOGGLE:
                return ControllerHandler.dribbleToggle(packet, buttonValue);
                
            case DRIBBLE_SPEED:
                return ControllerHandler.dribbleSpeed(packet, buttonValue);
                
            case DIRECTION_POV:
                return ControllerHandler.directionPOV(packet, buttonValue);
                
            case DIRECTION_FORWARD:
                return ControllerHandler.velocityY(buttonValue, packet);
                
            case DIRECTION_BACKWARD:
                return ControllerHandler.velocityY(-buttonValue, packet);
                
            case DIRECTION_LEFT:
                return ControllerHandler.velocityX(-buttonValue, packet);
                
            case DIRECTION_RIGHT:
                return ControllerHandler.velocityX(buttonValue, packet);
                
            case DIRECTION_X:
                return ControllerHandler.directionX(packet, buttonValue);

            case DIRECTION_Y:
                return ControllerHandler.directionY(packet, buttonValue);
                
            case ORIENTATION_Y:
            case ORIENTATION_X:
                return orientationXY(packet, currentButtonState);
                
            case ORIENTATION_WEST:
                if (ControllerHandler.isPressed(buttonValue)) 
                    return getOrientation(0, packet);
                return true;
                
            case ORIENTATION_EAST:
                if (ControllerHandler.isPressed(buttonValue)) 
                    return getOrientation((float)Math.PI, packet);
                return true;
                
            case STOP_ALL_ROBOTS:      // TODO Implement. 
                if (ControllerHandler.isPressed(buttonValue)) {
                    ControllerHandler.LOG.info("Stopped all robots");
                }
                return true;
                
            case SELECT_NEXT_ROBOT:
            case SELECT_PREV_ROBOT:
                if (ControllerHandler.isPressed(buttonValue)) 
                    ((ControllerListener) Services.get("ControllerListener").get())
                        .changeRobotId(this, function.equals(ButtonFunction.SELECT_NEXT_ROBOT));
                return true;
                
            case KICK_STRENGTH:
            case CHIP_STRENGTH:
                // Do nothing
                return true;
                
            default:
                ControllerHandler.LOG.info("No implementation for %s.\n", button.getKey());
                return false;
        }
    }
    
    /**
     * Calculates the X velocity and adds it to the packet like buttonValue * settings.getMaxVelocity
     * @param buttonValue The value of the button that got pressed (range: -1 : 1)
     * @param packet The packet the velocity will be added to
     * @return true if successful (always)
     */
    private static final boolean velocityX(final Float buttonValue, final RadioProtocolCommand.Builder packet){
        if (ControllerHandler.isPressed(buttonValue) && settings != null){
            packet.setVelocityX(buttonValue * settings.getMaxVelocity());
            return true;
        }else{
            ControllerHandler.LOG.warning("Settings in velocityX is null");
            return false;
        }
    }
    
    /**
     * Calculates the Y velocity and adds it to the packet like buttonValue * settings.getMaxVelocity
     * @param buttonValue The value of the button that got pressed (range: -1 : 1)
     * @param packet The packet the velocity will be added to
     * @return true if successful (always)
     */
    private static final boolean velocityY(final Float buttonValue, final RadioProtocolCommand.Builder packet){
        if (ControllerHandler.isPressed(buttonValue) && settings != null){
            packet.setVelocityY(buttonValue * settings.getMaxVelocity());
            return true;
        }else{
            ControllerHandler.LOG.warning("Settings in velocityY is null");
            return false;
        }
    }
    
    /**
     * Sets the dribbleSpin for the packet.
     * This value is "persistent" meaning that it will always read what the button says
     * @param packet The packet this value needs to be added to
     * @param buttonValue The speed, which is always 1.0f since it's a digital button
     * @return true if success (always)
     */
    private static final boolean dribblePersistent(final RadioProtocolCommand.Builder packet, final float buttonValue){
        float dribbleSpeed = buttonValue;
        if(settings != null)
            dribbleSpeed *= settings.getMaxDribbleSpeed();
        else
            LOG.warning("ControllerSettings not initialized. Could not read MaxFlatKickSpeed");

        packet.setDribblerSpin(dribbleSpeed);
        return true;
    }
    
    /**
     * Sets the FlatKick for the given packet.
     * @param packet The packet FlatKick needs to be added to
     * @param buttonValue The value of the button that triggered the event (not the actual power it'll chip at)
     * @param currentButtonState The current state of all buttons. Used to retrieve CHIP_STRENGTH
     * @return True if success (always)
     */
    private final boolean kick(final RadioProtocolCommand.Builder packet,
            final float buttonValue,
            final Map<ButtonFunction, Float> currentButtonState) {
        
        if (ControllerHandler.isPressed(buttonValue)) {
            // Kickstrength is the value of the assigned trigger. If it doesn't exist, use MAX_STRENGTH
            float kickStrength = this.layout.containsBinding(ButtonFunction.KICK_STRENGTH)
                    ? currentButtonState.get(ButtonFunction.KICK_STRENGTH) : 1;
                    
            if(settings != null)
                kickStrength *= settings.getMaxFlatKickSpeed();
            else
                LOG.warning("ControllerSettings not initialized. Could not read MaxFlatKickSpeed");
            // Make sure it's never below 0
            packet.setFlatKick(Math.abs(kickStrength));
        }
        return true;
    }
    
    /**
     * Sets the ChipKick for the given packet.
     * @param packet The packet ChipKick needs to be added to
     * @param buttonValue The value of the button that triggered the event (not the actual power it'll chip at)
     * @param currentButtonState The current state of all buttons. Used to retrieve KICK_STRENGTH
     * @return True if success (always)
     */
    private final boolean chip(final RadioProtocolCommand.Builder packet,
            final float buttonValue,
            final Map<ButtonFunction, Float> currentButtonState) {
            
        if (ControllerHandler.isPressed(buttonValue)) {
            // Kickstrength is the value of the assigned trigger. If it doesn't exist, use MAX_STRENGTH
            float chipStrength = this.layout.containsBinding(ButtonFunction.CHIP_STRENGTH)
                    ? currentButtonState.get(ButtonFunction.CHIP_STRENGTH) : 1;
                    
            if (settings != null)
                chipStrength *= settings.getMaxChipKickSpeed();
            else
                LOG.warning("ControllerSettings not initialized. Could not read MaxChipKickSpeed");
            // Make sure it's never below 0
            packet.setChipKick(Math.abs(chipStrength));
        }
        return true;
    }
    
    /**
     * WARNING: The inverse of buttonValue is used, since that's what the XBox controller uses
     * Calculates the Velocity on the Y axis and puts it into the packet
     * @param packet The packet that the velocityY gets added to
     * @param buttonValue The value the controller registered
     * @return True if success (always)
     */
    private static final boolean directionY(final RadioProtocolCommand.Builder packet, final float buttonValue){
        float velocity = -buttonValue;
        if(settings != null)
            velocity *= settings.getMaxVelocity();
        
        packet.setVelocityY(velocity);
        return true;
    }
    
    /**
     * Calculates the Velocity on the X axis and puts it into the packet
     * @param packet The packet that the velocityX gets added to
     * @param buttonValue The value the controller registered
     * @return True if success (always)
     */
    private static final boolean directionX(final RadioProtocolCommand.Builder packet, final float buttonValue){
        float velocity = buttonValue;
        if(settings != null)
            velocity *= settings.getMaxVelocity();
        
        packet.setVelocityX(velocity);
        return true;
    }
    
    /**
     * Toggles the dribbler from 0 to 100 and vice versa.
     * @param packet The packet this needs to be added to
     * @param buttonValue The speed at which the dribbler has to spin in percentage 0 - 100%
     * @return
     * @see #dribbleSpeed(protobuf.Radio.RadioProtocolCommand.Builder, float)
     */
    private static final boolean dribbleToggle(final RadioProtocolCommand.Builder packet, final float buttonValue){
        if (ControllerHandler.isPressed(buttonValue)) {
            // TODO Get robot of the right team
            final Optional<Model> oRobot = Models.get("robot B" + packet.getRobotId());
            if (!oRobot.isPresent()){
                ControllerHandler.LOG.info("Could not find robot %d", packet.getRobotId());
            }else {
                final float dribbleSpeed = ((Robot) oRobot.get()).getDribbleSpeed() == 0f ? 1f : 0f;
                packet.setDribblerSpin(dribbleSpeed);
            }
        }
        return true;
    }
    
    /**
     * Makes sure the dribbler goes with a certain speed and adds it to the packet
     * @param packet The packet the value will be added to
     * @param buttonValue The float value representing the speed (-1 : 1)
     * @return true if successful (always)
     * @see #dribbleToggle(protobuf.Radio.RadioProtocolCommand.Builder, float)
     */
    private static final boolean dribbleSpeed(final RadioProtocolCommand.Builder packet, final float buttonValue){
        float dribbleSpin = buttonValue;
        if(settings != null)
            dribbleSpin *= settings.getMaxDribbleSpeed();
        
        packet.setDribblerSpin(dribbleSpin);
        return true;
    }
    
    /**
     * Uses both {@link ButtonFunction#ORIENTATION_X} and  {@link ButtonFunction#ORIENTATION_Y}
     * to calculate the orientation the robot should face
     * @param packet The packet this'll be added to
     * @param currentButtonState The state all buttons currently have
     * @return true if succes, false if failed (like when 1 orientation isn't bound)
     * @see #getOrientation(float, protobuf.Radio.RadioProtocolCommand.Builder)
     */
    private final boolean orientationXY(final RadioProtocolCommand.Builder packet, final Map<ButtonFunction, Float> currentButtonState){
        // make sure both axis are bound
        if (!(this.layout.containsBinding(ButtonFunction.ORIENTATION_X)
                && this.layout.containsBinding(ButtonFunction.ORIENTATION_Y))) {
            ControllerHandler.LOG.warning("Both orientation directions (x and y) should be bound.");
            return false;
        }
        // calculate angle
        float goalAngle = (float) Math.atan2(currentButtonState.get(ButtonFunction.ORIENTATION_X),
                currentButtonState.get(ButtonFunction.ORIENTATION_Y));
        return getOrientation(goalAngle, packet);
    }
    
    /**
     * Compares the goalAngle with the current orientation of the
     * @param goalAngle The angle we want the robot to be at in radians
     * @param packet The packet this'll be added to
     * @return True if successful (always)
     */
    private static final boolean getOrientation(final float goalAngle, final RadioProtocolCommand.Builder packet){
        float rotationSpeed = goalAngle;
        // TODO Get robot of the right team
        final Optional<Model> oRobot = Models.get("robot B" + packet.getRobotId());
        // If the robot isn't present
        if (!oRobot.isPresent()){
            // Give a warning (It'll get send anyway)
            ControllerHandler.LOG.info("Could not find robot %d", packet.getRobotId());
        }
        // If it is present, however
        else {
            // Goal angle - currentAngle = angle we still have to turn
            rotationSpeed = rotationSpeed - ((Robot) oRobot.get()).getRobotOrientation();
        }

        if(settings != null)
            rotationSpeed *= settings.getMaxRotationSpeed();
        
        packet.setVelocityR(rotationSpeed);
        return true;
    }
    
    /**
     * Used for handling POV buttons (like the directional pad on the XBox controller)
     * Parses directional keys to VelocityX and VelocityY
     * @param packet The packet this will be added to
     * @param buttonValue The POV value that'll be used
     * @return true if success (always)
     */
    private static final boolean directionPOV(final RadioProtocolCommand.Builder packet, final float buttonValue){
        if (ControllerHandler.isPressed(buttonValue) && settings != null) {
            packet.setVelocityY((float) Math.sin(buttonValue * 2 * Math.PI) * settings.getMaxVelocity());
            packet.setVelocityX((float) Math.cos(buttonValue * 2 * Math.PI) * -settings.getMaxVelocity());
        }
        return true;
    }
}