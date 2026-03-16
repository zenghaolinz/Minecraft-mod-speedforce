package com.example.speedforce.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NormalArrowEntity extends AbstractArrow {

    public NormalArrowEntity(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(this.getBaseDamage() * 2.0);
    }

    public NormalArrowEntity(Level level, LivingEntity shooter, ItemStack pickupItem, ItemStack weapon) {
        super(ModEntityTypes.NORMAL_ARROW.get(), shooter, level, pickupItem, weapon);
        this.setBaseDamage(this.getBaseDamage() * 2.0);
    }

    public NormalArrowEntity(Level level, double x, double y, double z, ItemStack pickupItem, ItemStack weapon) {
        super(ModEntityTypes.NORMAL_ARROW.get(), x, y, z, level, pickupItem, weapon);
        this.setBaseDamage(this.getBaseDamage() * 2.0);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(com.example.speedforce.item.ModItems.NORMAL_ARROW.get());
    }
}