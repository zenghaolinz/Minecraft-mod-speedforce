package com.example.speedforce.mixin;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Server-side movement packet validation patch for phasing.
 *
 * EntityMixin changes the actual player movement collision, but
 * ServerGamePacketListenerImpl has an additional legality check that compares
 * the player's previous bounding box to the target position and teleports the
 * player back if it detects entering new block collision.
 *
 * When phasing horizontally through blocks, that validation must also allow the
 * move, otherwise the client/server will rubber-band at the wall.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(
        method = "isPlayerCollidingWithAnythingNew(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/phys/AABB;DDD)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void speedforce$allowPhasingIntoBlocks(
        LevelReader level,
        AABB previousBoundingBox,
        double targetX,
        double targetY,
        double targetZ,
        CallbackInfoReturnable<Boolean> cir
    ) {
        SpeedPlayerData data = this.player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower || data.speedLevel <= 0 || !data.isPhasing) return;
        if (this.player.isSpectator()) return;

        // false means "no new collision requiring rollback".
        // Actual Y collision is still handled by EntityMixin's vertical-only
        // collideBoundingBox() calculation.
        cir.setReturnValue(false);
    }
}
