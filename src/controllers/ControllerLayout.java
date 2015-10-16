package controllers;

import java.net.URI;
import java.util.Map.Entry;

import model.Model;
import model.enums.ButtonFunction;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import util.BiMap;
import util.Logger;

/**
 * Used for binding {@link AbstractComponent Components} as found in a {@link Controller} to a {@link ButtonFunction}.<br>
 * note: {@link ButtonFunctions} and {@link AbstractComponent} cannot be mounted more than one at a time.  
 * 
 * @author Jeroen
 *
 */
public class ControllerLayout extends Model{
	/**
	 * JInput Model representing physical {@link Controller} 
	 */
	private Controller controller;
	/**
	 * Map containing all {@link Component} as found on the {@link controller} and the bound {@link ButtonFunction functions}
	 */
	private BiMap<Component, ButtonFunction> bindings = new BiMap<Component, ButtonFunction>();
	// respective logger
	private Logger logger = Logger.getLogger();
	
	/**
	 * Instantiates a layout linked to given {@link Controller} 
	 * @param controller controller to link to
	 */
	public ControllerLayout(Controller controller){
		super("controller", controller.getType().toString());
		this.controller = controller;
	}
	
	/**
	 * Instantiates a layout linked to given {@link Controller}, and loads bindings from a given config
	 * 
	 * @param controller	controller to link to
	 * @param configFile	configfile with bindings
	 */
	public ControllerLayout(Controller controller, URI configFile){
		this(controller);
		logger.warning("Configfiles not yet implemented, no buttons assigned to controller");
		//TODO implement
	}

	/**
	 * attach a specific {@link AbstractComponent} to a {@link ButtonFunction}. Will overwrite a binding whenever a {@link AbstractComponent} is already bound to a {@link ButtonFunction} 
	 * 
	 * @param component	component to link to 
	 * @param function	buttonfunction to define
	 * @return
	 */
	public boolean attach(Component component, ButtonFunction function){
		//check if it will be overriden
		if(bindings.containsKey(component)){
			logger.warning(String.format("Button already bound %s (to %s).\n", component.toString(), bindings.get(component)));
			bindings.remove(component);
		}

		if(bindings.containsValue(function)){
			logger.warning(String.format("Function '%s' already bound, and will be overwriten.\n", function));
			bindings.removeByValue(function);
		}
		
		bindings.put(component, function);

		return true;
	}
	
	/**
	 * Detach a binding by {@link ButtonFunction}.
	 * 
	 * TODO refactor, since ButtonFunctions and AbstractComponents can only be bound to one of another, 
	 * there should be 0 reason to use streams
	 * 
	 * @param function	buttonFunction to detach
	 */
	public void detach(ButtonFunction function){
		bindings.removeByValue(function);
	}

	/**
	 * detatch the binding of a button
	 * 
	 * @param button button to detatch
	 * @return succes value
	 */
	public boolean detach(Component button){
		bindings.remove(button);
		return !bindings.containsKey(button);
	}

	public Controller getController(){
		return controller;
	}
	
	public BiMap<Component, ButtonFunction> getBindings(){
		return bindings;
	}

	public boolean containsBinding(ButtonFunction function) {
		//TODO too hackisch
		if((function.equals(ButtonFunction.CHIP_STRENGTH) || function.equals(ButtonFunction.KICK_STRENGTH)) && containsBinding(ButtonFunction.CHIPKICK_STRENGTH))
			return true;

		return bindings.entrySet().stream()
				.filter(entry -> entry.getValue().equals(function))
				.count() > 0;
	}

	public float get(ButtonFunction function) {
		//TODO dirty hackish
		if((function.equals(ButtonFunction.CHIP_STRENGTH) || function.equals(ButtonFunction.KICK_STRENGTH)) && containsBinding(ButtonFunction.CHIPKICK_STRENGTH))
			return get(ButtonFunction.CHIPKICK_STRENGTH);
			

		if(!containsBinding(function)){
			logger.warning("Could not get data for %s, no binding found.\n", function);
			return 0f;
		}

		return bindings.entrySet().stream()
				.filter(entry -> entry.getValue().equals(function))
				.map(entry -> entry.getKey().getPollData())
				.findFirst().get();
	}

	public boolean isComplete() {
		return hasOrientationButtons() && hasDirectionButtons();
	}

	public boolean hasOrientationButtons(){
		return bindings.entrySetByValue().stream().filter(entry -> entry.getKey().toString().contains("ORIENTATION_")).count() > 0;	
	}
	public boolean hasDirectionButtons(){
		return bindings.entrySetByValue().stream().filter(entry -> entry.getKey().toString().contains("DIRECTION_")).count() > 0;	
	}
}