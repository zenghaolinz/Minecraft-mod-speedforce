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