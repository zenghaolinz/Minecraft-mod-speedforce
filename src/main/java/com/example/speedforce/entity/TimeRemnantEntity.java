package com.example.speedforce.entity;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.*;

public class TimeRemnantEntity extends PathfinderMob {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> SPEED_LEVEL = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TRAIL_COLOR_R = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TRAIL_COLOR_G = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TRAIL_COLOR_B = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_SPRINTING = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int DEFAULT_DURATION = 2400;
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("speedforce", "remnant_speed");
    private static final ResourceLocation ATTACK_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("speedforce", "remnant_attack");

    public int remainingTicks = DEFAULT_DURATION;
    private LivingEntity ownerLastTarget = null;
    private List<Vec3> trailHistory = new ArrayList<>();
    private static final int MAX_TRAIL_HISTORY = 15;

    public TimeRemnantEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.ATTACK_SPEED, 4.0)
            .add(Attributes.STEP_HEIGHT, 1.5);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(SPEED_LEVEL, 4);
        builder.define(TRAIL_COLOR_R, 255);
        builder.define(TRAIL_COLOR_G, 210);
        builder.define(TRAIL_COLOR_B, 0);
        builder.define(IS_SPRINTING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.2, 10.0F, 2.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    public void setOwner(Player owner) {
        this.entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
        this.setCustomName(owner.getName());
        this.setCustomNameVisible(true);
        
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setItemSlot(slot, owner.getItemBySlot(slot).copy());
            this.setDropChance(slot, 0.0F);
        }
        
        SpeedPlayerData data = owner.getData(ModAttachments.SPEED_PLAYER);
        this.entityData.set(SPEED_LEVEL, data.speedLevel);
        this.entityData.set(TRAIL_COLOR_R, data.trailColorR);
        this.entityData.set(TRAIL_COLOR_G, data.trailColorG);
        this.entityData.set(TRAIL_COLOR_B, data.trailColorB);
        
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(owner.getMaxHealth());
        this.setHealth(owner.getMaxHealth());
        
        applySpeedForceAttributes();
    }

    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public Player getOwner() {
        UUID uuid = getOwnerUUID();
        if (uuid == null) return null;
        return this.level().getPlayerByUUID(uuid);
    }

    public int getSpeedLevel() {
        return this.entityData.get(SPEED_LEVEL);
    }

    public int getTrailColorR() {
        return this.entityData.get(TRAIL_COLOR_R);
    }

    public int getTrailColorG() {
        return this.entityData.get(TRAIL_COLOR_G);
    }

    public int getTrailColorB() {
        return this.entityData.get(TRAIL_COLOR_B);
    }

    private void applySpeedForceAttributes() {
        int speedLevel = getSpeedLevel();
        if (speedLevel <= 0) return;

        AttributeInstance speedAttr = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            double amount = Math.min(0.4, speedLevel * 0.05);
            AttributeModifier modifier = new AttributeModifier(SPEED_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE);
            speedAttr.removeModifier(SPEED_MODIFIER_ID);
            speedAttr.addTransientModifier(modifier);
        }

        AttributeInstance attackAttr = getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            double amount = speedLevel * 0.5;
            AttributeModifier modifier = new AttributeModifier(ATTACK_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE);
            attackAttr.removeModifier(ATTACK_MODIFIER_ID);
            attackAttr.addTransientModifier(modifier);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            remainingTicks--;
            if (remainingTicks <= 0) {
                this.discard();
                return;
            }

            boolean isMoving = this.position().distanceToSqr(this.xOld, this.yOld, this.zOld) > 0.001;
            this.entityData.set(IS_SPRINTING, isMoving);
        }

        updateTrailHistory();
    }

    private void updateTrailHistory() {
        Vec3 currentPos = this.position();
        trailHistory.add(0, currentPos);
        if (trailHistory.size() > MAX_TRAIL_HISTORY) {
            trailHistory.remove(trailHistory.size() - 1);
        }
    }

    public List<Vec3> getTrailHistory() {
        return trailHistory;
    }

    public boolean shouldRenderTrail() {
        if (this.level().isClientSide) {
            return this.position().distanceToSqr(this.xOld, this.yOld, this.zOld) > 0.001 && getSpeedLevel() > 0;
        }
        return this.entityData.get(IS_SPRINTING) && getSpeedLevel() > 0;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID uuid = getOwnerUUID();
        if (uuid != null) {
            tag.putUUID("OwnerUUID", uuid);
        }
        tag.putInt("RemainingTicks", remainingTicks);
        tag.putInt("SpeedLevel", getSpeedLevel());
        tag.putInt("TrailColorR", getTrailColorR());
        tag.putInt("TrailColorG", getTrailColorG());
        tag.putInt("TrailColorB", getTrailColorB());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("OwnerUUID")) {
            this.entityData.set(OWNER_UUID, Optional.of(tag.getUUID("OwnerUUID")));
        }
        remainingTicks = tag.getInt("RemainingTicks");
        if (tag.contains("SpeedLevel")) {
            this.entityData.set(SPEED_LEVEL, tag.getInt("SpeedLevel"));
        }
        if (tag.contains("TrailColorR")) {
            this.entityData.set(TRAIL_COLOR_R, tag.getInt("TrailColorR"));
        }
        if (tag.contains("TrailColorG")) {
            this.entityData.set(TRAIL_COLOR_G, tag.getInt("TrailColorG"));
        }
        if (tag.contains("TrailColorB")) {
            this.entityData.set(TRAIL_COLOR_B, tag.getInt("TrailColorB"));
        }
        applySpeedForceAttributes();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
    }

    public int getRemainingSeconds() {
        return remainingTicks / 20;
    }

    public static class FollowOwnerGoal extends Goal {
        private final TimeRemnantEntity remnant;
        private final double speedModifier;
        private final float stopDistance;
        private final float startDistance;
        private Player owner;
        private int timeToRecalcPath;

        public FollowOwnerGoal(TimeRemnantEntity remnant, double speedModifier, float startDistance, float stopDistance) {
            this.remnant = remnant;
            this.speedModifier = speedModifier;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            owner = remnant.getOwner();
            if (owner == null) return false;
            if (remnant.getTarget() != null) return false;
            return remnant.distanceToSqr(owner) >= startDistance * startDistance;
        }

        @Override
        public boolean canContinueToUse() {
            if (owner == null) return false;
            if (remnant.getTarget() != null) return false;
            return remnant.distanceToSqr(owner) > stopDistance * stopDistance;
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            owner = null;
            remnant.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (owner != null) {
                remnant.getLookControl().setLookAt(owner, 10.0F, remnant.getMaxHeadXRot());
                if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = this.adjustedTickDelay(10);
                    if (remnant.distanceToSqr(owner) > 256.0) {
                        remnant.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                    } else {
                        remnant.getNavigation().moveTo(owner, speedModifier);
                    }
                }
            }
        }
    }

    public static class OwnerHurtTargetGoal extends Goal {
        private final TimeRemnantEntity remnant;
        private LivingEntity target;

        public OwnerHurtTargetGoal(TimeRemnantEntity remnant) {
            this.remnant = remnant;
        }

        @Override
        public boolean canUse() {
            Player owner = remnant.getOwner();
            if (owner == null) return false;
            target = owner.getLastHurtMob();
            if (target == null) return false;
            if (target instanceof Player && ((Player) target).isCreative()) return false;
            return remnant.canAttack(target);
        }

        @Override
        public void start() {
            remnant.setTarget(target);
        }
    }
}