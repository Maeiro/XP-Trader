package com.maeiro.xptrader.registry;

import com.maeiro.xptrader.XPTraderMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, XPTraderMod.MODID);

    public static final RegistryObject<Item> XP_TRADER_WORKSTATION_ITEM = ITEMS.register(
            "xp_trader_workstation",
            () -> new BlockItem(ModBlocks.XP_TRADER_WORKSTATION.get(), new Item.Properties())
    );

    private ModItems() {
    }
}
