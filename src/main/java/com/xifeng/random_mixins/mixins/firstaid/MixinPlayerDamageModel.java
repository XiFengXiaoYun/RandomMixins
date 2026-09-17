package com.xifeng.random_mixins.mixins.firstaid;

import com.xifeng.random_mixins.util.firstaid.PartScalingData;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.common.damagesystem.PlayerDamageModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(value = PlayerDamageModel.class, remap = false)
public abstract class MixinPlayerDamageModel {
    @Shadow
    private float prevScaleFactor;

    @Unique
    PlayerDamageModel randomMixins$model = (PlayerDamageModel) (Object) this;

    /**
     * @author xifeng
     * @reason try to fix the issue
     */
    @Overwrite
    public void runScaleLogic(EntityPlayer player) {
        if (!FirstAidConfig.scaleMaxHealth) return;

        player.world.profiler.startSection("healthscaling");
        float globalFactor = player.getMaxHealth() / 20F;

        if (Math.abs(globalFactor - prevScaleFactor) < 1e-6) {
            player.world.profiler.endSection();
            return;
        }

        if (FirstAidConfig.debug) {
            FirstAid.LOGGER.info("Starting health scaling factor {} -> {} (max health {})",
                    prevScaleFactor, globalFactor, player.getMaxHealth());
        }

        player.world.profiler.startSection("calculate");

        List<PartScalingData> scalingData = new ArrayList<>();
        float totalExpectedHealth = 0F;

        for (AbstractDamageablePart part : randomMixins$model) {
            float exactHealth = part.initialMaxHealth * globalFactor;
            int baseValue = (int) Math.floor(exactHealth);
            float remainder = exactHealth - baseValue;

            scalingData.add(new PartScalingData(part, baseValue, remainder));
            totalExpectedHealth += exactHealth;
        }

        int targetTotal = Math.round(totalExpectedHealth);
        int currentTotal = scalingData.stream().mapToInt(d -> d.baseValue).sum();
        int pointsToDistribute = targetTotal - currentTotal;

        scalingData.sort(Comparator.comparingDouble((PartScalingData d) -> d.remainder).reversed());

        for (PartScalingData data : scalingData) {
            if (pointsToDistribute > 0) {
                data.finalValue = data.baseValue + 1;
                pointsToDistribute--;
            } else if (pointsToDistribute < 0 && data.baseValue > 1) {
                data.finalValue = data.baseValue - 1;
                pointsToDistribute++;
            } else {
                data.finalValue = data.baseValue;
            }
        }

        player.world.profiler.endStartSection("apply");

        for (PartScalingData data : scalingData) {
            AbstractDamageablePart part = data.part;
            int oldMax = part.getMaxHealth();
            int newMax = data.finalValue;

            if (oldMax == newMax) continue;

            float oldCurrent = part.currentHealth;
            if (oldCurrent > newMax) {
                if (FirstAidConfig.debug) {
                    FirstAid.LOGGER.warn("Part {} health {} exceeds new max {}, scaling proportionally",
                            part.part.name(), oldCurrent, newMax);
                }
                float scaleRatio = (float) newMax / oldMax;
                part.currentHealth = oldCurrent * scaleRatio;
            }

            part.setMaxHealth(newMax);

            if (FirstAidConfig.debug) {
                FirstAid.LOGGER.info("Part {} max health: {} -> {} (current: {})",
                        part.part.name(), oldMax, newMax, part.currentHealth);
            }
        }

        if (!player.world.isRemote && player instanceof EntityPlayerMP) {
            randomMixins$model.scheduleResync();
        }

        prevScaleFactor = globalFactor;
        player.world.profiler.endSection();
    }

}
