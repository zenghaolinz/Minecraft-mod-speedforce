package com.example.speedforce.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

    private ItemStack findQuiver(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof QuiverItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean hasAmmunition(Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        
        ItemStack quiver = findQuiver(player);
        if (!quiver.isEmpty()) {
            ItemStack selectedArrow = QuiverItem.getSelectedArrow(quiver);
            if (!selectedArrow.isEmpty() && selectedArrow.getItem() instanceof ArrowItem) {
                return true;
            }
        }
        
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() instanceof ArrowItem) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bowStack = player.getItemInHand(hand);
        if (hasAmmunition(player)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bowStack);
        }
        return InteractionResultHolder.fail(bowStack);
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
            ItemStack quiver = findQuiver(player);
            boolean fromQuiver = false;
            
            if (!quiver.isEmpty()) {
                ItemStack selectedArrow = QuiverItem.getSelectedArrow(quiver);
                if (!selectedArrow.isEmpty() && selectedArrow.getItem() instanceof ArrowItem) {
                    arrowStack = selectedArrow.copy();
                    arrowStack.setCount(1);
                    fromQuiver = true;
                }
            }
            
            if (!fromQuiver) {
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack invStack = player.getInventory().getItem(i);
                    if (invStack.getItem() instanceof ArrowItem) {
                        arrowStack = invStack;
                        break;
                    }
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
            if (velocity < 0.1D) return;
            
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 1.0F);
            
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
                if (fromQuiver) {
                    QuiverItem.consumeArrow(quiver, 1);
                } else {
                    arrowStack.shrink(1);
                    if (arrowStack.isEmpty()) {
                        player.getInventory().removeItem(arrowStack);
                    }
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