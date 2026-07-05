package com.model.tank.init;

import com.model.tank.ModularTank;
import com.model.tank.item.CannonballItem;
import com.model.tank.item.TankBoxItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ModularTank.MODID);
    public static final RegistryObject<Item> TANK_BOX = ITEMS.register("tank_box", TankBoxItem::new);
    public static final RegistryObject<Item> VEHICLE_MAKER = ITEMS.register("vehicle_maker", ()-> new BlockItem(ModBlocks.VEHICLE_MAKER.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CANNONBALL_ITEM = ITEMS.register("cannonball_item", CannonballItem::new);
}
