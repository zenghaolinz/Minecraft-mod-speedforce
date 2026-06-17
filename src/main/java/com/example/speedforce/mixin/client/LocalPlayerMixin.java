package com.example.speedforce.mixin.client;

import com.example.speedforce.util.PhasingStateAccess;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents the client-only "push out of blocks" logic from fighting phasing.
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Inject(method = "moveTowardsClosestSpace(DD)V", at = @At("HEAD"), cancellable = true)
    private void speedforce$skipPushOut(double x, double z, CallbackInfo ci) {
        if ((Object) this instanceof PhasingStateAccess access && access.speedforce$isPhasingActive()) {
            ci.cancel();
        }
    }
}
