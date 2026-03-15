package com.example.speedforce.block;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpeedForceMod.MOD_ID);

    public static final DeferredBlock<ParticleAcceleratorBlock> PARTICLE_ACCELERATOR = 
        BLOCKS.register("particle_accelerator", 
            () -> new ParticleAcceleratorBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));
}