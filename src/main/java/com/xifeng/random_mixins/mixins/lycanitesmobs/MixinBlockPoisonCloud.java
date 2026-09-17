package com.xifeng.random_mixins.mixins.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.block.effect.BlockPoisonCloud;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(BlockPoisonCloud.class)
public class MixinBlockPoisonCloud extends BlockBase {
    public MixinBlockPoisonCloud(Material material, ModInfo group, String name) {
        super(material, group, name);
    }

    /**
     * @author xifeng
     * @reason prevent endless poison cloud on mushroom island
     */
    @Overwrite(remap = false)
    public boolean canRemove(World world, BlockPos pos, IBlockState state, Random rand) {
        return super.canRemove(world, pos, state, rand);
    }
}
