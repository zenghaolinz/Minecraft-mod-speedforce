package com.example.speedforce.item;

import com.example.speedforce.inventory.QuiverMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class QuiverItem extends Item {

    public static final int SLOT_COUNT = 5;

    public QuiverItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                (containerId, playerInventory, playerIn) -> new QuiverMenu(containerId, playerInventory, stack),
                Component.translatable("item.speedforce.quiver")
            ));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static CompoundTag getCustomData(ItemStack stack) {
        var customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : new CompoundTag();
    }

    private static void setCustomData(ItemStack stack, CompoundTag tag) {
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, 
            net.minecraft.world.item.component.CustomData.of(tag));
    }

    public static NonNullList<ItemStack> getInventory(ItemStack stack) {
        NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        CompoundTag tag = getCustomData(stack);
        if (tag.contains("Items", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag itemTag = listTag.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < SLOT_COUNT) {
                    items.set(slot, loadItemStack(itemTag));
                }
            }
        }
        return items;
    }

    public static void saveInventory(ItemStack stack, NonNullList<ItemStack> items) {
        CompoundTag tag = getCustomData(stack);
        ListTag listTag = new ListTag();
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag itemTag = saveItemStack(itemStack);
                itemTag.putByte("Slot", (byte) i);
                listTag.add(itemTag);
            }
        }
        tag.put("Items", listTag);
        setCustomData(stack, tag);
    }

    private static ItemStack loadItemStack(CompoundTag tag) {
        if (!tag.contains("id", Tag.TAG_STRING)) return ItemStack.EMPTY;
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
        if (id == null) return ItemStack.EMPTY;
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(id);
        if (item.isEmpty()) return ItemStack.EMPTY;
        int count = tag.contains("Count", Tag.TAG_INT) ? tag.getInt("Count") : 1;
        return new ItemStack(item.get(), count);
    }

    private static CompoundTag saveItemStack(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        tag.putString("id", id.toString());
        tag.putInt("Count", stack.getCount());
        return tag;
    }

    public static int getSelectedSlot(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        return tag.contains("SelectedSlot") ? tag.getInt("SelectedSlot") : 0;
    }

    public static void setSelectedSlot(ItemStack stack, int slot) {
        CompoundTag tag = getCustomData(stack);
        tag.putInt("SelectedSlot", Math.max(0, Math.min(slot, SLOT_COUNT - 1)));
        setCustomData(stack, tag);
    }

    public static ItemStack getSelectedArrow(ItemStack quiverStack) {
        NonNullList<ItemStack> items = getInventory(quiverStack);
        int selectedSlot = getSelectedSlot(quiverStack);
        return items.get(selectedSlot);
    }

    public static boolean consumeArrow(ItemStack quiverStack, int amount) {
        NonNullList<ItemStack> items = getInventory(quiverStack);
        int selectedSlot = getSelectedSlot(quiverStack);
        ItemStack arrowStack = items.get(selectedSlot);
        
        if (!arrowStack.isEmpty() && arrowStack.getCount() >= amount) {
            arrowStack.shrink(amount);
            saveInventory(quiverStack, items);
            return true;
        }
        return false;
    }

    public static int getArrowCount(ItemStack quiverStack) {
        NonNullList<ItemStack> items = getInventory(quiverStack);
        int selectedSlot = getSelectedSlot(quiverStack);
        ItemStack arrowStack = items.get(selectedSlot);
        return arrowStack.isEmpty() ? 0 : arrowStack.getCount();
    }

    public static void cycleSelectedSlot(ItemStack quiverStack) {
        int current = getSelectedSlot(quiverStack);
        NonNullList<ItemStack> items = getInventory(quiverStack);
        
        for (int i = 1; i < SLOT_COUNT; i++) {
            int nextSlot = (current + i) % SLOT_COUNT;
            if (!items.get(nextSlot).isEmpty()) {
                setSelectedSlot(quiverStack, nextSlot);
                return;
            }
        }
    }
}