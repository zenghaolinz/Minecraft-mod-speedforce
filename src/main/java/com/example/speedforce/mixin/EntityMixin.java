package com.example.speedforce.mixin;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(
        method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void speedforce$applyPhasingCollision(Vec3 requestedMovement, CallbackInfoReturnable<Vec3> cir) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof Player player)) return;

        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
        if (!data.isPhasing) return;
        if (!data.hasPower || data.speedLevel <= 0) return;
        if (player.isSpectator()) return;

        /*
         * Phasing only ignores horizontal block collision.
         *
         * X/Z movement is left untouched so players can pass through walls.
         * Y movement is resolved by vanilla collision so floors and ceilings
         * still support/block the player.
         *
         * Do NOT use noPhysics/noGravity: those bypass the whole movement stack
         * and cause sinking, jitter, and broken ground-state prediction.
         */
        Vec3 verticalMovement = new Vec3(0.0D, requestedMovement.y, 0.0D);

        Vec3 resolvedVertical = Entity.collideBoundingBox(
            self,
            verticalMovement,
            self.getBoundingBox(),
            self.level(),
            // Only verify vertical block/world collision. Avoid auxiliary entity
            // collisions here while validating horizontal phasing behavior.
            Collections.emptyList()
        );

        cir.setReturnValue(new Vec3(
            requestedMovement.x,
            resolvedVertical.y,
            requestedMovement.z
        ));
    }
}
