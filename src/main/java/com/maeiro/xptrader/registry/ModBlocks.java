package com.maeiro.xptrader.registry;

import com.maeiro.xptrader.XPTraderMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, XPTraderMod.MODID);

    public static final RegistryObject<Block> XP_TRADER_WORKSTATION = BLOCKS.register(
            "xp_trader_workstation",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5F)
                    .sound(SoundType.WOOD))
    );

    private ModBlocks() {
    }
}
