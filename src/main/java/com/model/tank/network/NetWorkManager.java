package com.model.tank.network;

import com.model.tank.ModularTank;
import com.model.tank.network.C2S.ClientCraftVehicle;
import com.model.tank.network.C2S.ClientTankReload;
import com.model.tank.network.C2S.ClientTankShoot;
import com.model.tank.network.S2C.ServerTankShoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static com.model.tank.ModularTank.VERSION;

public class NetWorkManager {
    private static int ID = 0;
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ModularTank.MODID, "networking"),
            ()->VERSION,
            VERSION::equals, VERSION::equals
            );
    private static int getID(){
        return ID++;
    }
    public static void init(){
        CHANNEL.messageBuilder(ClientCraftVehicle.class,getID()).encoder(ClientCraftVehicle::encode).
                decoder(ClientCraftVehicle::decode).consumerMainThread(ClientCraftVehicle::run).add();
        CHANNEL.messageBuilder(ClientTankShoot.class,getID()).encoder(ClientTankShoot::encode).
                decoder(ClientTankShoot::decode).consumerMainThread(ClientTankShoot::run).add();
        CHANNEL.messageBuilder(ServerTankShoot.class,getID()).encoder(ServerTankShoot::encode).
                decoder(ServerTankShoot::decode).consumerMainThread(ServerTankShoot::run).add();
        CHANNEL.messageBuilder(ClientTankReload.class,getID()).encoder(ClientTankReload::encode).
                decoder(ClientTankReload::decode).consumerMainThread(ClientTankReload::run).add();
    }
    public static <MSG> void sendToServer(MSG packet){
        CHANNEL.sendToServer(packet);
    }
    public static <MSG> void sendToPlayer(MSG packet, ServerPlayer player){
        CHANNEL.send(PacketDistributor.PLAYER.with(()->player), packet);
    }
}
