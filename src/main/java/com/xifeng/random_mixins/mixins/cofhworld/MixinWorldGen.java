package com.xifeng.random_mixins.mixins.cofhworld;

import cofh.cofhworld.util.random.WeightedBlock;
import cofh.cofhworld.world.generator.WorldGen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = WorldGen.class,remap = false)
public class MixinWorldGen {

    /**
     * @author 1
     * @reason 2
     */
    @Overwrite
    public static boolean setBlock(World world, BlockPos pos, WeightedBlock ore) {
        if (ore == null) return false;

        Chunk chunk = world.getChunk(pos);

        IBlockState oldState = chunk.setBlockState(pos, ore.getState());

        if (oldState != null) {
            if (ore.block.hasTileEntity(ore.getState())) {
                TileEntity tile = world.getTileEntity(pos);
                if (tile != null) {
                    NBTTagCompound nbt = new NBTTagCompound();
                    tile.writeToNBT(nbt);
                    tile.readFromNBT(ore.getData(nbt));
                }
            }
            return true;
        }
        return false;
    }
}
