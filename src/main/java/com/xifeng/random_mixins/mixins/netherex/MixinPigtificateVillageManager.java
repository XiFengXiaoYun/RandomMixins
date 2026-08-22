package com.xifeng.random_mixins.mixins.netherex;

import logictechcorp.netherex.village.PigtificateVillageData;
import logictechcorp.netherex.village.PigtificateVillageManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(value = PigtificateVillageManager.class, remap = false)
public class MixinPigtificateVillageManager {
    @Final
    @Shadow
    private Map<Integer, PigtificateVillageData> pigtificateVillageData;

    @Unique
    private final Set<Integer> randomMixins$attemptedLoads = new HashSet<>();

    @Inject(method = "getVillageData", at = @At("HEAD"), cancellable = true)
    private void onGetVillageData(World world, boolean createData, CallbackInfoReturnable<PigtificateVillageData> cir) {
        int dim = world.provider.getDimension();

        if(!pigtificateVillageData.containsKey(dim) && randomMixins$attemptedLoads.contains(dim) && !createData) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "getVillageData", at = @At("RETURN"))
    private void afterGetVillageData(World world, boolean createData, CallbackInfoReturnable<PigtificateVillageData> cir) {
        randomMixins$attemptedLoads.add(world.provider.getDimension());
    }

    @Inject(method = "unloadVillageData", at = @At("HEAD"))
    private void onUnload(int dimensionId, CallbackInfo ci) {
        randomMixins$attemptedLoads.remove(dimensionId);
    }
}
