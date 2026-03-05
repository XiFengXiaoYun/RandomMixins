package com.xifeng.random_mixins.mixins.artemislib;

import com.artemis.artemislib.compatibilities.sizeCap.ISizeCap;
import com.artemis.artemislib.compatibilities.sizeCap.SizeCapPro;
import com.artemis.artemislib.util.AttachAttributes;
import com.artemis.artemislib.util.attributes.ArtemisLibAttributes;
import com.xifeng.random_mixins.util.AttributeCache;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.WeakHashMap;

@Mixin(value = AttachAttributes.class, remap = false)
public abstract class MixinAttachAttributes {
    @Unique
    private static final WeakHashMap<EntityLivingBase, AttributeCache> randomAddon$cache = new WeakHashMap<>();

    /**
     * @author xifeng
     * @reason for performance
     */
    @Overwrite
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();

        // 快速路径：跳过玩家
        if (entity instanceof EntityPlayer) return;

        // 获取或创建缓存（只包含引用，不检查修饰符）
        AttributeCache cache = randomAddon$cache.get(entity);
        if (cache == null) {
            // 首次遇到该实体，初始化缓存
            ISizeCap cap = entity.getCapability(SizeCapPro.sizeCapability, null);
            if (cap == null) return; // 无能力，直接返回

            AbstractAttributeMap map = entity.getAttributeMap();
            IAttributeInstance heightInst = map.getAttributeInstance(ArtemisLibAttributes.ENTITY_HEIGHT);
            IAttributeInstance widthInst = map.getAttributeInstance(ArtemisLibAttributes.ENTITY_WIDTH);

            if (heightInst == null && widthInst == null) return; // 无属性，直接返回

            cache = new AttributeCache(heightInst, widthInst, cap);
            randomAddon$cache.put(entity, cache);
        }

        double currentHeight = cache.height.getAttributeValue();
        double currentWidth = cache.width.getAttributeValue();

        boolean needCheckModifiers = cache.wasTransformed || currentHeight != cache.lastHeightValue || currentWidth != cache.lastWidthValue;
        if (!needCheckModifiers) {
            return; // 无变化，直接返回，避免所有后续开销！
        }
        boolean hasModifier = !cache.height.getModifiers().isEmpty() || !cache.width.getModifiers().isEmpty();

        cache.lastHeightValue = currentHeight;
        cache.lastWidthValue = currentWidth;

        randomAddon$handleSize(cache.sizeCap, entity, (float) currentHeight, (float) currentWidth, hasModifier);
    }

    @Unique
    private static void randomAddon$handleSize(ISizeCap cap, EntityLivingBase entity, float height, float width, boolean hasModifier) {
        if(!hasModifier && cap.getTrans()) {
            entity.height = height;
            entity.width = width;
            final double d0 = width / 2.0D;
            final AxisAlignedBB aabb = entity.getEntityBoundingBox();
            entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, aabb.minY, entity.posZ - d0,
                    entity.posX + d0, aabb.minY + height, entity.posZ + d0));
            cap.setTrans(false);
        }

        if(hasModifier) {
            if(!cap.getTrans()) {
                cap.setDefaultHeight(entity.height);
                cap.setDefaultWidth(entity.width);
                cap.setTrans(true);
            } else {
                width = MathHelper.clamp(width, 0.04F, width);
                height = MathHelper.clamp(height, 0.08F, height);
                entity.height = height;
                entity.width = width;

                final double d0 = width / 2.0D;
                final AxisAlignedBB aabb = entity.getEntityBoundingBox();
                entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, aabb.minY, entity.posZ - d0,
                        entity.posX + d0, aabb.minY + entity.height, entity.posZ + d0));
            }
        }
    }
}
