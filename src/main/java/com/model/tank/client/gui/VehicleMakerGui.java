package com.model.tank.client.gui;

import com.model.tank.ModularTank;
import com.model.tank.client.gui.widgets.TechnologyTreeWidget;
import com.model.tank.client.inventory.VehicleMakerMenu;
import com.model.tank.network.C2S.ClientCraftVehicle;
import com.model.tank.network.NetWorkManager;
import com.model.tank.resource.Countries;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VehicleMakerGui extends AbstractContainerScreen<VehicleMakerMenu> {
    public static final ResourceLocation VEHICLE_MAKER_GUI_TEXTURE_LOCATION = new ResourceLocation(ModularTank.MODID, "textures/gui/vehicle_maker.png");

    private Countries current_page = Countries.CN;
    private final TechnologyTreeWidget widget;
    private ImageButton ibutton = null;

    public VehicleMakerGui(VehicleMakerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 256;
        this.imageHeight = 128;
        this.widget = new TechnologyTreeWidget(leftPos+2,topPos+19,189,107);
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        addCountryButton();
        addCraftButton();
        freshTechnologyTree();
    }
    private void freshTechnologyTree(){
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
        ImageButton ibutton = new ImageButton(leftPos+220,topPos+114,32,10,76,130,VEHICLE_MAKER_GUI_TEXTURE_LOCATION,button->{
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
            if (this.widget.getSelectedVehicle() != null) {
                this.ibutton.visible = true;
            }else this.ibutton.visible = false;
        }
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        renderBackground(guiGraphics);
        guiGraphics.blit(VEHICLE_MAKER_GUI_TEXTURE_LOCATION, leftPos, topPos, 0,0, 256, 128);
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
