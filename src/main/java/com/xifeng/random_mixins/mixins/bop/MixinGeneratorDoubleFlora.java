package com.xifeng.random_mixins.mixins.bop;

import biomesoplenty.api.block.BOPBlocks;
import biomesoplenty.api.block.IBlockPosQuery;
import biomesoplenty.api.config.IConfigObj;
import biomesoplenty.common.block.BlockBOPDecoration;
import biomesoplenty.common.block.BlockBOPDoubleDecoration;
import biomesoplenty.common.block.BlockBOPDoublePlant;
import biomesoplenty.common.util.biome.GeneratorUtils;
import biomesoplenty.common.world.generator.GeneratorDoubleFlora;
import biomesoplenty.common.world.generator.GeneratorReplacing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;
import java.util.Random;

@Mixin(GeneratorDoubleFlora.class)
public class MixinGeneratorDoubleFlora extends GeneratorReplacing {
    @Shadow(remap = false)
    protected int generationAttempts;

    @Shadow(remap = false)
    protected IBlockState withTop;

    protected MixinGeneratorDoubleFlora(float amountPerChunk, IBlockPosQuery placeOn, IBlockPosQuery replace, IBlockState with, GeneratorUtils.ScatterYMethod scatterYMethod) {
        super(amountPerChunk, placeOn, replace, with, scatterYMethod);
    }


    /**
     * @author xifeng
     * @reason optimize the grass check, may affect the generation slightly
     */
    @Overwrite
    public boolean generate(@Nonnull World world, @Nonnull Random random, @Nonnull BlockPos pos) {
        Block bottomBlock = this.with.getBlock();
        for (int i = 0; i < this.generationAttempts; ++i) {
            BlockPos genPos = pos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            if (this.with == BOPBlocks.double_plant.getDefaultState().withProperty(BlockBOPDoublePlant.VARIANT, BlockBOPDoublePlant.DoublePlantType.SEA_OATS).withProperty(BlockBOPDoublePlant.HALF, BlockBOPDoubleDecoration.Half.LOWER)) {
                boolean grassCheck = false;
                //reduce the search range for performance
                for (int x = -2; x <= 2; x++) {
                    for (int y = 1; y < 2; y ++) {
                        for (int z = -2; z <= 2; z++) {
                            if (world.getBlockState(genPos.down().add(x, y, z)).getBlock() instanceof BlockGrass) {
                                grassCheck = true;
                                break;
                            }
                        }
                    }
                }
                if (!grassCheck) { return false; }
            }

            if (this.placeOn.matches(world, genPos.down()) && this.replace.matches(world, genPos) && this.replace.matches(world, genPos.up()) && genPos.getY() < 254) {
                boolean canStay;
                if (bottomBlock instanceof BlockBOPDecoration) {
                    canStay = ((BlockBOPDecoration)bottomBlock).canBlockStay(world, genPos, this.with);
                } else if (bottomBlock instanceof BlockBush) {
                    canStay = bottomBlock.canPlaceBlockAt(world, genPos);
                } else {
                    canStay = bottomBlock.canPlaceBlockAt(world, genPos);
                }
                if (canStay) {
                    world.setBlockState(genPos, this.with, 2);
                    world.setBlockState(genPos.up(), this.withTop, 2);
                }
            }
        }
        return true;
    }

    @Override
    public void configure(IConfigObj iConfigObj) {

    }
}
