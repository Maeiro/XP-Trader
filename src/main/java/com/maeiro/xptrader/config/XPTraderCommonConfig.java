package com.maeiro.xptrader.config;

import com.mojang.logging.LogUtils;
import com.maeiro.xptrader.XPTraderMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

import java.util.List;

@Mod.EventBusSubscriber(modid = XPTraderMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class XPTraderCommonConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int LEVEL_COUNT = 5;

    private static final List<Integer> DEFAULT_LEVELS_PER_TRADE = List.of(12, 20, 24, 28, 32);
    private static final List<Integer> DEFAULT_EMERALDS_PER_TRADE = List.of(1, 2, 3, 4, 5);
    private static final List<Integer> DEFAULT_MAX_USES_PER_TRADE = List.of(16, 14, 12, 10, 8);
    private static final List<Integer> DEFAULT_VILLAGER_XP_PER_TRADE = List.of(2, 5, 10, 15, 20);

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> LEVELS_PER_TRADE_BY_LEVEL =
            BUILDER.comment("Player levels consumed per trade by villager level [1..5].")
                    .defineListAllowEmpty("levelsPerTradeByLevel", DEFAULT_LEVELS_PER_TRADE, XPTraderCommonConfig::isPositiveInteger);

    private static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> EMERALDS_PER_TRADE_BY_LEVEL =
            BUILDER.comment("Emeralds awarded per villager level [1..5].")
                    .defineListAllowEmpty("emeraldsPerTradeByLevel", DEFAULT_EMERALDS_PER_TRADE, XPTraderCommonConfig::isPositiveInteger);

    private static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> MAX_USES_PER_TRADE_BY_LEVEL =
            BUILDER.comment("Trade max uses per villager level [1..5].")
                    .defineListAllowEmpty("maxUsesPerTradeByLevel", DEFAULT_MAX_USES_PER_TRADE, XPTraderCommonConfig::isPositiveInteger);

    private static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> VILLAGER_XP_PER_TRADE_BY_LEVEL =
            BUILDER.comment("Villager XP gained per trade by villager level [1..5].")
                    .defineListAllowEmpty("villagerXpPerTradeByLevel", DEFAULT_VILLAGER_XP_PER_TRADE, XPTraderCommonConfig::isPositiveInteger);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static List<Integer> levelsPerTradeByLevel = DEFAULT_LEVELS_PER_TRADE;
    private static List<Integer> emeraldsPerTradeByLevel = DEFAULT_EMERALDS_PER_TRADE;
    private static List<Integer> maxUsesPerTradeByLevel = DEFAULT_MAX_USES_PER_TRADE;
    private static List<Integer> villagerXpPerTradeByLevel = DEFAULT_VILLAGER_XP_PER_TRADE;

    private XPTraderCommonConfig() {
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        levelsPerTradeByLevel = validateAndCopy(LEVELS_PER_TRADE_BY_LEVEL.get(), DEFAULT_LEVELS_PER_TRADE, "levelsPerTradeByLevel");
        emeraldsPerTradeByLevel = validateAndCopy(EMERALDS_PER_TRADE_BY_LEVEL.get(), DEFAULT_EMERALDS_PER_TRADE, "emeraldsPerTradeByLevel");
        maxUsesPerTradeByLevel = validateAndCopy(MAX_USES_PER_TRADE_BY_LEVEL.get(), DEFAULT_MAX_USES_PER_TRADE, "maxUsesPerTradeByLevel");
        villagerXpPerTradeByLevel = validateAndCopy(VILLAGER_XP_PER_TRADE_BY_LEVEL.get(), DEFAULT_VILLAGER_XP_PER_TRADE, "villagerXpPerTradeByLevel");
    }

    public static int levelsForTradeForLevel(int level) {
        return valueForLevel(levelsPerTradeByLevel, level, DEFAULT_LEVELS_PER_TRADE);
    }

    public static int emeraldsForLevel(int level) {
        return valueForLevel(emeraldsPerTradeByLevel, level, DEFAULT_EMERALDS_PER_TRADE);
    }

    public static int maxUsesForLevel(int level) {
        return valueForLevel(maxUsesPerTradeByLevel, level, DEFAULT_MAX_USES_PER_TRADE);
    }

    public static int villagerXpForLevel(int level) {
        return valueForLevel(villagerXpPerTradeByLevel, level, DEFAULT_VILLAGER_XP_PER_TRADE);
    }

    private static boolean isPositiveInteger(Object value) {
        return value instanceof Integer integer && integer > 0;
    }

    private static List<Integer> validateAndCopy(List<? extends Integer> configuredValues, List<Integer> defaults, String keyName) {
        if (configuredValues.size() != LEVEL_COUNT || configuredValues.stream().anyMatch(value -> value == null || value <= 0)) {
            LOGGER.warn("Invalid config for '{}': expected exactly {} positive integers. Falling back to defaults {}.",
                    keyName, LEVEL_COUNT, defaults);
            return defaults;
        }
        return List.copyOf(configuredValues);
    }

    private static int valueForLevel(List<Integer> values, int level, List<Integer> defaults) {
        int index = Math.max(1, Math.min(LEVEL_COUNT, level)) - 1;
        if (values.size() != LEVEL_COUNT) {
            return defaults.get(index);
        }
        return values.get(index);
    }
}
