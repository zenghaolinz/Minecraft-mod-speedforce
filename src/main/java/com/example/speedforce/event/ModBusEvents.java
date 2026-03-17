package com.example.speedforce.event;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.entity.ModEntityTypes;
import com.example.speedforce.entity.TimeRemnantEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.TIME_REMNANT.get(), TimeRemnantEntity.createAttributes().build());
    }
}
