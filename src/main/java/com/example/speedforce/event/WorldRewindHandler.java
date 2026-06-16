package com.example.speedforce.event;

import com.example.speedforce.mixin.LivingEntityAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.*;
import java.util.logging.Logger;

/**
 * Handles entity rewind for the world.
 *
 * Design: each snapshot records the COMPLETE set of tracked entities in the area.
 * On rewind, we reconcile by UUID:
 *   - current has, target doesn't → remove (drop later items, etc.)
 *   - target has, current doesn't → recreate from NBT (revive dead mobs, etc.)
 *   - both have → update position/state
 *
 * For revivals, we use the LAST_ALIVE snapshot (not the post-death corpse NBT)
 * to ensure resurrection is from a healthy state, and we additionally clean
 * runtime death/hurt animation fields via LivingEntityAccessor.
 */
@EventBusSubscriber(modid = "speedforce")
public class WorldRewindHandler {

    private static final Logger LOGGER = Logger.getLogger("SpeedForce/WorldRewind");

    /**
     * Full entity snapshot with complete NBT data.
     */
    public record EntitySnapshot(
        UUID uuid,
        ResourceLocation typeId,
        CompoundTag nbt,
        boolean isLivingEntity,
        float health
    ) {}

    /**
     * A complete world state at a single sample point.
     */
    public record WorldSnapshot(
        long gameTime,
        Map<UUID, EntitySnapshot> entities
    ) {}

    private static final Map<ResourceKey<Level>, Deque<WorldSnapshot>> HISTORY = new HashMap<>();

    /**
     * Last-alive snapshot per entity UUID, per dimension.
     * Used as the source for resurrection — we must NOT use the post-death
     * corpse NBT (Health=0, DeathTime>0) when reviving.
     */
    private static final Map<ResourceKey<Level>, Map<UUID, EntitySnapshot>> LAST_ALIVE_SNAPSHOTS = new HashMap<>();

    private static final int SNAPSHOT_INTERVAL = 5;          // Record every 5 ticks
    private static final int MAX_HISTORY_SNAPSHOTS = 40;     // 40 * 5 = 200 ticks = 10s coverage
    private static final double ENTITY_TRACK_RANGE = 64.0;   // Only track entities within 64 blocks

    public static int getHistorySize(ServerLevel level) {
        Deque<WorldSnapshot> history = HISTORY.get(level.dimension());
        return history != null ? history.size() : 0;
    }

    public static void truncateHistory(ServerLevel level, int targetSize) {
        Deque<WorldSnapshot> history = HISTORY.get(level.dimension());
        if (history != null) {
            while (history.size() > targetSize) {
                history.pollFirst();
            }
        }
    }

    // ====== Entity tracking filter ======

    /**
     * Whether an entity should be tracked in world snapshots.
     * Tracks all non-player entities except purely visual ones.
     * Excludes dead/dying living entities so we don't capture corpse states.
     */
    private static boolean shouldCapture(Entity entity) {
        if (entity instanceof Player) return false;

        if (entity instanceof LivingEntity living) {
            // Skip corpses — only capture living entities while they are alive.
            // The LAST_ALIVE_SNAPSHOTS map will be used for resurrection.
            if (!living.isAlive() || living.isDeadOrDying() || living.getHealth() <= 0.0F) {
                return false;
            }
            return true;
        }

        // Non-living tracked types
        return entity instanceof ItemEntity
            || entity instanceof ExperienceOrb
            || entity instanceof Projectile
            || entity instanceof PrimedTnt
            || entity instanceof FallingBlockEntity
            || entity instanceof Boat
            || entity instanceof Minecart
            || entity instanceof ArmorStand
            || entity instanceof ItemFrame
            || entity instanceof Painting;
    }

    /** Whether an entity matches our tracked types (regardless of alive status). */
    private static boolean isTrackedType(Entity entity) {
        if (entity instanceof Player) return false;
        return entity instanceof LivingEntity
            || entity instanceof ItemEntity
            || entity instanceof ExperienceOrb
            || entity instanceof Projectile
            || entity instanceof PrimedTnt
            || entity instanceof FallingBlockEntity
            || entity instanceof Boat
            || entity instanceof Minecart
            || entity instanceof ArmorStand
            || entity instanceof ItemFrame
            || entity instanceof Painting;
    }

    /** Check if entity is within range of any player with rewind capability. */
    private static boolean isInRangeOfRewindPlayer(Entity entity, ServerLevel level) {
        for (ServerPlayer p : level.players()) {
            var data = p.getData(com.example.speedforce.capability.ModAttachments.SPEED_PLAYER);
            if (data.hasPower && data.speedLevel > 0) {
                if (entity.distanceToSqr(p) <= ENTITY_TRACK_RANGE * ENTITY_TRACK_RANGE) {
                    return true;
                }
            }
        }
        return false;
    }

    // ====== Snapshot recording ======

