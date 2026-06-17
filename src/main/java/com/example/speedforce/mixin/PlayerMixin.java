package com.example.speedforce.mixin;

import com.example.speedforce.util.PhasingStateAccess;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin implements PhasingStateAccess {

    @Unique
    private boolean speedforce$phasingActive;

    @Override
    public boolean speedforce$isPhasingActive() {
        return this.speedforce$phasingActive;
    }

    @Override
    public void speedforce$setPhasingActive(boolean active) {
        this.speedforce$phasingActive = active;
    }
}
