package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.client.model.QuiverModel;
import com.example.speedforce.client.render.QuiverLayer;
import com.example.speedforce.client.renderer.TimeRemnantRenderer;
import com.example.speedforce.entity.ModEntityTypes;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(QuiverModel.LAYER_LOCATION, QuiverModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.TIME_REMNANT.get(), TimeRemnantRenderer::new);
    }

    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        QuiverModel quiverModel = new QuiverModel(event.getEntityModels().bakeLayer(QuiverModel.LAYER_LOCATION));

        for (var skin : event.getSkins()) {
            var renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new QuiverLayer(playerRenderer, quiverModel));
            }
        }
    }
}