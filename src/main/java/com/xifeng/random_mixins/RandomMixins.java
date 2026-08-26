package com.xifeng.random_mixins;

import com.xifeng.random_mixins.config.ModConfig;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class RandomMixins {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    public static boolean iafEnabled() {
        return Loader.isModLoaded("iceandfire") && ModConfig.IaFRotN.enable;
    }
    public static boolean dynaoresEnabled() {
        return Loader.isModLoaded("dynaores") && ModConfig.dynaoresEnabled;
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
    }

}
