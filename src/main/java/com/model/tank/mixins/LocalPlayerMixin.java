package com.model.tank.mixins;

import com.model.tank.api.client.interfaces.ILocalPlayer;
import com.model.tank.client.key.ShootKey;
import com.model.tank.entities.TankEntity;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements ILocalPlayer {
    private @Unique boolean modularTank$aim = false;
    private @Unique boolean modularTank$checkModules = false;
    @Override
    public void shoot() {
        TankEntity tank = (TankEntity) ((LocalPlayer)(Object)this).getVehicle();
        if(tank == null)return;
        ShootKey.shoot(tank);
    }

    @Override
    public void aim(boolean aim) {
        this.modularTank$aim = aim;
    }

    @Override
    public boolean isAim() {
        return modularTank$aim;
    }

    @Override
    public void checkModules(boolean isChecking) {
        this.modularTank$checkModules = isChecking;
    }

    @Override
    public boolean isCheckingModules() {
        return this.modularTank$checkModules;
    }
}
