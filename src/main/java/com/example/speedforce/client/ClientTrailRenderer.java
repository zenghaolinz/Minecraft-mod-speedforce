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
        public final float yRot;
        public final boolean isMoving;

        public TrailPoint(Vec3 pos, Vec3[] jitters, float yRot, boolean isMoving) {
            this.pos = pos;
            this.jitters = jitters;
            this.yRot = yRot;
            this.isMoving = isMoving;
        }
    }

    private static final Deque<TrailPoint> history = new ArrayDeque<>();
    private static final int MAX_AGE = 15;
    private static long lastTickCount = -1;
    
    private static final float[] BRANCH_OFFSETS_X = {-0.1f, 0.1f, -0.1f, 0.1f, 0.0f};
    private static final float[] BRANCH_OFFSETS_Y = {0.2f, 0.6f, 1.0f, 1.4f, 1.7f};

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
            double spread = isMoving ? 1.0 : 0.5;
            
            if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0) {
                Vec3[] jitters = new Vec3[5];
                for (int i = 0; i < 5; i++) {
                    jitters[i] = new Vec3(
                        (Math.random() - 0.5) * 0.5 * spread, 
                        (Math.random() - 0.5) * 0.2 * spread, 
                        (Math.random() - 0.5) * 0.5 * spread
                    );
                }
                history.addFirst(new TrailPoint(player.position(), jitters, player.getYRot(), isMoving));
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
        Player player = mc.player;
        if (player == null) return;

        if (mc.options.getCameraType() == net.minecraft.client.CameraType.FIRST_PERSON) return;

        synchronized (history) {
            if (history.size() < 2) return;

            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = event.getCamera().getPosition();

            float pt = mc.getTimer().getGameTimeDeltaTicks();
            double px = Mth.lerp(pt, player.xOld, player.getX());
            double py = Mth.lerp(pt, player.yOld, player.getY());
            double pz = Mth.lerp(pt, player.zOld, player.getZ());
            Vec3 playerRenderPos = new Vec3(px, py, pz);

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

            int r = ClientSpeedData.trailColorR;
            int g = ClientSpeedData.trailColorG;
            int b = ClientSpeedData.trailColorB;

            for (int branch = 0; branch < 5; branch++) {
                Vec3 prevPoint = null;
                int age = 0;

                for (TrailPoint tp : history) {
                    float ageRatio = (float) age / MAX_AGE;
                    float alpha = 1.0F - (ageRatio * ageRatio);
                    int a = (int) (alpha * 200);
                    if (a <= 5) {
                        age++;
                        continue;
                    }

                    Vec3 jitter = tp.jitters[branch];
                    float branchOffsetX = BRANCH_OFFSETS_X[branch];
                    float branchOffsetY = BRANCH_OFFSETS_Y[branch];
                    
                    float rad = (float) Math.toRadians(-tp.yRot);
                    float cos = (float) Math.cos(rad);
                    float sin = (float) Math.sin(rad);

                    double spreadFactor = tp.isMoving ? (1.0 + ageRatio * 1.5) : 1.0;
                    double localX = (branchOffsetX + (age == 0 ? 0 : jitter.x * (1 - ageRatio * 0.6))) * spreadFactor;
                    double localZ = (age == 0 ? 0 : jitter.z * (1 - ageRatio * 0.6)) * spreadFactor;

                    double globalOffsetX = localX * cos - localZ * sin;
                    double globalOffsetZ = localX * sin + localZ * cos;

                    Vec3 basePos = (age == 0) ? playerRenderPos : tp.pos;
                    Vec3 currentPoint = basePos.add(globalOffsetX, branchOffsetY + jitter.y, globalOffsetZ);

                    if (prevPoint != null) {
                        float width = 0.05f * (1 - ageRatio * 0.4f);
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width, r, g, b, (int)(a * 0.8));
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width * 0.3f, 255, 255, 255, a);
                    }

                    prevPoint = currentPoint;
                    age++;
                }
            }

            BufferUploader.drawWithShader(builder.buildOrThrow());

            RenderSystem.depthMask(true);
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