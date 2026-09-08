package com.xifeng.random_mixins.mixins.villagenames;

import astrotibs.villagenames.config.village.VillageGeneratorConfigHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mixin(VillageGeneratorConfigHandler.class)
public class MixinVillageGeneratorConfigHandler {

    /**
     * @author xifeng
     * @reason remove the unnecessary exception catch
     */
    @Overwrite(remap = false)
    public static Map<String, ArrayList<String>> unpackBiomes(String[] inputList) {
        ArrayList<String> biomeNames = new ArrayList<>();
        ArrayList<String> villageTypes = new ArrayList<>();
        ArrayList<String> materialTypes = new ArrayList<>();
        ArrayList<String> disallowModSubs = new ArrayList<>();

        for (String entry : inputList) {
            String entry1 =  entry.replace("(", "").replace(")", "");
            String[] splitEntry = entry1.split("\\|", -1);

            String biomeName = splitEntry.length > 0 ? splitEntry[0].trim() : "";
            String villageType = splitEntry.length > 1 ? splitEntry[1].trim() : "";
            String materialType = splitEntry.length > 2 ? splitEntry[2].trim() : "";
            String disallowModSub = splitEntry.length > 3 ? splitEntry[3].trim() : "";

            if(!biomeName.isEmpty()) {
                biomeNames.add(biomeName);
                villageTypes.add(villageType);
                materialTypes.add(materialType);
                disallowModSubs.add(disallowModSub);
            }
        }

        Map<String,ArrayList<String>> map =new HashMap<>();
        map.put("BiomeNames",biomeNames);
        map.put("VillageTypes",villageTypes);
        map.put("MaterialTypes",materialTypes);
        map.put("DisallowModSubs",disallowModSubs);

        return map;
    }
}
