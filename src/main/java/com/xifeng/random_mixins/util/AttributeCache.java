package com.xifeng.random_mixins.util;

import com.artemis.artemislib.compatibilities.sizeCap.ISizeCap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public final class AttributeCache {
    public final IAttributeInstance height;
    public final IAttributeInstance width;
    public final ISizeCap sizeCap;
    public boolean wasTransformed = false;
    public double lastHeightValue = 1.0;
    public double lastWidthValue = 1.0;

    public AttributeCache(IAttributeInstance h, IAttributeInstance w,  ISizeCap s) {
        this.height = h;
        this.width = w;
        this.sizeCap = s;
    }
}
