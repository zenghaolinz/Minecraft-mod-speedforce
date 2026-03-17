package com.example.speedforce.capability;

import net.minecraft.world.phys.Vec3;

public record RewindState(Vec3 pos, float yRot, float xRot, float health) {}