package com.example.speedforce.network;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.item.QuiverItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CycleQuiverPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CycleQuiverPayload> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SpeedForceMod.MOD_ID, "cycle_quiver"));

    public static final StreamCodec<FriendlyByteBuf, CycleQuiverPayload> STREAM_CODEC = StreamCodec.of(
        CycleQuiverPayload::encode,
        CycleQuiverPayload::decode
    );

    private static void encode(FriendlyByteBuf buf, CycleQuiverPayload msg) {
    }

    private static CycleQuiverPayload decode(FriendlyByteBuf buf) {
        return new CycleQuiverPayload();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CycleQuiverPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack quiver = findQuiver(player);
                if (!quiver.isEmpty()) {
                    QuiverItem.cycleSelectedSlot(quiver);
                }
            }
        });
    }

    private static ItemStack findQuiver(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof QuiverItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}