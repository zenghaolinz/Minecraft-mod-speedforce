package com.example.speedforce.network;

import com.example.speedforce.capability.SpeedPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncSpeedDataPayload(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing, int trailColorR, int trailColorG, int trailColorB) 
    implements CustomPacketPayload {
    
    public static final Type<SyncSpeedDataPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "sync_speed_data"));
    
    public static final StreamCodec<ByteBuf, SyncSpeedDataPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeBoolean(payload.hasPower());
            buf.writeInt(payload.speedLevel());
            buf.writeBoolean(payload.isBulletTimeActive());
            buf.writeBoolean(payload.isPhasing());
            buf.writeInt(payload.trailColorR());
            buf.writeInt(payload.trailColorG());
            buf.writeInt(payload.trailColorB());
        },
        buf -> new SyncSpeedDataPayload(
            buf.readBoolean(), buf.readInt(), buf.readBoolean(), buf.readBoolean(),
            buf.readInt(), buf.readInt(), buf.readInt()
        )
    );

    public SyncSpeedDataPayload(SpeedPlayerData data) {
        this(data.hasPower, data.speedLevel, data.isBulletTimeActive, data.isPhasing, data.trailColorR, data.trailColorG, data.trailColorB);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}