// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\block\entity\ModBlockEntities.java
// ============================================================================
package com.example.speedforce.block.entity;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpeedForceMod.MOD_ID);

    public static final Supplier<BlockEntityType<ParticleAcceleratorBlockEntity>> PARTICLE_ACCELERATOR = 
        BLOCK_ENTITIES.register("particle_accelerator", 
            () -> BlockEntityType.Builder.of(ParticleAcceleratorBlockEntity::new, 
                ModBlocks.PARTICLE_ACCELERATOR.get()).build(null));
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\block\entity\ParticleAcceleratorBlockEntity.java
// ============================================================================
package com.example.speedforce.block.entity;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.network.ModNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ParticleAcceleratorBlockEntity extends BlockEntity {

    private Player activatingPlayer = null;
    private int activationTicks = 0;
    private static final int LIGHTNING_DELAY = 40;

    public ParticleAcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PARTICLE_ACCELERATOR.get(), pos, state);
    }

    public boolean canActivate(Player player) {
        var data = player.getData(ModAttachments.SPEED_PLAYER);
        return !data.hasPower && activatingPlayer == null;
    }

    public void startActivation(Player player) {
        this.activatingPlayer = player;
        this.activationTicks = LIGHTNING_DELAY;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ParticleAcceleratorBlockEntity blockEntity) {
        if (blockEntity.activatingPlayer == null || blockEntity.activationTicks <= 0) return;

        blockEntity.activationTicks--;

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                5, 0.3, 0.5, 0.3, 0.1);
        }

        if (blockEntity.activationTicks <= 0) {
            blockEntity.summonLightningAndGrantPower(level, pos);
            blockEntity.activatingPlayer = null;
            blockEntity.setChanged();
        }
    }

    private void summonLightningAndGrantPower(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel && activatingPlayer != null) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                lightning.setVisualOnly(true);
                serverLevel.addFreshEntity(lightning);
            }

            activatingPlayer.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(true, 1));
            
            if (activatingPlayer instanceof ServerPlayer serverPlayer) {
                ModNetworking.syncToClient(serverPlayer);
            }

            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                1, 0, 0, 0, 0);
        }
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\block\ModBlocks.java
// ============================================================================
package com.example.speedforce.block;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpeedForceMod.MOD_ID);

    public static final DeferredBlock<ParticleAcceleratorBlock> PARTICLE_ACCELERATOR = 
        BLOCKS.register("particle_accelerator", 
            () -> new ParticleAcceleratorBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\block\ParticleAcceleratorBlock.java
// ============================================================================
package com.example.speedforce.block;

import com.example.speedforce.block.entity.ModBlockEntities;
import com.example.speedforce.block.entity.ParticleAcceleratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ParticleAcceleratorBlock extends Block implements EntityBlock {

    public ParticleAcceleratorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ParticleAcceleratorBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, 
                                  Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ParticleAcceleratorBlockEntity accelerator) {
                if (accelerator.canActivate(player)) {
                    accelerator.startActivation(player);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, 
                                                                    BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;
        return createTickerHelper(blockEntityType, ModBlockEntities.PARTICLE_ACCELERATOR.get(), 
            ParticleAcceleratorBlockEntity::tick);
    }

    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> type, BlockEntityType<E> targetType, BlockEntityTicker<? super E> ticker) {
        return type == targetType ? (BlockEntityTicker<A>) ticker : null;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\capability\ModAttachments.java
// ============================================================================
package com.example.speedforce.capability;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "speedforce");

    public static final Supplier<AttachmentType<SpeedPlayerData>> SPEED_PLAYER = 
        ATTACHMENT_TYPES.register("speed_player",
            () -> AttachmentType.builder(() -> new SpeedPlayerData())
                .serialize(SpeedPlayerData.CODEC)
                .copyOnDeath()
                .build());
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\capability\SpeedPlayerData.java
// ============================================================================
package com.example.speedforce.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SpeedPlayerData {
    public boolean hasPower = false;
    public int speedLevel = 0;
    public boolean isBulletTimeActive = false;
    public boolean isPhasing = false;
    public int trailColorR = 255;
    public int trailColorG = 210;
    public int trailColorB = 0;
    public int customTrailColorR = 255;
    public int customTrailColorG = 210;
    public int customTrailColorB = 0;

    public SpeedPlayerData() {}

    public SpeedPlayerData(boolean hasPower, int speedLevel) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
    }

    public SpeedPlayerData(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
        this.isBulletTimeActive = isBulletTimeActive;
        this.isPhasing = isPhasing;
    }

    public SpeedPlayerData(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing, int trailColorR, int trailColorG, int trailColorB) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
        this.isBulletTimeActive = isBulletTimeActive;
        this.isPhasing = isPhasing;
        this.trailColorR = trailColorR;
        this.trailColorG = trailColorG;
        this.trailColorB = trailColorB;
        this.customTrailColorR = trailColorR;
        this.customTrailColorG = trailColorG;
        this.customTrailColorB = trailColorB;
    }

    public SpeedPlayerData(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing, 
                           int trailColorR, int trailColorG, int trailColorB,
                           int customTrailColorR, int customTrailColorG, int customTrailColorB) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
        this.isBulletTimeActive = isBulletTimeActive;
        this.isPhasing = isPhasing;
        this.trailColorR = trailColorR;
        this.trailColorG = trailColorG;
        this.trailColorB = trailColorB;
        this.customTrailColorR = customTrailColorR;
        this.customTrailColorG = customTrailColorG;
        this.customTrailColorB = customTrailColorB;
    }

    public static final Codec<SpeedPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("hasPower", false).forGetter(d -> d.hasPower),
        Codec.INT.optionalFieldOf("speedLevel", 0).forGetter(d -> d.speedLevel),
        Codec.BOOL.optionalFieldOf("isBulletTimeActive", false).forGetter(d -> d.isBulletTimeActive),
        Codec.BOOL.optionalFieldOf("isPhasing", false).forGetter(d -> d.isPhasing),
        Codec.INT.optionalFieldOf("trailColorR", 255).forGetter(d -> d.trailColorR),
        Codec.INT.optionalFieldOf("trailColorG", 210).forGetter(d -> d.trailColorG),
        Codec.INT.optionalFieldOf("trailColorB", 0).forGetter(d -> d.trailColorB),
        Codec.INT.optionalFieldOf("customTrailColorR", 255).forGetter(d -> d.customTrailColorR),
        Codec.INT.optionalFieldOf("customTrailColorG", 210).forGetter(d -> d.customTrailColorG),
        Codec.INT.optionalFieldOf("customTrailColorB", 0).forGetter(d -> d.customTrailColorB)
    ).apply(instance, SpeedPlayerData::new));
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\client\ClientKeybinds.java
// ============================================================================
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

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\client\ClientSpeedData.java
// ============================================================================
package com.example.speedforce.client;

