package com.example.speedforce.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GreenArrowBowItem extends BowItem {

    public GreenArrowBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    private boolean hasFullGreenArrowSet(Player player) {
        return isGreenArrowItem(player.getItemBySlot(EquipmentSlot.HEAD)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.CHEST)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.LEGS)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.FEET));
    }

    private boolean isGreenArrowItem(ItemStack stack) {
        return stack.getItem() instanceof FlashSuitArmorItem armor && 
               armor.getSuitType() == SuitType.GREEN_ARROW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            boolean hasInfinity = player.getAbilities().instabuild;
            boolean hasFullSet = hasFullGreenArrowSet(player);
            
            ItemStack arrowStack = ItemStack.EMPTY;
            
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (invStack.getItem() == ModItems.NORMAL_ARROW.get()) {
                    arrowStack = invStack;
                    break;
                }
            }
            
            if (!hasInfinity && arrowStack.isEmpty()) {
                return;
            }
            
            if (arrowStack.isEmpty()) {
                arrowStack = new ItemStack(ModItems.NORMAL_ARROW.get());
            }
            
            ArrowItem arrowItem = (ArrowItem) arrowStack.getItem();
            AbstractArrow arrow = arrowItem.createArrow(level, arrowStack, player, stack);
            
            int charge;
            if (hasFullSet) {
                charge = 20;
            } else {
                charge = this.getUseDuration(stack, entityLiving) - timeLeft;
                charge = net.minecraft.util.Mth.clamp(charge, 0, 20);
            }
            
            float velocity = calculateArrowVelocity(charge);
            
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 0.0F);
            
            if (hasFullSet) {
                arrow.setBaseDamage(arrow.getBaseDamage() * 3.0);
            }
            
            if (velocity >= 1.0F) {
                arrow.setCritArrow(true);
            }
            
            if (hasInfinity) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            
            level.addFreshEntity(arrow);
            
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));
            
            if (!hasInfinity) {
                arrowStack.shrink(1);
                if (arrowStack.isEmpty()) {
                    player.getInventory().removeItem(arrowStack);
                }
            }
        }
    }

    private static float calculateArrowVelocity(int charge) {
        float f = charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
}