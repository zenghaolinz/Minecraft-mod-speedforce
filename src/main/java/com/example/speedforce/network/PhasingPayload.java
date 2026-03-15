package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PhasingPayload() implements CustomPacketPayload {
    public static final Type<PhasingPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "phasing_payload"));
    
    public static final StreamCodec<ByteBuf, PhasingPayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new PhasingPayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}