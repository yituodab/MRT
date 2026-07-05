package com.model.tank.init;

import com.model.tank.ModularTank;
import com.model.tank.block.VehicleMakerBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, ModularTank.MODID);
    public static final RegistryObject<Block> VEHICLE_MAKER = BLOCKS.register("vehicle_maker", VehicleMakerBlock::new);
}
