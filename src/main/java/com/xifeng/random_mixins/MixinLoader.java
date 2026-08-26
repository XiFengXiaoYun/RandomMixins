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
        addMixinConfig("netherex", () -> Loader.isModLoaded("netherex"));
        addMixinConfig("iceandfire", RandomMixins::iafEnabled);
        addMixinConfig("dynaores", RandomMixins::dynaoresEnabled);
    }

    private static void addMixinConfig(String mod, BooleanSupplier supplier) {
        MIXIN_CONFIGS.put("mixins.random_mixins." + mod + ".json", supplier);
        RandomMixins.LOGGER.info("RandomMixins: Loaded mixins for mod " + mod);
    }
}