public class ClientSpeedData {
    public static boolean hasPower = false;
    public static int speedLevel = 0;
    public static boolean isBulletTimeActive = false;
    public static boolean isPhasing = false;
    public static int trailColorR = 255;
    public static int trailColorG = 210;
    public static int trailColorB = 0;
    public static int customTrailColorR = 255;
    public static int customTrailColorG = 210;
    public static int customTrailColorB = 0;
    public static boolean showHelp = true;
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\client\ClientTrailRenderer.java
// ============================================================================
package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.Deque;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class ClientTrailRenderer {

    private static class TrailPoint {
        public final Vec3 pos;
        public final Vec3[] jitters;

        public TrailPoint(Vec3 pos, Vec3[] jitters) {
            this.pos = pos;
            this.jitters = jitters;
        }
    }

    private static final Deque<TrailPoint> history = new ArrayDeque<>();
    private static final int MAX_AGE = 12;
    private static long lastTickCount = -1;
    
    private static final float[] BRANCH_OFFSETS_X = {-0.4f, -0.2f, 0.0f, 0.2f, 0.4f};

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.isPaused()) return;

        long currentTicks = mc.level.getGameTime();
        if (currentTicks == lastTickCount) return;
        lastTickCount = currentTicks;

        synchronized (history) {
            boolean isMoving = player.getDeltaMovement().lengthSqr() > 0.001;
            
            if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0 && isMoving) {
                Vec3[] jitters = new Vec3[5];
                for (int i = 0; i < 5; i++) {
                    jitters[i] = new Vec3((Math.random() - 0.5) * 0.8, (Math.random() - 0.5) * 0.3, (Math.random() - 0.5) * 0.8);
                }
                history.addFirst(new TrailPoint(player.position(), jitters));
            } else if (!history.isEmpty()) {
                history.removeLast();
            }

            if (history.size() > MAX_AGE) {
                history.removeLast();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.options.getCameraType() == net.minecraft.client.CameraType.FIRST_PERSON) return;