    private static EntitySnapshot captureFullSnapshot(Entity entity) {
        CompoundTag tag = new CompoundTag();
        entity.save(tag);
        boolean isLiving = entity instanceof LivingEntity;
        float health = isLiving ? ((LivingEntity) entity).getHealth() : 0.0F;
        return new EntitySnapshot(
            entity.getUUID(),
            BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()),
            tag,
            isLiving,
            health
        );
    }

    private static WorldSnapshot captureSnapshot(ServerLevel level) {
        Map<UUID, EntitySnapshot> entities = new HashMap<>();
        Map<UUID, EntitySnapshot> lastAliveForDim = LAST_ALIVE_SNAPSHOTS
            .computeIfAbsent(level.dimension(), k -> new HashMap<>());

        for (Entity entity : level.getAllEntities()) {
            if (entity == null || !isTrackedType(entity)) continue;
            if (!isInRangeOfRewindPlayer(entity, level)) continue;

            try {
                if (shouldCapture(entity)) {
                    EntitySnapshot snap = captureFullSnapshot(entity);
                    entities.put(entity.getUUID(), snap);

                    // For living entities, also remember this as the last alive state
                    if (entity instanceof LivingEntity) {
                        lastAliveForDim.put(entity.getUUID(), snap);
                    }
                }
                // else: corpse / dying — do not record this state
            } catch (Exception e) {
                LOGGER.warning("Failed to snapshot entity: uuid=" + entity.getUUID()
                    + " type=" + entity.getType() + " - " + e.getMessage());
            }
        }

        // Garbage-collect LAST_ALIVE_SNAPSHOTS entries for entities that haven't
        // been seen for a while (entity removed from tracking range, etc.)
        // Only keep entries whose UUID we still see in the current scan.
        // To avoid expensive scans, we keep entries until the dimension reset.
        return new WorldSnapshot(level.getGameTime(), entities);
    }

    // ====== Death-state sanitization ======

    /**
     * Clean death/hurt state from NBT before loading. Used only on resurrection.
     */
    private static CompoundTag sanitizeRevivalNbt(CompoundTag original, float targetHealth) {
        CompoundTag tag = original.copy();
        tag.putShort("DeathTime", (short) 0);
        tag.putShort("HurtTime", (short) 0);
        tag.putFloat("Health", Math.max(1.0F, targetHealth));
        return tag;
    }

    /**
     * Clean runtime death/hurt state on a freshly loaded LivingEntity.
     * Required because the NBT load only restores some fields; the rest
     * (the `dead` flag, hurtDuration) remain at their default-loaded values
     * but to be safe we clear them all.
     */
    private static void resetRevivedLivingEntity(LivingEntity living, float targetHealth) {
        LivingEntityAccessor accessor = (LivingEntityAccessor) living;
        accessor.speedforce$setDead(false);
        accessor.speedforce$setDeathTime(0);
        accessor.speedforce$setHurtTime(0);
        accessor.speedforce$setHurtDuration(0);

        float health = Math.max(1.0F, targetHealth);
        health = Math.min(health, living.getMaxHealth());
        living.setHealth(health);

        living.fallDistance = 0;
    }

    // ====== Entity recreation ======

    /**
     * Recreate an entity from its snapshot.
     * @param resurrection true if this is a "revival" of a previously-dead entity —
     *                     in that case we sanitize death/hurt state.
     */
    private static Entity recreateEntity(ServerLevel level, EntitySnapshot snapshot, boolean resurrection) {
        CompoundTag tag = snapshot.nbt().copy();

        if (resurrection) {
            tag = sanitizeRevivalNbt(tag, snapshot.health());
        }

        // Remove UUID from tag so we can set it after creation without conflict
        tag.remove("UUID");

        Entity restored = EntityType.loadEntityRecursive(
            tag,
            level,
            java.util.function.Function.identity()
        );

        if (restored == null) {
            LOGGER.warning("Failed to load rewind entity: uuid=" + snapshot.uuid()
                + " type=" + snapshot.typeId());
            return null;
        }

        restored.setUUID(snapshot.uuid());

        // Reset runtime death/hurt fields BEFORE adding to world,
        // so clients never see a partially-dead state.
        if (restored instanceof LivingEntity living && resurrection) {
            resetRevivedLivingEntity(living, snapshot.health());
        }

        if (!level.tryAddFreshEntityWithPassengers(restored)) {
            LOGGER.warning("Failed to add rewind entity: uuid=" + snapshot.uuid()
                + " type=" + snapshot.typeId());
            return null;
        }

        return restored;
    }

    // ====== Per-tick logic ======

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        ResourceKey<Level> dim = level.dimension();
        Deque<WorldSnapshot> history = HISTORY.computeIfAbsent(dim, k -> new ArrayDeque<>());

        Optional<RewindSession> sessionOpt = RewindSession.get(dim);

        if (sessionOpt.isPresent()) {
            // ====== REWIND: UUID reconciliation ======
            if (!history.isEmpty()) {
                long cursor = sessionOpt.get().getCursorGameTime();

                // Find the snapshot closest to (but not after) the cursor time
                WorldSnapshot targetFrame = null;
                for (WorldSnapshot snap : history) {
                    if (snap.gameTime() <= cursor) {
                        targetFrame = snap;
                        break; // Newest-first, so first match is closest to cursor
                    }
                }

                if (targetFrame != null) {
                    reconcileEntities(level, targetFrame);
                }
            }
        } else {
            // ====== NOT REWINDING: Record snapshots ======
            if (level.getGameTime() % SNAPSHOT_INTERVAL != 0) {
                return;
            }

            // Only record if a player with rewind capability exists
            boolean hasPlayerNeedingHistory = false;
            for (ServerPlayer p : level.players()) {
                var data = p.getData(com.example.speedforce.capability.ModAttachments.SPEED_PLAYER);
                if (data.hasPower && data.speedLevel > 0) {
                    hasPlayerNeedingHistory = true;
                    break;
                }
            }

            if (hasPlayerNeedingHistory) {
                WorldSnapshot snapshot = captureSnapshot(level);
                history.addFirst(snapshot);
                if (history.size() > MAX_HISTORY_SNAPSHOTS) {
                    history.removeLast();
                }
            }
        }
    }

    /**
     * Core UUID reconciliation algorithm:
     *   current - target → remove (entities created after target time)
     *   target - current → create (entities that existed at target but are now gone)
     *   current ∩ target → update position/state from target NBT
     */
    private static void reconcileEntities(ServerLevel level, WorldSnapshot targetFrame) {
        Map<UUID, EntitySnapshot> targetEntities = targetFrame.entities();
        Map<UUID, EntitySnapshot> lastAliveForDim = LAST_ALIVE_SNAPSHOTS
            .getOrDefault(level.dimension(), Collections.emptyMap());

        // Collect current entities in range (exclude players)
        Map<UUID, Entity> currentEntities = new HashMap<>();
        for (Entity entity : level.getAllEntities()) {
            if (entity != null && isTrackedType(entity) && !(entity instanceof Player)) {
                currentEntities.put(entity.getUUID(), entity);
            }
        }

        // === Remove: current has, target doesn't ===
        for (Map.Entry<UUID, Entity> entry : currentEntities.entrySet()) {
            if (!targetEntities.containsKey(entry.getKey())) {
                entry.getValue().discard();
            }
        }

        // === Create: target has, current doesn't (RESURRECTION PATH) ===
        for (Map.Entry<UUID, EntitySnapshot> entry : targetEntities.entrySet()) {
            if (!currentEntities.containsKey(entry.getKey())) {
                EntitySnapshot snap = entry.getValue();

                // For living entities, prefer the last-alive snapshot if available
                // (the one in the frame should already be alive, but as a safety net)
                EntitySnapshot revivalSource = snap;
                if (snap.isLivingEntity()) {
                    EntitySnapshot lastAlive = lastAliveForDim.get(snap.uuid());
                    if (lastAlive != null && lastAlive.health() > 0.0F) {
                        revivalSource = lastAlive;
                    }
                }

                // Mark as resurrection only if it's a living entity that needs revival
                boolean resurrection = revivalSource.isLivingEntity()
                    && revivalSource.health() > 0.0F;

                recreateEntity(level, revivalSource, resurrection);
            }
        }

        // === Update: both have ===
        for (Map.Entry<UUID, EntitySnapshot> entry : targetEntities.entrySet()) {
            Entity current = currentEntities.get(entry.getKey());
            if (current == null) continue;

            EntitySnapshot snap = entry.getValue();

            // For short-lived entities (items, projectiles, TNT, falling blocks),
            // recreate from full NBT is more reliable than field-by-field patching.
            if (current instanceof ItemEntity || current instanceof Projectile
                || current instanceof PrimedTnt || current instanceof FallingBlockEntity
                || current instanceof ExperienceOrb) {
                current.discard();
                // Not resurrection — these aren't living entities
                recreateEntity(level, snap, false);
                continue;
            }

            // For persistent entities (mobs, armor stands, vehicles, etc.),
            // update position and health. Full NBT restore is too disruptive.
            CompoundTag tag = snap.nbt();
            if (tag.contains("Pos")) {
                net.minecraft.nbt.ListTag posList = tag.getList("Pos", 6);
                current.teleportTo(posList.getDouble(0), posList.getDouble(1), posList.getDouble(2));
            }
            if (tag.contains("Rotation")) {
                net.minecraft.nbt.ListTag rotList = tag.getList("Rotation", 5);
                current.setYRot(rotList.getFloat(0));
                current.setXRot(rotList.getFloat(1));
            }
            if (tag.contains("Motion")) {
                net.minecraft.nbt.ListTag motionList = tag.getList("Motion", 6);
                current.setDeltaMovement(
                    motionList.getDouble(0),
                    motionList.getDouble(1),
                    motionList.getDouble(2)
                );
            }
            current.fallDistance = 0;

            if (current instanceof LivingEntity le) {
                if (tag.contains("Health")) {
                    le.setHealth(tag.getFloat("Health"));
                }
                // NOTE: do NOT clear hurtTime/deathTime here — this is normal
                // position rewind, not a resurrection. The original red-flash
                // animation should be preserved during rewind playback.
            }
        }
    }
}
