package com.example.speedforce.util;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.world.entity.player.Player;

/**
 * Central helper for keeping persisted phasing data and runtime phasing cache
 * in sync.
 */
public final class PhasingStateManager {
    private PhasingStateManager() {}

    public static void setPhasing(Player player, SpeedPlayerData data, boolean active) {
        data.isPhasing = active;
        player.setData(ModAttachments.SPEED_PLAYER, data);
        updateCache(player, active);
    }

    public static void updateCache(Player player, boolean active) {
        if (player instanceof PhasingStateAccess access) {
            access.speedforce$setPhasingActive(active);
        }
    }

    public static void syncCacheFromData(Player player) {
        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
        boolean active = data.hasPower && data.speedLevel > 0 && data.isPhasing && !player.isSpectator();
        updateCache(player, active);
    }

    public static void clearLegacyPhysicsFlags(Player player) {
        if (player.isSpectator()) return;
        player.noPhysics = false;
        if (player.isNoGravity()) {
            player.setNoGravity(false);
        }
    }
}
