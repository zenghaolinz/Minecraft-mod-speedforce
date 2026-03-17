package com.example.speedforce.entity;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(Registries.ENTITY_TYPE, SpeedForceMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<NormalArrowEntity>> NORMAL_ARROW =
        ENTITY_TYPES.register("normal_arrow", () ->
            EntityType.Builder.<NormalArrowEntity>of(NormalArrowEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(4)
                .updateInterval(20)
                .build(SpeedForceMod.MOD_ID + ":normal_arrow")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<TimeRemnantEntity>> TIME_REMNANT =
        ENTITY_TYPES.register("time_remnant", () ->
            EntityType.Builder.<TimeRemnantEntity>of(TimeRemnantEntity::new, MobCategory.CREATURE)
                .sized(0.6F, 1.8F)
                .clientTrackingRange(10)
                .updateInterval(1)
                .build(SpeedForceMod.MOD_ID + ":time_remnant")
        );
}