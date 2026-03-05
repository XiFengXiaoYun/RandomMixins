package com.xifeng.random_mixins.mixins.lycanitesmobs;

import com.lycanitesmobs.ExtendedWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = ExtendedWorld.class, remap = false)
public abstract class MixinExtendedWorld {
    @Shadow
    private static final String EXT_PROP_NAME = "LycanitesMobs";

    @Unique
    private static final Map<World, ExtendedWorld> randomAddon$loadedExtWorlds = new ConcurrentHashMap<>();


    /**
     * @author XiFengXiaoYun
     * @reason 暂时修复并发修改的问题
     */
    @Overwrite
    public static ExtendedWorld getForWorld(World world) {
        if (world == null) {
            return null;
        }

        synchronized (randomAddon$loadedExtWorlds) {
            ExtendedWorld worldExt = randomAddon$loadedExtWorlds.get(world);
            if (worldExt != null) {
                return worldExt;
            }

            WorldSavedData worldSavedData = world.getPerWorldStorage().getOrLoadData(ExtendedWorld.class, EXT_PROP_NAME);
            if (worldSavedData != null) {
                worldExt = (ExtendedWorld) worldSavedData;
                worldExt.world = world;
                worldExt.init();
            } else {
                worldExt = new ExtendedWorld(EXT_PROP_NAME);
                worldExt.world = world;
                world.getPerWorldStorage().setData(EXT_PROP_NAME, worldExt);
            }

            randomAddon$loadedExtWorlds.put(world, worldExt);
            return worldExt;
        }
    }


    /**
     * @author XiFengXiaoYun
     * @reason 暂时修复并发修改的问题
     */
    @Overwrite
    public static void remove(World world) {
        synchronized (randomAddon$loadedExtWorlds) {
            randomAddon$loadedExtWorlds.remove(world);
        }
    }
}
