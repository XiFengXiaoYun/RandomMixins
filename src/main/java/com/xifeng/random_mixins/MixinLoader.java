package com.xifeng.random_mixins;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.*;
import java.util.function.BooleanSupplier;

public class MixinLoader implements ILateMixinLoader {
    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new LinkedHashMap<>();

    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<>(MIXIN_CONFIGS.keySet());
    }

    static {
        addMixinConfig("mixins.random_mixins.lycanitesmobs.json",  "lycanitesmobs");
        addMixinConfig("mixins.random_mixins.netherex.json",  "netherex");
        addMixinConfig("mixins.random_mixins.artemislib.json",   "artemislib");
    }

    private static void addMixinConfig(final String mixinConfig, String mod) {
        MIXIN_CONFIGS.put(mixinConfig, () -> Loader.isModLoaded(mod));
        RandomMixins.LOGGER.info("Loaded mixins " + mixinConfig + " for mod " + mod);
    }
}
