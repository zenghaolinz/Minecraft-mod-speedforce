package com.example.speedforce.util;

/**
 * Runtime-only phasing state cache.
 *
 * Collision and movement packet validation are hot paths. They should not read
 * the persisted Attachment each time; instead they read this boolean cache from
 * the Player mixin.
 */
public interface PhasingStateAccess {
    boolean speedforce$isPhasingActive();
    void speedforce$setPhasingActive(boolean active);
}
