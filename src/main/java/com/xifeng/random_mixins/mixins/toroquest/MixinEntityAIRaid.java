package com.xifeng.random_mixins.mixins.toroquest;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.torocraft.toroquest.entities.ai.EntityAIRaid;
import org.spongepowered.asm.mixin.*;

import java.util.Random;

@Mixin(value = EntityAIRaid.class)
public class MixinEntityAIRaid {
    @Shadow(remap = false)
    @Final
    public EntityCreature entity;

    @Shadow(remap = false)
    @Final
    private int minDistanceFromCenter;


    @Shadow(remap = false)
    private int centerX;
    @Shadow(remap = false)
    private int centerZ;

    @Shadow(remap = false)
    @Final
    private int moveDistance;

    @Shadow(remap = false)
    @Final
    private double movementSpeed;

    @Unique
    private final Random randomMixins$rand = new Random();

    /**
     * @author xifeng
     * @reason fix the bug of original method
     */
    @Overwrite(remap = false)
    public boolean inCorrectPosition() {
        double dx = this.entity.posX - this.centerX;
        double dz = this.entity.posZ - this.centerZ;
        double distSq = dx * dx + dz * dz;
        return distSq < (double) this.minDistanceFromCenter * this.minDistanceFromCenter;
    }

    /**
     * @author xifeng
     * @reason reduce the task update frequency for performance
     */
    @Overwrite
    public void updateTask() {
        if(entity.ticksExisted % 20 != 0) {
            return;
        }
        if((this.entity.getNavigator().noPath() && this.randomMixins$rand.nextInt(8) == 0) || this.randomMixins$rand.nextInt(32) == 0) {
            this.move(this.entity.world, this.entity.getPosition());
        }
    }

    /**
     * @author xifeng
     * @reason reduce nested pathfinding
     */
    @Overwrite(remap = false)
    private boolean move(World world, BlockPos start) {
        double dx = this.centerX - start.getX();
        double dz = this.centerZ - start.getZ();
        double xz = Math.abs(dx) + Math.abs(dz);

        if (xz < this.minDistanceFromCenter) {
            return false;
        }

        double targetX = dx / xz * this.moveDistance + start.getX() + (this.randomMixins$rand.nextInt(5) - 2);
        double targetZ = dz / xz * this.moveDistance + start.getZ() + (this.randomMixins$rand.nextInt(5) - 2);

        BlockPos moveTo = findValidSurface(world, new BlockPos(targetX, start.getY(), targetZ), 8);
        if (moveTo != null && randomMixins$tryMoveTo(moveTo)) {
            return true;
        }

        if (this.randomMixins$rand.nextBoolean()) {
            Vec3d targetVec = new Vec3d(targetX, this.entity.posY, targetZ);

            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(
                    this.entity, 20, 8, targetVec);

            if (vec3d == null) {
                vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(
                        this.entity, 16, 8, targetVec);
            }
            if (vec3d == null) {
                vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(
                        this.entity, 8, 8, targetVec);
            }

            if (vec3d != null) {
                return randomMixins$tryMoveTo(new BlockPos(vec3d));
            }
        }

        return false;
    }

    /**
     * @author xifeng
     * @reason reduce getBlockState calls
     */
    @Overwrite(remap = false)
    public static BlockPos findValidSurface( World world, BlockPos startPos, int yOffset ) {
        IBlockState state = world.getBlockState(startPos);
        if (randomMixins$isValidStandingPosition(world, startPos)) {
            return startPos;
        }
        BlockPos pos = startPos.up();
        for (int i = 0; i < yOffset; i++) {
            if (randomMixins$isLiquid(state)) return null;
            if (randomMixins$isValidStandingPosition(world, pos)) {
                return pos.down();
            }
            pos = pos.up();
            state = world.getBlockState(pos);
        }
        pos = startPos.down();
        state = world.getBlockState(pos);
        for (int i = 0; i < yOffset; i++) {
            if (randomMixins$isLiquid(state)) return null;
            if (randomMixins$isValidStandingPosition(world, pos)) {
                return pos;
            }
            pos = pos.down();
            state = world.getBlockState(pos);
        }

        return null;
    }

    @Unique
    private boolean randomMixins$tryMoveTo(BlockPos pos) {
        return this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), this.movementSpeed);
    }

    @Unique
    private static boolean randomMixins$isLiquid(IBlockState state) {
        return state.getBlock() instanceof BlockLiquid && state.getBlock().getDefaultState() != Blocks.WATER.getDefaultState();
    }

    @Unique
    private static boolean randomMixins$isValidStandingPosition(World world, BlockPos pos) {
        IBlockState feet = world.getBlockState(pos);
        IBlockState ground = world.getBlockState(pos.down());
        return !feet.getBlock().getDefaultState().isFullCube() && ground.getBlock().getDefaultState().isFullCube();
    }
}
