package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RemnantStatePayload(boolean hasRemnant, int remainingSeconds) implements CustomPacketPayload {
    public static final Type<RemnantStatePayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "remnant_state"));
    
    public static final StreamCodec<ByteBuf, RemnantStatePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, RemnantStatePayload::hasRemnant,
        ByteBufCodecs.VAR_INT, RemnantStatePayload::remainingSeconds,
        RemnantStatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}