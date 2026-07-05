package com.model.tank.resource.data.tank;

import com.google.gson.annotations.SerializedName;
import com.model.tank.resource.Countries;
import net.minecraft.resources.ResourceLocation;

public class TankIndexData {
    @SerializedName("tank_data")
    private ResourceLocation tank_data;
    @SerializedName("display")
    private ResourceLocation display;
    @SerializedName("country")
    private Countries country = Countries.CN;

    public ResourceLocation getTankData() {
        return tank_data;
    }

    public ResourceLocation getDisplay() {
        return display;
    }

    public Countries getCountry() {
        return country;
    }
}
