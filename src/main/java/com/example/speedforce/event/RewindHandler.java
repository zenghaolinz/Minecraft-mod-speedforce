package com.example.speedforce.event;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.network.RewindStatePayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID)
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

    /**
     * Full player snapshot recorded every tick.
     * Includes inventory, experience, and food to prevent item duplication on rewind.
     */
    public record PlayerSnapshot(
        long gameTime,
        Vec3 pos,
        Vec3 deltaMovement,
        float yRot,
        float xRot,
        float health,
        float fallDistance,
        ListTag inventory,
        int selectedSlot,
        int experienceLevel,
        int totalExperience,
        float experienceProgress,
        int foodLevel,
        float saturation
    ) {}

    // ====== Per-player position history ======
    private static final Map<UUID, Deque<PlayerSnapshot>> POSITION_HISTORY = new HashMap<>();
    private static final int MAX_POSITION_HISTORY = 200; // 10 seconds at 20 tps

    // ====== Per-player rewind state ======
    private static final Map<UUID, RewindState> REWIND_STATES = new HashMap<>();
    private static final Map<UUID, TimeAnchor> TIME_ANCHORS = new HashMap<>();

    // ====== Public API ======

    public static RewindState getState(UUID uuid) {
        return REWIND_STATES.computeIfAbsent(uuid, k -> new RewindState());
    }

    public static boolean isPlayerRewinding(UUID uuid) {
        return RewindSession.isRestoring(getDimensionForPlayer(uuid));
    }

    public static int getFramesRewound(UUID uuid) {
        RewindState state = REWIND_STATES.get(uuid);
        return state != null ? state.framesRewound : 0;
    }

    /** Get cursor game time for a dimension. Delegates to RewindSession. */
    public static long getCursorGameTime(ResourceKey<Level> dimension) {
        return RewindSession.getCursorGameTime(dimension);
    }

    // ====== Start / Stop ======

    public static void startRewind(ServerPlayer player) {
        UUID uuid = player.getUUID();
        var data = player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower || data.speedLevel <= 0) return;

        ResourceKey<Level> dimension = player.level().dimension();

        // Only one rewind session per dimension
        if (RewindSession.isRestoring(dimension)) return;

        long currentGameTime = player.level().getGameTime();

        TimeAnchor anchor = new TimeAnchor(player);
        TIME_ANCHORS.put(uuid, anchor);

        RewindState state = getState(uuid);
        state.phase = RewindPhase.REWINDING;
        state.framesRewound = 0;
        state.rewindSpeed = 1;

        // Create unified rewind session
        RewindSession session = new RewindSession(uuid, dimension, currentGameTime);
        RewindSession.start(session);

        syncRewindStateToClient(player);
    }

    public static void stopRewind(ServerPlayer player) {
        confirmRewind(player);
    }

    public static void confirmRewind(ServerPlayer player) {
        UUID uuid = player.getUUID();
        ResourceKey<Level> dimension = player.level().dimension();

        RewindState state = REWIND_STATES.get(uuid);

        // Truncate position history to the rewind point
        if (state != null && state.framesRewound > 0) {
            Deque<PlayerSnapshot> history = POSITION_HISTORY.get(uuid);
            if (history != null) {
                int framesToKeep = Math.max(0, history.size() - state.framesRewound);
                while (history.size() > framesToKeep) {
                    history.pollFirst();
                }
            }
        }

        TIME_ANCHORS.remove(uuid);
        REWIND_STATES.remove(uuid);

        // Stop the unified session
        RewindSession.stop(dimension);

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

    // ====== Per-tick logic ======

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        UUID uuid = player.getUUID();
        var data = player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower || data.speedLevel <= 0) {
            REWIND_STATES.remove(uuid);
            TIME_ANCHORS.remove(uuid);
            // Keep position history for potential future use
            return;
        }

        RewindState state = REWIND_STATES.get(uuid);

        if (state != null && state.phase == RewindPhase.REWINDING) {
            handleRewinding((ServerPlayer) player, state);
        } else {
            // === Record current position to history ===
            recordSnapshot((ServerPlayer) player);
        }
    }

    /** Record a full PlayerSnapshot for the current tick. */
    private static void recordSnapshot(ServerPlayer player) {
        UUID uuid = player.getUUID();
        Deque<PlayerSnapshot> history = POSITION_HISTORY.computeIfAbsent(uuid, k -> new ArrayDeque<>());

        // Save inventory
        ListTag inventoryTag = player.getInventory().save(new ListTag());

        // Save food data
        FoodData foodData = player.getFoodData();

        history.addFirst(new PlayerSnapshot(
            player.level().getGameTime(),
            new Vec3(player.getX(), player.getY(), player.getZ()),
            player.getDeltaMovement(),
            player.getYRot(),
            player.getXRot(),
            player.getHealth(),
            player.fallDistance,
            inventoryTag,
            player.getInventory().selected,
            player.experienceLevel,
            player.totalExperience,
            player.experienceProgress,
            foodData.getFoodLevel(),
            foodData.getSaturationLevel()
        ));

        // Trim old entries
        while (history.size() > MAX_POSITION_HISTORY) {
            history.pollLast();
        }
    }

    /** Restore a player to a specific snapshot. */
    private static void restoreFromSnapshot(ServerPlayer player, PlayerSnapshot snap) {
        // Position and movement
        player.teleportTo(snap.pos().x, snap.pos().y, snap.pos().z);
        player.setYRot(snap.yRot());
        player.setXRot(snap.xRot());
        player.setDeltaMovement(snap.deltaMovement());
        player.fallDistance = snap.fallDistance();

        // Inventory — clear and reload to prevent item duplication
        player.getInventory().clearContent();
        player.getInventory().load(snap.inventory());
        player.getInventory().selected = snap.selectedSlot();

        // Experience
        player.experienceLevel = snap.experienceLevel();
        player.totalExperience = snap.totalExperience();
        player.experienceProgress = snap.experienceProgress();

        // Food
        player.getFoodData().setFoodLevel(snap.foodLevel());
        player.getFoodData().setSaturation(snap.saturation());

        // Sync containers
        player.inventoryMenu.broadcastChanges();
        if (player.containerMenu != player.inventoryMenu) {
            player.containerMenu.broadcastChanges();
        }

        // Health — configurable, default true for first version
        player.setHealth(snap.health());
    }

    private static void handleRewinding(ServerPlayer player, RewindState state) {
        player.fallDistance = 0;
        player.setSprinting(true);

        UUID uuid = player.getUUID();
        Deque<PlayerSnapshot> history = POSITION_HISTORY.get(uuid);

        if (history == null || history.isEmpty()) {
            stopRewind(player);
            return;
        }

        // Calculate target frame index
        int targetIndex = Math.min(state.framesRewound, history.size() - 1);

        // Walk the deque to find the target snapshot
        PlayerSnapshot target = null;
        int i = 0;
        for (PlayerSnapshot snap : history) {
            if (i == targetIndex) {
                target = snap;
                break;
            }
            i++;
        }

        if (target != null) {
            restoreFromSnapshot(player, target);
        }

        // Advance cursor and frame counter
        state.framesRewound += state.rewindSpeed;
        RewindSession.get(player.level().dimension()).ifPresent(s -> s.advanceCursor(state.rewindSpeed));

        // If we've rewound past all history, auto-stop
        if (state.framesRewound >= history.size()) {
            stopRewind(player);
            return;
        }

        syncRewindStateToClient(player);
    }

    private static void syncRewindStateToClient(ServerPlayer player) {
        UUID uuid = player.getUUID();
        RewindState state = REWIND_STATES.get(uuid);
        if (state == null) return;

        int totalHistory = POSITION_HISTORY.getOrDefault(uuid, new ArrayDeque<>()).size();

        PacketDistributor.sendToPlayer(player, new RewindStatePayload(
            state.phase.ordinal(),
            state.framesRewound,
            state.rewindSpeed,
            0,
            totalHistory
        ));
    }

    public static TimeAnchor getTimeAnchor(UUID uuid) {
        return TIME_ANCHORS.get(uuid);
    }

    public static int getPositionHistorySize(UUID uuid) {
        Deque<PlayerSnapshot> history = POSITION_HISTORY.get(uuid);
        return history != null ? history.size() : 0;
    }

    /** Helper to get a player's current dimension from the UUID. */
    private static ResourceKey<Level> getDimensionForPlayer(UUID uuid) {
        // This is used for isPlayerRewinding check; we check the session instead
        return null; // Not used directly — isPlayerRewinding checks via RewindSession
    }

    /** Player logout cleanup — prevent memory leaks. */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        REWIND_STATES.remove(uuid);
        TIME_ANCHORS.remove(uuid);
        POSITION_HISTORY.remove(uuid);

        // Stop any session owned by this player
        ResourceKey<Level> dim = event.getEntity().level().dimension();
        RewindSession.get(dim).ifPresent(session -> {
            if (session.getOwnerUuid().equals(uuid)) {
                RewindSession.stop(dim);
            }
        });
    }
}
