package com.example.speedforce.client;

import com.example.speedforce.network.BulletTimePayload;
import com.example.speedforce.network.PhasingPayload;
import com.example.speedforce.network.RewindPayload;
import com.example.speedforce.network.SpeedLevelPayload;
import com.example.speedforce.network.TimeRemnantPayload;
import com.example.speedforce.network.TogglePowerPayload;
import com.example.speedforce.particle.ModParticles;
import com.example.speedforce.client.particle.YellowFlashParticle;
import com.example.speedforce.client.screen.QuiverScreen;
import com.example.speedforce.client.screen.SpeedForceWorkbenchScreen;
import com.example.speedforce.menu.ModMenuTypes;
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

    public static final KeyMapping CYCLE_QUIVER_KEY = new KeyMapping(
        "key.speedforce.cycle_quiver",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
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

    public static final KeyMapping REWIND_KEY = new KeyMapping(
        "key.speedforce.rewind",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "category.speedforce.keys"
    );

    public static final KeyMapping TIME_REMNANT_KEY = new KeyMapping(
        "key.speedforce.time_remnant",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_H,
        "category.speedforce.keys"
    );

    private static boolean wasRewinding = false;

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

        while (CYCLE_QUIVER_KEY.consumeClick()) {
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                new com.example.speedforce.network.CycleQuiverPayload());
        }

        while (TIME_REMNANT_KEY.consumeClick()) {
            if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel >= 4) {
                PacketDistributor.sendToServer(new TimeRemnantPayload(true));
            }
        }

        boolean isRewindingNow = REWIND_KEY.isDown() && ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0;
        if (isRewindingNow != wasRewinding) {
            PacketDistributor.sendToServer(new RewindPayload(isRewindingNow));
            wasRewinding = isRewindingNow;
        }

        if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0) {
            if (isRewindingNow) {
                ClientSpeedData.clientHistorySize = Math.max(0, ClientSpeedData.clientHistorySize - 1);
            } else {
                ClientSpeedData.clientHistorySize = Math.min(200, ClientSpeedData.clientHistorySize + 1);
            }
        } else {
            ClientSpeedData.clientHistorySize = 0;
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
        event.register(ClientKeybinds.CYCLE_QUIVER_KEY);
        event.register(ClientKeybinds.REWIND_KEY);
        event.register(ClientKeybinds.TIME_REMNANT_KEY);
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
        event.registerEntityRenderer(
            com.example.speedforce.entity.ModEntityTypes.TIME_REMNANT.get(), 
            com.example.speedforce.client.renderer.TimeRemnantRenderer::new
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

    @SubscribeEvent
    public static void registerMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.SPEED_FORCE_WORKBENCH.get(), SpeedForceWorkbenchScreen::new);
        event.register(ModMenuTypes.QUIVER.get(), QuiverScreen::new);
    }
}