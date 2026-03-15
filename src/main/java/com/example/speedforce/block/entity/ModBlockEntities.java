package com.example.speedforce.block.entity;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpeedForceMod.MOD_ID);

    public static final Supplier<BlockEntityType<ParticleAcceleratorBlockEntity>> PARTICLE_ACCELERATOR = 
        BLOCK_ENTITIES.register("particle_accelerator", 
            () -> BlockEntityType.Builder.of(ParticleAcceleratorBlockEntity::new, 
                ModBlocks.PARTICLE_ACCELERATOR.get()).build(null));
}