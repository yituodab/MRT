package com.model.tank.client.gui.widgets;

import com.model.tank.resource.Countries;
import com.model.tank.resource.DataLoader;
import com.model.tank.resource.data.Recipe;
import com.model.tank.resource.data.TechnologyTree;
import com.model.tank.utils.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TechnologyTreeWidget extends AbstractWidget {

    private int insideWidth;
    private int insideHeight;
    private int insideX;
    private int insideY;
    private final List<VehicleWidget> widgets = new ArrayList<>();
    private VehicleWidget selectedVehicle = null;
    public TechnologyTreeWidget(int x, int y, int pWidth, int pHeight) {
        super(x, y, pWidth, pHeight, Component.literal("Technology Tree"));
        insideWidth = pWidth;
        insideHeight = height;
        insideX = 0;
        insideY = 0;
    }
    public void addWidget(ResourceLocation recipeId,Recipe recipe,int x,int y){
        this.widgets.add(new VehicleWidget(x,y,recipeId,recipe));
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.widgets.forEach((widget->{
            widget.renderWidget(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        }));
    }

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        super.onDrag(pMouseX, pMouseY, pDragX, pDragY);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        for(VehicleWidget widget : this.widgets){
            if(widget.isMouseOver(pMouseX,pMouseY)){
                this.selectedVehicle = widget;
            }
            break;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public VehicleWidget getSelectedVehicle() {
        return selectedVehicle;
    }
    public void init(Countries country){
        this.widgets.clear();
        this.selectedVehicle = null;
        TechnologyTree tree = DataLoader.getTechnologyTree(country);
        if(tree == null)return;
        if(tree.getRecipes_to_render().isEmpty())return;
        this.insideWidth = tree.getRecipes_to_render().size()*42 + 10;
        for(List<TechnologyTree.TechnologyRecipe> trees : tree.getRecipes_to_render()){
            this.insideHeight = Math.max(this.insideHeight, trees.size()*26+10);
        }
        int Xspacing = insideWidth/MathUtils.NoZero(tree.getRecipes_to_render().size()).intValue();
        int row = 0;
        for(List<TechnologyTree.TechnologyRecipe> trees : tree.getRecipes_to_render()){
            int line = 0;
            int Yspacing = insideHeight/MathUtils.NoZero(trees.size()).intValue();
            for(TechnologyTree.TechnologyRecipe recipe : trees){
                this.addWidget(recipe.getId(),recipe.getRecipe(),getX()+10+row*Xspacing*42,getY()+10+line*Yspacing*26);
                line++;
            }
            row++;
        }
    }
}
