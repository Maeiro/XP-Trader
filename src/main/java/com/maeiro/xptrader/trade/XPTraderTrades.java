package com.maeiro.xptrader.trade;

import com.maeiro.xptrader.XPTraderMod;
import com.maeiro.xptrader.config.XPTraderCommonConfig;
import com.maeiro.xptrader.registry.ModBlocks;
import com.maeiro.xptrader.registry.ModVillagerProfessions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = XPTraderMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class XPTraderTrades {
    private static final float PRICE_MULTIPLIER = 0.0F;
    private static final String VIRTUAL_TOKEN_TAG = "xp_trader_virtual_token";
    private static Field merchantMenuTraderField;
    private static Field merchantMenuContainerField;

    private XPTraderTrades() {
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (!Objects.equals(event.getType(), ModVillagerProfessions.XP_TRADER.get())) {
            return;
        }

        Int2ObjectMap<List<VillagerTrades.ItemListing>> tradesByLevel = event.getTrades();
        for (int level = 1; level <= 5; level++) {
            final int villagerLevel = level;
            VillagerTrades.ItemListing listing = (trader, random) -> {
                ItemStack input = new ItemStack(Items.EXPERIENCE_BOTTLE, XPTraderCommonConfig.levelsForTradeForLevel(villagerLevel));
                ItemStack output = new ItemStack(Items.EMERALD, XPTraderCommonConfig.emeraldsForLevel(villagerLevel));

                return new MerchantOffer(
                        input,
                        output,
                        XPTraderCommonConfig.maxUsesForLevel(villagerLevel),
                        XPTraderCommonConfig.villagerXpForLevel(villagerLevel),
                        PRICE_MULTIPLIER
                );
            };
            tradesByLevel.computeIfAbsent(level, ignored -> new ArrayList<>()).add(listing);
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ResourceLocation professionId = ForgeRegistries.VILLAGER_PROFESSIONS.getKey(ModVillagerProfessions.XP_TRADER.get());
        ResourceLocation poiFromState = PoiTypes.forState(ModBlocks.XP_TRADER_WORKSTATION.get().defaultBlockState())
                .map(holder -> ForgeRegistries.POI_TYPES.getKey(holder.value()))
                .orElse(null);

        XPTraderMod.LOGGER.info(
                "[XP Trader] Level-backed trade mode enabled. professionId={}, poiFromWorkstationState={}.",
                professionId,
                poiFromState
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }

        purgeVirtualTokens(event.player);

        if (!(event.player.containerMenu instanceof MerchantMenu menu)) {
            return;
        }

        Merchant trader = getTrader(menu);
        if (!(trader instanceof Villager villager) || !isXpTrader(villager)) {
            return;
        }

        MerchantContainer container = getTradeContainer(menu);
        if (container == null) {
            return;
        }

        int availableLevels = Math.max(0, event.player.experienceLevel);
        container.setItem(0, availableLevels > 0 ? createVirtualBottleToken(Math.min(64, availableLevels)) : ItemStack.EMPTY);
        container.setItem(1, ItemStack.EMPTY);
        container.setChanged();
    }

    @SubscribeEvent
    public static void onTradeCompleted(TradeWithVillagerEvent event) {
        AbstractVillager villager = event.getAbstractVillager();
        if (!(villager instanceof Villager realVillager) || !isXpTrader(realVillager)) {
            return;
        }

        int requiredLevels = event.getMerchantOffer().getCostA().getCount();
        event.getEntity().giveExperienceLevels(-requiredLevels);
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        purgeVirtualTokens(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        purgeVirtualTokens(event.getEntity());
    }

    private static boolean isXpTrader(Villager villager) {
        ResourceLocation professionId = ForgeRegistries.VILLAGER_PROFESSIONS.getKey(villager.getVillagerData().getProfession());
        return professionId != null && XPTraderMod.MODID.equals(professionId.getNamespace());
    }

    private static ItemStack createVirtualBottleToken(int count) {
        ItemStack stack = new ItemStack(Items.EXPERIENCE_BOTTLE, Math.min(64, count));
        stack.getOrCreateTag().putBoolean(VIRTUAL_TOKEN_TAG, true);
        return stack;
    }

    private static void purgeVirtualTokens(net.minecraft.world.entity.player.Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isVirtualToken(stack)) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (isVirtualToken(carried)) {
            player.containerMenu.setCarried(ItemStack.EMPTY);
        }
    }

    private static boolean isVirtualToken(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != Items.EXPERIENCE_BOTTLE) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(VIRTUAL_TOKEN_TAG);
    }

    private static Merchant getTrader(MerchantMenu menu) {
        Field traderField = getMerchantMenuTraderField();
        if (traderField == null) {
            return null;
        }

        try {
            return (Merchant) traderField.get(menu);
        } catch (IllegalAccessException error) {
            XPTraderMod.LOGGER.error("[XP Trader] Failed to access trader from MerchantMenu.", error);
            return null;
        }
    }

    private static MerchantContainer getTradeContainer(MerchantMenu menu) {
        Field tradeContainerField = getMerchantMenuContainerField();
        if (tradeContainerField == null) {
            return null;
        }

        try {
            return (MerchantContainer) tradeContainerField.get(menu);
        } catch (IllegalAccessException error) {
            XPTraderMod.LOGGER.error("[XP Trader] Failed to access MerchantContainer from MerchantMenu.", error);
            return null;
        }
    }

    private static Field getMerchantMenuTraderField() {
        if (merchantMenuTraderField == null) {
            merchantMenuTraderField = resolveFieldByType(MerchantMenu.class, Merchant.class, "trader");
        }
        return merchantMenuTraderField;
    }

    private static Field getMerchantMenuContainerField() {
        if (merchantMenuContainerField == null) {
            merchantMenuContainerField = resolveFieldByType(MerchantMenu.class, MerchantContainer.class, "tradeContainer");
        }
        return merchantMenuContainerField;
    }

    private static Field resolveFieldByType(Class<?> owner, Class<?> type, String preferredName) {
        try {
            Field namedField = owner.getDeclaredField(preferredName);
            if (type.isAssignableFrom(namedField.getType())) {
                namedField.setAccessible(true);
                return namedField;
            }
        } catch (NoSuchFieldException ignored) {
        }

        for (Field field : owner.getDeclaredFields()) {
            if (type.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                return field;
            }
        }

        XPTraderMod.LOGGER.error("[XP Trader] Could not resolve field of type {} in {}.", type.getName(), owner.getName());
        return null;
    }
}
