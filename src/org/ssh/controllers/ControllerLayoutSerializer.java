package org.ssh.controllers;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Custom json-serializer for the {@link ControllerLayout} {@link ControllerLayout} couldn't be
 * serialized using the default method, since {@link Multimap} needs to be converted using the
 * asMap() function
 * 
 * @author Thomas Hakkers
 * @see {@link ControllerLayoutDeserializer}
 */
public class ControllerLayoutSerializer implements JsonSerializer<ControllerLayout> {
    
    @Override
    public JsonElement serialize(ControllerLayout src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        Gson gson = new Gson();
        // Convert the name to Json
        object.add("name", gson.toJsonTree(src.getController().getName()));
        // Convert the bindings to Json as a map.
        object.add("bindings", gson.toJsonTree(src.bindings.asMap()));
        return object;
    }
}
