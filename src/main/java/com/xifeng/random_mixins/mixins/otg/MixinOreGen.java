package com.xifeng.random_mixins.mixins.otg;

import com.pg85.otg.generator.resource.OreGen;
import com.xifeng.random_mixins.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OreGen.class, remap = false)
public abstract class MixinOreGen {

    /**
     * @author xifeng
     * @reason disable the otg ore gen system
     */
    @Inject(method = "spawn", at = @At("HEAD"), cancellable = true)
    private void disableOreGen(CallbackInfo ci) {
        if(ModConfig.OTG.disableOreGen) ci.cancel();
    }
}
