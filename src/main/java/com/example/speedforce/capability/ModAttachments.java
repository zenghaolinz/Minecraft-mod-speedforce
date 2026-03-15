package com.example.speedforce.capability;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "speedforce");

    public static final Supplier<AttachmentType<SpeedPlayerData>> SPEED_PLAYER = 
        ATTACHMENT_TYPES.register("speed_player",
            () -> AttachmentType.builder(() -> new SpeedPlayerData())
                .serialize(SpeedPlayerData.CODEC)
                .copyOnDeath()
                .build());
}