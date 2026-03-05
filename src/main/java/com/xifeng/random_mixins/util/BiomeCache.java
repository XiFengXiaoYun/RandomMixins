package com.xifeng.random_mixins.util;

import net.minecraft.world.biome.Biome;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BiomeCache {
    private static final int MAX_SIZE = 256;
    private static final LinkedHashMap<Long, Biome> cache =
            new LinkedHashMap<Long, Biome>(MAX_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, Biome> eldest) {
                    return size() > MAX_SIZE;
                }
            };

    public static Biome get(long key) { return cache.get(key); }
    public static void put(long key, Biome value) { cache.put(key, value); }
}
