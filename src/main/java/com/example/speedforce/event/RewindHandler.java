package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.network.RewindStatePayload;
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
        REWINDING
    }

    public static class RewindState {
        public RewindPhase phase = RewindPhase.IDLE;
        public int rewindSpeed = 1;
        public int framesRewound = 0;
    }

    public static final Map<UUID, Boolean> IS_REWINDING = new HashMap<>();
    public static final Map<UUID, Double[]> REWIND_START_POS = new HashMap<>();
    public static final Map<UUID, Integer> REWIND_HISTORY_SIZE = new HashMap<>();
    
    private static final Map<UUID, RewindState> REWIND_STATES = new HashMap<>();
    private static final Map<UUID, TimeAnchor> TIME_ANCHORS = new HashMap<>();

    public static RewindState getState(UUID uuid) {
        return REWIND_STATES.computeIfAbsent(uuid, k -> new RewindState());
    }

    public static boolean isPlayerRewinding(UUID uuid) {
        RewindState state = REWIND_STATES.get(uuid);
        return state != null && state.phase == RewindPhase.REWINDING;
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
        
        syncRewindStateToClient(player);
    }

    public static void stopRewind(ServerPlayer player) {
        confirmRewind(player);
    }

    public static void confirmRewind(ServerPlayer player) {
        UUID uuid = player.getUUID();
        
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

        if (state.phase == RewindPhase.REWINDING) {
            handleRewinding((ServerPlayer) player, state);
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

    private static void syncRewindStateToClient(ServerPlayer player) {
        UUID uuid = player.getUUID();
        RewindState state = REWIND_STATES.get(uuid);
        if (state == null) return;

        int phaseOrdinal = state.phase.ordinal();
        int totalHistory = REWIND_HISTORY_SIZE.getOrDefault(uuid, 0);

        PacketDistributor.sendToPlayer(player, new RewindStatePayload(
            phaseOrdinal,
            state.framesRewound,
            state.rewindSpeed,
            0,
            totalHistory
        ));
    }

    public static TimeAnchor getTimeAnchor(UUID uuid) {
        return TIME_ANCHORS.get(uuid);
    }
}