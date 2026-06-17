package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.network.ModNetworking;
import com.example.speedforce.util.PhasingStateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "speedforce")
public class ModEvents {

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.level().isClientSide && player.hasEffect(MobEffects.POISON)) {
                if (player.getRandom().nextFloat() < 0.3f) {
                    player.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(true, 1));
                    player.removeEffect(MobEffects.POISON);
                    if (player instanceof ServerPlayer serverPlayer) {
                        ModNetworking.syncToClient(serverPlayer);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PhasingStateManager.clearLegacyPhysicsFlags(serverPlayer);
            PhasingStateManager.syncCacheFromData(serverPlayer);
            ModNetworking.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer newPlayer) {
            SpeedPlayerData data = newPlayer.getData(ModAttachments.SPEED_PLAYER);
            // Instant abilities should not persist through death/clone.
            data.isPhasing = false;
            data.isBulletTimeActive = false;
            newPlayer.setData(ModAttachments.SPEED_PLAYER, data);
            PhasingStateManager.clearLegacyPhysicsFlags(newPlayer);
            PhasingStateManager.syncCacheFromData(newPlayer);
            ModNetworking.syncToClient(newPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SpeedPlayerData data = serverPlayer.getData(ModAttachments.SPEED_PLAYER);
            data.isPhasing = false;
            data.isBulletTimeActive = false;
            serverPlayer.setData(ModAttachments.SPEED_PLAYER, data);
            PhasingStateManager.clearLegacyPhysicsFlags(serverPlayer);
            PhasingStateManager.syncCacheFromData(serverPlayer);
            ModNetworking.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PhasingStateManager.clearLegacyPhysicsFlags(serverPlayer);
            PhasingStateManager.syncCacheFromData(serverPlayer);
            ModNetworking.syncToClient(serverPlayer);
        }
    }
}