        synchronized (history) {
            if (history.size() < 2) return;

            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = event.getCamera().getPosition();

            poseStack.pushPose();
            Matrix4f pose = poseStack.last().pose();

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            float[] yOffsets = {0.1f, 0.5f, 0.9f, 1.3f, 1.7f};
            int r = ClientSpeedData.trailColorR;
            int g = ClientSpeedData.trailColorG;
            int b = ClientSpeedData.trailColorB;

            for (int branch = 0; branch < 5; branch++) {
                Vec3 prevPoint = null;
                int age = 0;

                for (TrailPoint tp : history) {
                    float alpha = 1.0F - ((float) age / MAX_AGE);
                    int a = (int) (alpha * alpha * 255);
                    if (a <= 5) {
                        age++;
                        continue;
                    }

                    Vec3 jitter = tp.jitters[branch];
                    float branchOffsetX = BRANCH_OFFSETS_X[branch];
                    
                    double offsetX = branchOffsetX + (age == 0 ? 0 : jitter.x);

                    Vec3 currentPoint = tp.pos.add(offsetX, yOffsets[branch] + jitter.y, jitter.z);

                    if (prevPoint != null) {
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, 0.06f, r, g, b, (int)(a * 0.6));
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, 0.02f, 255, 255, 255, a);
                    }

                    prevPoint = currentPoint;
                    age++;
                }
            }

            BufferUploader.drawWithShader(builder.buildOrThrow());

            RenderSystem.depthMask(true);
            RenderSystem.disableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }

    private static void drawLightningSegment(BufferBuilder builder, Matrix4f pose, Vec3 start, Vec3 end, Vec3 cameraPos, float width, int r, int g, int b, int a) {
        Vec3 dir = end.subtract(start);
        if (dir.lengthSqr() < 1e-5) return;

        Vec3 normal = dir.cross(cameraPos.subtract(start)).normalize().scale(width);

        float sx = (float) (start.x - cameraPos.x);
        float sy = (float) (start.y - cameraPos.y);
        float sz = (float) (start.z - cameraPos.z);

        float ex = (float) (end.x - cameraPos.x);
        float ey = (float) (end.y - cameraPos.y);
        float ez = (float) (end.z - cameraPos.z);

        float nx = (float) normal.x, ny = (float) normal.y, nz = (float) normal.z;

        builder.addVertex(pose, sx + nx, sy + ny, sz + nz).setColor(r, g, b, a);
        builder.addVertex(pose, sx - nx, sy - ny, sz - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex - nx, ey - ny, ez - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex + nx, ey + ny, ez + nz).setColor(r, g, b, a);
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\client\ColorPaletteScreen.java
// ============================================================================
package com.example.speedforce.client;

import com.example.speedforce.network.TrailColorPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class ColorPaletteScreen extends Screen {
    private static final int[][] PRESET_COLORS = {
        {255, 210, 0}, {255, 0, 0}, {0, 150, 255}, {0, 255, 100},
        {180, 0, 255}, {255, 255, 255}, {0, 255, 255}, {255, 100, 0},
        {255, 150, 200}, {100, 255, 100}, {150, 150, 255}, {255, 255, 0}
    };
    
    private static final String[] COLOR_KEYS = {
        "yellow", "red", "blue", "green", "purple", "white",
        "cyan", "orange", "pink", "lime", "lightblue", "gold"
    };

    private int selectedR = 255;
    private int selectedG = 210;
    private int selectedB = 0;
    private float hue = 0.0f;
    private int hueSliderY;

    public ColorPaletteScreen() {
        super(Component.translatable("gui.speedforce.color_palette"));
    }

    @Override
    protected void init() {
        super.init();
        
        this.selectedR = ClientSpeedData.trailColorR;
        this.selectedG = ClientSpeedData.trailColorG;
        this.selectedB = ClientSpeedData.trailColorB;
        
        float[] hsb = java.awt.Color.RGBtoHSB(selectedR, selectedG, selectedB, null);
        this.hue = hsb[0];

        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;

        for (int i = 0; i < PRESET_COLORS.length; i++) {
            final int idx = i;
            int row = i / 6;
            int col = i % 6;
            int x = centerX - 153 + col * 52;
            int y = startY + row * 28;
            final int[] color = PRESET_COLORS[idx];
            
            this.addRenderableWidget(Button.builder(
                Component.translatable("gui.speedforce.color." + COLOR_KEYS[idx]), 
                btn -> {
                    selectedR = color[0];
                    selectedG = color[1];
                    selectedB = color[2];
                    updateHue();
                }
            ).bounds(x, y, 50, 22).build());
        }

        hueSliderY = startY + 70;

        this.addRenderableWidget(Button.builder(Component.translatable("gui.speedforce.apply"), btn -> applyColor())
            .bounds(centerX - 55, this.height - 50, 50, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.speedforce.cancel"), btn -> this.onClose())
            .bounds(centerX + 5, this.height - 50, 50, 20).build());
    }

    private void updateHue() {
        float[] hsb = java.awt.Color.RGBtoHSB(selectedR, selectedG, selectedB, null);
        this.hue = hsb[0];
    }

    private void applyColor() {
        ClientSpeedData.trailColorR = selectedR;
        ClientSpeedData.trailColorG = selectedG;
        ClientSpeedData.trailColorB = selectedB;
        PacketDistributor.sendToServer(new TrailColorPayload(selectedR, selectedG, selectedB));
        this.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;
        
        graphics.fill(centerX - 160, 12, centerX + 160, 32, 0xCC000000);
        graphics.drawCenteredString(this.font, Component.translatable("gui.speedforce.select_color"), centerX, 20, 0xFFFFFF);
        
        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int row = i / 6;
            int col = i % 6;
            int x = centerX - 153 + col * 52;
            int y = startY + row * 28;
            int[] color = PRESET_COLORS[i];
            
            boolean isSelected = (selectedR == color[0] && selectedG == color[1] && selectedB == color[2]);
            if (isSelected) {
                graphics.renderOutline(x - 1, y - 1, 52, 24, 0xFFFFFFFF);
            }
        }

        graphics.fill(centerX - 102, hueSliderY - 18, centerX + 102, hueSliderY - 2, 0xCC000000);
        graphics.drawCenteredString(this.font, Component.translatable("gui.speedforce.hue"), centerX, hueSliderY - 14, 0xFFFFFF);

        int sliderX = centerX - 100;
        int sliderWidth = 200;
        graphics.fill(sliderX - 1, hueSliderY - 1, sliderX + sliderWidth + 1, hueSliderY + 21, 0xFF000000);
        for (int i = 0; i < sliderWidth; i++) {
            float h = i / (float) sliderWidth;
            int color = java.awt.Color.HSBtoRGB(h, 1.0f, 1.0f);
            graphics.fill(sliderX + i, hueSliderY, sliderX + i + 1, hueSliderY + 20, color | 0xFF000000);
        }
        
        int markerX = sliderX + (int) (hue * sliderWidth);
        graphics.fill(markerX - 2, hueSliderY - 3, markerX + 2, hueSliderY + 23, 0xFFFFFFFF);

        int previewX = centerX - 40;
        int previewY = this.height - 90;
        graphics.fill(previewX - 1, previewY - 1, previewX + 81, previewY + 31, 0xFF000000);
        graphics.fill(previewX, previewY, previewX + 80, previewY + 30, 
            (0xFF << 24) | (selectedR << 16) | (selectedG << 8) | selectedB);
        
        graphics.fill(centerX - 80, this.height - 58, centerX + 80, this.height - 45, 0xCC000000);
        graphics.drawCenteredString(this.font, String.format("RGB: %d, %d, %d", selectedR, selectedG, selectedB), 
            centerX, this.height - 55, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        handleSliderClick(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        handleSliderClick(mouseX, mouseY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void handleSliderClick(double mouseX, double mouseY) {
        int sliderX = this.width / 2 - 100;
        int sliderWidth = 200;
        
        if (mouseY >= hueSliderY - 5 && mouseY <= hueSliderY + 25) {
            hue = (float) Math.max(0, Math.min(1, (mouseX - sliderX) / sliderWidth));
            int color = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
            selectedR = (color >> 16) & 0xFF;
            selectedG = (color >> 8) & 0xFF;
            selectedB = color & 0xFF;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\client\particle\YellowFlashParticle.java
// ============================================================================
package com.example.speedforce.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;

public class YellowFlashParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected YellowFlashParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.friction = 0.9F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize *= 2.5F;
        this.lifetime = 8 + this.random.nextInt(6);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.alpha = 1.0F - ((float)this.age / (float)this.lifetime);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public net.minecraft.client.particle.Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new YellowFlashParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\client\SpeedHudRenderer.java
// ============================================================================
package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.item.FlashSuitArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class SpeedHudRenderer {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        GuiGraphics graphics = event.getGuiGraphics();

        if (!ClientSpeedData.hasPower) {
            graphics.fill(8, 8, 180, 40, 0x80000000);
            graphics.drawString(mc.font, "未获得神速力", 12, 12, 0xFF5555);
            graphics.drawString(mc.font, "获取方式: 中毒+闪电击中", 12, 24, 0xAAAAAA);
            graphics.drawString(mc.font, "或使用粒子加速器", 12, 36, 0xAAAAAA);
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        boolean hasFullSuit = hasFullFlashSuit(player);
        int maxLevel = hasFullSuit ? 14 : 10;

        if (ClientSpeedData.showHelp) {
            graphics.fill(8, 8, 140, 84, 0x80000000);
            graphics.drawString(mc.font, "C - 开关超能力", 12, 12, 0xFFFFFF);
            graphics.drawString(mc.font, "X - 加速 / Z - 减速", 12, 24, 0xFFFFFF);
            graphics.drawString(mc.font, "B - 子弹时间", 12, 36, 0xFFFFFF);
            graphics.drawString(mc.font, "N - 拖尾颜色", 12, 48, 0xFFFFFF);
            graphics.drawString(mc.font, "U - 收起帮助", 12, 60, 0xAAAAAA);
            
            String statusText = ClientSpeedData.speedLevel > 0 ? 
                "状态: Lv." + ClientSpeedData.speedLevel + "/" + maxLevel : "状态: 未启用";
            int statusColor = ClientSpeedData.speedLevel > 0 ? 0x55FF55 : 0xFF5555;
            graphics.drawString(mc.font, statusText, 12, 72, statusColor);

            int colorBoxX = 115;
            graphics.fill(colorBoxX, 60, colorBoxX + 20, 80, 0xFF000000);
            int trailColor = (0xFF << 24) | (ClientSpeedData.trailColorR << 16) | 
                             (ClientSpeedData.trailColorG << 8) | ClientSpeedData.trailColorB;
            graphics.fill(colorBoxX + 1, 61, colorBoxX + 19, 79, trailColor);
        } else {
            graphics.fill(8, 8, 100, 24, 0x80000000);
            graphics.drawString(mc.font, "[U] 帮助", 12, 12, 0xAAAAAA);
            
            String statusText = "Lv." + ClientSpeedData.speedLevel + "/" + maxLevel;
            int statusColor = ClientSpeedData.speedLevel > 0 ? 0x55FF55 : 0xFF5555;
            graphics.drawString(mc.font, statusText, 60, 12, statusColor);
        }

        int rightX = screenWidth - 70;
        
        graphics.fill(rightX, 10, rightX + 60, 30, 0x80000000);
        graphics.fill(rightX, 10, rightX + 60, 11, 0xFFD4AF37);
        graphics.fill(rightX, 29, rightX + 60, 30, 0xFFD4AF37);
        graphics.fill(rightX, 10, rightX + 1, 30, 0xFFD4AF37);
        graphics.fill(rightX + 59, 10, rightX + 60, 30, 0xFFD4AF37);
        
        graphics.drawString(mc.font, "⚡", rightX + 12, 16, 0xFFFF00);

        if (ClientSpeedData.isBulletTimeActive) {
            graphics.drawString(mc.font, "⌛", rightX + 42, 16, 0x00FFFF);
        }

        graphics.drawString(mc.font, String.valueOf(ClientSpeedData.speedLevel), rightX + 5, 38, 0xFFFFFF);

        double dx = player.getX() - player.xo;
        double dz = player.getZ() - player.zo;
        double speedBps = Math.sqrt(dx * dx + dz * dz) * 20.0;
        double speedKmh = speedBps * 3.6;

        int barWidth = 36;
        int barX = rightX + 20;
        int barY = 40;
        graphics.fill(barX, barY, barX + barWidth, barY + 4, 0xFF555555);

        double maxDisplaySpeed = 150.0; 
        float speedRatio = (float) Math.min(1.0, speedKmh / maxDisplaySpeed);
        int fillWidth = (int) (barWidth * speedRatio);
        
        if (fillWidth > 0) {
            graphics.fill(barX, barY, barX + fillWidth, barY + 4, 0xFFFF0000);
        }

        String kmhText = String.format("%.0f km/h", speedKmh);
        int textWidth = mc.font.width(kmhText);
        graphics.drawString(mc.font, kmhText, rightX + 60 - textWidth, 52, 0xFFFFFF);
    }

    private static boolean hasFullFlashSuit(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof FlashSuitArmorItem;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\command\SpeedForceCommand.java
// ============================================================================
package com.example.speedforce.command;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.network.ModNetworking;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SpeedForceCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("speedforce")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("grant")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    grantPower(player, 1);
                    context.getSource().sendSuccess(() -> Component.translatable("message.speedforce.granted"), false);
                    return 1;
                })
                .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        int level = IntegerArgumentType.getInteger(context, "level");
                        grantPower(player, level);
                        context.getSource().sendSuccess(() -> 
                            Component.translatable("message.speedforce.granted_level", level), false);
                        return 1;
                    }))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        ServerPlayer target = EntityArgument.getPlayer(context, "player");
                        grantPower(target, 1);
                        context.getSource().sendSuccess(() -> 
                            Component.translatable("message.speedforce.granted_to", target.getName().getString()), false);
                        return 1;
                    }))
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                        .executes(context -> {
                            ServerPlayer target = EntityArgument.getPlayer(context, "player");
                            int level = IntegerArgumentType.getInteger(context, "level");
                            grantPower(target, level);
                            context.getSource().sendSuccess(() -> 
                                Component.translatable("message.speedforce.granted_to_level", target.getName().getString(), level), false);
                            return 1;
                        }))))
            .then(Commands.literal("revoke")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    revokePower(player);
                    context.getSource().sendSuccess(() -> Component.translatable("message.speedforce.revoked"), false);
                    return 1;
                })
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        ServerPlayer target = EntityArgument.getPlayer(context, "player");
                        revokePower(target);
                        context.getSource().sendSuccess(() -> 
                            Component.translatable("message.speedforce.revoked_from", target.getName().getString()), false);
                        return 1;
                    })))
            .then(Commands.literal("info")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    var data = player.getData(ModAttachments.SPEED_PLAYER);
                    context.getSource().sendSuccess(() -> 
                        Component.translatable("message.speedforce.info", 
                            data.hasPower ? "Yes" : "No", 
                            data.speedLevel,
                            data.trailColorR, data.trailColorG, data.trailColorB), false);
                    return 1;
                }))
            .then(Commands.literal("color")
                .requires(source -> source.hasPermission(0))
                .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                    .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                int r = IntegerArgumentType.getInteger(context, "r");
                                int g = IntegerArgumentType.getInteger(context, "g");
                                int b = IntegerArgumentType.getInteger(context, "b");
                                SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                                if (!data.hasPower) {
                                    context.getSource().sendFailure(Component.translatable("message.speedforce.no_power"));
                                    return 0;
                                }
                                data.trailColorR = r;
                                data.trailColorG = g;
                                data.trailColorB = b;
                                player.setData(ModAttachments.SPEED_PLAYER, data);
                                ModNetworking.syncToClient(player);
                                context.getSource().sendSuccess(() -> 
                                    Component.translatable("message.speedforce.color_set", r, g, b), false);
                                return 1;
                            })))))
        );
    }

    private static void grantPower(ServerPlayer player, int level) {
        player.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(true, level));
        ModNetworking.syncToClient(player);
    }

    private static void revokePower(ServerPlayer player) {
        player.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(false, 0));
        ModNetworking.syncToClient(player);
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\entity\ModEntityTypes.java
// ============================================================================
package com.example.speedforce.entity;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(Registries.ENTITY_TYPE, SpeedForceMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<NormalArrowEntity>> NORMAL_ARROW =
        ENTITY_TYPES.register("normal_arrow", () ->
            EntityType.Builder.<NormalArrowEntity>of(NormalArrowEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(4)
                .updateInterval(20)
                .build(SpeedForceMod.MOD_ID + ":normal_arrow")
        );
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\entity\NormalArrowEntity.java
// ============================================================================
package com.example.speedforce.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NormalArrowEntity extends AbstractArrow {

    public NormalArrowEntity(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(this.getBaseDamage() * 2.0);
    }

    public NormalArrowEntity(Level level, LivingEntity shooter, ItemStack pickupItem, ItemStack weapon) {
        super(ModEntityTypes.NORMAL_ARROW.get(), shooter, level, pickupItem, weapon);
        this.setBaseDamage(this.getBaseDamage() * 2.0);
    }

    public NormalArrowEntity(Level level, double x, double y, double z, ItemStack pickupItem, ItemStack weapon) {
        super(ModEntityTypes.NORMAL_ARROW.get(), x, y, z, level, pickupItem, weapon);
        this.setBaseDamage(this.getBaseDamage() * 2.0);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(com.example.speedforce.item.ModItems.NORMAL_ARROW.get());
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\event\ModEvents.java
// ============================================================================
package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.network.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "speedforce")
public class ModEvents {

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.level().isClientSide && player.hasEffect(MobEffects.POISON)) {
                if (player.getRandom().nextFloat() < 0.3f) {
                    player.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(true, 1));
                    player.removeEffect(MobEffects.POISON);
                    if (player instanceof ServerPlayer serverPlayer) {
                        ModNetworking.syncToClient(serverPlayer);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ModNetworking.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ModNetworking.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ModNetworking.syncToClient(serverPlayer);
        }
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\event\PowerTickHandler.java
// ============================================================================
package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.item.FlashSuitArmorItem;
import com.example.speedforce.item.SuitType;
import com.example.speedforce.network.ModNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = "speedforce")
public class PowerTickHandler {

    private static final ResourceLocation SPEED_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("speedforce", "speed_boost");
    private static final ResourceLocation ATTACK_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("speedforce", "attack_boost");
    private static final ResourceLocation ATTACK_SPEED_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("speedforce", "attack_speed_boost");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower) {
            removeSpeedModifier(player);
            removeAttackModifier(player);
            removeAttackSpeedModifier(player);
            if (!player.isSpectator()) {
                player.noPhysics = false;
                player.setNoGravity(false);
            }
            if (data.isBulletTimeActive || data.isPhasing) {
                data.isBulletTimeActive = false;
                data.isPhasing = false;
                player.setData(ModAttachments.SPEED_PLAYER, data);
                if (player instanceof ServerPlayer serverPlayer) {
                    ModNetworking.syncToClient(serverPlayer);
                }
            }
            return;
        }

        SuitType wornSuit = getWornSuitType(player);
        handleSuitColorUpdate(player, data, wornSuit);
        
        int maxLevel = wornSuit != null ? 10 + wornSuit.getSpeedBonus() : 10;
        if (data.speedLevel > maxLevel) {
            data.speedLevel = maxLevel;
            player.setData(ModAttachments.SPEED_PLAYER, data);
        }

        applySpeedModifier(player, data);
        applyAttackModifier(player, data);
        applyAttackSpeedModifier(player, data);
        handleRegeneration(player, data);
        handleWaterWalk(player, data);
        handleWallRun(player, data);
        handleSpeedFire(player, data, wornSuit);
        handlePhasing(player, data);

        if (player.tickCount % 20 == 0 && player instanceof ServerPlayer serverPlayer) {
            ModNetworking.syncToClient(serverPlayer);
        }
    }

    private static void handleSuitColorUpdate(Player player, SpeedPlayerData data, SuitType suitType) {
        boolean colorChanged = false;
        if (suitType != null) {
            if (data.trailColorR != suitType.getTrailColorR()) {
                data.trailColorR = suitType.getTrailColorR();
                colorChanged = true;
            }
            if (data.trailColorG != suitType.getTrailColorG()) {
                data.trailColorG = suitType.getTrailColorG();
                colorChanged = true;
            }
            if (data.trailColorB != suitType.getTrailColorB()) {
                data.trailColorB = suitType.getTrailColorB();
                colorChanged = true;
            }
        } else {
            if (data.trailColorR != data.customTrailColorR) {
                data.trailColorR = data.customTrailColorR;
                colorChanged = true;
            }
            if (data.trailColorG != data.customTrailColorG) {
                data.trailColorG = data.customTrailColorG;
                colorChanged = true;
            }
            if (data.trailColorB != data.customTrailColorB) {
                data.trailColorB = data.customTrailColorB;
                colorChanged = true;
            }
        }
        if (colorChanged) {
            player.setData(ModAttachments.SPEED_PLAYER, data);
        }
    }

    private static SuitType getWornSuitType(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        if (helmet.getItem() instanceof FlashSuitArmorItem helmetItem &&
            chestplate.getItem() instanceof FlashSuitArmorItem chestplateItem &&
            leggings.getItem() instanceof FlashSuitArmorItem leggingsItem &&
            boots.getItem() instanceof FlashSuitArmorItem bootsItem) {
            
            SuitType type = helmetItem.getSuitType();
            if (chestplateItem.getSuitType() == type &&
                leggingsItem.getSuitType() == type &&
                bootsItem.getSuitType() == type) {
                return type;
            }
        }
        return null;
    }

    private static float lastTickRate = 20.0F;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        int maxBulletTimeLevel = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
            if (data.hasPower && data.speedLevel > 0 && data.isBulletTimeActive) {
                maxBulletTimeLevel = Math.max(maxBulletTimeLevel, data.speedLevel);
            }
        }

        float targetTickRate;
        if (maxBulletTimeLevel > 0) {
            targetTickRate = 20.0F / (3.0F + maxBulletTimeLevel);
            targetTickRate = Math.max(0.5F, targetTickRate);
        } else {
            targetTickRate = 20.0F;
        }

        if (lastTickRate != targetTickRate) {
            server.tickRateManager().setTickRate(targetTickRate);
            lastTickRate = targetTickRate;
        }
    }

    private static void applySpeedModifier(Player player, SpeedPlayerData data) {
        var instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null) {
            double amount = data.speedLevel * 0.3;
            if (data.isBulletTimeActive && data.speedLevel > 0) {
                amount *= (3.0 + data.speedLevel);
            }
            
            AttributeModifier modifier = new AttributeModifier(
                SPEED_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE
            );
            if (!instance.hasModifier(SPEED_MODIFIER_ID)) {
                instance.addTransientModifier(modifier);
            } else if (instance.getModifier(SPEED_MODIFIER_ID).amount() != amount) {
                instance.removeModifier(SPEED_MODIFIER_ID);
                instance.addTransientModifier(modifier);
            }
        }
    }

    private static void removeSpeedModifier(Player player) {
        var instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null && instance.hasModifier(SPEED_MODIFIER_ID)) {
            instance.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    private static void applyAttackModifier(Player player, SpeedPlayerData data) {
        if (data.speedLevel <= 0) {
            removeAttackModifier(player);
            return;
        }
        var instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null) {
            double amount = data.speedLevel * 0.5;
            AttributeModifier modifier = new AttributeModifier(
                ATTACK_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE
            );
            if (!instance.hasModifier(ATTACK_MODIFIER_ID)) {
                instance.addTransientModifier(modifier);
            } else if (instance.getModifier(ATTACK_MODIFIER_ID).amount() != amount) {
                instance.removeModifier(ATTACK_MODIFIER_ID);
                instance.addTransientModifier(modifier);
            }
        }
    }

    private static void removeAttackModifier(Player player) {
        var instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null && instance.hasModifier(ATTACK_MODIFIER_ID)) {
            instance.removeModifier(ATTACK_MODIFIER_ID);
        }
    }

    private static void applyAttackSpeedModifier(Player player, SpeedPlayerData data) {
        var instance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null) {
            double amount = 0;
            if (data.speedLevel > 0 && data.isBulletTimeActive) {
                amount = 4.0 * (2.0 + data.speedLevel);
            }
            
            if (amount > 0) {
                AttributeModifier modifier = new AttributeModifier(
                    ATTACK_SPEED_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE
                );
                if (!instance.hasModifier(ATTACK_SPEED_MODIFIER_ID)) {
                    instance.addTransientModifier(modifier);
                } else if (instance.getModifier(ATTACK_SPEED_MODIFIER_ID).amount() != amount) {
                    instance.removeModifier(ATTACK_SPEED_MODIFIER_ID);
                    instance.addTransientModifier(modifier);
                }
            } else {
                removeAttackSpeedModifier(player);
            }
        }
    }

    private static void removeAttackSpeedModifier(Player player) {
        var instance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null && instance.hasModifier(ATTACK_SPEED_MODIFIER_ID)) {
            instance.removeModifier(ATTACK_SPEED_MODIFIER_ID);
        }
    }

    private static void handleRegeneration(Player player, SpeedPlayerData data) {
        if (data.speedLevel > 0 && player.tickCount % 40 == 0 && player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0F + data.speedLevel * 0.2F);
        }
    }

    private static void handleWaterWalk(Player player, SpeedPlayerData data) {
        if (data.speedLevel >= 3 && player.isSprinting()) {
            BlockPos posBelow = player.blockPosition().below();
            if (player.level().getFluidState(posBelow).isEmpty() == false) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                player.fallDistance = 0.0F;
                player.setOnGround(true);
            }
        }
    }

    private static void handleWallRun(Player player, SpeedPlayerData data) {
        if (data.speedLevel >= 4 && player.isSprinting() && player.horizontalCollision) {
            player.setDeltaMovement(player.getDeltaMovement().x, 0.4, player.getDeltaMovement().z);
            player.fallDistance = 0.0F;
        }
    }

    private static void handleSpeedFire(Player player, SpeedPlayerData data, SuitType suitType) {
        if (data.speedLevel >= 5 && player.isSprinting()) {
            if (suitType == null) {
                player.setRemainingFireTicks(60);
            }
        }
    }

    private static void handlePhasing(Player player, SpeedPlayerData data) {
        if (data.isPhasing) {
            player.noPhysics = true;
            player.setNoGravity(true);
            player.fallDistance = 0.0F;
        } else if (!player.isSpectator()) {
            player.noPhysics = false;
            player.setNoGravity(false);
        }
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\item\FlashSuitArmorItem.java
// ============================================================================
package com.example.speedforce.item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FlashSuitArmorItem extends ArmorItem {

    private final SuitType suitType;

    public FlashSuitArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Properties properties, SuitType suitType) {
        super(material, type, properties);
        this.suitType = suitType;
    }

    public SuitType getSuitType() {
        return suitType;
    }

    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player)) return;

        if (this.suitType == SuitType.GREEN_ARROW && hasFullGreenArrowSet(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, false, false));
            if (player.hasEffect(MobEffects.POISON)) {
                player.removeEffect(MobEffects.POISON);
            }
        }

        if (this.getEquipmentSlot() == EquipmentSlot.FEET) {
            handleWaterWalk(player);
        }
    }

    private boolean hasFullGreenArrowSet(Player player) {
        return isGreenArrowItem(player.getItemBySlot(EquipmentSlot.HEAD)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.CHEST)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.LEGS)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.FEET));
    }

    private boolean isGreenArrowItem(ItemStack stack) {
        return stack.getItem() instanceof FlashSuitArmorItem armor && 
               armor.getSuitType() == SuitType.GREEN_ARROW;
    }

    private void handleWaterWalk(Player player) {
        if (player.isSprinting()) {
            var posBelow = player.blockPosition().below();
            var stateBelow = player.level().getBlockState(posBelow);
            if (stateBelow.is(net.minecraft.world.level.block.Blocks.WATER)) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                player.fallDistance = 0.0F;
                player.setOnGround(true);
            }
        }
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\item\GreenArrowBowItem.java
// ============================================================================
package com.example.speedforce.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GreenArrowBowItem extends BowItem {

    public GreenArrowBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 24000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            boolean hasInfinity = player.getAbilities().instabuild;
            
            ItemStack arrowStack = ItemStack.EMPTY;
            
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (invStack.getItem() == ModItems.NORMAL_ARROW.get()) {
                    arrowStack = invStack;
                    break;
                }
            }
            
            if (!hasInfinity && arrowStack.isEmpty()) {
                return;
            }
            
            if (arrowStack.isEmpty()) {
                arrowStack = new ItemStack(ModItems.NORMAL_ARROW.get());
            }
            
            ArrowItem arrowItem = (ArrowItem) arrowStack.getItem();
            AbstractArrow arrow = arrowItem.createArrow(level, arrowStack, player, stack);
            
            int charge = 20;
            
            float velocity = calculateArrowVelocity(charge) * 1.5F;
            
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 0.0F);
            arrow.setBaseDamage(arrow.getBaseDamage() * 3.0);
            
            if (velocity >= 1.0F) {
                arrow.setCritArrow(true);
            }
            
            if (hasInfinity) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            
            level.addFreshEntity(arrow);
            
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));
            
            if (!hasInfinity) {
                arrowStack.shrink(1);
                if (arrowStack.isEmpty()) {
                    player.getInventory().removeItem(arrowStack);
                }
            }
        }
    }

    private static float calculateArrowVelocity(int charge) {
        float f = charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\item\ModCreativeTabs.java
// ============================================================================
package com.example.speedforce.item;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpeedForceMod.MOD_ID);

    public static final Supplier<CreativeModeTab> SPEEDFORCE_TAB = CREATIVE_MODE_TABS.register("speedforce_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.speedforce_tab"))
            .icon(() -> new ItemStack(ModItems.FLASH_HELMET.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.FLASH_HELMET.get());
                output.accept(ModItems.FLASH_CHESTPLATE.get());
                output.accept(ModItems.FLASH_LEGGINGS.get());
                output.accept(ModItems.FLASH_BOOTS.get());
                
                output.accept(ModItems.REVERSE_FLASH_HELMET.get());
                output.accept(ModItems.REVERSE_FLASH_CHESTPLATE.get());
                output.accept(ModItems.REVERSE_FLASH_LEGGINGS.get());
                output.accept(ModItems.REVERSE_FLASH_BOOTS.get());
                
                output.accept(ModItems.ZOOM_HELMET.get());
                output.accept(ModItems.ZOOM_CHESTPLATE.get());
                output.accept(ModItems.ZOOM_LEGGINGS.get());
                output.accept(ModItems.ZOOM_BOOTS.get());
                
                output.accept(ModItems.FLASH_S4_HELMET.get());
                output.accept(ModItems.FLASH_S4_CHESTPLATE.get());
                output.accept(ModItems.FLASH_S4_LEGGINGS.get());
                output.accept(ModItems.FLASH_S4_BOOTS.get());
                
                output.accept(ModItems.FLASH_S5_HELMET.get());
                output.accept(ModItems.FLASH_S5_CHESTPLATE.get());
                output.accept(ModItems.FLASH_S5_LEGGINGS.get());
                output.accept(ModItems.FLASH_S5_BOOTS.get());
                
                output.accept(ModItems.KID_FLASH_HELMET.get());
                output.accept(ModItems.KID_FLASH_CHESTPLATE.get());
                output.accept(ModItems.KID_FLASH_LEGGINGS.get());
                output.accept(ModItems.KID_FLASH_BOOTS.get());
                
                output.accept(ModItems.GREEN_ARROW_HELMET.get());
                output.accept(ModItems.GREEN_ARROW_CHESTPLATE.get());
                output.accept(ModItems.GREEN_ARROW_LEGGINGS.get());
                output.accept(ModItems.GREEN_ARROW_BOOTS.get());
                output.accept(ModItems.GREEN_ARROW_BOW.get());
                output.accept(ModItems.NORMAL_ARROW.get());
                
                output.accept(ModItems.PARTICLE_ACCELERATOR.get());
            }).build());
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\item\ModItems.java
// ============================================================================
package com.example.speedforce.item;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpeedForceMod.MOD_ID);

    public static final DeferredItem<BlockItem> PARTICLE_ACCELERATOR = ITEMS.register("particle_accelerator",
        () -> new BlockItem(ModBlocks.PARTICLE_ACCELERATOR.get(), new net.minecraft.world.item.Item.Properties()));

    private static Holder<ArmorMaterial> createArmorMaterial(String name) {
        return Holder.direct(new ArmorMaterial(
            new EnumMap<>(ArmorItem.Type.class) {{
                put(ArmorItem.Type.HELMET, 2);
                put(ArmorItem.Type.CHESTPLATE, 5);
                put(ArmorItem.Type.LEGGINGS, 6);
                put(ArmorItem.Type.BOOTS, 2);
            }},
            12,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.of(net.minecraft.world.item.Items.LEATHER),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SpeedForceMod.MOD_ID, name))),
            0.0F,
            0.0F
        ));
    }

    public static final Holder<ArmorMaterial> FLASH_MATERIAL = createArmorMaterial("flash");
    public static final Holder<ArmorMaterial> REVERSE_FLASH_MATERIAL = createArmorMaterial("reverse_flash");
    public static final Holder<ArmorMaterial> ZOOM_MATERIAL = createArmorMaterial("zoom");
    public static final Holder<ArmorMaterial> FLASH_S4_MATERIAL = createArmorMaterial("flash_s4");
    public static final Holder<ArmorMaterial> FLASH_S5_MATERIAL = createArmorMaterial("flash_s5");
    public static final Holder<ArmorMaterial> KID_FLASH_MATERIAL = createArmorMaterial("kid_flash");
    public static final Holder<ArmorMaterial> GREEN_ARROW_MATERIAL = createArmorMaterial("green_arrow");

    public static final DeferredItem<FlashSuitArmorItem> FLASH_HELMET = ITEMS.register("flash_helmet",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_CHESTPLATE = ITEMS.register("flash_chestplate",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_LEGGINGS = ITEMS.register("flash_leggings",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_BOOTS = ITEMS.register("flash_boots",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_HELMET = ITEMS.register("reverse_flash_helmet",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_CHESTPLATE = ITEMS.register("reverse_flash_chestplate",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_LEGGINGS = ITEMS.register("reverse_flash_leggings",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_BOOTS = ITEMS.register("reverse_flash_boots",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_HELMET = ITEMS.register("zoom_helmet",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_CHESTPLATE = ITEMS.register("zoom_chestplate",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_LEGGINGS = ITEMS.register("zoom_leggings",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_BOOTS = ITEMS.register("zoom_boots",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_HELMET = ITEMS.register("flash_s4_helmet",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_CHESTPLATE = ITEMS.register("flash_s4_chestplate",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_LEGGINGS = ITEMS.register("flash_s4_leggings",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_BOOTS = ITEMS.register("flash_s4_boots",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_HELMET = ITEMS.register("flash_s5_helmet",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_CHESTPLATE = ITEMS.register("flash_s5_chestplate",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_LEGGINGS = ITEMS.register("flash_s5_leggings",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_BOOTS = ITEMS.register("flash_s5_boots",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_HELMET = ITEMS.register("kid_flash_helmet",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_CHESTPLATE = ITEMS.register("kid_flash_chestplate",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_LEGGINGS = ITEMS.register("kid_flash_leggings",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_BOOTS = ITEMS.register("kid_flash_boots",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_HELMET = ITEMS.register("green_arrow_helmet",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_CHESTPLATE = ITEMS.register("green_arrow_chestplate",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_LEGGINGS = ITEMS.register("green_arrow_leggings",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_BOOTS = ITEMS.register("green_arrow_boots",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<GreenArrowBowItem> GREEN_ARROW_BOW = ITEMS.register("green_arrow_bow",
        () -> new GreenArrowBowItem(new net.minecraft.world.item.Item.Properties().durability(384)));

    public static final DeferredItem<NormalArrowItem> NORMAL_ARROW = ITEMS.register("normal_arrow",
        () -> new NormalArrowItem(new net.minecraft.world.item.Item.Properties()));
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\item\NormalArrowItem.java
// ============================================================================
package com.example.speedforce.item;

import com.example.speedforce.entity.ModEntityTypes;
import com.example.speedforce.entity.NormalArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NormalArrowItem extends ArrowItem {

    public NormalArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammoStack, LivingEntity shooter, ItemStack weapon) {
        NormalArrowEntity arrow = new NormalArrowEntity(level, shooter, ammoStack, weapon);
        return arrow;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\item\SuitType.java
// ============================================================================
package com.example.speedforce.item;

public enum SuitType {
    FLASH("flash", 255, 210, 0, 4),
    REVERSE_FLASH("reverse_flash", 255, 0, 0, 5),
    ZOOM("zoom", 0, 150, 255, 6),
    FLASH_S4("flash_s4", 255, 210, 0, 4),
    FLASH_S5("flash_s5", 255, 210, 0, 4),
    KID_FLASH("kid_flash", 255, 200, 0, 4),
    GREEN_ARROW("green_arrow", 0, 180, 0, 0);

    private final String name;
    private final int trailColorR;
    private final int trailColorG;
    private final int trailColorB;
    private final int speedBonus;

    SuitType(String name, int trailColorR, int trailColorG, int trailColorB, int speedBonus) {
        this.name = name;
        this.trailColorR = trailColorR;
        this.trailColorG = trailColorG;
        this.trailColorB = trailColorB;
        this.speedBonus = speedBonus;
    }

    public String getName() {
        return name;
    }

    public int getTrailColorR() {
        return trailColorR;
    }

    public int getTrailColorG() {
        return trailColorG;
    }

    public int getTrailColorB() {
        return trailColorB;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\mixin\EntityMixin.java
// ============================================================================
package com.example.speedforce.mixin;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public boolean noPhysics;

    @Shadow
    public Level level;

    @Inject(method = "collide", at = @At("HEAD"), cancellable = true)
    private void onCollide(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof Player player) {
            SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
            if (data.isPhasing) {
                cir.setReturnValue(movement);
            }
        }
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\mixin\PlayerMixin.java
// ============================================================================
package com.example.speedforce.mixin;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\network\BulletTimePayload.java
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BulletTimePayload() implements CustomPacketPayload {
    public static final Type<BulletTimePayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "bullet_time_payload"));
    
    public static final StreamCodec<ByteBuf, BulletTimePayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new BulletTimePayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\network\ModNetworking.java
// ============================================================================
package com.example.speedforce.network;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.client.ClientSpeedData;
import com.example.speedforce.item.FlashSuitArmorItem;
import com.example.speedforce.item.SuitType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetworking {

    @SubscribeEvent
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(SpeedForceMod.MOD_ID);

        registrar.playToServer(TogglePowerPayload.TYPE, TogglePowerPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (!data.hasPower) return;
                    if (data.speedLevel > 0) {
                        data.speedLevel = 0;
                        data.isBulletTimeActive = false;
                        data.isPhasing = false;
                    } else {
                        data.speedLevel = 1;
                    }
                    player.setData(ModAttachments.SPEED_PLAYER, data);
                    syncToClient(player);
                }
            });
        });

        registrar.playToServer(SpeedLevelPayload.TYPE, SpeedLevelPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower) {
                        SuitType suitType = getWornSuitType(player);
                        int maxLevel = suitType != null ? 10 + suitType.getSpeedBonus() : 10;
                        if (payload.increase()) {
                            data.speedLevel = Math.min(maxLevel, data.speedLevel + 1);
                        } else {
                            data.speedLevel = Math.max(1, data.speedLevel - 1);
                        }
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToServer(BulletTimePayload.TYPE, BulletTimePayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower && data.speedLevel > 0) {
                        data.isBulletTimeActive = !data.isBulletTimeActive;
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToServer(PhasingPayload.TYPE, PhasingPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower) {
                        data.isPhasing = !data.isPhasing;
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToClient(SyncSpeedDataPayload.TYPE, SyncSpeedDataPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                ClientSpeedData.hasPower = payload.hasPower();
                ClientSpeedData.speedLevel = payload.speedLevel();
                ClientSpeedData.isBulletTimeActive = payload.isBulletTimeActive();
                ClientSpeedData.isPhasing = payload.isPhasing();
                ClientSpeedData.trailColorR = payload.trailColorR();
                ClientSpeedData.trailColorG = payload.trailColorG();
                ClientSpeedData.trailColorB = payload.trailColorB();
                ClientSpeedData.customTrailColorR = payload.customTrailColorR();
                ClientSpeedData.customTrailColorG = payload.customTrailColorG();
                ClientSpeedData.customTrailColorB = payload.customTrailColorB();

                if (context.player() != null) {
                    context.player().setData(ModAttachments.SPEED_PLAYER,
                        new SpeedPlayerData(payload.hasPower(), payload.speedLevel(), payload.isBulletTimeActive(), payload.isPhasing(), 
                                            payload.trailColorR(), payload.trailColorG(), payload.trailColorB(),
                                            payload.customTrailColorR(), payload.customTrailColorG(), payload.customTrailColorB())
                    );
                }
            });
        });

        registrar.playToServer(TrailColorPayload.TYPE, TrailColorPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower) {
                        SuitType suitType = getWornSuitType(player);
                        data.customTrailColorR = payload.r();
                        data.customTrailColorG = payload.g();
                        data.customTrailColorB = payload.b();
                        if (suitType == null) {
                            data.trailColorR = payload.r();
                            data.trailColorG = payload.g();
                            data.trailColorB = payload.b();
                        }
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });
    }

    public static void syncToClient(ServerPlayer player) {
        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
        PacketDistributor.sendToPlayer(player, new SyncSpeedDataPayload(data));
    }

    private static SuitType getWornSuitType(ServerPlayer player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        if (helmet.getItem() instanceof FlashSuitArmorItem helmetItem &&
            chestplate.getItem() instanceof FlashSuitArmorItem chestplateItem &&
            leggings.getItem() instanceof FlashSuitArmorItem leggingsItem &&
            boots.getItem() instanceof FlashSuitArmorItem bootsItem) {
            
            SuitType type = helmetItem.getSuitType();
            if (chestplateItem.getSuitType() == type &&
                leggingsItem.getSuitType() == type &&
                bootsItem.getSuitType() == type) {
                return type;
            }
        }
        return null;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\network\PhasingPayload.java
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PhasingPayload() implements CustomPacketPayload {
    public static final Type<PhasingPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "phasing_payload"));
    
    public static final StreamCodec<ByteBuf, PhasingPayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new PhasingPayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\network\SpeedLevelPayload.java
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpeedLevelPayload(boolean increase) implements CustomPacketPayload {
    public static final Type<SpeedLevelPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "speed_level_payload"));
    
    public static final StreamCodec<ByteBuf, SpeedLevelPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, SpeedLevelPayload::increase,
        SpeedLevelPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\network\SyncSpeedDataPayload.java
// ============================================================================
package com.example.speedforce.network;

import com.example.speedforce.capability.SpeedPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncSpeedDataPayload(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing, 
                                   int trailColorR, int trailColorG, int trailColorB,
                                   int customTrailColorR, int customTrailColorG, int customTrailColorB) 
    implements CustomPacketPayload {
    
    public static final Type<SyncSpeedDataPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "sync_speed_data"));
    
    public static final StreamCodec<ByteBuf, SyncSpeedDataPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeBoolean(payload.hasPower());
            buf.writeInt(payload.speedLevel());
            buf.writeBoolean(payload.isBulletTimeActive());
            buf.writeBoolean(payload.isPhasing());
            buf.writeInt(payload.trailColorR());
            buf.writeInt(payload.trailColorG());
            buf.writeInt(payload.trailColorB());
            buf.writeInt(payload.customTrailColorR());
            buf.writeInt(payload.customTrailColorG());
            buf.writeInt(payload.customTrailColorB());
        },
        buf -> new SyncSpeedDataPayload(
            buf.readBoolean(), buf.readInt(), buf.readBoolean(), buf.readBoolean(),
            buf.readInt(), buf.readInt(), buf.readInt(),
            buf.readInt(), buf.readInt(), buf.readInt()
        )
    );

    public SyncSpeedDataPayload(SpeedPlayerData data) {
        this(data.hasPower, data.speedLevel, data.isBulletTimeActive, data.isPhasing, 
             data.trailColorR, data.trailColorG, data.trailColorB,
             data.customTrailColorR, data.customTrailColorG, data.customTrailColorB);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\network\TogglePowerPayload.java
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TogglePowerPayload() implements CustomPacketPayload {
    public static final Type<TogglePowerPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "toggle_power_payload"));
    
    public static final StreamCodec<ByteBuf, TogglePowerPayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new TogglePowerPayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\network\TrailColorPayload.java
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TrailColorPayload(int r, int g, int b) implements CustomPacketPayload {
    public static final Type<TrailColorPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "trail_color_payload"));
    
    public static final StreamCodec<ByteBuf, TrailColorPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeInt(payload.r);
            buf.writeInt(payload.g);
            buf.writeInt(payload.b);
        },
        buf -> new TrailColorPayload(buf.readInt(), buf.readInt(), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\particle\ModParticles.java
// ============================================================================
package com.example.speedforce.particle;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
        DeferredRegister.create(Registries.PARTICLE_TYPE, SpeedForceMod.MOD_ID);

    public static final Supplier<SimpleParticleType> YELLOW_FLASH =
        PARTICLE_TYPES.register("yellow_flash", () -> new SimpleParticleType(true));
}

// ============================================================================
// D:\open code project\神速力mod\src\main\java\com\example\speedforce\SpeedForceMod.java
// ============================================================================
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

