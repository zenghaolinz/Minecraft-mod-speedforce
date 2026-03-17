package com.example.speedforce.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class TimeAnchor {
    public final Vec3 playerPos;
    public final float yRot;
    public final float xRot;
    public final float health;
    public final long gameTime;
    public final int blockHistorySize;
    public final int entityHistorySize;

    public TimeAnchor(ServerPlayer player) {
        this.playerPos = player.position();
        this.yRot = player.getYRot();
        this.xRot = player.getXRot();
        this.health = player.getHealth();
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
    }

    public int getBlockHistorySize() {
        return blockHistorySize;
    }

    public int getEntityHistorySize() {
        return entityHistorySize;
    }
}