package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RewindStatePayload(
    int phase,
    int framesRewound,
    int rewindSpeed,
    int confirmTimeRemaining,
    int totalHistorySize
) implements CustomPacketPayload {
    public static final Type<RewindStatePayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "rewind_state"));
    
    public static final StreamCodec<ByteBuf, RewindStatePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, RewindStatePayload::phase,
        ByteBufCodecs.VAR_INT, RewindStatePayload::framesRewound,
        ByteBufCodecs.VAR_INT, RewindStatePayload::rewindSpeed,
        ByteBufCodecs.VAR_INT, RewindStatePayload::confirmTimeRemaining,
        ByteBufCodecs.VAR_INT, RewindStatePayload::totalHistorySize,
        RewindStatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}