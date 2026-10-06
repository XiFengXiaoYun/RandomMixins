package com.xifeng.random_mixins.mixins.cofhworld;

import cofh.cofhworld.data.numbers.INumberProvider;
import cofh.cofhworld.util.random.WeightedBlock;
import cofh.cofhworld.world.generator.WorldGen;
import cofh.cofhworld.world.generator.WorldGenMinableCluster;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.*;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(WorldGenMinableCluster.class)
public class MixinWorldGenMinableCluster extends WorldGen {

    @Shadow(remap = false)
    @Final
    private List<WeightedBlock> cluster;
    @Shadow(remap = false)
    @Final
    private INumberProvider genClusterSize;
    @Shadow(remap = false)
    @Final
    private  WeightedBlock[] genBlock;

    //use cache to reduce the BlockMatcher.forBlock() call
    @Unique
    private static final Map<Block, BlockMatcher> randomMixins$matcherCache = new IdentityHashMap<>();

    /**
     * @author xifeng
     * @reason using BlockPos.PooledMutableBlockPos and inline setBlock to reduce BlockPos allocation
     */
    @Overwrite
    public boolean generate(World world, Random rand, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        Chunk chunkCached = world.getChunk(pos);
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        int blocks = MathHelper.clamp(genClusterSize.intValue(world, rand, new INumberProvider.DataHolder(pos)), 1, 42);
        if (blocks < 4) {
            return generateTiny(world, rand, blocks, x, y, z);
        }

        float f = rand.nextFloat() * (float) Math.PI;
        float xMin = x + (MathHelper.sin(f) * blocks) / 8F;
        float xMax = x - (MathHelper.sin(f) * blocks) / 8F;
        float zMin = z + (MathHelper.cos(f) * blocks) / 8F;
        float zMax = z - (MathHelper.cos(f) * blocks) / 8F;
        float yMin = (y + rand.nextInt(3)) - 2;
        float yMax = (y + rand.nextInt(3)) - 2;

        xMax -= xMin;
        yMax -= yMin;
        zMax -= zMin;

        boolean r = false;
        final WeightedBlock[] genBlockLocal = this.genBlock;
        final List<WeightedBlock> clusterLocal = this.cluster;
        final float invBlocks = 1.0F / blocks;

        BlockPos.PooledMutableBlockPos mutablePos = BlockPos.PooledMutableBlockPos.retain();

        try {
            for (int i = 0; i <= blocks; i++) {
                float xCenter = xMin + (xMax * i) * invBlocks;
                float yCenter = yMin + (yMax * i) * invBlocks;
                float zCenter = zMin + (zMax * i) * invBlocks;

                float size = ((float) rand.nextDouble() * blocks) / 16F;

                float sinVal = MathHelper.sin((i * (float) Math.PI) / blocks);
                float mod = ((sinVal + 1F) * size + 1F) * 0.5F;
                float invMod = 1.0F / mod;

                int xStart = MathHelper.floor(xCenter - mod);
                int yStart = MathHelper.floor(yCenter - mod);
                int zStart = MathHelper.floor(zCenter - mod);
                int xStop = MathHelper.floor(xCenter + mod);
                int yStop = MathHelper.floor(yCenter + mod);
                int zStop = MathHelper.floor(zCenter + mod);

                for (int blockX = xStart; blockX <= xStop; blockX++) {
                    float xDist = ((blockX + 0.5F) - xCenter) * invMod;
                    float xDistSq = xDist * xDist;
                    if (xDistSq >= 1F) continue;

                    for (int blockY = yStart; blockY <= yStop; blockY++) {
                        float yDist = ((blockY + 0.5F) - yCenter) * invMod;
                        float yDistSq = yDist * yDist;
                        float xyDistSq = yDistSq + xDistSq;
                        if (xyDistSq >= 1F) continue;

                        for (int blockZ = zStart; blockZ <= zStop; blockZ++) {
                            float zDist = ((blockZ + 0.5F) - zCenter) * invMod;
                            float zDistSq = zDist * zDist;
                            if (zDistSq + xyDistSq >= 1F) continue;

                            int currentChunkX = blockX >> 4;
                            int currentChunkZ = blockZ >> 4;
                            if (currentChunkX != chunkX || currentChunkZ != chunkZ) {
                                chunkCached = world.getChunk(currentChunkX, currentChunkZ);
                                chunkX = currentChunkX;
                                chunkZ = currentChunkZ;
                            }

                            mutablePos.setPos(blockX, blockY, blockZ);

                            if (randomMixins$canGenerateInBlock(world, mutablePos, genBlockLocal, chunkCached)) {
                                WeightedBlock ore = selectBlock(rand, clusterLocal);
                                if (randomMixins$setBlock(world, mutablePos, ore, chunkCached)) {
                                    r = true;
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            mutablePos.release();
        }
        return r;
    }

    /**
     * @author 1
     * @reason 2
     */
    @Overwrite(remap = false)
    public boolean generateTiny(World world, Random random, int clusterSize, int x, int y, int z) {
        boolean r = false;
        BlockPos.PooledMutableBlockPos mutablePos = BlockPos.PooledMutableBlockPos.retain();
        try {
            mutablePos.setPos(x, y, z);
            if (WorldGen.canGenerateInBlock(world, mutablePos, genBlock)) {
                WeightedBlock ore = selectBlock(random, cluster);
                if (WorldGen.setBlock(world, mutablePos, ore)) {
                    r = true;
                }
            }
            for (int i = 1; i < clusterSize; i++) {
                mutablePos.setPos(x + random.nextInt(2), y + random.nextInt(2), z + random.nextInt(2));
                if (WorldGen.canGenerateInBlock(world, mutablePos, genBlock)) {
                    WeightedBlock ore = selectBlock(random, cluster);
                    if (WorldGen.setBlock(world, mutablePos, ore)) {
                        r = true;
                    }
                }
            }
        } finally {
            mutablePos.release();
        }
        return r;
    }

    @Unique
    private static boolean randomMixins$canGenerateInBlock(World world, BlockPos pos, WeightedBlock[] mat, Chunk chunk) {
        if (mat == null || mat.length == 0) return true;

        IBlockState state = chunk.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);

        for (WeightedBlock genBlock : mat) {
            if(block == genBlock.block) return true;
            if ((genBlock.metadata == -1 || genBlock.metadata == meta) && block.isReplaceableOreGen(state, world, pos, randomMixins$get(genBlock.block))) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean randomMixins$setBlock(World world, BlockPos pos, WeightedBlock ore, Chunk chunk) {
        if (ore == null) return false;

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

    @Unique
    private static BlockMatcher randomMixins$get(Block block) {
        BlockMatcher matcher = randomMixins$matcherCache.get(block);
        if (matcher != null) {
            return matcher;
        }
        matcher = BlockMatcher.forBlock(block);
        randomMixins$matcherCache.put(block, matcher);
        return matcher;
    }
}
