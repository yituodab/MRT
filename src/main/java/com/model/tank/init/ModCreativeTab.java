package com.model.tank.init;

import com.model.tank.ModularTank;
import com.model.tank.api.nbt.TankBoxDataManager;
import com.model.tank.resource.DataLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModularTank.MODID);
    public static final RegistryObject<CreativeModeTab> TANK_TAB = CREATIVE_MODE_TABS.register("tank_tab",
            () -> CreativeModeTab.builder().icon(ModCreativeTab::getIcon).
                    title(Component.translatable("itemGroup.modulartank")).
                    displayItems((parameters, output)->{
                        DataLoader.getAllTanks().forEach(tankDataEntry -> {
                            ItemStack item = ModItems.TANK_BOX.get().getDefaultInstance();
                            item.getOrCreateTag().putString(TankBoxDataManager.TANK_ID, tankDataEntry.getKey().toString());
                            output.accept(item);
                        });
                        output.accept(ModItems.VEHICLE_MAKER.get());
                    })
                    .build());
    private static ItemStack getIcon(){
        return ModItems.TANK_BOX.get().getDefaultInstance();
    }
}
