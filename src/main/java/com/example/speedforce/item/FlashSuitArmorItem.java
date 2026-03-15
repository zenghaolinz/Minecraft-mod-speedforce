package com.example.speedforce.item;

import net.minecraft.core.Holder;
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

        if (this.getEquipmentSlot() == EquipmentSlot.FEET) {
            handleWaterWalk(player);
        }
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