package com.example.speedforce.network;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.client.ClientSpeedData;
import com.example.speedforce.item.FlashSuitArmorItem;
import com.example.speedforce.item.SuitType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetworking {

    @SubscribeEvent
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(SpeedForceMod.MOD_ID);

        registrar.playToServer(TogglePowerPayload.TYPE, TogglePowerPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (!data.hasPower) return;
                    if (data.speedLevel > 0) {
                        data.speedLevel = 0;
                        data.isBulletTimeActive = false;
                        data.isPhasing = false;
                    } else {
                        data.speedLevel = 1;
                    }
                    player.setData(ModAttachments.SPEED_PLAYER, data);
                    syncToClient(player);
                }
            });
        });

        registrar.playToServer(SpeedLevelPayload.TYPE, SpeedLevelPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower) {
                        SuitType suitType = getWornSuitType(player);
                        int maxLevel = suitType != null ? 10 + suitType.getSpeedBonus() : 10;
                        if (payload.increase()) {
                            data.speedLevel = Math.min(maxLevel, data.speedLevel + 1);
                        } else {
                            data.speedLevel = Math.max(1, data.speedLevel - 1);
                        }
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToServer(BulletTimePayload.TYPE, BulletTimePayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower && data.speedLevel > 0) {
                        data.isBulletTimeActive = !data.isBulletTimeActive;
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToServer(PhasingPayload.TYPE, PhasingPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower) {
                        data.isPhasing = !data.isPhasing;
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToClient(SyncSpeedDataPayload.TYPE, SyncSpeedDataPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                ClientSpeedData.hasPower = payload.hasPower();
                ClientSpeedData.speedLevel = payload.speedLevel();
                ClientSpeedData.isBulletTimeActive = payload.isBulletTimeActive();
                ClientSpeedData.isPhasing = payload.isPhasing();
                ClientSpeedData.trailColorR = payload.trailColorR();
                ClientSpeedData.trailColorG = payload.trailColorG();
                ClientSpeedData.trailColorB = payload.trailColorB();
                ClientSpeedData.customTrailColorR = payload.customTrailColorR();
                ClientSpeedData.customTrailColorG = payload.customTrailColorG();
                ClientSpeedData.customTrailColorB = payload.customTrailColorB();

                if (context.player() != null) {
                    context.player().setData(ModAttachments.SPEED_PLAYER,
                        new SpeedPlayerData(payload.hasPower(), payload.speedLevel(), payload.isBulletTimeActive(), payload.isPhasing(), 
                                            payload.trailColorR(), payload.trailColorG(), payload.trailColorB(),
                                            payload.customTrailColorR(), payload.customTrailColorG(), payload.customTrailColorB())
                    );
                }
            });
        });

        registrar.playToServer(TrailColorPayload.TYPE, TrailColorPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower) {
                        SuitType suitType = getWornSuitType(player);
                        data.customTrailColorR = payload.r();
                        data.customTrailColorG = payload.g();
                        data.customTrailColorB = payload.b();
                        if (suitType == null) {
                            data.trailColorR = payload.r();
                            data.trailColorG = payload.g();
                            data.trailColorB = payload.b();
                        }
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });
    }

    public static void syncToClient(ServerPlayer player) {
        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
        PacketDistributor.sendToPlayer(player, new SyncSpeedDataPayload(data));
    }

    private static SuitType getWornSuitType(ServerPlayer player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        if (helmet.getItem() instanceof FlashSuitArmorItem helmetItem &&
            chestplate.getItem() instanceof FlashSuitArmorItem chestplateItem &&
            leggings.getItem() instanceof FlashSuitArmorItem leggingsItem &&
            boots.getItem() instanceof FlashSuitArmorItem bootsItem) {
            
            SuitType type = helmetItem.getSuitType();
            if (chestplateItem.getSuitType() == type &&
                leggingsItem.getSuitType() == type &&
                bootsItem.getSuitType() == type) {
                return type;
            }
        }
        return null;
    }
}