package com.xifeng.random_mixins.config;

import com.xifeng.random_mixins.Tags;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID)
public class ModConfig {
    public static IaFRotN ice_and_fire;
    public static OTG otg;
    @Config.Comment("Enable the dynaores item name fix, now can use lang key to localization the item name")
    public static boolean dynaoresEnabled = true;

    public static class IaFRotN {
        @Config.Comment("Enable this feature, require Ice and Fire RotN edition")
        public static boolean enable = false;

        @Config.Comment("The ghost spawn limit of ghost chest")
        public static int spawnLimit = 5;
    }

    public static class OTG {
        @Config.Comment("Set true to disable the ore gen feature of OTG, so that use other mods(e.g. cofh world) to handle the ore gen")
        public static boolean disableOreGen = false;
    }
}
