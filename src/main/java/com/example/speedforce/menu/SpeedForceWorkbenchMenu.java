package com.example.speedforce.menu;

import com.example.speedforce.block.entity.SpeedForceWorkbenchBlockEntity;
import com.example.speedforce.util.SuitPriceRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public class SpeedForceWorkbenchMenu extends AbstractContainerMenu {

    public static final MenuType<SpeedForceWorkbenchMenu> TYPE = 
        IMenuTypeExtension.create(SpeedForceWorkbenchMenu::new);

    private final SpeedForceWorkbenchBlockEntity blockEntity;
    private final Player player;

    public SpeedForceWorkbenchMenu(int containerId, Inventory playerInventory, SpeedForceWorkbenchBlockEntity blockEntity) {
        super(TYPE, containerId);
        this.blockEntity = blockEntity;
        this.player = playerInventory.player;

        for (int i = 0; i < 4; i++) {
            this.addSlot(new Slot(blockEntity, i, 145, 19 + i * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        for (int i = 0; i < 6; i++) {
            this.addSlot(new Slot(blockEntity, i + 4, 15 + i * 18, 93));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 128 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 186));
        }
    }

    public SpeedForceWorkbenchMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer));
    }

    private static SpeedForceWorkbenchBlockEntity getBlockEntity(Inventory playerInventory, FriendlyByteBuf buffer) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (blockEntity instanceof SpeedForceWorkbenchBlockEntity workbench) {
            return workbench;
        }
        throw new IllegalStateException("Block entity is not a SpeedForceWorkbenchBlockEntity");
    }

    public int getMaterialValue() {
        int value = 0;
        for (int i = 4; i < 10; i++) {
            ItemStack stack = this.slots.get(i).getItem();
            if (!stack.isEmpty()) {
                value += SuitPriceRegistry.getMaterialValue(stack);
            }
        }
        return value;
    }

    public void purchaseSuit(com.example.speedforce.item.SuitType suitType) {
        if (blockEntity != null && blockEntity.canPurchaseSuit(suitType)) {
            if (!player.level().isClientSide) {
                blockEntity.purchaseSuit(suitType, player);
                broadcastChanges();
            }
        }
    }

    public void purchasePiece(com.example.speedforce.item.SuitType suitType, int pieceIndex) {
        if (blockEntity != null && blockEntity.canPurchasePiece(suitType, pieceIndex)) {
            if (!player.level().isClientSide) {
                blockEntity.purchasePiece(suitType, pieceIndex, player);
                broadcastChanges();
            }
        }
    }

    public int getSuitPrice(com.example.speedforce.item.SuitType suitType) {
        return SuitPriceRegistry.getPrice(suitType);
    }

    public int getPiecePrice(com.example.speedforce.item.SuitType suitType, int pieceIndex) {
        return SuitPriceRegistry.getPiecePrice(suitType, pieceIndex);
    }

    public boolean canAffordSuit(com.example.speedforce.item.SuitType suitType) {
        return getMaterialValue() >= SuitPriceRegistry.getPrice(suitType);
    }

    public boolean canAffordPiece(com.example.speedforce.item.SuitType suitType, int pieceIndex) {
        return getMaterialValue() >= SuitPriceRegistry.getPiecePrice(suitType, pieceIndex);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack originalStack = slotStack.copy();

        if (slotIndex < 10) {
            if (!this.moveItemStackTo(slotStack, 10, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(slotStack, 4, 10, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && blockEntity.stillValid(player);
    }
}