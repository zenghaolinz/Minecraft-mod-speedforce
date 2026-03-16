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
    public static final Holder<ArmorMaterial> FLASH_S4_MATERIAL = createArmorMaterial("flash_s4");
    public static final Holder<ArmorMaterial> FLASH_S5_MATERIAL = createArmorMaterial("flash_s5");
    public static final Holder<ArmorMaterial> KID_FLASH_MATERIAL = createArmorMaterial("kid_flash");
    public static final Holder<ArmorMaterial> GREEN_ARROW_MATERIAL = createArmorMaterial("green_arrow");

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

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_HELMET = ITEMS.register("flash_s4_helmet",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_CHESTPLATE = ITEMS.register("flash_s4_chestplate",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_LEGGINGS = ITEMS.register("flash_s4_leggings",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_BOOTS = ITEMS.register("flash_s4_boots",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_HELMET = ITEMS.register("flash_s5_helmet",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_CHESTPLATE = ITEMS.register("flash_s5_chestplate",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_LEGGINGS = ITEMS.register("flash_s5_leggings",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_BOOTS = ITEMS.register("flash_s5_boots",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_HELMET = ITEMS.register("kid_flash_helmet",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_CHESTPLATE = ITEMS.register("kid_flash_chestplate",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_LEGGINGS = ITEMS.register("kid_flash_leggings",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_BOOTS = ITEMS.register("kid_flash_boots",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_HELMET = ITEMS.register("green_arrow_helmet",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_CHESTPLATE = ITEMS.register("green_arrow_chestplate",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_LEGGINGS = ITEMS.register("green_arrow_leggings",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_BOOTS = ITEMS.register("green_arrow_boots",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<GreenArrowBowItem> GREEN_ARROW_BOW = ITEMS.register("green_arrow_bow",
        () -> new GreenArrowBowItem(new net.minecraft.world.item.Item.Properties().durability(384)));

    public static final DeferredItem<NormalArrowItem> NORMAL_ARROW = ITEMS.register("normal_arrow",
        () -> new NormalArrowItem(new net.minecraft.world.item.Item.Properties()));
}