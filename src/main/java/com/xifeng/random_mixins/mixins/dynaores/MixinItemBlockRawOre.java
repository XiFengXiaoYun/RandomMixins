package com.xifeng.random_mixins.mixins.dynaores;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.smileycorp.dynaores.common.item.ItemBlockRawOre;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nonnull;

@Mixin(ItemBlockRawOre.class)
public class MixinItemBlockRawOre extends ItemBlock {
    public MixinItemBlockRawOre(Block block) {
        super(block);
    }

    /**
     * @author xifeng
     * @reason fix the localization issue of raw items, especially in Chinese
     */
    @MethodsReturnNonnullByDefault
    @Overwrite
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        return super.getItemStackDisplayName(stack);
    }
}
