package com.example.speedforce.particle;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
        DeferredRegister.create(Registries.PARTICLE_TYPE, SpeedForceMod.MOD_ID);

    public static final Supplier<SimpleParticleType> YELLOW_FLASH =
        PARTICLE_TYPES.register("yellow_flash", () -> new SimpleParticleType(true));
}