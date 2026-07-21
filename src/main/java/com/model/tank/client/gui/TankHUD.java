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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Map;

public class TankHUD implements IGuiOverlay {
    public static final float DEFAULT_H_FOV = 102.4F;
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
            else renderCrosshairs(guiGraphics,width,height,tank,player);
//            if(((ILocalPlayer)player).isCheckingModules())
//                renderModules(guiGraphics,tank , width, height);
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
    public void renderCrosshairs(GuiGraphics guiGraphics, int width, int height, TankEntity tank, Player player){
        float YRot = (player.getYHeadRot()+360.0F)%360.0F;
        if(Mth.clamp(YRot, 360-DEFAULT_H_FOV/2,360) == YRot || Mth.clamp(YRot, 0,DEFAULT_H_FOV/2) == YRot){
            if(Mth.clamp(tank.getTurretYRot(),
                    (YRot-DEFAULT_H_FOV/2+360)%360,
                    (YRot+DEFAULT_H_FOV/2+360)%360) == tank.getTurretYRot())return;
            if(Mth.clamp(tank.getTurretYRot(),0,(YRot+DEFAULT_H_FOV/2+360)%360) == tank.getTurretYRot()){
                YRot = tank.getTurretYRot()+360-(YRot-DEFAULT_H_FOV/2+360)%360;
            }else YRot = tank.getTurretYRot() - (YRot-DEFAULT_H_FOV/2+360)%360;
        }else if(Mth.clamp(tank.getTurretYRot(),
                YRot-DEFAULT_H_FOV/2,
                YRot+DEFAULT_H_FOV/2) == tank.getTurretYRot())
            YRot = tank.getTurretYRot()-(YRot-DEFAULT_H_FOV/2);
        else return;
        int X = (int)(YRot/DEFAULT_H_FOV*width);
        guiGraphics.blit(new ResourceLocation(ModularTank.MODID, "textures/hud/crosshairs.png"),
                X-16,height/2-16,0,0,16,16,16,16);
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
//    public void renderModules(GuiGraphics guiGraphics, TankEntity tank, int width, int height){
//        PoseStack poseStack = guiGraphics.pose();
//        EntityRenderer
//        poseStack.pushPose();
//        poseStack.popPose();
//    }
}
