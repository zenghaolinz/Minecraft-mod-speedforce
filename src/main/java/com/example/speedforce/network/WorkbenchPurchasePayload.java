package com.example.speedforce.network;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.entity.SpeedForceWorkbenchBlockEntity;
import com.example.speedforce.item.SuitType;
import com.example.speedforce.menu.SpeedForceWorkbenchMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WorkbenchPurchasePayload(SuitType suitType, int pieceIndex) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<WorkbenchPurchasePayload> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SpeedForceMod.MOD_ID, "workbench_purchase"));

    public static final StreamCodec<FriendlyByteBuf, WorkbenchPurchasePayload> STREAM_CODEC = StreamCodec.of(
        WorkbenchPurchasePayload::encode,
        WorkbenchPurchasePayload::decode
    );

    private static void encode(FriendlyByteBuf buf, WorkbenchPurchasePayload msg) {
        buf.writeUtf(msg.suitType().getName());
        buf.writeInt(msg.pieceIndex());
    }

    private static WorkbenchPurchasePayload decode(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        int pieceIndex = buf.readInt();
        SuitType type = SuitType.FLASH;
        for (SuitType t : SuitType.values()) {
            if (t.getName().equals(name)) {
                type = t;
                break;
            }
        }
        return new WorkbenchPurchasePayload(type, pieceIndex);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WorkbenchPurchasePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                AbstractContainerMenu container = player.containerMenu;
                if (container instanceof SpeedForceWorkbenchMenu menu) {
                    if (payload.pieceIndex() >= 0 && payload.pieceIndex() <= 3) {
                        menu.purchasePiece(payload.suitType(), payload.pieceIndex());
                    } else {
                        menu.purchaseSuit(payload.suitType());
                    }
                }
            }
        });
    }
}