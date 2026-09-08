package com.xifeng.random_mixins.mixins.villagenames;

import astrotibs.villagenames.config.GeneralConfig;
import astrotibs.villagenames.handler.SpawnNamingHandler;
import astrotibs.villagenames.name.NameGenerator;
import astrotibs.villagenames.utility.LogHelper;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(SpawnNamingHandler.class)
public class MixinSpawnNamingHandler {
    /**
     * @author xifeng
     * @reason optimize the code structure
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onPopulating(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityLiving && !(event.getEntity() instanceof EntityPlayer) && !event.getEntity().world.isRemote) {
            EntityLiving entity = (EntityLiving)(event.getEntity());
            //Avoid entity serialization
            int targetAge = entity instanceof EntityAgeable ? ((EntityAgeable) entity).getGrowingAge() : -1;

            String entityClassPath = entity.getClass().toString().substring(6);
            if (GeneralConfig.modNameMappingAutomatic_map.get("ClassPaths").contains(entityClassPath) ) {
                String addOrRemove = ((String) ((GeneralConfig.modNameMappingAutomatic_map.get("AddOrRemove")).get( GeneralConfig.modNameMappingAutomatic_map.get("ClassPaths").indexOf(entityClassPath) ))).trim();

                //For later use
                String nameTag = entity.getCustomNameTag().trim();

                if (addOrRemove.equals("add") && GeneralConfig.nameEntities) {
                    if (nameTag.isEmpty() ||
                            (entityClassPath.equals("net.daveyx0.primitivemobs.entity.passive.EntityTravelingMerchant")
                                    && nameTag.equals("Traveling Merchant"))
                    ) {

                        String nameType = (String) ((GeneralConfig.modNameMappingAutomatic_map.get("NameTypes")).get( GeneralConfig.modNameMappingAutomatic_map.get("ClassPaths").indexOf(entityClassPath) ));

                        String[] newNameA = NameGenerator.newRandomName(nameType, new Random());
                        String newName = (newNameA[1]+" "+newNameA[2]+" "+newNameA[3]);
                        if (GeneralConfig.addJobToName && ( !(entity instanceof EntityVillager) || targetAge>=0 )) {
                            String careerTag = (String) ((GeneralConfig.modNameMappingAutomatic_map.get("Professions")).get( GeneralConfig.modNameMappingAutomatic_map.get("ClassPaths").indexOf(entityClassPath) ));
                            newName += ( (careerTag.trim().isEmpty() ) ? "" : "("+careerTag+")" );
                        }
                        entity.setCustomNameTag( newName );
                    }
                    else if (!nameTag.contains("(") && GeneralConfig.addJobToName && ( !(entity instanceof EntityVillager) || targetAge>=0 )) { // Target is named but does not have job tag: add one!
                        String careerTag = (String) ((GeneralConfig.modNameMappingAutomatic_map.get("Professions")).get( GeneralConfig.modNameMappingAutomatic_map.get("ClassPaths").indexOf(entityClassPath) ));
                        String newCustomName = nameTag + ( careerTag.trim().isEmpty() ? "" : " ("+careerTag+")" );
                        entity.setCustomNameTag( newCustomName.trim() );
                    }
                    else if (nameTag.contains("(") && !GeneralConfig.addJobToName) {
                        entity.setCustomNameTag(nameTag.substring(0, nameTag.indexOf("(")).trim());
                    }
                }
                else if (addOrRemove.equals("remove")) {
                    if (!nameTag.isEmpty()) {
                        entity.setCustomNameTag("");
                    }
                }
                if (!entity.isNonBoss() && entity.hasCustomName()) {
                    if (entity instanceof EntityDragon) {
                        LogHelper.warn("Ender Dragon custom names do not work properly.");
                    }
                }
            }
        }
    }
}
