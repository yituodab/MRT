package com.model.tank.resource.client.data.tank;

import com.google.gson.annotations.SerializedName;
import com.model.tank.ModularTank;
import net.minecraft.resources.ResourceLocation;

public class TankDisplay {
    @SerializedName("name")
    private String name = "mrt.tanks.default";
    @SerializedName("model")
    private ResourceLocation model = new ResourceLocation(ModularTank.MODID, "default.json");
    @SerializedName("texture")
    private ResourceLocation texture = new ResourceLocation(ModularTank.MODID, "default.png");;
    @SerializedName("icon")
    private ResourceLocation icon = new ResourceLocation(ModularTank.MODID, "default_icon.png");

    public String getName() {
        return name;
    }
    public ResourceLocation getModel() {
        return model;
    }
    public ResourceLocation getTexture() {
        return texture;
    }

    public ResourceLocation getIcon() {
        return icon;
    }
}
