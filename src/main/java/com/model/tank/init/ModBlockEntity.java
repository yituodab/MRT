package com.model.tank.init;

import com.model.tank.ModularTank;
import com.model.tank.block.blockentity.VehicleMakerBlockEntity;
import com.model.tank.client.inventory.VehicleMakerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntity {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, ModularTank.MODID);
    public static final RegistryObject<MenuType<VehicleMakerMenu>> VEHICLE_MAKER_MENU_TYPE = MENU_TYPES.register("vehicle_maker",
            ()-> IForgeMenuType.create((id,inventory,data)->new VehicleMakerMenu(id)));
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModularTank.MODID);
    public static final RegistryObject<BlockEntityType<VehicleMakerBlockEntity>> VEHICLE_MAKER_TYPE = BLOCK_TYPES.register("vehicle_maker",
            ()->BlockEntityType.Builder.of(VehicleMakerBlockEntity::new,
            ModBlocks.VEHICLE_MAKER.get()).build(null));
}
