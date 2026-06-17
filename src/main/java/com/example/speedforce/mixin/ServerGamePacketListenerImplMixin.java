package com.example.speedforce.mixin;

import com.example.speedforce.util.PhasingStateAccess;
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
    private void speedforce$allowPhasingMovement(
        LevelReader level,
        AABB previousBoundingBox,
        double targetX,
        double targetY,
        double targetZ,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (this.player instanceof PhasingStateAccess access && access.speedforce$isPhasingActive()) {
            cir.setReturnValue(false);
        }
    }
}
