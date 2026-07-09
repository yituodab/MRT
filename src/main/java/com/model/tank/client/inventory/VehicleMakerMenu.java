package com.model.tank.client.inventory;

import com.model.tank.api.nbt.TankBoxDataManager;
import com.model.tank.init.ModBlockEntity;
import com.model.tank.init.ModItems;
import com.model.tank.resource.DataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VehicleMakerMenu extends AbstractContainerMenu {
    public VehicleMakerMenu(int pContainerId) {
        super(ModBlockEntity.VEHICLE_MAKER_MENU_TYPE.get(), pContainerId);
    }
    public void craft(ResourceLocation recipeId, ServerPlayer player){
        Level level = player.level();
        if(!level.isClientSide){
            ItemStack itemStack = new ItemStack(ModItems.TANK_BOX.get());
            itemStack.getOrCreateTag().putString(TankBoxDataManager.TANK_ID,DataLoader.getRecipe(recipeId).getOutput().toString());
            ItemEntity entity = new ItemEntity(level,player.getX(),player.getY()+1,player.getZ(),itemStack);
            entity.setPickUpDelay(0);
            level.addFreshEntity(entity);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

}
