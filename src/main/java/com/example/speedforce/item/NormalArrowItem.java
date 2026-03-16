package com.example.speedforce.item;

import com.example.speedforce.entity.ModEntityTypes;
import com.example.speedforce.entity.NormalArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NormalArrowItem extends ArrowItem {

    public NormalArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammoStack, LivingEntity shooter, ItemStack weapon) {
        NormalArrowEntity arrow = new NormalArrowEntity(level, shooter, ammoStack, weapon);
        return arrow;
    }
}