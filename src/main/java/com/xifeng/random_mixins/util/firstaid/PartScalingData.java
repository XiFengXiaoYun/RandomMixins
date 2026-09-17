package com.xifeng.random_mixins.util.firstaid;

import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;

public class PartScalingData {
    public final AbstractDamageablePart part;
    public final int baseValue;
    public final float remainder;
    public int finalValue;

    public PartScalingData(AbstractDamageablePart part, int baseValue, float remainder) {
        this.part = part;
        this.baseValue = baseValue;
        this.remainder = remainder;
    }
}
