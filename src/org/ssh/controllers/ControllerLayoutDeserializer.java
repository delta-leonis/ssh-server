package org.ssh.controllers;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.ssh.managers.manager.Services;
import org.ssh.models.enums.ButtonFunction;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * Custom json-deserializer for the {@link ControllerLayout}
 *
 * @author Thomas Hakkers
 * @see {@link ControllerLayoutSerializer}
 */
public class ControllerLayoutDeserializer implements JsonDeserializer<ControllerLayout> {
    
    @Override
    public ControllerLayout deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){
        // Get the name of the controller for reference
        String name = json.getAsJsonObject().get("name").getAsString();
        
        // Get the controller based on its name
        Optional<Controller> optionalController = Arrays.asList(ControllerEnvironment.getDefaultEnvironment().getControllers())
                .stream()
                // Filter by name
                .filter(c -> c.getName().equals(name))
                // Check if the controller is already in use
                .filter(c -> !((ControllerListener) Services.get("ControllerListener").get())
                    .containsController(c))
                // Retrieve the first in the list
                .findFirst();
        
        // If we have a controlelr
        if(optionalController.isPresent()){
            // Start constructing the layout
            Controller controller = optionalController.get();
            ControllerLayout layout = new ControllerLayout(controller);
            Multimap<Component, ButtonFunction> bindings =  ArrayListMultimap.create();
            
            // Assign bindings
            // For each entry in the json layout
            for (Map.Entry<String, JsonElement> entry : ((JsonObject) json.getAsJsonObject().get("bindings")).entrySet()){
                // Check whether the key exists
                Optional<Component> component = layout.getComponent(entry.getKey());
                // If it does, add it to the bindings
                if(component.isPresent())               
                    entry.getValue().getAsJsonArray().forEach(jsonValue -> bindings.put(component.get(), ButtonFunction.valueOf(jsonValue.getAsString())));
            }
            // Give the new bindings to the layout and return it
            layout.bindings = bindings;
            return layout;
        }
        return null;
    }
}
