package com.example.speedforce.util;

import com.example.speedforce.item.ModItems;
import com.example.speedforce.item.SuitType;
import net.minecraft.world.item.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class SuitPriceRegistry {
    private static final Map<SuitType, Integer> SUIT_PRICES = new HashMap<>();
    private static final int[][] PIECE_PRICES = new int[SuitType.values().length][4];

    static {
        SUIT_PRICES.put(SuitType.FLASH, 4800);
        SUIT_PRICES.put(SuitType.REVERSE_FLASH, 6000);
        SUIT_PRICES.put(SuitType.ZOOM, 7200);
        SUIT_PRICES.put(SuitType.FLASH_S4, 4800);
        SUIT_PRICES.put(SuitType.FLASH_S5, 4800);
        SUIT_PRICES.put(SuitType.KID_FLASH, 4800);
        SUIT_PRICES.put(SuitType.GREEN_ARROW, 3000);

        PIECE_PRICES[SuitType.FLASH.ordinal()] = new int[]{1000, 1400, 1400, 1000};
        PIECE_PRICES[SuitType.REVERSE_FLASH.ordinal()] = new int[]{1250, 1750, 1750, 1250};
        PIECE_PRICES[SuitType.ZOOM.ordinal()] = new int[]{1500, 2100, 2100, 1500};
        PIECE_PRICES[SuitType.FLASH_S4.ordinal()] = new int[]{1000, 1400, 1400, 1000};
        PIECE_PRICES[SuitType.FLASH_S5.ordinal()] = new int[]{1000, 1400, 1400, 1000};
        PIECE_PRICES[SuitType.KID_FLASH.ordinal()] = new int[]{1000, 1400, 1400, 1000};
        PIECE_PRICES[SuitType.GREEN_ARROW.ordinal()] = new int[]{600, 900, 900, 600};
    }

    public static int getPrice(SuitType type) {
        return SUIT_PRICES.getOrDefault(type, 4800);
    }

    public static Map<SuitType, Integer> getAllPrices() {
        return new HashMap<>(SUIT_PRICES);
    }

    public static int getPiecePrice(SuitType suit, int pieceIndex) {
        if (pieceIndex < 0 || pieceIndex > 3) return 0;
        int ordinal = suit.ordinal();
        if (ordinal < 0 || ordinal >= PIECE_PRICES.length) return 0;
        return PIECE_PRICES[ordinal][pieceIndex];
    }

    public static ItemStack getSuitPiece(SuitType suit, int pieceIndex) {
        return switch (suit) {
            case FLASH -> switch (pieceIndex) {
                case 0 -> new ItemStack(ModItems.FLASH_HELMET.get());
                case 1 -> new ItemStack(ModItems.FLASH_CHESTPLATE.get());
                case 2 -> new ItemStack(ModItems.FLASH_LEGGINGS.get());
                case 3 -> new ItemStack(ModItems.FLASH_BOOTS.get());
                default -> ItemStack.EMPTY;
            };
            case REVERSE_FLASH -> switch (pieceIndex) {
                case 0 -> new ItemStack(ModItems.REVERSE_FLASH_HELMET.get());
                case 1 -> new ItemStack(ModItems.REVERSE_FLASH_CHESTPLATE.get());
                case 2 -> new ItemStack(ModItems.REVERSE_FLASH_LEGGINGS.get());
                case 3 -> new ItemStack(ModItems.REVERSE_FLASH_BOOTS.get());
                default -> ItemStack.EMPTY;
            };
            case ZOOM -> switch (pieceIndex) {
                case 0 -> new ItemStack(ModItems.ZOOM_HELMET.get());
                case 1 -> new ItemStack(ModItems.ZOOM_CHESTPLATE.get());
                case 2 -> new ItemStack(ModItems.ZOOM_LEGGINGS.get());
                case 3 -> new ItemStack(ModItems.ZOOM_BOOTS.get());
                default -> ItemStack.EMPTY;
            };
            case FLASH_S4 -> switch (pieceIndex) {
                case 0 -> new ItemStack(ModItems.FLASH_S4_HELMET.get());
                case 1 -> new ItemStack(ModItems.FLASH_S4_CHESTPLATE.get());
                case 2 -> new ItemStack(ModItems.FLASH_S4_LEGGINGS.get());
                case 3 -> new ItemStack(ModItems.FLASH_S4_BOOTS.get());
                default -> ItemStack.EMPTY;
            };
            case FLASH_S5 -> switch (pieceIndex) {
                case 0 -> new ItemStack(ModItems.FLASH_S5_HELMET.get());
                case 1 -> new ItemStack(ModItems.FLASH_S5_CHESTPLATE.get());
                case 2 -> new ItemStack(ModItems.FLASH_S5_LEGGINGS.get());
                case 3 -> new ItemStack(ModItems.FLASH_S5_BOOTS.get());
                default -> ItemStack.EMPTY;
            };
            case KID_FLASH -> switch (pieceIndex) {
                case 0 -> new ItemStack(ModItems.KID_FLASH_HELMET.get());
                case 1 -> new ItemStack(ModItems.KID_FLASH_CHESTPLATE.get());
                case 2 -> new ItemStack(ModItems.KID_FLASH_LEGGINGS.get());
                case 3 -> new ItemStack(ModItems.KID_FLASH_BOOTS.get());
                default -> ItemStack.EMPTY;
            };
            case GREEN_ARROW -> switch (pieceIndex) {
                case 0 -> new ItemStack(ModItems.GREEN_ARROW_HELMET.get());
                case 1 -> new ItemStack(ModItems.GREEN_ARROW_CHESTPLATE.get());
                case 2 -> new ItemStack(ModItems.GREEN_ARROW_LEGGINGS.get());
                case 3 -> new ItemStack(ModItems.GREEN_ARROW_BOOTS.get());
                default -> ItemStack.EMPTY;
            };
        };
    }

    public static int getMaterialValue(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        return ItemValueRegistry.getValueWithCount(stack.getItem(), stack.getCount());
    }
}