package com.example.speedforce.client;

import com.example.speedforce.network.BulletTimePayload;
import com.example.speedforce.network.PhasingPayload;
import com.example.speedforce.network.SpeedLevelPayload;
import com.example.speedforce.network.TogglePowerPayload;
import com.example.speedforce.particle.ModParticles;
import com.example.speedforce.client.particle.YellowFlashParticle;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "speedforce", value = Dist.CLIENT)
public class ClientKeybinds {
    public static final KeyMapping TOGGLE_POWER_KEY = new KeyMapping(
        "key.speedforce.toggle_power",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_C,
        "category.speedforce.keys"
    );

    public static final KeyMapping SPEED_UP_KEY = new KeyMapping(
        "key.speedforce.speed_up",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        "category.speedforce.keys"
    );

    public static final KeyMapping SPEED_DOWN_KEY = new KeyMapping(
        "key.speedforce.speed_down",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_Z,
        "category.speedforce.keys"
    );

    public static final KeyMapping BULLET_TIME_KEY = new KeyMapping(
        "key.speedforce.bullet_time",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        "category.speedforce.keys"
    );

    public static final KeyMapping PHASING_KEY = new KeyMapping(
        "key.speedforce.phasing",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_V,
        "category.speedforce.keys"
    );

    public static final KeyMapping COLOR_PALETTE_KEY = new KeyMapping(
        "key.speedforce.color_palette",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_N,
        "category.speedforce.keys"
    );

    public static final KeyMapping TOGGLE_HELP_KEY = new KeyMapping(
        "key.speedforce.toggle_help",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_U,
        "category.speedforce.keys"
    );

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (TOGGLE_POWER_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new TogglePowerPayload());
        }

        while (SPEED_UP_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new SpeedLevelPayload(true));
        }

        while (SPEED_DOWN_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new SpeedLevelPayload(false));
        }

        while (BULLET_TIME_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new BulletTimePayload());
        }

        while (PHASING_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new PhasingPayload());
        }

        while (COLOR_PALETTE_KEY.consumeClick()) {
            if (ClientSpeedData.hasPower) {
                mc.setScreen(new ColorPaletteScreen());
            }
        }

        while (TOGGLE_HELP_KEY.consumeClick()) {
            ClientSpeedData.showHelp = !ClientSpeedData.showHelp;
        }
    }
}

@EventBusSubscriber(modid = "speedforce", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
class ClientModEvents {
    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(ClientKeybinds.TOGGLE_POWER_KEY);
        event.register(ClientKeybinds.SPEED_UP_KEY);
        event.register(ClientKeybinds.SPEED_DOWN_KEY);
        event.register(ClientKeybinds.BULLET_TIME_KEY);
        event.register(ClientKeybinds.PHASING_KEY);
        event.register(ClientKeybinds.COLOR_PALETTE_KEY);
        event.register(ClientKeybinds.TOGGLE_HELP_KEY);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.YELLOW_FLASH.get(), YellowFlashParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
            com.example.speedforce.entity.ModEntityTypes.NORMAL_ARROW.get(), 
            com.example.speedforce.client.renderer.NormalArrowRenderer::new
        );
    }

    @SubscribeEvent
    public static void onClientSetup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.renderer.item.ItemProperties.register(
                com.example.speedforce.item.ModItems.GREEN_ARROW_BOW.get(),
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    return entity.getUseItem() != stack ? 0.0F : 
                        (float)(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
                }
            );
            net.minecraft.client.renderer.item.ItemProperties.register(
                com.example.speedforce.item.ModItems.GREEN_ARROW_BOW.get(),
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "pulling"),
                (stack, level, entity, seed) -> 
                    entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
            );
        });
    }
}