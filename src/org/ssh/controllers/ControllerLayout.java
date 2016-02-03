package org.ssh.controllers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import org.ssh.models.AbstractModel;
import org.ssh.models.enums.ButtonFunction;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Used for binding {@link AbstractComponent Components} as found in a {@link Controller} to a
 * {@link ButtonFunction}.<br>
 *
 * @author Jeroen de Jong
 * @author Thomas Hakkers
 */
public class ControllerLayout extends AbstractModel {

    /**
     * JInput Model representing physical {@link Controller}
     */
    private final transient Controller controller;
    /**
     * Map containing all {@link Component} as found on the {@link #controller} and the bound
     * {@link ButtonFunction functions}
     */
    private Multimap<Component, ButtonFunction> bindings = ArrayListMultimap.create();

    /**
     * Instantiates a layout linked  to given {@link Controller}
     *
     * @param controller controller to link to
     */
    public ControllerLayout(final Controller controller) {
        super("controller", controller.getType().toString());
        this.controller = controller;
    }

    @Override
    public void initialize() {
        // no default values
    }


    public static boolean createDefaultLayout(ControllerLayout layout) {
        // assign a bunch of buttons (note that the identifiernames are specific for Windows
        // since this is only a demo it shouldn't be much of a problem
        if (layout.getController().getType() == Controller.Type.GAMEPAD) {
            layout.attach(layout.getComponent(Component.Identifier.Button._1), ButtonFunction.KICK);
            layout.attach(layout.getComponent(Component.Identifier.Button._0), ButtonFunction.CHIP);
            layout.attach(layout.getComponent(Component.Identifier.Button._3), ButtonFunction.DRIBBLE_TOGGLE);
            layout.attach(layout.getComponent(Component.Identifier.Axis.X), ButtonFunction.DIRECTION_X);
            layout.attach(layout.getComponent(Component.Identifier.Axis.Y), ButtonFunction.DIRECTION_Y);
            //layout.attach(layout.getComponent(Component.Identifier.Axis.Z), ButtonFunction.DIRECTION_Y);
            layout.attach(layout.getComponent(Component.Identifier.Axis.RY), ButtonFunction.ORIENTATION_X);
            layout.attach(layout.getComponent(Component.Identifier.Axis.RX), ButtonFunction.ORIENTATION_Y);
            layout.attach(layout.getComponent(Component.Identifier.Axis.Z), ButtonFunction.CHIP_STRENGTH);
            layout.attach(layout.getComponent(Component.Identifier.Axis.Z), ButtonFunction.KICK_STRENGTH);
            layout.attach(layout.getComponent(Component.Identifier.Button._7), ButtonFunction.SELECT_NEXT_ROBOT);
            layout.attach(layout.getComponent(Component.Identifier.Button._6), ButtonFunction.SELECT_PREV_ROBOT);
            layout.attach(layout.getComponent(Component.Identifier.Button._8), ButtonFunction.STOP_ALL_ROBOTS);
            layout.attach(layout.getComponent(Component.Identifier.Axis.POV), ButtonFunction.DIRECTION_POV);
        } else if (layout.getController().getType() == Controller.Type.KEYBOARD) {
            layout.attach(layout.getComponent(Component.Identifier.Key.F), ButtonFunction.KICK);
            layout.attach(layout.getComponent(Component.Identifier.Key.G), ButtonFunction.CHIP);
            layout.attach(layout.getComponent(Component.Identifier.Key.H), ButtonFunction.DRIBBLE_TOGGLE);
            layout.attach(layout.getComponent(Component.Identifier.Key.A), ButtonFunction.DIRECTION_LEFT);
            layout.attach(layout.getComponent(Component.Identifier.Key.D), ButtonFunction.DIRECTION_RIGHT);
            layout.attach(layout.getComponent(Component.Identifier.Key.W), ButtonFunction.DIRECTION_FORWARD);
            layout.attach(layout.getComponent(Component.Identifier.Key.S), ButtonFunction.DIRECTION_BACKWARD);
            layout.attach(layout.getComponent(Component.Identifier.Key.Q), ButtonFunction.ORIENTATION_WEST);
            layout.attach(layout.getComponent(Component.Identifier.Key.E), ButtonFunction.ORIENTATION_EAST);
            layout.attach(layout.getComponent(Component.Identifier.Key._2), ButtonFunction.SELECT_NEXT_ROBOT);
            layout.attach(layout.getComponent(Component.Identifier.Key._1), ButtonFunction.SELECT_PREV_ROBOT);
            layout.attach(layout.getComponent(Component.Identifier.Key.SPACE), ButtonFunction.STOP_ALL_ROBOTS);
        } else {
            return false;
        }
        //layout.saveAsDefault();
        return true;
    }

    /**
     * attach a specific {@link AbstractComponent} to a {@link ButtonFunction}. Will overwrite a
     * binding whenever a {@link AbstractComponent} is already bound to a {@link ButtonFunction}
     *
     * @param component component to link to
     * @param function  buttonfunction to define
     * @return true on success
     */
    public boolean attach(final Component component, final ButtonFunction function) {
        return this.bindings.put(component, function);
    }

    /**
     * Checks whether the given {@link ButtonFunction} is already bound to a {@link Component}
     *
     * @param function The {@link ButtonFunction} to check for
     * @return true if it's already bound, false otherwise
     */
    public boolean containsBinding(final ButtonFunction function) {
        return this.bindings.entries().stream().filter(entry -> entry.getValue().equals(function)).count() > 0;
    }

    /**
     * Detach a binding by {@link ButtonFunction}.
     *
     * @param function buttonFunction to detach
     */
    public void detach(final ButtonFunction function) {
        this.bindings.removeAll(function);
    }

    /**
     * detatch the binding of a button
     *
     * @param button button to detatch
     * @return succes value
     */
    public boolean detach(final Component button) {
        this.bindings.removeAll(button);
        return !this.bindings.containsKey(button);
    }

    /**
     * Gets the value for a specific {@link ButtonFunction}
     *
     * @param function function to read
     * @return value for specific function
     */
    public float get(final ButtonFunction function) {

        if (!this.containsBinding(function)) {
            ControllerLayout.LOG.info("Could not get data for %s, no binding found.\n", function);
            return 0f;
        }

        return this.bindings.entries().stream().filter(entry -> entry.getValue().equals(function))
                .map(entry -> entry.getKey().getPollData()).findFirst().get();
    }

    /**
     * @return Map with all the bindings for this layout
     */
    public Multimap<Component, ButtonFunction> getBindings() {
        return this.bindings;
    }

    /**
     * Sets the bindings for the {@link ControllerLayout}
     * @param bindings the bindings to set
     */
    public void setBindings(Multimap<Component, ButtonFunction> bindings){
        this.bindings = bindings;
    }

    /**
     * @param id unique identifier for a component
     * @return a 'physical' component on a {@link Controller}
     */
    public Component getComponent(final Identifier id) {
        return this.controller.getComponent(id);
    }

    /**
     * @param identifier The identifier of the component, can be retrieved with {@link Component#getIdentifier()}
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
        return String.format("%s %s", Controller.class.getSimpleName(), this.controller.getName()).replace(" ", "_")
                + ".json";
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
     * @param pattern piece of String to match
     * @return succes value
     */
    public boolean hasButtons(final String pattern) {
        return this.bindings.values().stream().anyMatch(entry -> entry.toString().contains(pattern));
    }

    /**
     * @return whether this controller is complete (e.g. has orientation and directional assignments)
     */
    public boolean isComplete() {
        return this.hasButtons("DIRECTION_") && this.hasButtons("ORIENTATION_");
    }

}