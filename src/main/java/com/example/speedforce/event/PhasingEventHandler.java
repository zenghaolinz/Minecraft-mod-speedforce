package com.example.speedforce.event;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Miscellaneous phasing-related event handling.
 */
@EventBusSubscriber(modid = SpeedForceMod.MOD_ID)
public final class PhasingEventHandler {

    private PhasingEventHandler() {}

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
        if (!data.hasPower || data.speedLevel <= 0 || !data.isPhasing) return;

        // While phasing, being inside blocks is intentional.
        if (event.getSource().is(DamageTypes.IN_WALL)) {
            event.setCanceled(true);
        }
    }
}
