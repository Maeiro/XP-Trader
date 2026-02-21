package com.maeiro.xptrader.registry;

import com.google.common.collect.ImmutableSet;
import com.maeiro.xptrader.XPTraderMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, XPTraderMod.MODID);
    public static final ResourceKey<PoiType> XP_TRADER_POI_KEY =
            ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(XPTraderMod.MODID, "xp_trader_poi"));

    public static final RegistryObject<PoiType> XP_TRADER_POI = POI_TYPES.register(
            "xp_trader_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(ModBlocks.XP_TRADER_WORKSTATION.get().getStateDefinition().getPossibleStates()),
                    1,
                    1
            )
    );

    private ModPoiTypes() {
    }
}
