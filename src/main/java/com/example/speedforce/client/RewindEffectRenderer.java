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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.Random;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class RewindEffectRenderer {

    private static final int LAYER_COUNT = 3;
    private static final int LIGHTNINGS_PER_LAYER = 4;
    private static final float[] LAYER_HEIGHTS = {0.5f, 1.0f, 1.4f};
    private static final float ORBIT_RADIUS = 0.7f;
    private static final int SEGMENTS_PER_LIGHTNING = 3;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        if (!ClientRewindData.isRewinding()) return;
        
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();
        float pt = mc.getTimer().getGameTimeDeltaTicks();
        float gameTime = player.tickCount + pt;
        
        double px = Mth.lerp(pt, player.xOld, player.getX());
        double py = Mth.lerp(pt, player.yOld, player.getY());
        double pz = Mth.lerp(pt, player.zOld, player.getZ());
        Vec3 playerPos = new Vec3(px, py, pz);
        
        int r = ClientSpeedData.trailColorR;
        int g = ClientSpeedData.trailColorG;
        int b = ClientSpeedData.trailColorB;

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

        Random random = new Random();
        
        for (int layer = 0; layer < LAYER_COUNT; layer++) {
            float height = LAYER_HEIGHTS[layer];
            float rotationOffset = layer * (float)(Math.PI / LAYER_COUNT);
            
            for (int i = 0; i < LIGHTNINGS_PER_LAYER; i++) {
                float baseAngle = (float)(i * 2 * Math.PI / LIGHTNINGS_PER_LAYER);
                float currentAngle = baseAngle + gameTime * 0.3f + rotationOffset;
                
                Vec3 prevPoint = null;
                
                for (int seg = 0; seg <= SEGMENTS_PER_LIGHTNING; seg++) {
                    float segmentAngle = currentAngle + seg * (float)(Math.PI / 6);
                    float segHeight = height + seg * 0.05f;
                    float segRadius = ORBIT_RADIUS + (seg % 2 == 0 ? 0.05f : -0.05f);
                    
                    double x = Math.cos(segmentAngle) * segRadius;
                    double z = Math.sin(segmentAngle) * segRadius;
                    
                    random.setSeed((long)(gameTime * 100 + layer * 1000 + i * 10000 + seg));
                    double jitterX = (random.nextDouble() - 0.5) * 0.15;
                    double jitterY = (random.nextDouble() - 0.5) * 0.1;
                    double jitterZ = (random.nextDouble() - 0.5) * 0.15;
                    
                    Vec3 currentPoint = playerPos.add(x + jitterX, segHeight + jitterY, z + jitterZ);
                    
                    if (prevPoint != null) {
                        float segRatio = (float) seg / SEGMENTS_PER_LIGHTNING;
                        int alpha = (int)(200 * (1.0f - segRatio * 0.3f));
                        float width = 0.04f * (1.0f - segRatio * 0.2f);
                        
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width, r, g, b, alpha);
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width * 0.3f, 255, 255, 255, alpha);
                    }
                    
                    prevPoint = currentPoint;
                }
            }
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