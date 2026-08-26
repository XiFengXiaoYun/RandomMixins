package com.xifeng.random_mixins.mixins.dynaores;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.smileycorp.dynaores.common.item.ItemRawOre;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nonnull;

@Mixin(ItemRawOre.class)
public class MixinItemRawOre extends Item {
    /**
     * @author xifeng
     * @reason fix the localization issue of raw items, especially in Chinese
     */
    @MethodsReturnNonnullByDefault
    @Overwrite
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        //Localizing by lang files instead of auto generating
        return super.getItemStackDisplayName(stack);
    }
}
