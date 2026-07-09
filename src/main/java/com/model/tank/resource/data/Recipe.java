package com.model.tank.resource.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class Recipe {
    @SerializedName("materials")
    private Material[] materials;

    @SerializedName("output")
    private ResourceLocation output;

    @SerializedName("next")
    private ResourceLocation[] next_recipe;

    public Material[] getMaterials() {
        return materials;
    }

    public ResourceLocation getOutput() {
        return output;
    }

    public ResourceLocation[] getNext() {
        return next_recipe;
    }

    public static class Material {
        private Ingredient item;
        private int count = 1;
        public Material(Ingredient item, int count){
            this.item = item;
            this.count = count;
        }

        public Ingredient getItem() {
            return item;
        }

        public int getCount() {
            return count;
        }
    }
}
