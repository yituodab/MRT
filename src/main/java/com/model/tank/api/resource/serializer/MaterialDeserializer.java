package com.model.tank.api.resource.serializer;

import com.google.gson.*;
import com.model.tank.resource.data.Recipe;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

import java.lang.reflect.Type;

public class MaterialDeserializer implements JsonDeserializer<Recipe.Material> {
    @Override
    public Recipe.Material deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){
        if(!json.isJsonObject()){
            return null;
        }
        JsonObject object = json.getAsJsonObject();
        Ingredient item = Ingredient.EMPTY;
        if(object.has("item")){
            item = Ingredient.fromJson(object.get("item"));
        }
        int count = GsonHelper.getAsInt(object,"count");
        return new Recipe.Material(item,count);
    }
}
