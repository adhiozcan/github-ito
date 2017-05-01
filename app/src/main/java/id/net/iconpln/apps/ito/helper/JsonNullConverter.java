package id.net.iconpln.apps.ito.helper;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Ozcan on 18/04/2017.
 */

public class JsonNullConverter implements JsonSerializer<String> {
    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return new JsonPrimitive("");
        } else {
            return new JsonPrimitive(src.toString());
        }
    }

    public String deserialize(JsonElement json, Type typeOfSrc, JsonDeserializationContext context)
            throws JsonParseException {
        return json.getAsJsonPrimitive().getAsString();
    }
}
