package com.example.speedforce.mixin.client;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents the client-only "push out of blocks" logic from fighting phasing.
 *
 * Even if collision is bypassed, LocalPlayer.moveTowardsClosestSpace() can still
 * apply a horizontal push when the client believes the player is inside a block,
 * causing resistance/jitter near walls. While phasing, this correction is wrong:
 * being inside blocks is intentional.
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Inject(method = "moveTowardsClosestSpace(DD)V", at = @At("HEAD"), cancellable = true)
    private void speedforce$disablePushOutWhilePhasing(double x, double z, CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);

        if (data.hasPower && data.speedLevel > 0 && data.isPhasing) {
            ci.cancel();
        }
    }
}
