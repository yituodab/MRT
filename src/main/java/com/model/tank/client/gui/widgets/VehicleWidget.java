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
    private final ResourceLocation recipeId;
    private final Recipe recipe;

    public VehicleWidget(int pX, int pY,ResourceLocation id,Recipe recipe) {
        super(pX, pY, 32, 16, Component.literal(recipe.getOutput().toString()));
        this.recipe = recipe;
        this.recipeId = id;
    }

    public void render(GuiGraphics guiGraphics, int x, int y){
        guiGraphics.blit(VehicleMakerGui.VEHICLE_MAKER_GUI_TEXTURE_LOCATION,x,y,42,130,32,16);
        guiGraphics.blit(DataLoader.getTankIndex(recipe.getOutput()).getDisplay().getIcon(),x+1,y+1,0,0,0,12,12,32,32);
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public ResourceLocation getRecipeId() {
        return recipeId;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
