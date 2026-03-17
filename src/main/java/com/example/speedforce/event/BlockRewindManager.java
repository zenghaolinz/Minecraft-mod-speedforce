package com.example.speedforce.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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

    private static final Map<ResourceKey<Level>, Deque<BlockChange>> HISTORY = new HashMap<>();
    private static final int MAX_HISTORY_TICKS = 200;

    private static boolean isRewinding(ServerLevel level) {
        for (Player p : level.players()) {
            if (RewindHandler.IS_REWINDING.getOrDefault(p.getUUID(), false)) {
                return true;
            }
        }
        return false;
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

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (isRewinding(level)) return;

        long time = level.getGameTime();
        Deque<BlockChange> history = HISTORY.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>());

        for (BlockPos pos : event.getAffectedBlocks()) {
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                BlockEntity be = level.getBlockEntity(pos);
                CompoundTag tag = be != null ? be.saveWithFullMetadata(level.registryAccess()) : null;
                history.addLast(new BlockChange(time, pos.immutable(), state, tag));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof PrimedTnt tnt && event.getLevel() instanceof ServerLevel level) {
            if (isRewinding(level)) return;
            HISTORY.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>())
                   .addLast(new BlockChange(level.getGameTime(), tnt.blockPosition(), Blocks.TNT.defaultBlockState(), null));
        }
    }

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

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (isRewinding(level)) return;

        BlockPos pos = event.getPos();
        BlockState oldState = level.getBlockState(pos);

        HISTORY.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>())
               .addLast(new BlockChange(level.getGameTime(), pos.immutable(), oldState, null));
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        ResourceKey<Level> dim = level.dimension();
        Deque<BlockChange> history = HISTORY.get(dim);

        if (history == null || history.isEmpty()) return;

        if (isRewinding(level)) {
            int stepsPerTick = 40;

            for (int i = 0; i < stepsPerTick; i++) {
                BlockChange change = history.pollLast();
                if (change == null) break;

                level.setBlock(change.pos(), change.oldState(), 3 | 16);

                if (change.blockEntityData() != null) {
                    BlockEntity be = level.getBlockEntity(change.pos());
                    if (be != null) {
                        be.loadWithComponents(change.blockEntityData(), level.registryAccess());
                    }
                }

                level.sendParticles(ParticleTypes.PORTAL,
                    change.pos().getX() + 0.5, change.pos().getY() + 0.5, change.pos().getZ() + 0.5,
                    3, 0.2, 0.2, 0.2, 0.1);
            }
        } else {
            long currentTime = level.getGameTime();
            while (history.peekFirst() != null && (currentTime - history.peekFirst().time() > MAX_HISTORY_TICKS)) {
                history.pollFirst();
            }
        }
    }
}