package com.example.speedforce.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = "speedforce")
public class BlockRewindManager {

    public record BlockChange(long time, BlockPos pos, BlockState oldState, CompoundTag blockEntityData) {}

    /**
     * Pre-explosion snapshot of a block, captured BEFORE the explosion destroys blocks.
     */
    private record PreExplosionBlock(BlockState state, CompoundTag blockEntityData) {}

    private static final Map<ResourceKey<Level>, Deque<BlockChange>> HISTORY = new HashMap<>();
    private static final int MAX_HISTORY_TICKS = 200;

    /**
     * Cache of block states captured before an explosion destroys them.
     * Keyed by dimension, populated in ExplosionEvent.Start, consumed in ExplosionEvent.Detonate.
     */
    private static final Map<ResourceKey<Level>, Map<BlockPos, PreExplosionBlock>> PRE_EXPLOSION_CACHE = new HashMap<>();

    private static boolean isRewinding(ServerLevel level) {
        return RewindSession.isRestoring(level.dimension());
    }

    public static int getHistorySize(ServerLevel level) {
        Deque<BlockChange> history = HISTORY.get(level.dimension());
        return history != null ? history.size() : 0;
    }

    public static void truncateHistory(ServerLevel level, int targetSize) {
        Deque<BlockChange> history = HISTORY.get(level.dimension());
        if (history != null && history.size() > targetSize) {
            Deque<BlockChange> newHistory = new ArrayDeque<>();
            int skipCount = history.size() - targetSize;
            int i = 0;
            for (BlockChange change : history) {
                if (i >= skipCount) {
                    newHistory.addLast(change);
                }
                i++;
            }
            HISTORY.put(level.dimension(), newHistory);
        }
    }

    // ====== Event listeners: record block changes ======

    /**
     * Capture block states BEFORE the explosion destroys them.
     * ExplosionEvent.Start fires before any blocks are affected.
     */
    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (isRewinding(level)) return;

        Explosion explosion = event.getExplosion();
        net.minecraft.world.phys.Vec3 center = explosion.center();
        float radius = explosion.radius();

        Map<BlockPos, PreExplosionBlock> cache = new HashMap<>();
        int r = (int) Math.ceil(radius) + 1;
        BlockPos centerPos = BlockPos.containing(center);

        for (BlockPos pos : BlockPos.betweenClosed(
                centerPos.getX() - r, centerPos.getY() - r, centerPos.getZ() - r,
                centerPos.getX() + r, centerPos.getY() + r, centerPos.getZ() + r)) {
            double dx = pos.getX() + 0.5 - center.x;
            double dy = pos.getY() + 0.5 - center.y;
            double dz = pos.getZ() + 0.5 - center.z;
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq <= (radius + 1) * (radius + 1)) {
                BlockState state = level.getBlockState(pos);
                if (!state.isAir()) {
                    BlockEntity be = level.getBlockEntity(pos);
                    CompoundTag tag = be != null ? be.saveWithFullMetadata(level.registryAccess()) : null;
                    cache.put(pos.immutable(), new PreExplosionBlock(state, tag));
                }
            }
        }

        PRE_EXPLOSION_CACHE.put(level.dimension(), cache);
    }

    /**
     * Record destroyed blocks using the pre-explosion snapshot.
     * At this point blocks have already been set to air, so we must use the cached states.
     */
    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (isRewinding(level)) return;

        Map<BlockPos, PreExplosionBlock> cache = PRE_EXPLOSION_CACHE.remove(level.dimension());

        long time = level.getGameTime();
        Deque<BlockChange> history = HISTORY.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>());

        for (BlockPos pos : event.getAffectedBlocks()) {
            if (cache != null) {
                PreExplosionBlock pre = cache.get(pos);
                if (pre != null && !pre.state.isAir()) {
                    history.addLast(new BlockChange(time, pos.immutable(), pre.state, pre.blockEntityData));
                }
            }
        }
    }

    /**
     * TNT entity spawned: record the TNT block position so rewind restores unlit TNT block.
     */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof PrimedTnt tnt && event.getLevel() instanceof ServerLevel level) {
            if (isRewinding(level)) return;
            HISTORY.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>())
                   .addLast(new BlockChange(level.getGameTime(), tnt.blockPosition(), Blocks.TNT.defaultBlockState(), null));
        }
    }

    /**
     * Block broken: record the pre-break state (event.getState() is correct here).
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (isRewinding(level)) return;

        BlockPos pos = event.getPos();
        BlockEntity be = level.getBlockEntity(pos);
        CompoundTag tag = be != null ? be.saveWithFullMetadata(level.registryAccess()) : null;

        HISTORY.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>())
               .addLast(new BlockChange(level.getGameTime(), pos.immutable(), event.getState(), tag));
    }

    /**
     * Block placed: record the OLD state (before placement) using BlockSnapshot.
     * BlockSnapshot captures the state at the time it was created (before the block change).
     */
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (isRewinding(level)) return;

        BlockSnapshot snapshot = event.getBlockSnapshot();
        BlockState oldState = snapshot.getState();
        CompoundTag oldTag = snapshot.getTag() != null ? snapshot.getTag().copy() : null;

        HISTORY.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>())
               .addLast(new BlockChange(level.getGameTime(), snapshot.getPos().immutable(), oldState, oldTag));
    }

    // ====== Per-tick: cursor-based restoration / expiry cleanup ======

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        ResourceKey<Level> dim = level.dimension();
        Deque<BlockChange> history = HISTORY.get(dim);

        if (history == null || history.isEmpty()) return;

        if (isRewinding(level)) {
            // Cursor-based restoration: undo all changes newer than cursor
            long cursor = RewindSession.getCursorGameTime(dim);
            int restored = 0;
            int maxPerTick = 40;

            while (!history.isEmpty() && restored < maxPerTick) {
                BlockChange change = history.peekLast();
                if (change.time() <= cursor) {
                    break; // This change is at or before the cursor — stop
                }
                history.pollLast(); // Remove from history

                // Restore the old state
                level.setBlock(change.pos(), change.oldState(), 3 | 16);

                if (change.blockEntityData() != null) {
                    BlockEntity be = level.getBlockEntity(change.pos());
                    if (be != null) {
                        be.loadWithComponents(change.blockEntityData().copy(), level.registryAccess());
                        be.setChanged();
                    }
                }

                level.sendParticles(ParticleTypes.PORTAL,
                    change.pos().getX() + 0.5, change.pos().getY() + 0.5, change.pos().getZ() + 0.5,
                    3, 0.2, 0.2, 0.2, 0.1);

                restored++;
            }
        } else {
            // Expire old entries
            long currentTime = level.getGameTime();
            while (history.peekFirst() != null && (currentTime - history.peekFirst().time() > MAX_HISTORY_TICKS)) {
                history.pollFirst();
            }
        }
    }
}
