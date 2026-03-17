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

    private static final int BRANCH_COUNT = 12;
    private static final float BRANCH_LENGTH_MIN = 1.5f;
    private static final float BRANCH_LENGTH_MAX = 2.5f;
    private static final int SEGMENTS_PER_BRANCH = 4;
    private static final int R = 255;
    private static final int G = 210;
    private static final int B = 0;

    private static final Random random = new Random();

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        if (!ClientRewindData.isRewinding()) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        float pt = mc.getTimer().getGameTimeDeltaTicks();
        double px = Mth.lerp(pt, player.xOld, player.getX());
        double py = Mth.lerp(pt, player.yOld, player.getY());
        double pz = Mth.lerp(pt, player.zOld, player.getZ());
        Vec3 playerPos = new Vec3(px, py, pz);

        Vec3 origin = playerPos.add(0, 1.0, 0);

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

        float gameTime = player.tickCount + pt;
        random.setSeed((long) (gameTime * 100));

        for (int i = 0; i < BRANCH_COUNT; i++) {
            float baseAngle = (float) (i * 2 * Math.PI / BRANCH_COUNT);
            float angleJitter = (random.nextFloat() - 0.5f) * 0.2f;
            float angle = baseAngle + angleJitter;

            float branchLength = BRANCH_LENGTH_MIN + random.nextFloat() * (BRANCH_LENGTH_MAX - BRANCH_LENGTH_MIN);
            
            drawLightningBranch(builder, pose, origin, cameraPos, angle, branchLength, gameTime, i);
        }

        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void drawLightningBranch(BufferBuilder builder, Matrix4f pose, Vec3 origin, Vec3 cameraPos, 
                                             float angle, float length, float gameTime, int branchIndex) {
        Vec3 prevPoint = origin;
        
        for (int seg = 0; seg < SEGMENTS_PER_BRANCH; seg++) {
            float segmentLength = length / SEGMENTS_PER_BRANCH;
            
            float jitterX = (random.nextFloat() - 0.5f) * 0.3f;
            float jitterY = (random.nextFloat() - 0.5f) * 0.2f;
            float jitterZ = (random.nextFloat() - 0.5f) * 0.3f;
            
            float dx = (float) Math.cos(angle) * segmentLength + jitterX;
            float dy = jitterY + (random.nextFloat() - 0.5f) * 0.1f;
            float dz = (float) Math.sin(angle) * segmentLength + jitterZ;
            
            Vec3 currentPoint = prevPoint.add(dx, dy, dz);
            
            float segmentRatio = (float) (seg + 1) / SEGMENTS_PER_BRANCH;
            int alpha = (int) (200 * (1.0f - segmentRatio * 0.5f));
            
            float width = 0.04f * (1.0f - segmentRatio * 0.3f);
            
            drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width, R, G, B, alpha);
            drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width * 0.3f, 255, 255, 255, alpha);
            
            prevPoint = currentPoint;
        }
    }

    private static void drawLightningSegment(BufferBuilder builder, Matrix4f pose, Vec3 start, Vec3 end, 
                                              Vec3 cameraPos, float width, int r, int g, int b, int a) {
        Vec3 dir = end.subtract(start);
        if (dir.lengthSqr() < 1e-7) return;

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