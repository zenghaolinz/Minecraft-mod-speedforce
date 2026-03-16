package com.example.speedforce.item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FlashSuitArmorItem extends ArmorItem {

    private final SuitType suitType;

    public FlashSuitArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Properties properties, SuitType suitType) {
        super(material, type, properties);
        this.suitType = suitType;
    }

    public SuitType getSuitType() {
        return suitType;
    }

    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player)) return;

        if (this.suitType == SuitType.GREEN_ARROW && hasFullGreenArrowSet(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, false, false));
            if (player.hasEffect(MobEffects.POISON)) {
                player.removeEffect(MobEffects.POISON);
            }
        }

        if (this.getEquipmentSlot() == EquipmentSlot.FEET) {
            handleWaterWalk(player);
        }
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

    private void handleWaterWalk(Player player) {
        if (player.isSprinting()) {
            var posBelow = player.blockPosition().below();
            var stateBelow = player.level().getBlockState(posBelow);
            if (stateBelow.is(net.minecraft.world.level.block.Blocks.WATER)) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                player.fallDistance = 0.0F;
                player.setOnGround(true);
            }
        }
    }
}