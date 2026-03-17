package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TimeRemnantPayload(boolean summon) implements CustomPacketPayload {
    public static final Type<TimeRemnantPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "time_remnant"));
    
    public static final StreamCodec<ByteBuf, TimeRemnantPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, TimeRemnantPayload::summon,
        TimeRemnantPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}