package com.model.tank.client.gui.widgets;

import com.model.tank.client.gui.VehicleMakerGui;
import com.model.tank.resource.DataLoader;
import com.model.tank.resource.data.Recipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class VehicleWidget extends AbstractWidget {
    private final ResourceLocation icon;
    private final String name;
    private final ResourceLocation recipeId;
    private final Recipe recipe;

    public VehicleWidget(int pX, int pY,ResourceLocation id,Recipe recipe) {
        super(pX, pY, 32, 16, Component.literal(recipe.getOutput().toString()));
        this.recipe = recipe;
        this.recipeId = id;
        this.icon = DataLoader.getTankIndex(recipe.getOutput()).getDisplay().getIcon();
        this.name = DataLoader.getTankIndex(recipe.getOutput()).getDisplay().getName();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public ResourceLocation getRecipeId() {
        return recipeId;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.blit(VehicleMakerGui.VEHICLE_MAKER_GUI_TEXTURE_LOCATION,this.getX(),this.getY(),42,130,32,16);
        guiGraphics.blit(this.icon,this.getX()+1,this.getY()+1,0,0,0,12,12,32,32);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
