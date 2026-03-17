package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.item.ModItems;
import com.example.speedforce.item.QuiverItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class QuiverHudOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean holdingBow = mainHand.getItem() == ModItems.GREEN_ARROW_BOW.get() || offHand.getItem() == ModItems.GREEN_ARROW_BOW.get();

        if (!holdingBow) return;

        ItemStack quiver = findQuiver(player);
        if (quiver.isEmpty()) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        NonNullList<ItemStack> arrows = QuiverItem.getInventory(quiver);
        int selectedSlot = QuiverItem.getSelectedSlot(quiver);

        int slotSize = 22;
        int startX = screenWidth - slotSize - 2;
        int startY = (screenHeight - (5 * slotSize)) / 2;

        for (int i = 0; i < 5; i++) {
            int y = startY + i * slotSize;

            graphics.fill(startX, y, startX + 20, y + 20, 0x80000000);

            if (i == selectedSlot) {
                graphics.fill(startX - 1, y - 1, startX + 21, y, 0xFFFFFFFF);
                graphics.fill(startX - 1, y + 20, startX + 21, y + 21, 0xFFFFFFFF);
                graphics.fill(startX - 1, y, startX, y + 20, 0xFFFFFFFF);
                graphics.fill(startX + 20, y, startX + 21, y + 20, 0xFFFFFFFF);
            } else {
                graphics.fill(startX - 1, y - 1, startX + 21, y, 0xFF555555);
                graphics.fill(startX - 1, y + 20, startX + 21, y + 21, 0xFF555555);
                graphics.fill(startX - 1, y, startX, y + 20, 0xFF555555);
                graphics.fill(startX + 20, y, startX + 21, y + 20, 0xFF555555);
            }

            ItemStack arrow = arrows.get(i);
            if (!arrow.isEmpty()) {
                graphics.renderItem(arrow, startX + 2, y + 2);
                graphics.renderItemDecorations(mc.font, arrow, startX + 2, y + 2);
            }
        }
    }

    private static ItemStack findQuiver(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof QuiverItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}