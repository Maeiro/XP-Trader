package com.maeiro.xptrader.registry;

import com.google.common.collect.ImmutableSet;
import com.maeiro.xptrader.XPTraderMod;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModVillagerProfessions {
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, XPTraderMod.MODID);

    public static final RegistryObject<VillagerProfession> XP_TRADER = VILLAGER_PROFESSIONS.register(
            "xp_trader",
            () -> new VillagerProfession(
                    "xp_trader",
                    holder -> holder.is(ModPoiTypes.XP_TRADER_POI_KEY),
                    holder -> holder.is(ModPoiTypes.XP_TRADER_POI_KEY),
                    ImmutableSet.of(),
                    ImmutableSet.of(ModBlocks.XP_TRADER_WORKSTATION.get()),
                    SoundEvents.VILLAGER_WORK_LIBRARIAN
            )
    );

    private ModVillagerProfessions() {
    }
}
