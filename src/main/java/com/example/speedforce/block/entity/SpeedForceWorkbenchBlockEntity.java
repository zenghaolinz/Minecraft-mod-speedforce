package com.example.speedforce.block.entity;

import com.example.speedforce.item.ModItems;
import com.example.speedforce.item.SuitType;
import com.example.speedforce.util.SuitPriceRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedForceWorkbenchBlockEntity extends BlockEntity implements Container {

    private NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);

    public SpeedForceWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPEED_FORCE_WORKBENCH.get(), pos, state);
    }

    public int calculateMaterialValue() {
        int value = 0;
        for (int i = 4; i < 10; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty()) {
                value += SuitPriceRegistry.getMaterialValue(stack);
            }
        }
        return value;
    }

    public boolean canPurchasePiece(SuitType suitType, int pieceIndex) {
        return calculateMaterialValue() >= SuitPriceRegistry.getPiecePrice(suitType, pieceIndex);
    }

    public boolean canPurchaseSuit(SuitType suitType) {
        return calculateMaterialValue() >= SuitPriceRegistry.getPrice(suitType);
    }

    public boolean purchasePiece(SuitType suitType, int pieceIndex, Player player) {
        int price = SuitPriceRegistry.getPiecePrice(suitType, pieceIndex);
        
        if (calculateMaterialValue() < price) return false;
        
        int remaining = price;
        for (int i = 4; i < 10; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty()) {
                int valPerItem = SuitPriceRegistry.getMaterialValue(new ItemStack(stack.getItem()));
                if (valPerItem > 0) {
                    while (!stack.isEmpty() && remaining > 0) {
                        stack.shrink(1);
                        remaining -= valPerItem;
                    }
                }
            }
            if (remaining <= 0) break;
        }
        
        ItemStack purchasedItem = SuitPriceRegistry.getSuitPiece(suitType, pieceIndex).copy();
        if (!player.getInventory().add(purchasedItem)) {
            player.drop(purchasedItem, false);
        }
        
        setChanged();
        return true;
    }

    public boolean purchaseSuit(SuitType suitType, Player player) {
        int price = SuitPriceRegistry.getPrice(suitType);
        
        if (calculateMaterialValue() < price) return false;
        
        int remaining = price;
        for (int i = 4; i < 10; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty()) {
                int valPerItem = SuitPriceRegistry.getMaterialValue(new ItemStack(stack.getItem()));
                if (valPerItem > 0) {
                    while (!stack.isEmpty() && remaining > 0) {
                        stack.shrink(1);
                        remaining -= valPerItem;
                    }
                }
            }
            if (remaining <= 0) break;
        }
        
        ItemStack helmet = createSuitItem(suitType, ArmorItem.Type.HELMET);
        ItemStack chestplate = createSuitItem(suitType, ArmorItem.Type.CHESTPLATE);
        ItemStack leggings = createSuitItem(suitType, ArmorItem.Type.LEGGINGS);
        ItemStack boots = createSuitItem(suitType, ArmorItem.Type.BOOTS);
        
        if (!player.getInventory().add(helmet)) player.drop(helmet, false);
        if (!player.getInventory().add(chestplate)) player.drop(chestplate, false);
        if (!player.getInventory().add(leggings)) player.drop(leggings, false);
        if (!player.getInventory().add(boots)) player.drop(boots, false);
        
        setChanged();
        return true;
    }

    private ItemStack createSuitItem(SuitType suitType, ArmorItem.Type armorType) {
        if (suitType == SuitType.FLASH) {
            if (armorType == ArmorItem.Type.HELMET) return new ItemStack(ModItems.FLASH_HELMET.get());
            if (armorType == ArmorItem.Type.CHESTPLATE) return new ItemStack(ModItems.FLASH_CHESTPLATE.get());
            if (armorType == ArmorItem.Type.LEGGINGS) return new ItemStack(ModItems.FLASH_LEGGINGS.get());
            return new ItemStack(ModItems.FLASH_BOOTS.get());
        }
        if (suitType == SuitType.REVERSE_FLASH) {
            if (armorType == ArmorItem.Type.HELMET) return new ItemStack(ModItems.REVERSE_FLASH_HELMET.get());
            if (armorType == ArmorItem.Type.CHESTPLATE) return new ItemStack(ModItems.REVERSE_FLASH_CHESTPLATE.get());
            if (armorType == ArmorItem.Type.LEGGINGS) return new ItemStack(ModItems.REVERSE_FLASH_LEGGINGS.get());
            return new ItemStack(ModItems.REVERSE_FLASH_BOOTS.get());
        }
        if (suitType == SuitType.ZOOM) {
            if (armorType == ArmorItem.Type.HELMET) return new ItemStack(ModItems.ZOOM_HELMET.get());
            if (armorType == ArmorItem.Type.CHESTPLATE) return new ItemStack(ModItems.ZOOM_CHESTPLATE.get());
            if (armorType == ArmorItem.Type.LEGGINGS) return new ItemStack(ModItems.ZOOM_LEGGINGS.get());
            return new ItemStack(ModItems.ZOOM_BOOTS.get());
        }
        if (suitType == SuitType.FLASH_S4) {
            if (armorType == ArmorItem.Type.HELMET) return new ItemStack(ModItems.FLASH_S4_HELMET.get());
            if (armorType == ArmorItem.Type.CHESTPLATE) return new ItemStack(ModItems.FLASH_S4_CHESTPLATE.get());
            if (armorType == ArmorItem.Type.LEGGINGS) return new ItemStack(ModItems.FLASH_S4_LEGGINGS.get());
            return new ItemStack(ModItems.FLASH_S4_BOOTS.get());
        }
        if (suitType == SuitType.FLASH_S5) {
            if (armorType == ArmorItem.Type.HELMET) return new ItemStack(ModItems.FLASH_S5_HELMET.get());
            if (armorType == ArmorItem.Type.CHESTPLATE) return new ItemStack(ModItems.FLASH_S5_CHESTPLATE.get());
            if (armorType == ArmorItem.Type.LEGGINGS) return new ItemStack(ModItems.FLASH_S5_LEGGINGS.get());
            return new ItemStack(ModItems.FLASH_S5_BOOTS.get());
        }
        if (suitType == SuitType.KID_FLASH) {
            if (armorType == ArmorItem.Type.HELMET) return new ItemStack(ModItems.KID_FLASH_HELMET.get());
            if (armorType == ArmorItem.Type.CHESTPLATE) return new ItemStack(ModItems.KID_FLASH_CHESTPLATE.get());
            if (armorType == ArmorItem.Type.LEGGINGS) return new ItemStack(ModItems.KID_FLASH_LEGGINGS.get());
            return new ItemStack(ModItems.KID_FLASH_BOOTS.get());
        }
        if (suitType == SuitType.GREEN_ARROW) {
            if (armorType == ArmorItem.Type.HELMET) return new ItemStack(ModItems.GREEN_ARROW_HELMET.get());
            if (armorType == ArmorItem.Type.CHESTPLATE) return new ItemStack(ModItems.GREEN_ARROW_CHESTPLATE.get());
            if (armorType == ArmorItem.Type.LEGGINGS) return new ItemStack(ModItems.GREEN_ARROW_LEGGINGS.get());
            return new ItemStack(ModItems.GREEN_ARROW_BOOTS.get());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getContainerSize() {
        return 10;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ContainerHelper.removeItem(this.items, index, count);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }
}