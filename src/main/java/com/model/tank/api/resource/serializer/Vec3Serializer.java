package com.model.tank.api.resource.serializer;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Type;

public class Vec3Serializer implements JsonDeserializer<Vec3>, JsonSerializer<Vec3> {
    @Override
    public Vec3 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!json.isJsonArray()){
            return null;
        }
        JsonArray array = json.getAsJsonArray();
        double x = GsonHelper.convertToDouble(array.get(0), "array.0");
        double y = GsonHelper.convertToDouble(array.get(1), "array.1");
        double z = GsonHelper.convertToDouble(array.get(2), "array.2");
        return new Vec3(x, y, z);
    }

    @Override
    public JsonElement serialize(Vec3 src, Type typeOfSrc, JsonSerializationContext context) {
        JsonPrimitive x = new JsonPrimitive(src.x);
        JsonPrimitive y = new JsonPrimitive(src.y);
        JsonPrimitive z = new JsonPrimitive(src.z);
        JsonArray array = new JsonArray(3);
        array.set(0, x);
        array.set(1, y);
        array.set(2, z);
        return array;
    }
}
