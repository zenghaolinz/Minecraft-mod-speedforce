package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpeedLevelPayload(boolean increase) implements CustomPacketPayload {
    public static final Type<SpeedLevelPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "speed_level_payload"));
    
    public static final StreamCodec<ByteBuf, SpeedLevelPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, SpeedLevelPayload::increase,
        SpeedLevelPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}