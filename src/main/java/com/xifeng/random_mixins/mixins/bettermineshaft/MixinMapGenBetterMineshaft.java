package com.xifeng.random_mixins.mixins.bettermineshaft;

import com.yungnickyoung.minecraft.bettermineshafts.config.Configuration;
import com.yungnickyoung.minecraft.bettermineshafts.world.MapGenBetterMineshaft;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.common.BiomeDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(MapGenBetterMineshaft.class)
public class MixinMapGenBetterMineshaft extends MapGenMineshaft {

    /**
     * @author xifeng
     * @reason Avoid cascading worldgen and save performance in OTG world
     */
    @Overwrite
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        //The same as vanilla mineshaft gen
        long worldSeed = this.world.getSeed();
        Random chunkRand = this.world.rand;
        chunkRand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L + worldSeed);

        if (chunkRand.nextDouble() >= Configuration.mineshaftSpawnRate) {
            return false;
        }

        BlockPos pos = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);
        Biome biome = this.world.getBiomeProvider().getBiome(pos, Biomes.PLAINS);

        return !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN) &&
                !BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH);
    }
}
