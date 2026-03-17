package com.example.speedforce.inventory;

import com.example.speedforce.item.QuiverItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import javax.annotation.Nullable;

public class QuiverMenu extends AbstractContainerMenu {

    public static final MenuType<QuiverMenu> TYPE = IMenuTypeExtension.create(QuiverMenu::new);

    private final ItemStack quiverStack;
    private final QuiverContainer container;

    public QuiverMenu(int containerId, Inventory playerInventory, ItemStack quiverStack) {
        super(TYPE, containerId);
        this.quiverStack = quiverStack;
        this.container = new QuiverContainer(quiverStack);

        for (int i = 0; i < 5; i++) {
            this.addSlot(new QuiverSlot(container, i, 44 + i * 18, 36));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public QuiverMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, ItemStack.EMPTY);
    }

    public int getSelectedSlot() {
        return QuiverItem.getSelectedSlot(quiverStack);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack originalStack = slotStack.copy();

        if (slotIndex < 5) {
            if (!this.moveItemStackTo(slotStack, 5, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(slotStack, 0, 5, false)) {
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
        return true;
    }

    private static class QuiverSlot extends Slot {
        public QuiverSlot(QuiverContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof ArrowItem;
        }
    }
}