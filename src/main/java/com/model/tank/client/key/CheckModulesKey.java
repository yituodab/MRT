package com.model.tank.client.key;

import com.model.tank.api.client.interfaces.ILocalPlayer;
import com.model.tank.entities.TankEntity;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static com.model.tank.ModularTank.IsInGame;
import static com.model.tank.client.key.KeyRegister.MODULAR_TANK_CATEGORY;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class CheckModulesKey {
    public static final KeyMapping CHECK_MODULES_KEY = new KeyMapping("key.mrt.check_modules", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O,MODULAR_TANK_CATEGORY);
    @SubscribeEvent
    public static void onCheckModulesPress(InputEvent.Key event){
        if (IsInGame && CHECK_MODULES_KEY.matches(event.getKey(), 0)){
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            if (!(player.getVehicle() instanceof TankEntity)) {
                return;
            }
            if (player instanceof ILocalPlayer) {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    ((ILocalPlayer)player).checkModules(true);
                }
                if (event.getAction() == GLFW.GLFW_RELEASE) {
                    ((ILocalPlayer)player).checkModules(false);
                }
            }
        }
    }
}
