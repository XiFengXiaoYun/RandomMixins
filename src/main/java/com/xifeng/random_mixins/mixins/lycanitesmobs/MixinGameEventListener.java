package com.xifeng.random_mixins.mixins.lycanitesmobs;

import com.lycanitesmobs.GameEventListener;
import net.minecraftforge.event.world.WorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameEventListener.class, remap = false)
public abstract class MixinGameEventListener {
    @Inject(
            method = "onWorldLoading",
            remap = false,
            at = @At("HEAD"),
            cancellable = true)
    private void mixinWorldLoading(WorldEvent.Load event, CallbackInfo ci) {
        if(event.getWorld().isRemote) {
            ci.cancel();
        }
    }
}
