package com.xifeng.random_mixins.mixins.netherex;

import com.xifeng.random_mixins.util.BiomeCache;
import logictechcorp.libraryex.utility.CollectionHelper;
import logictechcorp.libraryex.utility.EntityHelper;
import logictechcorp.netherex.NetherExConfig;
import logictechcorp.netherex.init.NetherExBiomes;
import logictechcorp.netherex.mobeffect.MobEffectFrozen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = MobEffectFrozen.class, remap = false)
public abstract class MixinMobEffectFrozen {
    /**
     * @author xifeng
     * @reason for performance
     */
    @Overwrite
    public static boolean canFreeze(EntityLivingBase entity) {
        long cacheKey = (long) entity.getEntityWorld().provider.getDimension() << 32
                | (long) (entity.getPosition().getX() >> 4) << 16
                | (entity.getPosition().getZ() >> 4);

        Biome cachedBiome = BiomeCache.get(cacheKey);
        if (cachedBiome == null) {
            cachedBiome = entity.getEntityWorld().getBiome(entity.getPosition());
            BiomeCache.put(cacheKey, cachedBiome);
        }

        if(entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative()) {
            return cachedBiome != NetherExBiomes.ARCTIC_ABYSS || NetherExConfig.biome.arcticAbyss.canPlayersFreeze;
        }

        String entityRegistryName = EntityHelper.getEntityLocation(entity);
        return entityRegistryName != null && !CollectionHelper.contains(NetherExConfig.mobEffect.freeze.mobBlacklist, entityRegistryName);
    }
}
