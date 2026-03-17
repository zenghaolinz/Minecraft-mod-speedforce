package com.example.speedforce.client.render;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.client.model.QuiverModel;
import com.example.speedforce.item.QuiverItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class QuiverLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation QUIVER_TEXTURE = ResourceLocation.fromNamespaceAndPath(SpeedForceMod.MOD_ID, "textures/entity/quiver_dark_archer.png");
    private final QuiverModel quiverModel;

    public QuiverLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, QuiverModel model) {
        super(renderer);
        this.quiverModel = model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack quiver = findQuiver(player);
        if (quiver.isEmpty()) return;

        poseStack.pushPose();
        this.getParentModel().body.translateAndRotate(poseStack);

        poseStack.translate(0.25D, 0.05D, 0.25D);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(QUIVER_TEXTURE));
        this.quiverModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
    }

    private ItemStack findQuiver(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        if (mainHand.getItem() instanceof QuiverItem || offHand.getItem() instanceof QuiverItem) {
            return ItemStack.EMPTY;
        }
        
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof QuiverItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}