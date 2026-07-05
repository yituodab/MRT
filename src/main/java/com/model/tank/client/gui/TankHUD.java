package com.model.tank.client.gui;

import com.model.tank.ModularTank;
import com.model.tank.api.client.interfaces.ILocalPlayer;
import com.model.tank.entities.TankEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Map;

public class TankHUD implements IGuiOverlay {
    @Override
    public void render(ForgeGui forgeGui, GuiGraphics guiGraphics, float v, int width, int height) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof TankEntity tank) {
            guiGraphics.setColor(1, 1, 1, 1);
            // 载具当前信息
            renderTankInfo(guiGraphics, tank, width, height);
            // 瞄具HUD
            if(((ILocalPlayer)player).isAim())
                renderTankAim(guiGraphics, width, height);
            renderTankCannonballs(guiGraphics, tank, width, height);
        }
    }
    public void renderTankInfo(GuiGraphics guiGraphics, TankEntity tank, int width, int height){
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font,
                Component.translatable("mrt.hud.currentSpeed").append((int)(tank.getCurrentSpeed()*72)+" km/h"),
                4, height - 12, 0xFFFFFF, false);

    }
    public void renderTankAim(GuiGraphics guiGraphics, int width, int height){
        guiGraphics.blit(new ResourceLocation(ModularTank.MODID, "textures/hud/aim.png"),
                0,0,0,0,width,height,width,height);
    }
    public void renderTankCannonballs(GuiGraphics guiGraphics, TankEntity tank, int width, int height){
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Font font = mc.font;
        int cannonballTypeNumber = tank.getCannonballs().size();
        int renderStartX = width / 2 - (cannonballTypeNumber * 16);//cannonballTypeNumber/2*32
        int renderStartY = height - 32;
        int number = 0;
        for (Map.Entry<ResourceLocation, TankEntity.Cannonball> entry : tank.getCannonballs().entrySet()) {
            TankEntity.Cannonball cannonball = entry.getValue();
            int n = cannonball.getNumber();
            if (player != null && player.isCreative()) n = 999;
            guiGraphics.blit(new ResourceLocation(ModularTank.MODID, "textures/hud/cannonball_icons/" +
                            cannonball.getData().getType().toString().toLowerCase() + ".png"),
                    renderStartX + number * 32, renderStartY, 0, 0, 32, 32, 32, 32);
            guiGraphics.drawString(font, String.valueOf(n), renderStartX + number * 32 + 32 - font.width(String.valueOf(n)), renderStartY, 0xFFFFFF, false);
            guiGraphics.drawString(font, String.valueOf(number), renderStartX + number * 32 + 14, renderStartY + 23, 0xFFFFFF, false);
            number += 1;
        }
    }
}
