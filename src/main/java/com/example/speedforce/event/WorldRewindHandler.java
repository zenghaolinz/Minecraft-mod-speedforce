package com.example.speedforce.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.lang.reflect.Field;
import java.util.*;

@EventBusSubscriber(modid = "speedforce")
public class WorldRewindHandler {
    
    private static final Field ARROW_IN_GROUND_FIELD;
    static {
        try {
            ARROW_IN_GROUND_FIELD = AbstractArrow.class.getDeclaredField("inGround");
            ARROW_IN_GROUND_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access AbstractArrow.inGround field", e);
        }
    }
    
    public record DeadEntitySnapshot(EntityType<?> type, CompoundTag nbt, Vec3 pos, UUID uuid) {}
    public record EntitySnapshot(UUID uuid, Vec3 pos, float yRot, float xRot, Vec3 delta, int fuse, float health) {}
    
    public record TickSnapshot(List<DeadEntitySnapshot> deadEntities, List<EntitySnapshot> livingEntities) {}

    private static final Map<ResourceKey<Level>, Deque<TickSnapshot>> HISTORY = new HashMap<>();
    private static final Map<ResourceKey<Level>, List<DeadEntitySnapshot>> PENDING_DEAD_ENTITIES = new HashMap<>();

    private static boolean isLevelRewinding(ServerLevel level) {
        for (Player p : level.players()) {
            if (RewindHandler.IS_REWINDING.getOrDefault(p.getUUID(), false)) {
                return true;
            }
        }
        return false;
    }

    public static int getHistorySize(ServerLevel level) {
        Deque<TickSnapshot> history = HISTORY.get(level.dimension());
        return history != null ? history.size() : 0;
    }

    public static void truncateHistory(ServerLevel level, int targetSize) {
        Deque<TickSnapshot> history = HISTORY.get(level.dimension());
        if (history != null && history.size() > targetSize) {
            while (history.size() > targetSize) {
                history.pollFirst();
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level && !(entity instanceof Player)) {
            if (isLevelRewinding(level)) return;
            
            if (entity instanceof LivingEntity || entity instanceof PrimedTnt || entity instanceof ItemEntity || entity instanceof Projectile) {
                ResourceKey<Level> dim = level.dimension();
                List<DeadEntitySnapshot> deadEntities = PENDING_DEAD_ENTITIES.computeIfAbsent(dim, k -> new ArrayList<>());
                CompoundTag tag = new CompoundTag();
                entity.saveWithoutId(tag);
                deadEntities.add(new DeadEntitySnapshot(entity.getType(), tag, entity.position(), entity.getUUID()));
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ResourceKey<Level> dim = level.dimension();
            boolean rewinding = isLevelRewinding(level);
            Deque<TickSnapshot> history = HISTORY.computeIfAbsent(dim, k -> new ArrayDeque<>());
            
            if (rewinding) {
                if (!history.isEmpty()) {
                    TickSnapshot snapshot = history.pollFirst();
                    
                    for (DeadEntitySnapshot des : snapshot.deadEntities()) {
                        if (level.getEntity(des.uuid()) == null) { 
                            Entity entity = des.type().create(level);
                            if (entity != null) {
                                entity.load(des.nbt());
                                entity.setUUID(des.uuid());
                                if (entity instanceof PrimedTnt tnt && tnt.getFuse() <= 0) {
                                    tnt.setFuse(1);
                                }
                                level.addFreshEntity(entity);
                            }
                        }
                    }
                    
                    Set<UUID> validUUIDs = new HashSet<>();
                    for (DeadEntitySnapshot des : snapshot.deadEntities()) {
                        validUUIDs.add(des.uuid());
                    }
                    
                    for (EntitySnapshot es : snapshot.livingEntities()) {
                        validUUIDs.add(es.uuid());
                        Entity entity = level.getEntity(es.uuid());
                        if (entity != null) {
                            entity.teleportTo(es.pos().x, es.pos().y, es.pos().z);
                            entity.setYRot(es.yRot());
                            entity.setXRot(es.xRot());
                            entity.setDeltaMovement(es.delta());
                            entity.fallDistance = 0;
                            
                            if (entity instanceof PrimedTnt tnt) {
                                tnt.setFuse(Math.max(1, es.fuse()));
                            } else if (entity instanceof LivingEntity le) {
                                le.setHealth(es.health());
                            } else if (entity instanceof AbstractArrow arrow) {
                                try {
                                    ARROW_IN_GROUND_FIELD.setBoolean(arrow, false);
                                } catch (IllegalAccessException ignored) {}
                            }
                        }
                    }
                    
                    for (Entity entity : level.getAllEntities()) {
                        if (entity != null && !(entity instanceof Player)) {
                            if (entity instanceof LivingEntity || entity instanceof PrimedTnt || entity instanceof ItemEntity || entity instanceof Projectile) {
                                if (!validUUIDs.contains(entity.getUUID())) {
                                    entity.discard();
                                }
                            }
                        }
                    }
                }
                PENDING_DEAD_ENTITIES.remove(dim);
            } else {
                List<DeadEntitySnapshot> currentDead = new ArrayList<>(PENDING_DEAD_ENTITIES.getOrDefault(dim, Collections.emptyList()));
                List<EntitySnapshot> currentLiving = new ArrayList<>();
                
                for (Entity entity : level.getAllEntities()) {
                    if (entity != null && !(entity instanceof Player)) {
                        if (entity instanceof LivingEntity || entity instanceof PrimedTnt || entity instanceof ItemEntity || entity instanceof Projectile) {
                            int fuse = entity instanceof PrimedTnt tnt ? tnt.getFuse() : 0;
                            float health = entity instanceof LivingEntity le ? le.getHealth() : 0;
                            currentLiving.add(new EntitySnapshot(entity.getUUID(), entity.position(), entity.getYRot(), entity.getXRot(), entity.getDeltaMovement(), fuse, health));
                        }
                    }
                }
                
                history.addFirst(new TickSnapshot(currentDead, currentLiving));
                if (history.size() > 200) {
                    history.removeLast();
                }
                
                PENDING_DEAD_ENTITIES.remove(dim);
            }
        }
    }
}