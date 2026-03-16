package com.example.speedforce;

import com.example.speedforce.block.ModBlocks;
import com.example.speedforce.block.entity.ModBlockEntities;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.command.SpeedForceCommand;
import com.example.speedforce.entity.ModEntityTypes;
import com.example.speedforce.item.ModCreativeTabs;
import com.example.speedforce.item.ModItems;
import com.example.speedforce.particle.ModParticles;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(SpeedForceMod.MOD_ID)
public class SpeedForceMod {
    public static final String MOD_ID = "speedforce";

    public SpeedForceMod(IEventBus modEventBus) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        SpeedForceCommand.register(event.getDispatcher());
    }
}