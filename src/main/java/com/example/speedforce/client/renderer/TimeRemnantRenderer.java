package com.example.speedforce.client.renderer;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.entity.TimeRemnantEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class TimeRemnantRenderer extends MobRenderer<TimeRemnantEntity, PlayerModel<TimeRemnantEntity>> {

    private static final Map<UUID, ResourceLocation> SKIN_CACHE = new ConcurrentHashMap<>();
    
    public TimeRemnantRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
            new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            context.getModelManager()
        ));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(TimeRemnantEntity entity) {
        UUID ownerUUID = entity.getOwnerUUID();
        if (ownerUUID == null) {
            return DefaultPlayerSkin.getDefaultTexture();
        }
        
        ResourceLocation cachedSkin = SKIN_CACHE.get(ownerUUID);
        if (cachedSkin != null) {
            return cachedSkin;
        }
        
        Player owner = entity.getOwner();
        if (owner instanceof AbstractClientPlayer clientPlayer) {
            ResourceLocation skinLocation = clientPlayer.getSkin().texture();
            SKIN_CACHE.put(ownerUUID, skinLocation);
            return skinLocation;
        }
        
        return DefaultPlayerSkin.getDefaultTexture();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.cameraEntity == null) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        AABB searchBox = mc.cameraEntity.getBoundingBox().inflate(128.0D);
        List<TimeRemnantEntity> remnants = mc.level.getEntitiesOfClass(TimeRemnantEntity.class, searchBox);
        
        for (TimeRemnantEntity remnant : remnants) {
            if (remnant.shouldRenderTrail()) {
                renderRemnantTrail(remnant, poseStack, cameraPos);
            }
        }
    }

    private static void renderRemnantTrail(TimeRemnantEntity entity, PoseStack poseStack, Vec3 cameraPos) {
        List<Vec3> trailHistory = entity.getTrailHistory();
        if (trailHistory.size() < 2) return;

        int r = entity.getTrailColorR();
        int g = entity.getTrailColorG();
        int b = entity.getTrailColorB();

        poseStack.pushPose();
        Matrix4f pose = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float[] yOffsets = {0.1f, 0.5f, 0.9f, 1.3f, 1.7f};
        float[] BRANCH_OFFSETS_X = {-0.4f, -0.2f, 0.0f, 0.2f, 0.4f};
        Random random = new Random();
        boolean drewAnything = false;
        int maxAge = trailHistory.size();

        for (int branch = 0; branch < 5; branch++) {
            Vec3 prevPoint = null;
            int age = 0;

            for (Vec3 point : trailHistory) {
                float ageRatio = (float) age / maxAge;
                float alpha = 1.0F - (ageRatio * ageRatio);
                int a = (int)(alpha * 180);
                if (a <= 5) {
                    age++;
                    continue;
                }

                random.setSeed(entity.getId() * 1000L + age * 10L + branch);
                double jitterX = (random.nextDouble() - 0.5) * 0.8;
                double jitterY = (random.nextDouble() - 0.5) * 0.3;
                double jitterZ = (random.nextDouble() - 0.5) * 0.8;

                float branchOffsetX = BRANCH_OFFSETS_X[branch];
                double offsetX = branchOffsetX + (age == 0 ? 0 : jitterX);

                Vec3 currentPoint = point.add(offsetX, yOffsets[branch] + jitterY, jitterZ);

                if (prevPoint != null) {
                    float width = 0.04f * (1 - ageRatio * 0.4f);
                    drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width, r, g, b, (int)(a * 0.8));
                    drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width * 0.3f, 255, 255, 255, a);
                    drewAnything = true;
                }

                prevPoint = currentPoint;
                age++;
            }
        }

        if (!drewAnything) {
            builder.addVertex(pose, 0, 0, 0).setColor(0, 0, 0, 0);
            builder.addVertex(pose, 0, 0, 0).setColor(0, 0, 0, 0);
            builder.addVertex(pose, 0, 0, 0).setColor(0, 0, 0, 0);
            builder.addVertex(pose, 0, 0, 0).setColor(0, 0, 0, 0);
        }

        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void drawLightningSegment(BufferBuilder builder, Matrix4f pose, Vec3 start, Vec3 end, 
                                       Vec3 cameraPos, float width, int r, int g, int b, int a) {
        Vec3 dir = end.subtract(start);
        if (dir.lengthSqr() < 1e-7) return;

        Vec3 normal = dir.cross(cameraPos.subtract(start)).normalize().scale(width);

        float sx = (float)(start.x - cameraPos.x);
        float sy = (float)(start.y - cameraPos.y);
        float sz = (float)(start.z - cameraPos.z);

        float ex = (float)(end.x - cameraPos.x);
        float ey = (float)(end.y - cameraPos.y);
        float ez = (float)(end.z - cameraPos.z);

        float nx = (float)normal.x, ny = (float)normal.y, nz = (float)normal.z;

        builder.addVertex(pose, sx + nx, sy + ny, sz + nz).setColor(r, g, b, a);
        builder.addVertex(pose, sx - nx, sy - ny, sz - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex - nx, ey - ny, ez - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex + nx, ey + ny, ez + nz).setColor(r, g, b, a);
    }
}