package com.model.tank;

import com.model.tank.client.gui.VehicleMakerGui;
import com.model.tank.client.key.AimKey;
import com.model.tank.client.key.MoveKey;
import com.model.tank.client.key.ShootKey;
import com.model.tank.client.render.tank.TankRender;
import com.model.tank.client.render.cannonball.CannonballRender;
import com.model.tank.client.gui.TankHUD;
import com.model.tank.init.*;
import com.model.tank.network.NetWorkManager;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ModularTank.MODID)
public class ModularTank
{
    public static boolean IsInGame = Minecraft.getInstance().isWindowActive();
    public static final boolean TACZ_LOADED = ModList.get().isLoaded("tacz");
    public static final String MODID = "mrt";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogUtils.getLogger();
    

    public ModularTank()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.init());
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntity.BLOCK_TYPES.register(modEventBus);
        ModBlockEntity.MENU_TYPES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(NetWorkManager::init);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void keyRegister(RegisterKeyMappingsEvent event){
            event.register(AimKey.AIM_KEY);
            event.register(ShootKey.SHOOT_KEY);
            event.register(MoveKey.UP_KEY);
            event.register(MoveKey.DOWN_KEY);
            event.register(MoveKey.LEFT_KEY);
            event.register(MoveKey.RIGHT_KEY);
        }
        @SubscribeEvent
        public static void hudRegister(RegisterGuiOverlaysEvent event){
            event.registerAboveAll("tank_hud", new TankHUD());
        }
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(()->{
                MenuScreens.register(ModBlockEntity.VEHICLE_MAKER_MENU_TYPE.get(), VehicleMakerGui::new);
                EntityRenderers.register(ModEntities.TANKENTITY.get(), TankRender::new);
                EntityRenderers.register(ModEntities.CANNONBALLENTITY.get(), CannonballRender::new);
            });
        }
    }
}
