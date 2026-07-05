package com.model.tank.mixins;

import com.model.tank.api.client.interfaces.IEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements IEntity {

}
