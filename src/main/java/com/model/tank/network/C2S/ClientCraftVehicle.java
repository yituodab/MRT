package com.model.tank.network.C2S;

import com.model.tank.client.inventory.VehicleMakerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientCraftVehicle(ResourceLocation recipeId) {
    public void encode(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeResourceLocation(recipeId);
    }
    public static ClientCraftVehicle decode(FriendlyByteBuf friendlyByteBuf){
        return new ClientCraftVehicle(friendlyByteBuf.readResourceLocation());
    }
    public void run(Supplier<NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(()->{
            ServerPlayer player = supplier.get().getSender();
            if(player == null)return;
            if(player.containerMenu instanceof VehicleMakerMenu menu){
                menu.craft(this.recipeId,player);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
