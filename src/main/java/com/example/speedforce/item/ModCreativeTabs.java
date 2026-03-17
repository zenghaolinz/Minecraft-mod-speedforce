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
                
                output.accept(ModItems.FLASH_S4_HELMET.get());
                output.accept(ModItems.FLASH_S4_CHESTPLATE.get());
                output.accept(ModItems.FLASH_S4_LEGGINGS.get());
                output.accept(ModItems.FLASH_S4_BOOTS.get());
                
                output.accept(ModItems.FLASH_S5_HELMET.get());
                output.accept(ModItems.FLASH_S5_CHESTPLATE.get());
                output.accept(ModItems.FLASH_S5_LEGGINGS.get());
                output.accept(ModItems.FLASH_S5_BOOTS.get());
                
                output.accept(ModItems.KID_FLASH_HELMET.get());
                output.accept(ModItems.KID_FLASH_CHESTPLATE.get());
                output.accept(ModItems.KID_FLASH_LEGGINGS.get());
                output.accept(ModItems.KID_FLASH_BOOTS.get());
                
                output.accept(ModItems.GREEN_ARROW_HELMET.get());
                output.accept(ModItems.GREEN_ARROW_CHESTPLATE.get());
                output.accept(ModItems.GREEN_ARROW_LEGGINGS.get());
                output.accept(ModItems.GREEN_ARROW_BOOTS.get());
                output.accept(ModItems.GREEN_ARROW_BOW.get());
                output.accept(ModItems.NORMAL_ARROW.get());
                output.accept(ModItems.QUIVER.get());
                
                output.accept(ModItems.PARTICLE_ACCELERATOR.get());
                output.accept(ModItems.SPEED_FORCE_WORKBENCH.get());
            }).build());
}