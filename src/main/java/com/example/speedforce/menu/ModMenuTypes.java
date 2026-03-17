package com.example.speedforce.menu;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.inventory.QuiverMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = 
        DeferredRegister.create(Registries.MENU, SpeedForceMod.MOD_ID);

    public static final Supplier<MenuType<SpeedForceWorkbenchMenu>> SPEED_FORCE_WORKBENCH = 
        MENUS.register("speed_force_workbench", () -> SpeedForceWorkbenchMenu.TYPE);

    public static final Supplier<MenuType<QuiverMenu>> QUIVER = 
        MENUS.register("quiver", () -> QuiverMenu.TYPE);
}