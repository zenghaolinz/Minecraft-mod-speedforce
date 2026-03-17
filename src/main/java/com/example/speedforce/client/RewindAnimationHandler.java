package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class RewindAnimationHandler {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (!ClientRewindData.isRewinding()) return;
        
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        if (player != Minecraft.getInstance().player) return;
        
        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        
        float gameTime = player.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        float animSpeed = 0.6f;
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