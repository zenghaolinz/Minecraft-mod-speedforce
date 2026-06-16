package com.example.speedforce.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Unified rewind session — provides a single time cursor shared by
 * RewindHandler, BlockRewindManager and WorldRewindHandler.
 *
 * Only one session per dimension is allowed at a time.
 */
public final class RewindSession {

    private final UUID ownerUuid;
    private final ResourceKey<Level> dimension;
    private final long startGameTime;
    private long cursorGameTime;

    public RewindSession(UUID ownerUuid, ResourceKey<Level> dimension, long startGameTime) {
        this.ownerUuid = ownerUuid;
        this.dimension = dimension;
        this.startGameTime = startGameTime;
        this.cursorGameTime = startGameTime;
    }

    public UUID getOwnerUuid() { return ownerUuid; }
    public ResourceKey<Level> getDimension() { return dimension; }
    public long getStartGameTime() { return startGameTime; }
    public long getCursorGameTime() { return cursorGameTime; }

    /** Advance the cursor backwards by the given speed (in ticks). */
    public void advanceCursor(int speed) {
        cursorGameTime -= speed;
    }

    // ====== Static session registry ======

    private static final Map<ResourceKey<Level>, RewindSession> ACTIVE_SESSIONS = new HashMap<>();

    public static void start(RewindSession session) {
        ACTIVE_SESSIONS.put(session.getDimension(), session);
    }

    public static void stop(ResourceKey<Level> dimension) {
        ACTIVE_SESSIONS.remove(dimension);
    }

    public static Optional<RewindSession> get(ResourceKey<Level> dimension) {
        return Optional.ofNullable(ACTIVE_SESSIONS.get(dimension));
    }

    /** Whether a rewind session is active in the given dimension. */
    public static boolean isRestoring(ResourceKey<Level> dimension) {
        return ACTIVE_SESSIONS.containsKey(dimension);
    }

    /** Get the cursor game time for the dimension, or Long.MAX_VALUE if no session. */
    public static long getCursorGameTime(ResourceKey<Level> dimension) {
        RewindSession session = ACTIVE_SESSIONS.get(dimension);
        return session != null ? session.cursorGameTime : Long.MAX_VALUE;
    }

    /** Remove all sessions (e.g. on server stop). */
    public static void clearAll() {
        ACTIVE_SESSIONS.clear();
    }
}
