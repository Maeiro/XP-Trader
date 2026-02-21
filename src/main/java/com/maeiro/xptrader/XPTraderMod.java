package com.maeiro.xptrader;

import com.mojang.logging.LogUtils;
import com.maeiro.xptrader.config.XPTraderCommonConfig;
import com.maeiro.xptrader.registry.ModBlocks;
import com.maeiro.xptrader.registry.ModItems;
import com.maeiro.xptrader.registry.ModPoiTypes;
import com.maeiro.xptrader.registry.ModVillagerProfessions;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(XPTraderMod.MODID)
public class XPTraderMod {
    public static final String MODID = "xp_trader";
    public static final Logger LOGGER = LogUtils.getLogger();

    public XPTraderMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModPoiTypes.POI_TYPES.register(modEventBus);
        ModVillagerProfessions.VILLAGER_PROFESSIONS.register(modEventBus);

        modEventBus.addListener(this::onBuildCreativeModeTabContents);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, XPTraderCommonConfig.SPEC);
    }

    private void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.XP_TRADER_WORKSTATION_ITEM.get());
        }
    }
}
