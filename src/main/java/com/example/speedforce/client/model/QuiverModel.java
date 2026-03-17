package com.example.speedforce.client.model;

import com.example.speedforce.SpeedForceMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class QuiverModel extends EntityModel<Player> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(SpeedForceMod.MOD_ID, "quiver_layer"), "main");

    private final ModelPart quiver;

    public QuiverModel(ModelPart root) {
        this.quiver = root.getChild("quiver");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition quiver = partdefinition.addOrReplaceChild("quiver", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, -2.0F));

        PartDefinition pad = quiver.addOrReplaceChild("pad", CubeListBuilder.create()
            .texOffs(0, 26).addBox(-0.1F, -0.1F, 0.1F, 5.0F, 5.0F, 1.0F), 
            PartPose.offset(-4.3F, -0.1F, 1.5F));

        pad.addOrReplaceChild("strap5", CubeListBuilder.create()
            .texOffs(13, 27).addBox(0.0F, 0.0F, 0.0F, 5.0F, 1.0F, 4.0F), 
            PartPose.offsetAndRotation(0.0F, 3.9F, -3.8F, 0.0F, (float)Math.PI / 90F, -0.12217305F));

        PartDefinition baseRight = pad.addOrReplaceChild("baseRight", CubeListBuilder.create()
            .texOffs(0, 15).addBox(-1.5F, 0.0F, 0.5F, 1.0F, 9.0F, 2.0F), 
            PartPose.offsetAndRotation(1.8F, 0.9F, 0.7F, 0.0F, 0.0F, -0.43633232F));

        baseRight.addOrReplaceChild("baseBack", CubeListBuilder.create()
            .texOffs(6, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 9.0F, 1.0F), 
            PartPose.offset(-0.8F, 0.0F, 2.2F));

        baseRight.addOrReplaceChild("baseStrap", CubeListBuilder.create()
            .texOffs(0, 12).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F), 
            PartPose.offset(-2.0F, 1.0F, 0.0F));

        baseRight.addOrReplaceChild("baseStrap_1", CubeListBuilder.create()
            .texOffs(0, 12).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F), 
            PartPose.offset(1.3F, 1.0F, 0.0F));

        baseRight.addOrReplaceChild("baseLeft", CubeListBuilder.create()
            .texOffs(0, 15).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 9.0F, 2.0F), 
            PartPose.offset(0.9F, 0.0F, 0.5F));

        PartDefinition baseFront = baseRight.addOrReplaceChild("baseFront", CubeListBuilder.create()
            .texOffs(12, 16).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 9.0F, 1.0F), 
            PartPose.offset(0.2F, 0.0F, -0.1F));

        baseFront.addOrReplaceChild("baseBottom", CubeListBuilder.create()
            .texOffs(0, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F), 
            PartPose.offset(-1.0F, 8.7F, 1.6F));

        PartDefinition strap1 = pad.addOrReplaceChild("strap1", CubeListBuilder.create()
            .texOffs(0, 6).addBox(-4.0F, 0.0F, -4.0F, 9.0F, 1.0F, 5.0F), 
            PartPose.offsetAndRotation(4.0F, 2.5F, 0.0F, 0.0F, 0.0F, 0.62831855F));

        strap1.addOrReplaceChild("strap2", CubeListBuilder.create()
            .texOffs(5, 0).addBox(0.0F, -1.0F, -4.0F, 1.0F, 1.0F, 5.0F), 
            PartPose.offsetAndRotation(5.0F, 1.0F, 0.0F, 0.0F, (float)Math.PI / 180F, -0.62831855F));

        PartDefinition strap3 = strap1.addOrReplaceChild("strap3", CubeListBuilder.create()
            .texOffs(18, 1).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F), 
            PartPose.offsetAndRotation(-4.0F, 1.0F, 0.1F, (float)Math.PI / 180F, (float)(-Math.PI) / 180F, 0.9599311F));

        strap3.addOrReplaceChild("shape4", CubeListBuilder.create()
            .texOffs(21, 4).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F), 
            PartPose.offset(0.0F, 0.0F, -4.3F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(Player entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        quiver.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}