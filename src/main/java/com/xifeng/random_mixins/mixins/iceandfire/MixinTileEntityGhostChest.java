package com.xifeng.random_mixins.mixins.iceandfire;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityGhostChest;
import com.xifeng.random_mixins.config.ModConfig;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(value = TileEntityGhostChest.class)
public class MixinTileEntityGhostChest extends TileEntityChest {
    @Unique
    private int randomMixins$spawnedGhosts = 0;

    @Inject(
            method = "openInventory",
            at = @At("HEAD"),
            cancellable = true
    )
    private void beforeOpenInventory(EntityPlayer player, CallbackInfo ci) {
        if(this.randomMixins$spawnedGhosts >= ModConfig.IaFRotN.spawnLimit) {
            super.openInventory(player);
            ci.cancel();
            return;
        }
        this.randomMixins$spawnedGhosts ++;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.randomMixins$spawnedGhosts = compound.getInteger("SpawnedGhosts");
    }

    @Override
    @MethodsReturnNonnullByDefault
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("SpawnedGhosts", this.randomMixins$spawnedGhosts);
        return compound;
    }

}
