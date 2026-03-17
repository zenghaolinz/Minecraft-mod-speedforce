package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CancelRewindPayload() implements CustomPacketPayload {
    public static final Type<CancelRewindPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "cancel_rewind"));
    
    public static final StreamCodec<ByteBuf, CancelRewindPayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new CancelRewindPayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}