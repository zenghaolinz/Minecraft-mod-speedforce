package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TogglePowerPayload() implements CustomPacketPayload {
    public static final Type<TogglePowerPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "toggle_power_payload"));
    
    public static final StreamCodec<ByteBuf, TogglePowerPayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new TogglePowerPayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}