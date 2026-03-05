package com.xifeng.random_mixins.mixins.netherex;

import com.xifeng.random_mixins.util.BiomeCache;
import logictechcorp.netherex.NetherExConfig;
import logictechcorp.netherex.entity.passive.EntityPigtificate;
import logictechcorp.netherex.handler.LivingHandler;
import logictechcorp.netherex.init.NetherExBiomes;
import logictechcorp.netherex.init.NetherExMobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = LivingHandler.class, remap = false)
public abstract class MixinLivingHandler {
    /**
     * @author xifeng
     * @reason for performance
     */
    @SubscribeEvent
    @Overwrite
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        World world = event.getEntityLiving().getEntityWorld();
        EntityLivingBase entity = event.getEntityLiving();
        // Here we limit all the function of the original handler only works in nether
        if (world.provider.getDimension() != -1) return;
        if(world.isRemote) return;
        if(entity.ticksExisted % 5 !=0 ) return;
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isCreative() || player.isSpectator()) return;
        }
        if (entity.isPotionActive(NetherExMobEffects.FROZEN)) return;

        long cacheKey = (long) entity.getEntityWorld().provider.getDimension() << 32
                | (long) (entity.getPosition().getX() >> 4) << 16
                | (entity.getPosition().getZ() >> 4);
        Biome biome = BiomeCache.get(cacheKey);

        if (biome == NetherExBiomes.ARCTIC_ABYSS && world.rand.nextInt(NetherExConfig.biome.arcticAbyss.mobFreezeRarity * 5) == 0) {
            int duration = entity instanceof EntityPlayer ? 100 : 300;
            entity.addPotionEffect(new PotionEffect(NetherExMobEffects.FROZEN, duration, 0));
        }
        randomAddon$handlePigZombieConversion(entity, world);
    }

    @Unique
    private static void randomAddon$handlePigZombieConversion(EntityLivingBase entity, World world) {
        if(entity instanceof EntityPigZombie) {
            EntityPigZombie pigZombie = (EntityPigZombie) entity;
            NBTTagCompound data = pigZombie.getEntityData();

            if (!data.hasKey("ConversionTime")) return;

            int conversionTime = data.getInteger("ConversionTime");

            if (entity.ticksExisted % 100 == 0 && world.rand.nextFloat() < 0.3F) {
                int boosters = randomAddon$countIronBarsOptimized(world, pigZombie.getPosition());
                conversionTime -= Math.min(boosters, 14);
            } else {
                conversionTime -= 1;
            }

            if (conversionTime <= 0) {
                randomAddon$convertPigZombieToPigtificate(pigZombie, world);
            } else {
                data.setInteger("ConversionTime", conversionTime);
            }
        }
    }

    @Unique
    private static void randomAddon$convertPigZombieToPigtificate(EntityPigZombie pigZombie, World world) {
        EntityPigtificate pigtificate = new EntityPigtificate(world);
        pigtificate.copyLocationAndAnglesFrom(pigZombie);
        pigtificate.onInitialSpawn(world.getDifficultyForLocation(pigtificate.getPosition()), null);
        pigtificate.setLookingForHome();

        if(pigZombie.isChild())
        {
            pigtificate.setGrowingAge(-24000);
        }

        world.removeEntity(pigZombie);
        pigtificate.setNoAI(pigZombie.isAIDisabled());

        if(pigZombie.hasCustomName())
        {
            pigtificate.setCustomNameTag(pigZombie.getCustomNameTag());
            pigtificate.setAlwaysRenderNameTag(pigZombie.getAlwaysRenderNameTag());
        }

        world.spawnEntity(pigtificate);
        pigtificate.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
        world.playEvent(null, 1027, new BlockPos((int) pigZombie.posX, (int) pigZombie.posY, (int) pigZombie.posZ), 0);
    }

    @Unique
    private static int randomAddon$countIronBarsOptimized(World world, BlockPos center) {
        int count = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        if (world.getBlockState(center).getBlock() != Blocks.IRON_BARS) {
            return randomAddon$scanIronBarsSpiral(world, center, mutable);
        }

        for (int radius = 0; radius < 4 && count < 14; radius++) {
            for (int dx = -radius; dx <= radius && count < 14; dx++) {
                for (int dy = -radius; dy <= radius && count < 14; dy++) {
                    for (int dz = -radius; dz <= radius && count < 14; dz++) {
                        if (Math.abs(dx) != radius && Math.abs(dy) != radius && Math.abs(dz) != radius)
                            continue;

                        mutable.setPos(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                        if (world.getBlockState(mutable).getBlock() == Blocks.IRON_BARS) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    @Unique
    private static int randomAddon$scanIronBarsSpiral(World world, BlockPos center, BlockPos.MutableBlockPos mutable) {
        int count = 0;
        int[][] directions = {{1,0,0}, {-1,0,0}, {0,1,0}, {0,-1,0}, {0,0,1}, {0,0,-1}};

        for (int dist = 1; dist < 4 && count < 14; dist++) {
            for (int[] dir : directions) {
                mutable.setPos(
                        center.getX() + dir[0] * dist,
                        center.getY() + dir[1] * dist,
                        center.getZ() + dir[2] * dist
                );
                if (world.getBlockState(mutable).getBlock() == Blocks.IRON_BARS) {
                    count++;
                    if (count >= 14) break;
                }
            }
        }
        return count;
    }
}
