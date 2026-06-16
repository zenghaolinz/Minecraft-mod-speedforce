package com.example.speedforce.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class TimeAnchor {
    public final Vec3 playerPos;
    public final float yRot;
    public final float xRot;
    public final float health;
    public final float fallDistance;
    public final long gameTime;
    public final int blockHistorySize;
    public final int entityHistorySize;

    public TimeAnchor(ServerPlayer player) {
        // Copy position to ensure immutability (Vec3 from position() may be mutable in some MC versions)
        Vec3 pos = player.position();
        this.playerPos = new Vec3(pos.x, pos.y, pos.z);
        this.yRot = player.getYRot();
        this.xRot = player.getXRot();
        this.health = player.getHealth();
        this.fallDistance = player.fallDistance;
        this.gameTime = player.level().getGameTime();

        ServerLevel level = player.serverLevel();
        this.blockHistorySize = BlockRewindManager.getHistorySize(level);
        this.entityHistorySize = WorldRewindHandler.getHistorySize(level);
    }

    public void restorePlayer(ServerPlayer player) {
        player.teleportTo(playerPos.x, playerPos.y, playerPos.z);
        player.setYRot(yRot);
        player.setXRot(xRot);
        player.setHealth(health);
        player.fallDistance = fallDistance;
    }

    public int getBlockHistorySize() {
        return blockHistorySize;
    }

    public int getEntityHistorySize() {
        return entityHistorySize;
    }
}
