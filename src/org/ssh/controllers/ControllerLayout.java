package org.ssh.controllers;

import java.net.URI;
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
 * TODO finish javadoc
 *
 * @author Jeroen
 *        
 */
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
    private final Logger                           logger   = Logger.getLogger();
                                                            
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
     * Instantiates a layout linked to given {@link Controller}, and loads bindings from a given
     * config
     * 
     * @param controller
     *            controller to link to
     * @param configFile
     *            configfile with bindings
     */
    public ControllerLayout(final Controller controller, final URI configFile) {
        this(controller);
        this.logger.warning("Configfiles not yet implemented, no buttons assigned to controller");
        // TODO implement
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
            this.logger.warning(String.format("Button already bound %s (to %s).\n",
                    component.toString(),
                    this.bindings.get(component)));
            this.bindings.remove(component);
        }
        
        if (this.bindings.containsValue(function)) {
            this.logger.warning(String.format("Function '%s' already bound, and will be overwriten.\n", function));
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
    
    public float get(final ButtonFunction function) {
        // TODO dirty hackish
        if ((function.equals(ButtonFunction.CHIP_STRENGTH) || function.equals(ButtonFunction.KICK_STRENGTH))
                && this.containsBinding(ButtonFunction.CHIPKICK_STRENGTH))
            return this.get(ButtonFunction.CHIPKICK_STRENGTH);
            
        if (!this.containsBinding(function)) {
            this.logger.warning("Could not get data for %s, no binding found.\n", function);
            return 0f;
        }
        
        return this.bindings.entrySet().stream().filter(entry -> entry.getValue().equals(function))
                .map(entry -> entry.getKey().getPollData()).findFirst().get();
    }
    
    public BiMap<Component, ButtonFunction> getBindings() {
        return this.bindings;
    }
    
    public Component getComponent(final Identifier id) {
        return this.controller.getComponent(id);
    }
    
    public Optional<Component> getComponent(final String identifier) {
        return Stream.of(this.controller.getComponents())
                .filter(component -> component.getIdentifier().getName().equals(identifier)).findFirst();
    }
    
    @Override
    public String getConfigName() {
        return Controller.class.getName() + this.controller.getName() + ".json";
    }
    
    public Controller getController() {
        return this.controller;
    }
    
    public boolean hasDirectionButtons() {
        return this.bindings.entrySetByValue().stream()
                .filter(entry -> entry.getKey().toString().contains("DIRECTION_")).count() > 0;
    }
    
    public boolean hasOrientationButtons() {
        return this.bindings.entrySetByValue().stream()
                .filter(entry -> entry.getKey().toString().contains("ORIENTATION_")).count() > 0;
    }
    
    public boolean isComplete() {
        return this.hasOrientationButtons() && this.hasDirectionButtons();
    }
}