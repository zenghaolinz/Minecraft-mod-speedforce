package com.example.speedforce.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for LivingEntity death-state fields, used by WorldRewindHandler
 * when resurrecting an entity to clean up the runtime fields that are not
 * automatically reset by setHealth().
 *
 * Without this, a resurrected entity would still play the death animation
 * (deathTime > 0) and red hurt flash (hurtTime > 0), visible to clients
 * even when health is restored.
 */
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Accessor("dead")
    void speedforce$setDead(boolean dead);

    @Accessor("deathTime")
    void speedforce$setDeathTime(int deathTime);

    @Accessor("hurtTime")
    void speedforce$setHurtTime(int hurtTime);

    @Accessor("hurtDuration")
    void speedforce$setHurtDuration(int hurtDuration);
}
