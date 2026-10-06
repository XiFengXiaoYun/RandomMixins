package com.xifeng.random_mixins.mixins.otg;

import com.pg85.otg.generator.noise.NoiseGeneratorPerlin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = NoiseGeneratorPerlin.class, remap = false)
public abstract class MixinNoiseGeneratorPerlin {
    @Shadow
    private int[] permutations;
    @Shadow
    private double xCoord;
    @Shadow
    private double yCoord;
    @Shadow
    private double zCoord;

    @Shadow
    private double lerp(double d, double d1, double d2) {
        return 0;
    }

    @Unique
    private static final int[][] GRAD_3D = {
            { 1,  1,  0}, {-1,  1,  0}, { 1, -1,  0}, {-1, -1,  0},
            { 1,  0,  1}, {-1,  0,  1}, { 1,  0, -1}, {-1,  0, -1},
            { 0,  1,  1}, { 0, -1,  1}, { 0,  1, -1}, { 0, -1, -1},
            { 1,  1,  0}, { 1, -1,  0}, {-1,  1,  0}, {-1, -1,  0}
    };

    @Unique
    private static final int[][] GRAD_2D = {
            { 1,  0}, {-1,  0}, { 1,  0}, {-1,  0},
            { 1,  1}, {-1,  1}, { 1, -1}, {-1, -1},
            { 0,  1}, { 0,  1}, { 0, -1}, { 0, -1},
            { 1,  0}, { 1,  0}, {-1,  0}, {-1,  0}
    };

    @Unique
    private static double randomMixins$fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    @Unique
    private static double randomMixins$grad3D(int i, double x, double y, double z) {
        int[] g = GRAD_3D[i & 0xf];
        return g[0] * x + g[1] * y + g[2] * z;
    }

    @Unique
    private static double randomMixins$grad2D(int i, double x, double z) {
        int[] g = GRAD_2D[i & 0xf];
        return g[0] * x + g[1] * z;
    }

    /**
     * @author 1
     * @reason 2
     */
    @Overwrite
    void populateNoiseArray3D(double[] NoiseArray, double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double noiseScale) {
        int i1 = 0;
        double d7 = 1.0D / noiseScale;
        int i2 = -1;
        double d13 = 0.0D, d15 = 0.0D, d16 = 0.0D, d18 = 0.0D;

        final double xc = xCoord;
        final double yc = yCoord;
        final double zc = zCoord;

        for (int i5 = 0; i5 < xSize; i5++) {
            double d20 = xOffset + (double) i5 * xScale + xc;
            int k5 = (int) d20;
            if (d20 < (double) k5) { k5--; }
            int i6 = k5 & 0xff;
            d20 -= k5;
            double d22 = randomMixins$fade(d20);

            final int p_i6    = permutations[i6];
            final int p_i6_1  = permutations[i6 + 1];

            for (int j6 = 0; j6 < zSize; j6++) {
                double d24 = zOffset + (double) j6 * zScale + zc;
                int k6 = (int) d24;
                if (d24 < (double) k6) { k6--; }
                int l6 = k6 & 0xff;
                d24 -= k6;
                double d25 = randomMixins$fade(d24);

                for (int i7 = 0; i7 < ySize; i7++) {
                    double d26 = yOffset + (double) i7 * yScale + yc;
                    int j7 = (int) d26;
                    if (d26 < (double) j7) { j7--; }
                    int k7 = j7 & 0xff;
                    d26 -= j7;
                    double d27 = randomMixins$fade(d26);

                    if (i7 == 0 || k7 != i2) {
                        i2 = k7;
                        int j2 = p_i6 + k7;
                        int k2 = permutations[j2] + l6;
                        int l2 = permutations[j2 + 1] + l6;
                        int i3 = p_i6_1 + k7;
                        int k3 = permutations[i3] + l6;
                        int l3 = permutations[i3 + 1] + l6;

                        d13 = lerp(d22, randomMixins$grad3D(permutations[k2], d20, d26, d24), randomMixins$grad3D(permutations[k3],d20 - 1.0D, d26, d24));
                        d15 = lerp(d22, randomMixins$grad3D(permutations[l2], d20,d26 - 1.0D, d24), randomMixins$grad3D(permutations[l3],d20 - 1.0D,d26 - 1.0D, d24));
                        d16 = lerp(d22, randomMixins$grad3D(permutations[k2 + 1], d20, d26,d24 - 1.0D), randomMixins$grad3D(permutations[k3 + 1], d20 - 1.0D, d26,d24 - 1.0D));
                        d18 = lerp(d22, randomMixins$grad3D(permutations[l2 + 1], d20,d26 - 1.0D, d24 - 1.0D), randomMixins$grad3D(permutations[l3 + 1], d20 - 1.0D, d26 - 1.0D, d24 - 1.0D));
                    }
                    double d28 = lerp(d27, d13, d15);
                    double d29 = lerp(d27, d16, d18);
                    double d30 = lerp(d25, d28, d29);
                    NoiseArray[i1++] += d30 * d7;
                }
            }
        }
    }

    /**
     * @author 1
     * @reason 2
     */
    @Overwrite
    void populateNoiseArray2D(double[] NoiseArray, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double noiseScale) {
        int j3 = 0;
        double d12 = 1.0D / noiseScale;

        final double xc = xCoord;
        final double zc = zCoord;

        for (int i4 = 0; i4 < xSize; i4++) {
            double d14 = xOffset + (double) i4 * xScale + xc;
            int j4 = (int) d14;
            if (d14 < (double) j4) { j4--; }
            int k4 = j4 & 0xff;
            d14 -= j4;
            double d17 = randomMixins$fade(d14);

            final int p_k4   = permutations[k4];
            final int p_k4_1 = permutations[k4 + 1];

            for (int l4 = 0; l4 < zSize; l4++) {
                double d19 = zOffset + (double) l4 * zScale + zc;
                int j5 = (int) d19;
                if (d19 < (double) j5) { j5--; }
                int l5 = j5 & 0xff;
                d19 -= j5;
                double d21 = randomMixins$fade(d19);

                int j1 = permutations[p_k4]   + l5;
                int l1 = permutations[p_k4_1] + l5;

                double d9  = lerp(d17, randomMixins$grad2D(permutations[j1], d14, d19), randomMixins$grad2D(permutations[l1], d14 - 1.0D, d19));
                double d11 = lerp(d17, randomMixins$grad2D(permutations[j1 + 1], d14,d19 - 1.0D), randomMixins$grad2D(permutations[l1 + 1], d14 - 1.0D, d19 - 1.0D));
                double d23 = lerp(d21, d9, d11);
                NoiseArray[j3++] += d23 * d12;
            }
        }
    }
}
