package com.example.speedforce.network;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.client.ClientSpeedData;
import com.example.speedforce.item.FlashSuitArmorItem;
import com.example.speedforce.item.SuitType;
import com.example.speedforce.util.PhasingStateManager;
import net.minecraft.network.chat.Component;
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
                        PhasingStateManager.setPhasing(player, data, false);
                        player.connection.resetPosition();
                    } else {
                        data.speedLevel = 1;
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                    }
                    syncToClient(player);
                }
            });
        });

        registrar.playToServer(SpeedLevelPayload.TYPE, SpeedLevelPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower && data.speedLevel > 0) {
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
                    togglePhasing(player);
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
                    PhasingStateManager.updateCache(
                        context.player(),
                        payload.hasPower() && payload.speedLevel() > 0 && payload.isPhasing() && !context.player().isSpectator()
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

        registrar.playToServer(WorkbenchPurchasePayload.TYPE, WorkbenchPurchasePayload.STREAM_CODEC, 
            WorkbenchPurchasePayload::handle);

        registrar.playToServer(CycleQuiverPayload.TYPE, CycleQuiverPayload.STREAM_CODEC,
            CycleQuiverPayload::handle);

        registrar.playToServer(RewindPayload.TYPE, RewindPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    if (payload.startRewind()) {
                        com.example.speedforce.event.RewindHandler.startRewind(player);
                    } else {
                        com.example.speedforce.event.RewindHandler.stopRewind(player);
                    }
                }
            });
        });

        registrar.playToClient(RewindStatePayload.TYPE, RewindStatePayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                com.example.speedforce.client.ClientRewindData.phase = payload.phase();
                com.example.speedforce.client.ClientRewindData.framesRewound = payload.framesRewound();
                com.example.speedforce.client.ClientRewindData.rewindSpeed = payload.rewindSpeed();
                com.example.speedforce.client.ClientRewindData.totalHistorySize = payload.totalHistorySize();
            });
        });

        registrar.playToServer(TimeRemnantPayload.TYPE, TimeRemnantPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    if (payload.summon()) {
                        com.example.speedforce.event.TimeRemnantHandler.summonRemnant(player);
                    } else {
                        com.example.speedforce.event.TimeRemnantHandler.dismissRemnant(player);
                    }
                }
            });
        });

        registrar.playToClient(RemnantStatePayload.TYPE, RemnantStatePayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                com.example.speedforce.client.ClientRemnantData.setRemnantActive(payload.hasRemnant(), payload.remainingSeconds());
            });
        });
    }

    private static void togglePhasing(ServerPlayer player) {
        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower || data.speedLevel <= 0) {
            forceDisablePhasing(player, data);
            return;
        }

        if (com.example.speedforce.event.RewindHandler.isPlayerRewinding(player.getUUID())) {
            return;
        }

        boolean newState;
        if (!data.isPhasing) {
            newState = true;
        } else {
            // Before disabling, make sure the player is not inside solid blocks.
            // Otherwise vanilla collision would immediately suffocate/push them.
            boolean canExit = player.level().noCollision(player, player.getBoundingBox());
            if (!canExit) {
                player.displayClientMessage(
                    Component.literal("§c请离开方块内部后再关闭穿墙"),
                    true
                );
                return;
            }
            newState = false;
        }

        PhasingStateManager.setPhasing(player, data, newState);
        // Reset server movement baseline to avoid the first phasing tick being
        // corrected against old lastGood/firstGood coordinates.
        player.connection.resetPosition();
        syncToClient(player);
    }

    private static void forceDisablePhasing(ServerPlayer player, SpeedPlayerData data) {
        if (data.isPhasing) {
            PhasingStateManager.setPhasing(player, data, false);
            player.connection.resetPosition();
            syncToClient(player);
        }
        PhasingStateManager.clearLegacyPhysicsFlags(player);
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