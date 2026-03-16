package com.example.speedforce.client.renderer;

import com.example.speedforce.entity.NormalArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class NormalArrowRenderer extends ArrowRenderer<NormalArrowEntity> {
    
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("speedforce", "textures/entity/projectiles/normal_arrow.png");

    public NormalArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(NormalArrowEntity entity) {
        return TEXTURE;
    }
}