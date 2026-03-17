package com.example.speedforce.event;

import com.example.speedforce.item.QuiverItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;

@EventBusSubscriber(modid = "speedforce")
public class QuiverEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onArrowLoose(ArrowLooseEvent event) {
        if (event.isCanceled()) return;
        
        ItemStack bow = event.getBow();
        if (bow.getItem() != Items.BOW) {
            return;
        }

        ItemStack quiver = findQuiver(event.getEntity());
        if (quiver.isEmpty()) return;

        ItemStack selectedArrow = QuiverItem.getSelectedArrow(quiver);
        if (selectedArrow.isEmpty()) return;

        QuiverItem.consumeArrow(quiver, 1);
    }

    private static ItemStack findQuiver(net.minecraft.world.entity.player.Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof QuiverItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}