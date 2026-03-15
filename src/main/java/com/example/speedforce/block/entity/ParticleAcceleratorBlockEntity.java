package com.example.speedforce.block.entity;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.network.ModNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ParticleAcceleratorBlockEntity extends BlockEntity {

    private Player activatingPlayer = null;
    private int activationTicks = 0;
    private static final int LIGHTNING_DELAY = 40;

    public ParticleAcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PARTICLE_ACCELERATOR.get(), pos, state);
    }

    public boolean canActivate(Player player) {
        var data = player.getData(ModAttachments.SPEED_PLAYER);
        return !data.hasPower && activatingPlayer == null;
    }

    public void startActivation(Player player) {
        this.activatingPlayer = player;
        this.activationTicks = LIGHTNING_DELAY;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ParticleAcceleratorBlockEntity blockEntity) {
        if (blockEntity.activatingPlayer == null || blockEntity.activationTicks <= 0) return;

        blockEntity.activationTicks--;

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                5, 0.3, 0.5, 0.3, 0.1);
        }

        if (blockEntity.activationTicks <= 0) {
            blockEntity.summonLightningAndGrantPower(level, pos);
            blockEntity.activatingPlayer = null;
            blockEntity.setChanged();
        }
    }

    private void summonLightningAndGrantPower(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel && activatingPlayer != null) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                lightning.setVisualOnly(true);
                serverLevel.addFreshEntity(lightning);
            }

            activatingPlayer.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(true, 1));
            
            if (activatingPlayer instanceof ServerPlayer serverPlayer) {
                ModNetworking.syncToClient(serverPlayer);
            }

            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                1, 0, 0, 0, 0);
        }
    }
}