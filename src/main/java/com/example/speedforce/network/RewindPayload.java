package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RewindPayload(boolean startRewind) implements CustomPacketPayload {
    public static final Type<RewindPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "rewind_payload"));
    
    public static final StreamCodec<ByteBuf, RewindPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, RewindPayload::startRewind,
        RewindPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}