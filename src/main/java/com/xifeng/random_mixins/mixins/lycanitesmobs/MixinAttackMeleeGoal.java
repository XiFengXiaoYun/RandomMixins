package com.xifeng.random_mixins.mixins.lycanitesmobs;

import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttackMeleeGoal.class)
public abstract class MixinAttackMeleeGoal {

    @Shadow(remap = false)
    private EntityLivingBase attackTarget;

    @Inject(method = "updateTask", at = @At("HEAD"), cancellable = true)
    private void check(CallbackInfo ci) {
        if(this.attackTarget == null || !this.attackTarget.isEntityAlive()) ci.cancel();
    }
}
