package org.ssh.controllers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import org.ssh.models.enums.ButtonFunction;

import java.lang.reflect.Type;

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
        Multimap<String, ButtonFunction> multimapString = ArrayListMultimap.create();
        src.getBindings().entries().stream().forEach(entry -> multimapString.put(entry.getKey().getIdentifier().toString(), entry.getValue()));
        // Convert the bindings to Json as a map.
        object.add("bindings", gson.toJsonTree(multimapString.asMap()));
        return object;
    }
}
