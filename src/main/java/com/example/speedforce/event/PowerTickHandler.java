package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.item.FlashSuitArmorItem;
import com.example.speedforce.network.ModNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = "speedforce")
public class PowerTickHandler {

    private static final ResourceLocation SPEED_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("speedforce", "speed_boost");
    private static final ResourceLocation ATTACK_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("speedforce", "attack_boost");
    private static final ResourceLocation ATTACK_SPEED_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("speedforce", "attack_speed_boost");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower) {
            removeSpeedModifier(player);
            removeAttackModifier(player);
            removeAttackSpeedModifier(player);
            if (!player.isSpectator()) {
                player.noPhysics = false;
                player.setNoGravity(false);
            }
            if (data.isBulletTimeActive || data.isPhasing) {
                data.isBulletTimeActive = false;
                data.isPhasing = false;
                player.setData(ModAttachments.SPEED_PLAYER, data);
                if (player instanceof ServerPlayer serverPlayer) {
                    ModNetworking.syncToClient(serverPlayer);
                }
            }
            return;
        }

        applySpeedModifier(player, data);
        applyAttackModifier(player, data);
        applyAttackSpeedModifier(player, data);
        handleRegeneration(player, data);
        handleWaterWalk(player, data);
        handleWallRun(player, data);
        handleSpeedFire(player, data);
        handlePhasing(player, data);

        if (player.tickCount % 20 == 0 && player instanceof ServerPlayer serverPlayer) {
            ModNetworking.syncToClient(serverPlayer);
        }
    }

    private static float lastTickRate = 20.0F;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        int maxBulletTimeLevel = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
            if (data.hasPower && data.speedLevel > 0 && data.isBulletTimeActive) {
                maxBulletTimeLevel = Math.max(maxBulletTimeLevel, data.speedLevel);
            }
        }

        float targetTickRate;
        if (maxBulletTimeLevel > 0) {
            targetTickRate = 20.0F / (3.0F + maxBulletTimeLevel);
            targetTickRate = Math.max(0.5F, targetTickRate);
        } else {
            targetTickRate = 20.0F;
        }

        if (lastTickRate != targetTickRate) {
            server.tickRateManager().setTickRate(targetTickRate);
            lastTickRate = targetTickRate;
        }
    }

    private static void applySpeedModifier(Player player, SpeedPlayerData data) {
        var instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null) {
            double amount = data.speedLevel * 0.3;
            if (data.isBulletTimeActive && data.speedLevel > 0) {
                amount *= (3.0 + data.speedLevel);
            }
            
            AttributeModifier modifier = new AttributeModifier(
                SPEED_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE
            );
            if (!instance.hasModifier(SPEED_MODIFIER_ID)) {
                instance.addTransientModifier(modifier);
            } else if (instance.getModifier(SPEED_MODIFIER_ID).amount() != amount) {
                instance.removeModifier(SPEED_MODIFIER_ID);
                instance.addTransientModifier(modifier);
            }
        }
    }

    private static void removeSpeedModifier(Player player) {
        var instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null && instance.hasModifier(SPEED_MODIFIER_ID)) {
            instance.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    private static void applyAttackModifier(Player player, SpeedPlayerData data) {
        if (data.speedLevel <= 0) {
            removeAttackModifier(player);
            return;
        }
        var instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null) {
            double amount = data.speedLevel * 0.5;
            AttributeModifier modifier = new AttributeModifier(
                ATTACK_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE
            );
            if (!instance.hasModifier(ATTACK_MODIFIER_ID)) {
                instance.addTransientModifier(modifier);
            } else if (instance.getModifier(ATTACK_MODIFIER_ID).amount() != amount) {
                instance.removeModifier(ATTACK_MODIFIER_ID);
                instance.addTransientModifier(modifier);
            }
        }
    }

    private static void removeAttackModifier(Player player) {
        var instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null && instance.hasModifier(ATTACK_MODIFIER_ID)) {
            instance.removeModifier(ATTACK_MODIFIER_ID);
        }
    }

    private static void applyAttackSpeedModifier(Player player, SpeedPlayerData data) {
        var instance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null) {
            double amount = 0;
            if (data.speedLevel > 0 && data.isBulletTimeActive) {
                amount = 4.0 * (2.0 + data.speedLevel);
            }
            
            if (amount > 0) {
                AttributeModifier modifier = new AttributeModifier(
                    ATTACK_SPEED_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE
                );
                if (!instance.hasModifier(ATTACK_SPEED_MODIFIER_ID)) {
                    instance.addTransientModifier(modifier);
                } else if (instance.getModifier(ATTACK_SPEED_MODIFIER_ID).amount() != amount) {
                    instance.removeModifier(ATTACK_SPEED_MODIFIER_ID);
                    instance.addTransientModifier(modifier);
                }
            } else {
                removeAttackSpeedModifier(player);
            }
        }
    }

    private static void removeAttackSpeedModifier(Player player) {
        var instance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null && instance.hasModifier(ATTACK_SPEED_MODIFIER_ID)) {
            instance.removeModifier(ATTACK_SPEED_MODIFIER_ID);
        }
    }

    private static void handleRegeneration(Player player, SpeedPlayerData data) {
        if (data.speedLevel > 0 && player.tickCount % 40 == 0 && player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0F + data.speedLevel * 0.2F);
        }
    }

    private static void handleWaterWalk(Player player, SpeedPlayerData data) {
        if (data.speedLevel >= 3 && player.isSprinting()) {
            BlockPos posBelow = player.blockPosition().below();
            if (player.level().getFluidState(posBelow).isEmpty() == false) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                player.fallDistance = 0.0F;
                player.setOnGround(true);
            }
        }
    }

    private static void handleWallRun(Player player, SpeedPlayerData data) {
        if (data.speedLevel >= 4 && player.isSprinting() && player.horizontalCollision) {
            player.setDeltaMovement(player.getDeltaMovement().x, 0.4, player.getDeltaMovement().z);
            player.fallDistance = 0.0F;
        }
    }

    private static void handleSpeedFire(Player player, SpeedPlayerData data) {
        if (data.speedLevel >= 5 && player.isSprinting()) {
            if (!hasFullFlashSuit(player)) {
                player.setRemainingFireTicks(60);
            }
        }
    }

    private static void handlePhasing(Player player, SpeedPlayerData data) {
        if (data.isPhasing) {
            player.noPhysics = true;
            player.setNoGravity(true);
            player.fallDistance = 0.0F;
        } else if (!player.isSpectator()) {
            player.noPhysics = false;
            player.setNoGravity(false);
        }
    }

    private static boolean hasFullFlashSuit(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof FlashSuitArmorItem;
    }
}