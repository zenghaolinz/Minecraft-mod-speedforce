package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TrailColorPayload(int r, int g, int b) implements CustomPacketPayload {
    public static final Type<TrailColorPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "trail_color_payload"));
    
    public static final StreamCodec<ByteBuf, TrailColorPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeInt(payload.r);
            buf.writeInt(payload.g);
            buf.writeInt(payload.b);
        },
        buf -> new TrailColorPayload(buf.readInt(), buf.readInt(), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}