package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.entity.ModEntityTypes;
import com.example.speedforce.entity.TimeRemnantEntity;
import com.example.speedforce.network.RemnantStatePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "speedforce")
public class TimeRemnantHandler {

    private static final Map<UUID, Integer> PLAYER_REMNANT_IDS = new HashMap<>();

    public static void summonRemnant(ServerPlayer player) {
        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
        
        if (!data.hasPower || data.speedLevel < 4) {
            return;
        }
        
        if (PLAYER_REMNANT_IDS.containsKey(player.getUUID())) {
            dismissRemnant(player);
            return;
        }
        
        ServerLevel level = player.serverLevel();
        
        TimeRemnantEntity remnant = new TimeRemnantEntity(ModEntityTypes.TIME_REMNANT.get(), level);
        remnant.setPos(player.getX(), player.getY(), player.getZ());
        remnant.setOwner(player);
        
        level.addFreshEntity(remnant);
        
        PLAYER_REMNANT_IDS.put(player.getUUID(), remnant.getId());
        
        syncRemnantState(player);
    }

    public static void dismissRemnant(ServerPlayer player) {
        Integer entityId = PLAYER_REMNANT_IDS.remove(player.getUUID());
        if (entityId != null) {
            Entity entity = player.level().getEntity(entityId);
            if (entity instanceof TimeRemnantEntity remnant && remnant.isAlive()) {
                remnant.discard();
            }
        }
        
        syncRemnantState(player);
    }

    public static boolean hasRemnant(UUID playerUUID) {
        return PLAYER_REMNANT_IDS.containsKey(playerUUID);
    }

    public static TimeRemnantEntity getRemnant(ServerPlayer player) {
        Integer entityId = PLAYER_REMNANT_IDS.get(player.getUUID());
        if (entityId == null) return null;
        Entity entity = player.level().getEntity(entityId);
        if (entity instanceof TimeRemnantEntity remnant && remnant.isAlive()) {
            return remnant;
        }
        return null;
    }

    public static int getRemainingSeconds(ServerPlayer player) {
        TimeRemnantEntity remnant = getRemnant(player);
        return remnant != null ? remnant.getRemainingSeconds() : 0;
    }

    private static void syncRemnantState(ServerPlayer player) {
        boolean hasRemnant = PLAYER_REMNANT_IDS.containsKey(player.getUUID());
        int remainingSeconds = hasRemnant ? getRemainingSeconds(player) : 0;
        PacketDistributor.sendToPlayer(player, new RemnantStatePayload(hasRemnant, remainingSeconds));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        net.minecraft.server.MinecraftServer server = event.getServer();
        java.util.List<ServerPlayer> needsSync = new java.util.ArrayList<>();

        PLAYER_REMNANT_IDS.entrySet().removeIf(entry -> {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) return true;
            
            Entity entity = player.level().getEntity(entry.getValue());
            if (entity == null || !entity.isAlive()) {
                needsSync.add(player);
                return true;
            } else if (player.tickCount % 20 == 0) {
                needsSync.add(player);
            }
            return false;
        });

        for (ServerPlayer player : needsSync) {
            syncRemnantState(player);
        }
    }

    public static void cleanup() {
        PLAYER_REMNANT_IDS.clear();
    }
}