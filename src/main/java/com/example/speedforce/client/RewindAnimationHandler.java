package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import java.lang.reflect.Field;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class RewindAnimationHandler {

    private static Field positionField;
    private static Field speedField;
    
    static {
        try {
            positionField = net.minecraft.world.entity.WalkAnimationState.class.getDeclaredField("position");
            positionField.setAccessible(true);
            speedField = net.minecraft.world.entity.WalkAnimationState.class.getDeclaredField("speed");
            speedField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player != Minecraft.getInstance().player) return;
        if (!ClientRewindData.isRewinding()) return;

        float partialTicks = event.getPartialTick();
        float fakePosition = (player.tickCount + partialTicks) * 0.6f;
        
        try {
            if (positionField != null && speedField != null) {
                positionField.setFloat(player.walkAnimation, fakePosition);
                speedField.setFloat(player.walkAnimation, 1.0f);
            }
        } catch (IllegalAccessException ignored) {}
        
        @SuppressWarnings("unchecked")
        PlayerModel<Player> model = (PlayerModel<Player>) event.getRenderer().getModel();
        
        float gameTime = player.tickCount + partialTicks;
        float animSpeed = 15.0f;
        float legAmplitude = 1.2f;
        float armAmplitude = 0.8f;
        
        float cycle = Mth.sin(gameTime * animSpeed) * legAmplitude;

        model.leftLeg.xRot = cycle;
        model.rightLeg.xRot = -cycle;
        
        model.leftArm.xRot = -cycle * armAmplitude / legAmplitude;
        model.rightArm.xRot = cycle * armAmplitude / legAmplitude;
        
        model.leftLeg.yRot = 0;
        model.rightLeg.yRot = 0;
        model.leftLeg.zRot = 0;
        model.rightLeg.zRot = 0;
        
        model.leftArm.yRot = 0;
        model.rightArm.yRot = 0;
        model.leftArm.zRot = Mth.sin(gameTime * animSpeed * 0.5f) * 0.1f;
        model.rightArm.zRot = -Mth.sin(gameTime * animSpeed * 0.5f) * 0.1f;
        
        model.body.yRot = Mth.sin(gameTime * animSpeed * 0.3f) * 0.05f;
    }
}