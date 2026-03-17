package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.network.RewindStatePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "speedforce")
public class RewindHandler {

    public enum RewindPhase {
        IDLE,
        REWINDING,
        CONFIRMING,
        CANCELLING
    }

    public static class RewindState {
        public RewindPhase phase = RewindPhase.IDLE;
        public int rewindSpeed = 1;
        public int framesRewound = 0;
        public long confirmWindowStart = 0;
    }

    public static final Map<UUID, Boolean> IS_REWINDING = new HashMap<>();
    public static final Map<UUID, Double[]> REWIND_START_POS = new HashMap<>();
    public static final Map<UUID, Integer> REWIND_HISTORY_SIZE = new HashMap<>();
    
    private static final Map<UUID, RewindState> REWIND_STATES = new HashMap<>();
    private static final Map<UUID, TimeAnchor> TIME_ANCHORS = new HashMap<>();
    private static final int CANCEL_WINDOW_TICKS = 60;

    public static RewindState getState(UUID uuid) {
        return REWIND_STATES.computeIfAbsent(uuid, k -> new RewindState());
    }

    public static boolean isPlayerRewinding(UUID uuid) {
        RewindState state = REWIND_STATES.get(uuid);
        return state != null && state.phase == RewindPhase.REWINDING;
    }

    public static boolean isInConfirmWindow(UUID uuid) {
        RewindState state = REWIND_STATES.get(uuid);
        return state != null && state.phase == RewindPhase.CONFIRMING;
    }

    public static int getFramesRewound(UUID uuid) {
        RewindState state = REWIND_STATES.get(uuid);
        return state != null ? state.framesRewound : 0;
    }

    public static void startRewind(ServerPlayer player) {
        UUID uuid = player.getUUID();
        var data = player.getData(ModAttachments.SPEED_PLAYER);
        
        if (!data.hasPower || data.speedLevel <= 0) return;

        TimeAnchor anchor = new TimeAnchor(player);
        TIME_ANCHORS.put(uuid, anchor);

        RewindState state = getState(uuid);
        state.phase = RewindPhase.REWINDING;
        state.framesRewound = 0;
        state.rewindSpeed = 1;

        IS_REWINDING.put(uuid, true);
        REWIND_HISTORY_SIZE.put(uuid, anchor.getBlockHistorySize());
    }

    public static void stopRewind(ServerPlayer player) {
        UUID uuid = player.getUUID();
        RewindState state = REWIND_STATES.get(uuid);
        
        if (state == null || state.phase != RewindPhase.REWINDING) return;

        state.phase = RewindPhase.CONFIRMING;
        state.confirmWindowStart = player.level().getGameTime();
        
        IS_REWINDING.put(uuid, false);
    }

    public static void confirmRewind(ServerPlayer player) {
        UUID uuid = player.getUUID();
        
        TIME_ANCHORS.remove(uuid);
        REWIND_STATES.remove(uuid);
        REWIND_HISTORY_SIZE.remove(uuid);
        
        IS_REWINDING.put(uuid, false);
        
        PacketDistributor.sendToPlayer(player, new RewindStatePayload(0, 0, 1, 0, 0));
    }

    public static void cancelRewind(ServerPlayer player) {
        UUID uuid = player.getUUID();
        TimeAnchor anchor = TIME_ANCHORS.get(uuid);
        
        if (anchor != null) {
            ServerLevel level = player.serverLevel();
            
            BlockRewindManager.truncateHistory(level, anchor.getBlockHistorySize());
            WorldRewindHandler.truncateHistory(level, anchor.getEntityHistorySize());
            
            anchor.restorePlayer(player);
        }
        
        TIME_ANCHORS.remove(uuid);
        REWIND_STATES.remove(uuid);
        REWIND_HISTORY_SIZE.remove(uuid);
        
        IS_REWINDING.put(uuid, false);
        
        PacketDistributor.sendToPlayer(player, new RewindStatePayload(0, 0, 1, 0, 0));
    }

    public static void setRewindSpeed(UUID uuid, int speed) {
        RewindState state = REWIND_STATES.get(uuid);
        if (state != null && state.phase == RewindPhase.REWINDING) {
            state.rewindSpeed = Math.max(1, Math.min(10, speed));
        }
    }

    public static int getRewindSpeed(UUID uuid) {
        RewindState state = REWIND_STATES.get(uuid);
        return state != null ? state.rewindSpeed : 1;
    }

    public static int getRemainingConfirmTime(UUID uuid, long currentGameTime) {
        RewindState state = REWIND_STATES.get(uuid);
        if (state == null || state.phase != RewindPhase.CONFIRMING) return 0;
        
        long elapsed = currentGameTime - state.confirmWindowStart;
        return Math.max(0, CANCEL_WINDOW_TICKS - (int) elapsed);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        UUID uuid = player.getUUID();
        var data = player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower || data.speedLevel <= 0) {
            IS_REWINDING.put(uuid, false);
            REWIND_START_POS.remove(uuid);
            REWIND_HISTORY_SIZE.remove(uuid);
            REWIND_STATES.remove(uuid);
            TIME_ANCHORS.remove(uuid);
            return;
        }

        RewindState state = REWIND_STATES.get(uuid);
        if (state == null) return;

        switch (state.phase) {
            case REWINDING -> handleRewinding((ServerPlayer) player, state);
            case CONFIRMING -> handleConfirming((ServerPlayer) player, state);
            default -> {}
        }
    }

    private static void handleRewinding(ServerPlayer player, RewindState state) {
        player.fallDistance = 0;
        player.setSprinting(true);
        
        TimeAnchor anchor = TIME_ANCHORS.get(player.getUUID());
        if (anchor != null) {
            player.teleportTo(anchor.playerPos.x, anchor.playerPos.y, anchor.playerPos.z);
        }
        
        state.framesRewound += state.rewindSpeed;
        syncRewindStateToClient(player);
    }

    private static void handleConfirming(ServerPlayer player, RewindState state) {
        long currentTime = player.level().getGameTime();
        long elapsed = currentTime - state.confirmWindowStart;
        
        syncRewindStateToClient(player);
        
        if (elapsed >= CANCEL_WINDOW_TICKS) {
            confirmRewind(player);
        }
    }

    private static void syncRewindStateToClient(ServerPlayer player) {
        UUID uuid = player.getUUID();
        RewindState state = REWIND_STATES.get(uuid);
        if (state == null) return;

        int phaseOrdinal = state.phase.ordinal();
        int confirmTime = getRemainingConfirmTime(uuid, player.level().getGameTime());
        int totalHistory = REWIND_HISTORY_SIZE.getOrDefault(uuid, 0);

        PacketDistributor.sendToPlayer(player, new RewindStatePayload(
            phaseOrdinal,
            state.framesRewound,
            state.rewindSpeed,
            confirmTime,
            totalHistory
        ));
    }

    public static TimeAnchor getTimeAnchor(UUID uuid) {
        return TIME_ANCHORS.get(uuid);
    }
}