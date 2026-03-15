package com.example.speedforce.item;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpeedForceMod.MOD_ID);

    public static final DeferredItem<BlockItem> PARTICLE_ACCELERATOR = ITEMS.register("particle_accelerator",
        () -> new BlockItem(ModBlocks.PARTICLE_ACCELERATOR.get(), new net.minecraft.world.item.Item.Properties()));

    private static Holder<ArmorMaterial> createArmorMaterial(String name) {
        return Holder.direct(new ArmorMaterial(
            new EnumMap<>(ArmorItem.Type.class) {{
                put(ArmorItem.Type.HELMET, 2);
                put(ArmorItem.Type.CHESTPLATE, 5);
                put(ArmorItem.Type.LEGGINGS, 6);
                put(ArmorItem.Type.BOOTS, 2);
            }},
            12,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.of(net.minecraft.world.item.Items.LEATHER),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SpeedForceMod.MOD_ID, name))),
            0.0F,
            0.0F
        ));
    }

    public static final Holder<ArmorMaterial> FLASH_MATERIAL = createArmorMaterial("flash");
    public static final Holder<ArmorMaterial> REVERSE_FLASH_MATERIAL = createArmorMaterial("reverse_flash");
    public static final Holder<ArmorMaterial> ZOOM_MATERIAL = createArmorMaterial("zoom");
    public static final Holder<ArmorMaterial> JAY_MATERIAL = createArmorMaterial("jay");
    public static final Holder<ArmorMaterial> EARTHX_MATERIAL = createArmorMaterial("earthx");
    public static final Holder<ArmorMaterial> JAY_EARTH90_MATERIAL = createArmorMaterial("jay_earth90");
    public static final Holder<ArmorMaterial> FLASH_S1_MATERIAL = createArmorMaterial("flash_s1");

    public static final DeferredItem<FlashSuitArmorItem> FLASH_HELMET = ITEMS.register("flash_helmet",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_CHESTPLATE = ITEMS.register("flash_chestplate",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_LEGGINGS = ITEMS.register("flash_leggings",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_BOOTS = ITEMS.register("flash_boots",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_HELMET = ITEMS.register("reverse_flash_helmet",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_CHESTPLATE = ITEMS.register("reverse_flash_chestplate",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_LEGGINGS = ITEMS.register("reverse_flash_leggings",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_BOOTS = ITEMS.register("reverse_flash_boots",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_HELMET = ITEMS.register("zoom_helmet",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_CHESTPLATE = ITEMS.register("zoom_chestplate",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_LEGGINGS = ITEMS.register("zoom_leggings",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_BOOTS = ITEMS.register("zoom_boots",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> JAY_HELMET = ITEMS.register("jay_helmet",
        () -> new FlashSuitArmorItem(JAY_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.JAY));

    public static final DeferredItem<FlashSuitArmorItem> JAY_CHESTPLATE = ITEMS.register("jay_chestplate",
        () -> new FlashSuitArmorItem(JAY_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.JAY));

    public static final DeferredItem<FlashSuitArmorItem> JAY_LEGGINGS = ITEMS.register("jay_leggings",
        () -> new FlashSuitArmorItem(JAY_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.JAY));

    public static final DeferredItem<FlashSuitArmorItem> JAY_BOOTS = ITEMS.register("jay_boots",
        () -> new FlashSuitArmorItem(JAY_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.JAY));

    public static final DeferredItem<FlashSuitArmorItem> EARTHX_HELMET = ITEMS.register("earthx_helmet",
        () -> new FlashSuitArmorItem(EARTHX_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.EARTHX));

    public static final DeferredItem<FlashSuitArmorItem> EARTHX_CHESTPLATE = ITEMS.register("earthx_chestplate",
        () -> new FlashSuitArmorItem(EARTHX_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.EARTHX));

    public static final DeferredItem<FlashSuitArmorItem> EARTHX_LEGGINGS = ITEMS.register("earthx_leggings",
        () -> new FlashSuitArmorItem(EARTHX_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.EARTHX));

    public static final DeferredItem<FlashSuitArmorItem> EARTHX_BOOTS = ITEMS.register("earthx_boots",
        () -> new FlashSuitArmorItem(EARTHX_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.EARTHX));

    public static final DeferredItem<FlashSuitArmorItem> JAY_EARTH90_HELMET = ITEMS.register("jay_earth90_helmet",
        () -> new FlashSuitArmorItem(JAY_EARTH90_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.JAY_EARTH90));

    public static final DeferredItem<FlashSuitArmorItem> JAY_EARTH90_CHESTPLATE = ITEMS.register("jay_earth90_chestplate",
        () -> new FlashSuitArmorItem(JAY_EARTH90_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.JAY_EARTH90));

    public static final DeferredItem<FlashSuitArmorItem> JAY_EARTH90_LEGGINGS = ITEMS.register("jay_earth90_leggings",
        () -> new FlashSuitArmorItem(JAY_EARTH90_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.JAY_EARTH90));

    public static final DeferredItem<FlashSuitArmorItem> JAY_EARTH90_BOOTS = ITEMS.register("jay_earth90_boots",
        () -> new FlashSuitArmorItem(JAY_EARTH90_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.JAY_EARTH90));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S1_HELMET = ITEMS.register("flash_s1_helmet",
        () -> new FlashSuitArmorItem(FLASH_S1_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S1));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S1_CHESTPLATE = ITEMS.register("flash_s1_chestplate",
        () -> new FlashSuitArmorItem(FLASH_S1_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S1));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S1_LEGGINGS = ITEMS.register("flash_s1_leggings",
        () -> new FlashSuitArmorItem(FLASH_S1_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S1));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S1_BOOTS = ITEMS.register("flash_s1_boots",
        () -> new FlashSuitArmorItem(FLASH_S1_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S1));
}