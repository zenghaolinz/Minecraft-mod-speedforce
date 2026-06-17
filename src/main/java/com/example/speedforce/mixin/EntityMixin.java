package com.example.speedforce.mixin;

import com.example.speedforce.util.PhasingStateAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Unique
    private static final List<VoxelShape> SPEEDFORCE$NO_ENTITY_COLLISIONS = List.of();

    @Unique
    private static final double SPEEDFORCE$MOVEMENT_EPSILON = 1.0E-7D;

    @Inject(
        method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void speedforce$phaseHorizontalCollision(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        Entity self = (Entity) (Object) this;

        // Hot path: only PlayerMixin implements PhasingStateAccess.
        // Non-player entities return after a cheap interface check.
        if (!(self instanceof PhasingStateAccess access) || !access.speedforce$isPhasingActive()) {
            return;
        }

        double verticalY = movement.y;

        // No vertical movement: avoid collideBoundingBox() entirely.
        if (Math.abs(verticalY) < SPEEDFORCE$MOVEMENT_EPSILON) {
            cir.setReturnValue(new Vec3(movement.x, 0.0D, movement.z));
            return;
        }

        Vec3 verticalMovement = new Vec3(0.0D, verticalY, 0.0D);
        Vec3 resolvedVertical = Entity.collideBoundingBox(
            self,
            verticalMovement,
            self.getBoundingBox(),
            self.level(),
            SPEEDFORCE$NO_ENTITY_COLLISIONS
        );

        cir.setReturnValue(new Vec3(movement.x, resolvedVertical.y, movement.z));
    }
}
