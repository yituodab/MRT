package com.model.tank.client.gui;

import com.model.tank.ModularTank;
import com.model.tank.client.gui.widgets.TechnologyTreeWidget;
import com.model.tank.client.gui.widgets.VehicleWidget;
import com.model.tank.client.inventory.VehicleMakerMenu;
import com.model.tank.network.C2S.ClientCraftVehicle;
import com.model.tank.network.NetWorkManager;
import com.model.tank.resource.Countries;
import com.model.tank.resource.data.Recipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class VehicleMakerGui extends AbstractContainerScreen<VehicleMakerMenu> {
    public static final ResourceLocation VEHICLE_MAKER_GUI_TEXTURE_LOCATION = new ResourceLocation(ModularTank.MODID, "textures/gui/vehicle_maker.png");

    private Countries current_page = Countries.CN;
    private TechnologyTreeWidget widget;
    private ImageButton ibutton = null;

    public VehicleMakerGui(VehicleMakerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 256;
        this.imageHeight = 128;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        addCountryButton();
        addCraftButton();
        addTechnologyTree();
    }
    private void addTechnologyTree(){
        this.widget = new TechnologyTreeWidget(leftPos+2,topPos+19,189,107);
        widget.init(this.current_page);
        this.addRenderableWidget(widget);
    }
    private void addCountryButton(){
        int i = 0;
        for(Countries country : Countries.values()){
            if(this.current_page == country){
                this.addRenderableWidget(new ImageButton(leftPos+3+40*i,topPos+2,40,16,0,146,0,VEHICLE_MAKER_GUI_TEXTURE_LOCATION,button->{
                }));
            }else this.addRenderableWidget(new ImageButton(leftPos+3+40*i,topPos+2,40,16,0,130,VEHICLE_MAKER_GUI_TEXTURE_LOCATION,button->{
                this.current_page = country;
                this.init();
            }));
            i++;
        }
    }
    private void addCraftButton(){
        ImageButton ibutton = new ImageButton(leftPos+194,topPos+112,58,12,76,130,VEHICLE_MAKER_GUI_TEXTURE_LOCATION,button->{
            if(button.visible && widget.getSelectedVehicle() != null){
                NetWorkManager.sendToServer(new ClientCraftVehicle(widget.getSelectedVehicle().getRecipeId()));
            }
        });
        ibutton.visible = false;
        this.ibutton = ibutton;
        this.addRenderableWidget(this.ibutton);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if(this.ibutton != null) {
            this.ibutton.visible = this.widget.getSelectedVehicle() != null;
        }
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderCraftText(pGuiGraphics);
        renderVehicleInfo(pGuiGraphics);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        renderBackground(guiGraphics);
        guiGraphics.blit(VEHICLE_MAKER_GUI_TEXTURE_LOCATION, leftPos, topPos, 0,0, 256, 128);
    }
    private void renderCraftText(GuiGraphics guiGraphics){
        if(this.ibutton == null || !this.ibutton.visible)return;
        Component text = Component.translatable("mrt.gui.craft");
        int x = ibutton.getX() + (ibutton.getWidth() - font.width(text))/2;
        int y = ibutton.getY() + (ibutton.getHeight() - font.lineHeight)/2;
        guiGraphics.drawString(font, text, x, y, 0,false);
    }
    private void renderVehicleInfo(GuiGraphics guiGraphics){
        VehicleWidget vehicleWidget = this.widget.getSelectedVehicle();
        if(vehicleWidget == null || this.ibutton == null || !this.ibutton.visible)return;
        guiGraphics.blit(vehicleWidget.getIcon(),this.leftPos+194,this.topPos+21,0,0,0,48,48,32,32);
        guiGraphics.drawString(font, Component.translatable(vehicleWidget.getName()), this.leftPos+194,this.topPos+71,0,false);
        Recipe.Material[] materials = vehicleWidget.getRecipe().getMaterials();
        for(Recipe.Material material : materials){
            ItemStack item = material.getItem().getItems()[0];
            guiGraphics.renderFakeItem(item,leftPos+194,topPos+82);
            guiGraphics.drawString(font, "0/"+ material.getCount(),leftPos+211, topPos+85, 0, false);
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if(pDelta > 0){

        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return super.isMouseOver(pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}
}
