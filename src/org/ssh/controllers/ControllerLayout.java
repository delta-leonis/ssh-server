package org.ssh.controllers;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.ssh.models.Model;
import org.ssh.models.enums.ButtonFunction;
import org.ssh.util.BiMap;
import org.ssh.util.Logger;

import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;

/**
 * Used for binding {@link AbstractComponent Components} as found in a {@link Controller} to a
 * {@link ButtonFunction}.<br>
 * note: {@link ButtonFunctions} and {@link AbstractComponent} cannot be mounted more than one at a
 * time.
 *
 * @TODO finish javadoc
 * @TODO test Model implementation
 *       
 * @author Jeroen de Jong
 *         
 */
@SuppressWarnings ("serial")
public class ControllerLayout extends Model {
    
    /**
     * JInput Model representing physical {@link Controller}
     */
    private final Controller                       controller;
    /**
     * Map containing all {@link Component} as found on the {@link controller} and the bound
     * {@link ButtonFunction functions}
     */
    private final BiMap<Component, ButtonFunction> bindings = new BiMap<Component, ButtonFunction>();
    // respective logger
    private transient final static Logger          LOG      = Logger.getLogger();
                                                            
    /**
     * Instantiates a layout linked to given {@link Controller}
     * 
     * @param controller
     *            controller to link to
     */
    public ControllerLayout(final Controller controller) {
        super("controller", controller.getType().toString());
        this.controller = controller;
    }
    
    /**
     * attach a specific {@link AbstractComponent} to a {@link ButtonFunction}. Will overwrite a
     * binding whenever a {@link AbstractComponent} is already bound to a {@link ButtonFunction}
     * 
     * @param component
     *            component to link to
     * @param function
     *            buttonfunction to define
     * @return
     */
    public boolean attach(final Component component, final ButtonFunction function) {
        // check if it will be overriden
        if (this.bindings.containsKey(component)) {
            ControllerLayout.LOG.fine("Button already bound %s (to %s).",
                    component.toString(),
                    this.bindings.get(component));
            this.bindings.remove(component);
        }
        
        if (this.bindings.containsValue(function)) {
            ControllerLayout.LOG.fine("Function '%s' already bound, and will be overwriten.\n", function);
            this.bindings.removeByValue(function);
        }
        
        this.bindings.put(component, function);
        
        return true;
    }
    
    public boolean containsBinding(final ButtonFunction function) {
        // TODO too hackisch
        if ((function.equals(ButtonFunction.CHIP_STRENGTH) || function.equals(ButtonFunction.KICK_STRENGTH))
                && this.containsBinding(ButtonFunction.CHIPKICK_STRENGTH))
            return true;
            
        return this.bindings.entrySet().stream().filter(entry -> entry.getValue().equals(function)).count() > 0;
    }
    
    /**
     * Detach a binding by {@link ButtonFunction}.
     * 
     * @param function
     *            buttonFunction to detach
     */
    public void detach(final ButtonFunction function) {
        this.bindings.removeByValue(function);
    }
    
    /**
     * detatch the binding of a button
     * 
     * @param button
     *            button to detatch
     * @return succes value
     */
    public boolean detach(final Component button) {
        this.bindings.remove(button);
        return !this.bindings.containsKey(button);
    }
    
    /**
     * Gets the right
     * 
     * 
     * note: one exception does the following: whenever {@link ButtonFunction.CHIPKICK_STRENGTH
     * CHIPKICK_STRENGTH} is bound, and the argument states {@link ButtonFunction.CHIP_STRENGTH
     * CHIP_STRENGTH} or {@link ButtonFunction.KICK_STRENGTH KICK_STRENGTH}, it will return the
     * value for CHIPKICK_STRENGTH instead of a possible unbound KICK/CHIP_STRENGTH.
     * 
     * @param function
     *            function to read
     * @return value for specific function
     */
    public float get(final ButtonFunction function) {
        // TODO dirty hackish
        if ((function.equals(ButtonFunction.CHIP_STRENGTH) || function.equals(ButtonFunction.KICK_STRENGTH))
                && this.containsBinding(ButtonFunction.CHIPKICK_STRENGTH))
            return this.get(ButtonFunction.CHIPKICK_STRENGTH);
            
        if (!this.containsBinding(function)) {
            ControllerLayout.LOG.info("Could not get data for %s, no binding found.\n", function);
            return 0f;
        }
        
        return this.bindings.entrySet().stream().filter(entry -> entry.getValue().equals(function))
                .map(entry -> entry.getKey().getPollData()).findFirst().get();
    }
    
    /**
     * @return Map with all the bindings for this layout
     */
    public Map<Component, ButtonFunction> getBindings() {
        return this.bindings;
    }
    
    /**
     * @param id
     *            unique identifier for a component
     * @return a 'physical' component on a {@link Controller}
     */
    public Component getComponent(final Identifier id) {
        return this.controller.getComponent(id);
    }
    
    /**
     * @param identifier
     * @return maybe a component of with a specific identifier
     */
    public Optional<Component> getComponent(final String identifier) {
        return Stream.of(this.controller.getComponents())
                .filter(component -> component.getIdentifier().getName().equals(identifier)).findFirst();
    }
    
    /**
     * @return a configname for every controllertype
     */
    @Override
    public String getConfigName() {
        return Controller.class.getName() + this.controller.getName() + ".json";
    }
    
    /**
     * @return Model of physical controller currently in use
     */
    public Controller getController() {
        return this.controller;
    }
    
    /**
     * Check whether this layout has a couple of buttons matching a 'pattern'
     * 
     * @param pattern
     *            piece of String to match
     * @return succes value
     */
    public boolean hasButtons(String pattern) {
        return this.bindings.entrySetByValue().stream().filter(entry -> entry.getKey().toString().contains(pattern))
                .count() > 0;
    }
    
    /**
     * Check whether this controller is complete (e.g. has orientation and directional assignments)
     * 
     * @return
     */
    public boolean isComplete() {
        return this.hasButtons("DIRECTION_") && this.hasButtons("ORIENTATION_");
    }
}