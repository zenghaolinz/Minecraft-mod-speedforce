package com.example.speedforce.item;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpeedForceMod.MOD_ID);

    public static final Supplier<CreativeModeTab> SPEEDFORCE_TAB = CREATIVE_MODE_TABS.register("speedforce_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.speedforce_tab"))
            .icon(() -> new ItemStack(ModItems.FLASH_HELMET.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.FLASH_HELMET.get());
                output.accept(ModItems.FLASH_CHESTPLATE.get());
                output.accept(ModItems.FLASH_LEGGINGS.get());
                output.accept(ModItems.FLASH_BOOTS.get());
                
                output.accept(ModItems.REVERSE_FLASH_HELMET.get());
                output.accept(ModItems.REVERSE_FLASH_CHESTPLATE.get());
                output.accept(ModItems.REVERSE_FLASH_LEGGINGS.get());
                output.accept(ModItems.REVERSE_FLASH_BOOTS.get());
                
                output.accept(ModItems.ZOOM_HELMET.get());
                output.accept(ModItems.ZOOM_CHESTPLATE.get());
                output.accept(ModItems.ZOOM_LEGGINGS.get());
                output.accept(ModItems.ZOOM_BOOTS.get());
                
                output.accept(ModItems.JAY_HELMET.get());
                output.accept(ModItems.JAY_CHESTPLATE.get());
                output.accept(ModItems.JAY_LEGGINGS.get());
                output.accept(ModItems.JAY_BOOTS.get());
                
                output.accept(ModItems.EARTHX_HELMET.get());
                output.accept(ModItems.EARTHX_CHESTPLATE.get());
                output.accept(ModItems.EARTHX_LEGGINGS.get());
                output.accept(ModItems.EARTHX_BOOTS.get());
                
                output.accept(ModItems.JAY_EARTH90_HELMET.get());
                output.accept(ModItems.JAY_EARTH90_CHESTPLATE.get());
                output.accept(ModItems.JAY_EARTH90_LEGGINGS.get());
                output.accept(ModItems.JAY_EARTH90_BOOTS.get());
                
                output.accept(ModItems.FLASH_S1_HELMET.get());
                output.accept(ModItems.FLASH_S1_CHESTPLATE.get());
                output.accept(ModItems.FLASH_S1_LEGGINGS.get());
                output.accept(ModItems.FLASH_S1_BOOTS.get());
                
                output.accept(ModItems.PARTICLE_ACCELERATOR.get());
            }).build());
}