package org.ssh.models;

import com.google.gson.*;
import javafx.beans.property.*;
import javafx.beans.value.WritableValue;
import org.ssh.util.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This class serializes properties like the value it contains would be serialized. It deserialized these values from
 * a .JSON file back into the right property.
 *
 * @author Ryan
 */
public class PropertyTypeAdapter<T extends WritableValue> implements JsonSerializer<T>, JsonDeserializer<T> {
    /**
     * Logger to log loggable logstuff
     */
    private static final Logger LOG = Logger.getLogger();

    @Override
    public JsonElement serialize(T property, Type type, JsonSerializationContext jsonSerializationContext) {
        // Log the loggable logstuff discussed earlier on
        PropertyTypeAdapter.LOG.info("Serializing: " + type);

        // Every property implements the WritableValue inteface, so all of them have a method "getValue". The value
        // returned by this function should be serialized.
        return jsonSerializationContext.serialize(property.getValue());
    }

    @Override
    public T deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        // Log some more garbage
        PropertyTypeAdapter.LOG.info("Deserializing: " + type);

        // Primitive values always have type instance of "Class"
        if (type instanceof Class) {
            // Some stupid shit because there does not appear to be a way to create a javafx.beans.property of a given type.
            if (type.equals(BooleanProperty.class))
                return (T) new SimpleBooleanProperty(jsonElement.getAsBoolean());
            else if (type.equals(IntegerProperty.class))
                return (T) new SimpleIntegerProperty(jsonElement.getAsInt());
            else if (type.equals(LongProperty.class))
                return (T) new SimpleLongProperty(jsonElement.getAsLong());
            else if (type.equals(FloatProperty.class))
                return (T) new SimpleFloatProperty(jsonElement.getAsFloat());
            else if (type.equals(DoubleProperty.class))
                return (T) new SimpleDoubleProperty(jsonElement.getAsDouble());
            else if (type.equals(StringProperty.class))
                return (T) new SimpleStringProperty(jsonElement.getAsString());
            // Generified values alwways have type instance of "ParameterizedType"
        } else if (type instanceof ParameterizedType) {
            // Get the raw type to find if the type is object, or a list (rawtype = type - generic)
            final Type rawType = ((ParameterizedType) type).getRawType();

            // The generic types could be found by the method "getActualTypeArguments".
            // ListProperty and ObjectProperty always has only one.
            final Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];

            // if the type is an ObjectProperty
            if (rawType.equals(ObjectProperty.class))
                // return a new SimpleObjectProperty of the given generic type.
                return (T) new SimpleObjectProperty<>(jsonDeserializationContext.deserialize(jsonElement, genericType));

                // if the type is an ListProperty
            else if (rawType.equals(ListProperty.class)) {
                // A new arraylist should be created.
                final ArrayList<Object> list = new ArrayList<>();

                // And the data should be retrieved from the JSON file
                final JsonArray jsonArray = jsonElement.getAsJsonArray();

                // If the data was there
                if (jsonArray != null)
                    // loop through the data
                    for (int i = 0; i < jsonArray.size(); i++)
                        // and add every element as the given type
                        list.add(jsonDeserializationContext.deserialize(jsonArray.get(i), genericType));
                // put the list in a observableList, and put this observableList inside a new SimpleListproperty to return.
                return (T) new SimpleListProperty<>(javafx.collections.FXCollections.observableList(list));
            }
        }

        // if this is reached, nothing is returned so far, so the method was not able to deserialize this type.
        PropertyTypeAdapter.LOG.info("Value of type " + type.getTypeName() + " could not be deserialized using this type adapter (PropertyTypeAdapter)!");
        return null;
    }
}
