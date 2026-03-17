package com.example.speedforce.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class ItemValueRegistry {
    private static final Map<Item, Integer> ITEM_VALUES = new HashMap<>();

    static {
        ITEM_VALUES.put(Items.DIAMOND, 50);
        ITEM_VALUES.put(Items.DIAMOND_BLOCK, 450);
        ITEM_VALUES.put(Items.NETHERITE_INGOT, 100);
        ITEM_VALUES.put(Items.NETHERITE_BLOCK, 900);
        ITEM_VALUES.put(Items.EMERALD, 30);
        ITEM_VALUES.put(Items.EMERALD_BLOCK, 270);
        ITEM_VALUES.put(Items.GOLD_INGOT, 10);
        ITEM_VALUES.put(Items.GOLD_BLOCK, 90);
        ITEM_VALUES.put(Items.IRON_INGOT, 5);
        ITEM_VALUES.put(Items.IRON_BLOCK, 45);
        ITEM_VALUES.put(Items.COAL, 2);
        ITEM_VALUES.put(Items.CHARCOAL, 2);
        ITEM_VALUES.put(Items.REDSTONE, 3);
        ITEM_VALUES.put(Items.LAPIS_LAZULI, 5);
        ITEM_VALUES.put(Items.QUARTZ, 8);
        ITEM_VALUES.put(Items.ENDER_PEARL, 20);
        ITEM_VALUES.put(Items.NETHER_STAR, 500);
        ITEM_VALUES.put(Items.BEACON, 2000);
        ITEM_VALUES.put(Items.DRAGON_EGG, 10000);
        ITEM_VALUES.put(Items.ANCIENT_DEBRIS, 50);
        ITEM_VALUES.put(Items.NETHERITE_SCRAP, 25);
        ITEM_VALUES.put(Items.NETHERITE_HELMET, 500);
        ITEM_VALUES.put(Items.NETHERITE_CHESTPLATE, 800);
        ITEM_VALUES.put(Items.NETHERITE_LEGGINGS, 700);
        ITEM_VALUES.put(Items.NETHERITE_BOOTS, 400);
        ITEM_VALUES.put(Items.NETHERITE_SWORD, 400);
        ITEM_VALUES.put(Items.NETHERITE_PICKAXE, 400);
        ITEM_VALUES.put(Items.NETHERITE_AXE, 400);
        ITEM_VALUES.put(Items.NETHERITE_SHOVEL, 400);
        ITEM_VALUES.put(Items.NETHERITE_HOE, 400);
        ITEM_VALUES.put(Items.DIAMOND_SWORD, 100);
        ITEM_VALUES.put(Items.DIAMOND_PICKAXE, 100);
        ITEM_VALUES.put(Items.DIAMOND_AXE, 100);
        ITEM_VALUES.put(Items.DIAMOND_SHOVEL, 100);
        ITEM_VALUES.put(Items.DIAMOND_HOE, 100);
        ITEM_VALUES.put(Items.DIAMOND_HELMET, 150);
        ITEM_VALUES.put(Items.DIAMOND_CHESTPLATE, 250);
        ITEM_VALUES.put(Items.DIAMOND_LEGGINGS, 225);
        ITEM_VALUES.put(Items.DIAMOND_BOOTS, 125);
        ITEM_VALUES.put(Items.BLAZE_ROD, 15);
        ITEM_VALUES.put(Items.BLAZE_POWDER, 8);
        ITEM_VALUES.put(Items.GHAST_TEAR, 25);
        ITEM_VALUES.put(Items.CHORUS_FRUIT, 10);
        ITEM_VALUES.put(Items.SHULKER_SHELL, 30);
        ITEM_VALUES.put(Items.TOTEM_OF_UNDYING, 1500);
        ITEM_VALUES.put(Items.ENCHANTED_GOLDEN_APPLE, 500);
        ITEM_VALUES.put(Items.GOLDEN_APPLE, 100);
        ITEM_VALUES.put(Items.ELYTRA, 800);
        ITEM_VALUES.put(Items.TRIDENT, 400);
        ITEM_VALUES.put(Items.HEART_OF_THE_SEA, 300);
        ITEM_VALUES.put(Items.NAUTILUS_SHELL, 50);
        ITEM_VALUES.put(Items.PHANTOM_MEMBRANE, 20);
        ITEM_VALUES.put(Items.RABBIT_FOOT, 15);
        ITEM_VALUES.put(Items.TURTLE_SCUTE, 30);
        ITEM_VALUES.put(Items.COPPER_INGOT, 3);
        ITEM_VALUES.put(Items.COPPER_BLOCK, 27);
        ITEM_VALUES.put(Items.AMETHYST_SHARD, 8);
        ITEM_VALUES.put(Items.ECHO_SHARD, 100);
        ITEM_VALUES.put(Items.DISC_FRAGMENT_5, 200);
        ITEM_VALUES.put(Items.MUSIC_DISC_OTHERSIDE, 500);
        ITEM_VALUES.put(Items.MUSIC_DISC_5, 500);
        ITEM_VALUES.put(Items.NAUTILUS_SHELL, 50);
    }

    public static int getValue(Item item) {
        return ITEM_VALUES.getOrDefault(item, 0);
    }

    public static int getValueWithCount(Item item, int count) {
        return getValue(item) * count;
    }

    public static boolean hasValue(Item item) {
        return ITEM_VALUES.containsKey(item);
    }
}